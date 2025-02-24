package com.cubes_and_mods.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.Enumeration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ServiceMicroservices {

	public class RegisterMicroserviceRequest {
		public String public_key;
	}

    public HttpStatus RegisterMicroservice(String ip_port) {
        
		try {
            // Загружаем truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (InputStream is = new ClassPathResource("clientTrustStore.jks").getInputStream()) {
                trustStore.load(is, "yourpassword".toCharArray());
            }

            // Перебираем сертификаты
            Enumeration<String> aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
				System.out.println("Сертификат для службы: " + alias.replace("-cert", ""));
			}

				
        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return HttpStatus.OK;
    }
}
