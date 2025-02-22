package com.cubes_and_mods.host.service.mineserver_process;

/**
 * Использовал для подписки сокета к выводу процесса игрового сервера,
 * позволяет передать локигу вывода в лямбде
 * */
@FunctionalInterface
public interface ITextCallback {
	
	void Callback(String text);
}
