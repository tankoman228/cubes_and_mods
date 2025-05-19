package com.cubes_and_mods.auth.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Простой хеширователь паролей, генерация и проверка хешей
 * 
 * Хеширование с особой солью, защищиает от всего близкого к радужной таблице
 * Соль зависит от ID пользователя и настроек системы
 * */
@Component
public class PasswordHash {
		
	@Deprecated
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	@Deprecated
    public static boolean checkhash(String hash, String password) {
        return hash(password).equals(hash);
    }
	
	public PasswordHash() {
		
	}
    
    public static boolean TEST_HASH() {
         
    	var PasswordHash = new PasswordHash();
    	
    	boolean good = true;
    	
    	String pwd = "123";
    	int id = 12;
    	
    	for (int i = 0; i < 2; i++) {
    		good = good && PasswordHash.checkHash(PasswordHash.hash(pwd + i, id + i), pwd + i, id + i);
    		//System.out.println(i + " " + good);
    		//System.out.println(PasswordHash.hash(pwd + i, id + i));
    	}	
    	
    	return good;
    }
	
	@Value("#{new Long('${salt-modifier}')}")
	private Long salt_modifier = 3000L; // На практиике большое и секретное
	
	public String hash(String password_, int id_user) {
		
		// Самый солевой алгоритм в мире
		// Кстати, все пароли будут весьма большими и сложными, удачи во взломе радужными таблицами
		// система не даст зарегать простой пароль, запасайся квантовыми компьютерами
		
		long salt_seed = id_user * id_user * 11 / 2 + salt_modifier;
		Random random = new Random(salt_seed + password_.length());
		random = new Random(salt_seed);
		
		String salt = sha256(String.valueOf(random.nextInt() + password_.length()));
		salt = makeSalt(salt + password_, salt_modifier % 13 + 100);
		
		// Шифр Цезаря-Татыржи по приколу
		String pwd = "";
		String bibki = sha256(salt + password_.toLowerCase());
		for (int i = bibki.length()-1; i > 0; i--) {
			pwd += (char)((int)bibki.charAt(i) + salt_modifier);
			if (random.nextBoolean()) {
				pwd += (char)((int)bibki.charAt(i) - salt_modifier);
			}
		}
		
		var p = (sha256(password_ + pwd + salt) + bibki).replace("f", "🤓");
		
		//System.out.println(password_ + " " + p);
		
		return p;
	}
	
    private String makeSalt(String salt, long iterations) {
    	
    	if (iterations == 0)
    		return salt;
    	
    	return makeSalt(sha256(salt), iterations-1);
    }
	
    private String sha256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
    public boolean checkHash(String hash, String password, int id_user) {    
    	return hash.equals(this.hash(password, id_user));
    }
    

}
