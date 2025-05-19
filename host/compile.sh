#!/bin/bash

# Убиваем процесс, занимающий порт 8083
PORT=8083
PID=$(lsof -t -i:$PORT)
if [ -n "$PID" ]; then
    echo "Убиваем процесс, занимающий порт $PORT (PID: $PID)"
    kill -9 $PID
else
    echo "Нет процесса, занимающего порт $PORT"
fi

# Выполняем сборку проекта
echo "Сборка проекта..."
mvn clean install || { echo "Сборка проекта завершилась с ошибкой"; exit 1; }


echo "Перенос файлов внешней кофигурации"
SOURCE_FILE="paths.properties"
TARGET_DIR="target"

if [ -f "$SOURCE_FILE" ]; then
    cp "$SOURCE_FILE" "$TARGET_DIR/"
    echo "Файл $SOURCE_FILE успешно скопирован в папку $TARGET_DIR."
else
    echo "Файл $SOURCE_FILE не найден."
fi


# Переходим в директорию target
cd target || { echo "Не удалось перейти в директорию target"; exit 1; }

# Запускаем JAR-файл
echo "Запускаем JAR-файл..."
java -jar host-0.0.1-SNAPSHOT.jar

