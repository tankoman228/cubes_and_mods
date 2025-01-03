# Cubes & Mods : микросервис "usr"

## Установка

Откройте испольняемый jar файл проекта (см. релизы) при помощи архивного менеджера. 

Перейдите в BOOT-INF/classes

Отредактируйте application.properties, укажите валидные данные для подключения к базе данных

Всё, теперь достаточно запускать данную службу через java -jar

## Использование

Служба предоставляет управление списком пользователей. Реализует механизмы регистрации, авторизации, а также генерацию и проверку кодов подтверждения (как именно их связывать - вопрос не к этой службе).

То есть это просто проверка регистрации, авторизации и списка пользователей, механизмы блокирования и разблокирования пользователей и ничего более. Самый маленький микросервис из всех