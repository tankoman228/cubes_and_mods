package com.cubes_and_mods.usr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
    	System.out.println("Хэш: " + hash);
    	System.out.println("Пароль: " + password);
    	System.out.println("Хэш пароля: " + hash(password));
    	System.out.println(hash(password).equals(hash)); //!?
        return hash(password).equals(hash);
    }
    
    public static boolean TEST_HASH() {
         System.out.println("is checkhash working? "+checkhash(hash("123"), "123"));
    	 return checkhash(hash("123"), "123");
    }
}
