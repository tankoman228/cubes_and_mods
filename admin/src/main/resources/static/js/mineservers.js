console.log('mineservers import');

export let data = {
    mines: [],
	has_minechart: {}
}

export let methods = {
    loadMineDiagram(m) {
        // Пока не трогай, сам реализую
    },
    openConsole(m) {
        // Открытие сокета по адресу консоли
        const socketURL = `ws://${m.machine.address}/console`; // Формируем URL для WebSocket
        const socket = new WebSocket(socketURL);

        alert(
            "Вывод сокета можно посмотреть в консоли. " +
            "Откройте консоль JS через ПКМ -> посмотреть код. " +
            "Сделал так, чтобы не засорять страницу, поверьте, там данных выводит много. " +
            "Если хотите закрыть сокет, обновите страницу");

        // Отправляем ID mineserver при открытии соединения
        socket.onopen = () => {
            socket.send(m.mineserver.id);
            console.log('Сокет открыт:', socketURL);
        };

        // Обработка сообщений из сокета
        socket.onmessage = (event) => {
            console.log('Получено сообщение:', event.data);
        };

        socket.onclose = () => {
            console.log('Сокет закрыт');
        };

        socket.onerror = (error) => {
            console.error('Ошибка сокета:', error);
        };
    },
    async deleteMineserver(m) {
        // Запросить подтверждение
        const confirmation = confirm("Вы уверены, что хотите удалить этот игровой сервер?");
        if (!confirmation) return;

        try {
            // Отправляем POST запрос на удаление сервера
            const deleteServerResponse = await fetch(`http://${m.machine.address}/delete_server/${m.mineserver.id}`, {
                method: 'POST'
            });
            if (!deleteServerResponse.ok) {
                alert('Ошибка при удалении сервера'); return;
            }

            // Получаем все бекапы для данного mineserver
            const backupResponse = await fetch(`http://${m.machine.address}/backup/all/${m.mineserver.id}`, {
				method: 'POST'
			});
            const backups = await backupResponse.json();

            // Удаляем каждый бекап
            for (const backup of backups) {
                const deleteBackupResponse = await fetch(`http://${m.machine.address}/backup/delete/${m.mineserver.id}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(backup)
                });
                if (!deleteBackupResponse.ok) {
                    console.error('Ошибка при удалении бекапа:', backup);
                }
            }

            // Удаляем mineserver
            const finalDeleteResponse = await fetch(`http://localhost:8084/mineservers/delete/${m.mineserver.id}`, {
                method: 'DELETE'
            });
            if (!finalDeleteResponse.ok) {
                throw new Error('Ошибка при окончательном удалении mineserver');
            }

            alert('Сервер успешно удалён! Все файлы удалены');

            this.loadMines(); // Обновляем список серверов после удаления
        } catch (error) {
            alert('Ошибка при удалении mineserver:', error);
        }
    },
    async loadMines() {
        try {
            const response = await fetch('http://localhost:8084/mineservers/all', {
                method: 'POST'
            });
            if (!response.ok) {
                throw new Error('Ошибка при загрузке mineservers');
            }

            const mineservers = await response.json();

            this.mines = [];
            mineservers.forEach((mineserver) => {
				
				let machine = this.machines.find(m => m.id === mineserver.id_machine);
				let tariff = this.tariffs.find(t => t.id === mineserver.id_tariff);
				//let user = this.tariffs.find(t => t.id === mineserver.id_tariff);
				
				//alert(machine.name);
				//alert(tariff.name);
				
                this.mines.push({
				    mineserver: mineserver,
				    machine: machine,
				    tariff: tariff,
				    runtime: (mineserver.seconds_working / 3600 / tariff.hours_work_max * 100).toFixed(2) + "% = " + mineserver.seconds_working / 3600 + " ч. ",
				    disk: ((mineserver.memory_used / tariff.memory_limit) * 100).toPrecision(2) + " %"
				});
            });
            
        } catch (error) {
            console.error('Ошибка при загрузке mineservers:', error);
        }
    }
}

export async function mounted() {
    // Перед загрузкой списка надо подождать, чтобы загрузился список machines и tariffs
    await new Promise(r => setTimeout(r, 2000));
    await this.loadMines(); // Загружаем список mineservers
}

