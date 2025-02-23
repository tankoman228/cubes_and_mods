#!/bin/bash

# Имя контейнера
CONTAINER_NAME="mc_container"

echo "DELETE PREVIOUS"
sudo docker kill "$CONTAINER_NAME"
sudo docker rm "$CONTAINER_NAME"

# Создание и запуск контейнера с ограничениями по ресурсам
sudo docker run -dit \
  --name "$CONTAINER_NAME" \
  --memory="5g" \
  --cpus="4" \
  -p 12601:22 -p 25565:25565 -p 25566:25566 \
   pingvin-jdk17

# Установка SSH-сервера в контейнере и изменение конфигурации
sudo docker exec -it "$CONTAINER_NAME" bash -c "
  apt update && apt install -y openssh-server && \
  mkdir -p /var/run/sshd && \
  echo 'root:password1488' | chpasswd && \

sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config


  sed -i '1i PermitRootLogin yes' /etc/ssh/sshd_config && \
  sed -i '1i PasswordAuthentication yes' /etc/ssh/sshd_config && \
  service ssh start
"

# Печать сообщения об успешном запуске
echo "Контейнер '$CONTAINER_NAME' с SSH-сервером запущен."

# Дополнительная настройка firewall (если необходимо)
sudo ufw allow 12601/tcp
sudo ufw reload

DNS_SERVER="8.8.8.8"
CONTAINER_NAME="mc_container"

# Получаем ID контейнера
CONTAINER_ID=$(sudo docker inspect -f '{{.State.Pid}}' "$CONTAINER_NAME")

if [ -z "$CONTAINER_ID" ]; then
  echo "Контейнер $CONTAINER_NAME не найден!"
  exit 1
fi

# Создаём новый неймспейс netns для контейнера
sudo mkdir -p /var/run/netns
sudo ln -sf /proc/$CONTAINER_ID/ns/net /var/run/netns/$CONTAINER_NAME

# Запрещаем доступ к локальным подсетям
sudo ip netns exec $CONTAINER_NAME iptables -L -v -n

sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 10.8.0.0/24 -j REJECT

sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 192.168.0.0/16 -j REJECT
sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 10.0.0.0/8 ! -d "$VPN_SUBNET" -j REJECT
sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 10.0.0.0/8 ! -d "$DNS_SERVER" -j REJECT
sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 172.16.0.0/12 -j REJECT
sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 10.8.0.0/24 ! -d "$DNS_SERVER" -j REJECT

sudo ip netns exec $CONTAINER_NAME iptables -A OUTPUT -d 10.8.0.1 -p icmp -j REJECT


docker exec -it mc_container bash -c "echo 'nameserver 8.8.8.8' > /etc/resolv.conf"

echo "Доступ к локальной сети для контейнера '$CONTAINER_NAME' заблокирован."


