package com.cubes_and_mods.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.cubes_and_mods.auth.jpa.MicroserviceSession;
import com.cubes_and_mods.auth.jpa.repos.MicroserviceSessionRepos;
import com.cubes_and_mods.auth.service.VerifyWebClient.VerifyWebRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.Map;


@Service
public class ServiceMicroserviceSession {

	@Autowired
	private MicroserviceSessionRepos repos;

    public HttpStatus RegisterMicroservice(String ip_port, String service_type) {
        
    	System.out.println("start register");
    	
        var session = repos.findById(ip_port).orElse(null);
    	try {    
    		
            // Generate 2 cryptographically secure random numbers
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            int a = secureRandom.nextInt(10000018);
            int b = secureRandom.nextInt(10000840);

			VerifyWebRequest VerifyWebRequest = new VerifyWebRequest(a, b);
			
			System.out.println("client creating");
			VerifyWebClient verifyWebClient = new VerifyWebClient(ip_port, service_type);	
			if (!verifyWebClient.verify(VerifyWebRequest)) {
                if (session != null) {
                    session.setAlarm(true);
                    session.setBanned(true);
                    repos.save(session);
                }
				return HttpStatus.FORBIDDEN;
			}
            else {
                if (session == null) {
                    session = new MicroserviceSession();
                    session.setIpPort(ip_port);
                    session.setFirstRegister(LocalDateTime.now());
                    // По умолчанию все сессии забанены, их админы вручную разблокируют для избежания подделок
                    session.setAlarm(true);
                    session.setBanned(true);
                }
                session.setServiceType(service_type);
                session.setLastRegister(LocalDateTime.now());
                repos.save(session);		

                return HttpStatus.OK;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                session.setAlarm(true);
                repos.save(session);
            }
            return HttpStatus.BAD_REQUEST;
        }
    }
}
