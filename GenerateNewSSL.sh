#!/bin/sh

echo "Очистка ключей"
rm -rf "ssl_temp/"

# Список служб
SERVICES="servers host auth order admin web"

# Пароль для keystore/truststore (замени на свой)
PASSWORD="yourpassword"

# Директория для временного хранения сертификатов
TEMP_DIR="ssl_temp"
mkdir -p $TEMP_DIR

# Генерация ключей и сертификатов для каждой службы
for SERVICE in $SERVICES; do
    echo "Генерация ключа и сертификата для $SERVICE"
    
    # Генерация закрытого ключа
    openssl genrsa -out $TEMP_DIR/${SERVICE}.key 2048
    
    # Генерация самоподписного сертификата
    openssl req -new -x509 -key $TEMP_DIR/${SERVICE}.key -out $TEMP_DIR/${SERVICE}.crt -days 365 -subj "/CN=${SERVICE}"
    
    # Создание PKCS12 хранилища
    openssl pkcs12 -export -in $TEMP_DIR/${SERVICE}.crt -inkey $TEMP_DIR/${SERVICE}.key -out $TEMP_DIR/${SERVICE}.p12 -name ${SERVICE} -passout pass:$PASSWORD
    
    # Добавление сертификата в truststore клиента
    keytool -import -alias ${SERVICE}-cert -file $TEMP_DIR/${SERVICE}.crt -keystore $TEMP_DIR/clientTrustStore.jks -storepass $PASSWORD -noprompt
    
    # Копирование в проект
    if [ -d "${SERVICE}/src/main/resources" ]; then
        cp $TEMP_DIR/${SERVICE}.p12 ${SERVICE}/src/main/resources/${SERVICE}.p12
        cp $TEMP_DIR/clientTrustStore.jks ${SERVICE}/src/main/resources/clientTrustStore.jks
    fi

    echo "Ключ для ${SERVICE} готов"
done

echo "Генерация и копирование завершены."
echo "Новые сертификаты успешно (надеюсь) установлены для каждой службы"
echo "Убедитесь, что application.properties содержит верные данные о путях (пример на службе servers):"
echo ""
echo "server.ssl.key-store-type=PKCS12"
echo "server.ssl.key-store=classpath:servers.p12"
echo "server.ssl.key-store-password=yourpassword"
echo "server.ssl.key-alias=servers"
echo ""

echo 'У службы-клиента в ресурсы запихиваем clientTrustStore.jks'
echo
echo 'В коде веб-клиента добавляем доверенные сертификаты (их только что скрипт сгенерировал и установил): '
echo ' this.webClient = WebClient.builder()'
echo ' 	        		.baseUrl("https://localhost:8084/")	'
echo '	                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()'
echo '	                        .secure(sslContextSpec -> {'
echo '	                            try {'
echo '	                                // Загрузка вашего trust store'
echo '	                                KeyStore trustStore = KeyStore.getInstance("JKS");'
echo '	                                try (FileInputStream trustStoreStream = new FileInputStream("src/main/resources/clientTrustStore.jks")) {'
echo '	                                    trustStore.load(trustStoreStream, "yourpassword".toCharArray());'
echo '	                                }'
echo ''
echo '	                                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());'
echo '	                                trustManagerFactory.init(trustStore);'
echo ''
echo '	                                sslContextSpec.sslContext(SslContextBuilder.forClient()'
echo '	                                        .trustManager(trustManagerFactory));'
echo '	                            } catch (Exception e) {'
echo '	                                throw new RuntimeException("Failed to set SSL context", e);'
echo '	                            }'
echo '	                        }))'
echo '	                    )'
echo '	        		.build();'
echo ''
echo 'Теперь клиент только по верным сертификатам будет отправлять запросы. HTTP, к примеру, теперь принимать не будет, в адресе обязательно укажи https'

echo 'Ткни, чтоб завершить'
read 'Ткни, чтоб завершить'


