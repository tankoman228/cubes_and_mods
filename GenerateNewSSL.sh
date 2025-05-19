#!/bin/sh

TEMP_DIR="ssl_temp"
SERVICES="servers host auth order admin web"
PASSWORD="yourpassword"

echo "Очистка временной директории"
rm -rf "${TEMP_DIR}/"
mkdir -p ssl_temp

# Генерация ключей, сертификатов и добавление их в truststore
for SERVICE in $SERVICES; do

    # Создание пустого truststore
    echo "Создаём пустой truststore"
    keytool -genkey -alias temp -keystore $TEMP_DIR/clientTrustStore$SERVICE.jks -storepass $PASSWORD -keypass $PASSWORD -dname "CN=temp" -noprompt
    # Удаляем временную запись
    keytool -delete -alias temp -keystore $TEMP_DIR/clientTrustStore$SERVICE.jks -storepass $PASSWORD

    echo "Генерация ключа и сертификата для $SERVICE"
    
    
    # Генерация закрытого ключа
    openssl genrsa -out $TEMP_DIR/${SERVICE}.key 2048
    
    # Генерация самоподписного сертификата
    openssl req -new -x509 -key $TEMP_DIR/${SERVICE}.key -out $TEMP_DIR/${SERVICE}.crt -days 365 -config openssl.cnf

    # Создание PKCS12 хранилища (если требуется)
    openssl pkcs12 -export -in $TEMP_DIR/${SERVICE}.crt -inkey $TEMP_DIR/${SERVICE}.key -out $TEMP_DIR/${SERVICE}.p12 -name ${SERVICE} -passout pass:$PASSWORD
    

    echo "Создаём пустой truststore"

    keytool -genkey -alias temp -keystore $TEMP_DIR/clientTrustStore${SERVICE}.jks -storepass $PASSWORD -keypass $PASSWORD -dname "CN=temp" -noprompt

    # Удаляем временную запись
    keytool -delete -alias temp -keystore $TEMP_DIR/clientTrustStore${SERVICE}.jks -storepass $PASSWORD

    keytool -import -alias ${SERVICE}-cert -file $TEMP_DIR/${SERVICE}.crt -keystore $TEMP_DIR/clientTrustStore${SERVICE}.jks -storepass $PASSWORD -noprompt
    
    keytool -list -keystore $TEMP_DIR/clientTrustStore${SERVICE}.jks -storepass $PASSWORD

    # Если необходимо, копируем файлы в проект
    if [ -d "${SERVICE}/src/main/resources" ]; then
        cp $TEMP_DIR/${SERVICE}.p12 ${SERVICE}/src/main/resources/${SERVICE}.p12    
        for SERVICE2 in $SERVICES; do
            cp $TEMP_DIR/clientTrustStore${SERVICE}.jks ${SERVICE2}/src/main/resources/clientTrustStore${SERVICE}.jks  
        done
    fi

    echo "Ключ и сертификат для ${SERVICE} готовы"
done

echo "Скрипт выполнен. Обновлённый truststore находится в $TEMP_DIR/clientTrustStore.jks"


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


