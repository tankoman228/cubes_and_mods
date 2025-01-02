package com.cubes_and_mods.usr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Простой хеширователь паролей, генерация и проверка хешей
 * */
public class PasswordHash {
	
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

    public static boolean checkhash(String hash, String password) {
        return hash(password).equals(hash);
    }
    
    public static boolean TEST_HASH() {
    	 return checkhash(hash("123"), "123");
    }
}
