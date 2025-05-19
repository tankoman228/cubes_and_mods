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
import com.cubes_and_mods.auth.security.ProtectedRequest;
import com.cubes_and_mods.auth.service.VerifyWebClient.VerifyWebRequest;

import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * Управление сессиями микросервисов
 */
@Service
public class ServiceMicroserviceSession {

	@Autowired
	private MicroserviceSessionRepos repos;

    private static final HashMap<String, String> sessionsAddressKey = new HashMap<>();


    public HttpStatus RegisterMicroservice(String ip_port, String service_type, Callback callback) {
        
        var session = repos.findById(ip_port).orElse(null);
    	try {    
            // Генерация 2 криптографически безопасных случайных чисел
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            int a = secureRandom.nextInt(10000018);
            int b = secureRandom.nextInt(10000840);

			VerifyWebRequest VerifyWebRequest = new VerifyWebRequest(a, b);
			VerifyWebClient verifyWebClient = new VerifyWebClient(ip_port, service_type);
			
            // Проверяем, что микросервис имеет закрытый ключ для своего типа
			if (!verifyWebClient.verify(VerifyWebRequest)) {
                if (session != null) {
                    session.setAlarm(true);
                    session.setBanned(true);
                    repos.save(session);
                }
				return HttpStatus.FORBIDDEN; // Ключ не верный
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

                callback.session(session);

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
    @FunctionalInterface
    public interface Callback {
        void session(MicroserviceSession d);
    }


    public MicroserviceSession FindMicroserviceSession(ProtectedRequest<?> body) {

        try {
            var session = repos.findById(body.serviceSessionId).orElse(null);

            var key = sessionsAddressKey.get(body.serviceSessionId);
            var alpha_got = body.alpha;
    
            body.generateAlpha(key);
            var alpha_expected = body.alpha;
    
            if (!alpha_expected.equals(alpha_got)) {
                if (session != null) {
                    session.setAlarm(true);
                    repos.save(session);
                    repos.flush();
                }
                return null;
            }
            else {
                return session;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
