package com.cubes_and_mods.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import com.cubes_and_mods.auth.jpa.MicroserviceSession;
import com.cubes_and_mods.auth.jpa.repos.MicroserviceSessionRepos;
import com.cubes_and_mods.auth.service.VerifyWebClient.VerifyWebRequest;

import java.math.BigInteger;
import java.security.SecureRandom;


@Service
public class ServiceMicroserviceSession {

	@Autowired
	private MicroserviceSessionRepos repos;

    private static final HashMap<String, String> sessionsAddressKey = new HashMap<>();


    public HttpStatus RegisterMicroservice(String ip_port, String service_type) {
        
        var session = repos.findById(ip_port).orElse(null);
    	try {    
            // Генерация 2 криптографически безопасных случайных чисел
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            int a = secureRandom.nextInt(10000018);
            int b = secureRandom.nextInt(10000840);

			VerifyWebRequest VerifyWebRequest = new VerifyWebRequest(a, b);
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
                
                // А это позволит сохранить момент для моего криптографического алгоритма
                sessionsAddressKey.put(ip_port, String.valueOf(a + b));

                repos.save(session);	
                repos.flush();	

                return HttpStatus.OK;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                session.setAlarm(true);
                repos.save(session);
                repos.flush();
            }
            return HttpStatus.BAD_REQUEST;
        }
    }


    public MicroserviceSession FindMicroserviceSession(String alpha) {

        Calendar cal = Calendar.getInstance();
        cal.set(2000, 0, 1, 0, 0, 0);
        long baseTime = cal.getTimeInMillis();
        long currentHours = (System.currentTimeMillis() - baseTime) / (60 * 60 * 1000);
        
        for (Map.Entry<String, String> entry : sessionsAddressKey.entrySet()) {
            String ipPort = entry.getKey();
            String c = entry.getValue();
            
            // Check current hour
            Date date = new Date(baseTime + (currentHours * 60 * 60 * 1000));
            if (d(c, date).equals(alpha)) {
                return repos.findById(ipPort).orElse(null);
            }
            
            // Check -1 hour
            date = new Date(baseTime + ((currentHours - 1) * 60 * 60 * 1000));
            if (d(c, date).equals(alpha)) {
                return repos.findById(ipPort).orElse(null);
            }
            
            // Check +1 hour
            date = new Date(baseTime + ((currentHours + 1) * 60 * 60 * 1000));
            if (d(c, date).equals(alpha)) {
                return repos.findById(ipPort).orElse(null);
            }
        }
        
        return null;
    }

    private String d(String c, Date date) {

        try {
            SecretKeySpec key = new SecretKeySpec(c.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            byte[] alphaBytes = mac.doFinal(date.toString().getBytes());
            return String.format("%064x", new BigInteger(1, alphaBytes)); // Convert to HEX
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
