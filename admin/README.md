# Cubes & Mods : Микросервис "admin"

## Кратко

Данная служба позволяет администраторам редактировать список серверов, просматривать и собирать статистику. Отображает графики об использовании ресурсов. Также позволяет редактировать тарифы, блокировать пользователей и тестировать API.

По умолчанию запускается на 8001 порту. Предотвращайте ЛЮБОЙ ДОСТУП к службе, для не администраторов, ни в коем случае не пробрасывайте из локальной сети в глобальную. Эта панель предоставляет доступ к админским функциям.

## Установка

Для настройки откройте jar файл проекта (итоговый скомпилированный файл, вы его можете найти и скачать в релизах) в менеджере архивов. Перейдя в BOOT-INF/classes, отредактируйте application.properties, помимо прочих данных, укажите redirect.url. redirect - это служба, перенаправляющая запросы от служб buy, res и usr. Служба должна быть запущена и исправно работать. Если часть служб не работает или неверно указаны url и/или переадресация запросов. Рекомендуется запускать и устанавливать данную службу последней.

Перейдя в BOOT-INF/classes/static, отредактируйте config.js. Укажите прямые пути к службам. Если пути будут указаны неверно, сбор и получение данных не будет работать.

Сохраните изменения и запустите файл проекта через java -jar "res-[Версия].jar". Далее достаточно запускать файл таким же образом. Файлы со статистикой будут созданы автоматически в рабочей директории самой программой. Файлы можно удалять и редактировать, но это отобразится только на графиках.

## Использование

После запуска и корректной настройки вы увидите таблицу физических машин. Если таковые уже созданы, будут отображены графики в таблице. Если это первый запуск, создайте запись. 

Служба создана для руководителей абстрактной организации и/или системных администраторов. Следует разбираться в том, как именно работает 

Вводимые данные должны быть корректны. Укажите адрес, на котором запущена служба "game", а также ресурсы. Это максимум, который может быть выделен суммарно на все игровые сервера. Так если указано 16 гигабайт ОЗУ, на данной машине суммарно не будет занято больше ОЗУ игровыми серверами. Если на машине всего 16 гигабайт, укажите 14, оставьте 2 под систему и саму службу.

Подробно следуйте всем инструкциям в формах и подробно читайте текст. Так вы избежите ошибок при настройке и эксплуатации системы. 

На страничке по адресу http://localhost:8081 есть следующие разделы:

- сервера
- тарифы
- версии
- игровые сервера
- пользователи
- тестирование API (отладка для разработчиков)
