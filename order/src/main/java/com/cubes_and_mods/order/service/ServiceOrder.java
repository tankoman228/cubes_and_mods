package com.cubes_and_mods.order.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.order.jpa.Client;
import com.cubes_and_mods.order.jpa.Host;
import com.cubes_and_mods.order.jpa.Order;
import com.cubes_and_mods.order.jpa.Server;
import com.cubes_and_mods.order.jpa.repos.ClientRepos;
import com.cubes_and_mods.order.jpa.repos.HostRepos;
import com.cubes_and_mods.order.jpa.repos.OrderRepos;
import com.cubes_and_mods.order.jpa.repos.ServerRepos;
import com.cubes_and_mods.order.jpa.repos.TariffRepos;

@Service
public class ServiceOrder {

	@Autowired
	private OrderRepos orderRepos;

	@Autowired
	private ServerRepos serverRepos;

	@Autowired
	private TariffRepos tariffRepos;

	@Autowired
	private ClientRepos clientRepos;

	@Autowired
	private HostRepos hostRepos;


	private final Object lock = new Object();

	private final SecureRandom random = new SecureRandom();


    public HttpStatus AcceptOrder(String code) {

		synchronized (lock) {

			// Берём заказик
			var orderOptional = orderRepos.findAll().stream().filter(o -> o.getCode().equals(code)).findFirst();
			if (orderOptional.isEmpty()) return HttpStatus.NOT_FOUND;
			var order = orderOptional.get();

			if (order.getClosedAt() != null) return HttpStatus.BAD_REQUEST;

			// Обновляем запись о нём
			order.setConfirmed(true);
			order.setClosedAt(LocalDateTime.now());

			orderRepos.save(order);
			orderRepos.flush();

			var host = order.getHostOrder();

			// А теперь выполняем обещанное за заказ
			if (host != null) {

				// Смена тарифа
				if (!order.getTariffOrder().getId().equals(host.getTariffHost().getId())) {
					
					host.setSecondsWorking(0);
					host.setTariffHost(order.getTariffOrder());
				}

				// Продление
				else {
					host.setSecondsWorking(host.getSecondsWorking() - order.getTariffOrder().getHoursWorkMax() * 3600);
				}
			}
			else { // Новый игровой сервак!
				host = new Host();

				host.setName("Новый игровой сервер от " + LocalDateTime.now());
				host.setDescription("Создан после заказа " + code);

				host.setClientHost(order.getClientOrder());
				host.setTariffHost(order.getTariffOrder());
				host.setServerHost(order.getServerOrder());
				host.setSecondsWorking(0);
				host.setMemoryUsed(0L);
			}
			hostRepos.save(host);
			hostRepos.flush();

			// И обновляем данные о свободных ресурсах
			recalculateServer(host.getServerHost().getId());
		}

		return HttpStatus.OK;
    }

    public HttpStatus CancelOrder(String code) {

		System.out.println("[" + code + "] длина: " + code.length());

        synchronized (lock) {
			
			var orderOptional = orderRepos.findAll().stream().filter(o -> o.getCode().equals(code)).findFirst();
			if (orderOptional.isEmpty()) return HttpStatus.NOT_FOUND;
			var order = orderOptional.get();

			if (order.getClosedAt() != null) return HttpStatus.BAD_REQUEST;

			order.setConfirmed(false);
			order.setClosedAt(LocalDateTime.now());

			orderRepos.save(order);
			orderRepos.flush();

			recalculateServer(order.getServerOrder().getId());
		}

		return HttpStatus.OK;
    }

    public String MakeOrder(Order order, Client client) { 
               
        var tariff = tariffRepos.findById(order.getTariffOrder().getId());
        var serverOptional = serverRepos.findById(order.getServerOrder().getId());

        if (serverOptional.isEmpty()) return null;
        if (tariff.isEmpty()) return null;

		order.setTariffOrder(tariff.get());
		order.setClientOrder(client);
		order.setServerOrder(serverOptional.get());
		order.setHostOrder(order.getHostOrder());

		// Формируем уникальный код заказа, именно код, опасно использовать сырые ID
		var orderCode = 
        order.getClientOrder().getEmail() + 
        order.getTariffOrder().getName() + 
        LocalDateTime.now().toString() + 
        order.getServerOrder().getAddress() +  
        random.nextInt();
        while (orderCode.length() < 127) {
            orderCode += random.nextInt();
        }
        orderCode = orderCode.substring(0, 127);

		synchronized (lock) {
			
			var server = serverOptional.get();

			// Проверка, хватит ли ресурсов
			if (server.getCpuThreadsFree() < tariff.get().getCpuThreads() ||
				server.getRamFree() < tariff.get().getRam() ||
				server.getMemoryFree() < tariff.get().getMemoryLimit()) {
				System.out.println("Недостаточно ресурсов на сервере");
				return null;
			}

			// Создаём сам заказ
			order.setCode(orderCode); 
			order.setConfirmed(false);
			order.setClosedAt(null);
			order.setMadeAt(LocalDateTime.now());
			order.setClientOrder(client);

			// Сохраняем в базу и обновляем свободные ресурсы сервера
			orderRepos.save(order);
			orderRepos.flush();
			
			recalculateServer(server.getId());
		}

		return order.getCode();
    }

	/**
	 * Пересчитывает свободные ресурсы сервера, вызывать только внутри lock
	 */
	private void recalculateServer(Integer id_server) {
		
		var server = serverRepos.findById(id_server).get();
		
		// "Зануляем" всё зарезервированное
		short cpuThreadsFree = server.getCpuThreads();
		short ramFree = server.getRam();
		long memoryFree = server.getMemory();
		
		for (var order : server.getOrders()) {
			// Все заказы, которые ещё не закрыты, забирают ресурсы
			if (order.getClosedAt() == null) {
				cpuThreadsFree -= order.getTariffOrder().getCpuThreads();
				ramFree -= order.getTariffOrder().getRam();
				memoryFree -= order.getTariffOrder().getMemoryLimit();
			}
		}
		for (var host : server.getHosts()) {
			// Все хостинги на физическом сервере забирают ресурсы
			cpuThreadsFree -= host.getTariffHost().getCpuThreads();
			ramFree -= host.getTariffHost().getRam();
			memoryFree -= host.getTariffHost().getMemoryLimit();
		}

		server.setCpuThreadsFree(cpuThreadsFree);
		server.setRamFree(ramFree);
		server.setMemoryFree(memoryFree);

		serverRepos.save(server);
		serverRepos.flush();
	}
}
