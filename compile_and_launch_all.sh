#!/bin/bash

# Функция запуска микросервиса
launch_service() {
  SERVICE_NAME=$1
  gnome-terminal -- bash -c "
    echo Запуск $SERVICE_NAME...
    cd $SERVICE_NAME || exit 1
    mvn clean install || exit 1
    cd target || exit 1
    JAR_FILE=\$(ls *.jar | head -n 1)
    if [ -z \"\$JAR_FILE\" ]; then
      echo 'JAR файл не найден'
      exit 1
    fi
    java -jar \"\$JAR_FILE\"
  "
}

# Запуск auth первым
echo "Запуск auth..."
gnome-terminal -- bash -c "
  cd auth || exit 1
  mvn clean install || exit 1
  cd target || exit 1
  JAR_FILE=\$(ls *.jar | head -n 1)
  if [ -z \"\$JAR_FILE\" ]; then
    echo 'JAR файл не найден'
    exit 1
  fi
  java -jar \"\$JAR_FILE\"
"

# Ждём 5 секунд, чтобы auth успел запуститься
sleep 5

# Запускаем остальные микросервисы параллельно
for SERVICE in host order servers admin web; do
  launch_service "$SERVICE"
done

