package com.cubes_and_mods.host.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * "Безопасный" запрос, зная alpha и ключ, можно проверить подлинность (симметричный алгоритм)
 */
public class ProtectedRequest<T> {

    public T data; // Содержимое запроса
    public String alpha; // Цифровая подпись (не совсем, алгоритм симметричный, но беопасный)
    public String userSession; // Сессия пользователя, необязательное поле
    public String serviceSessionId; // Сессия микросервиса, задаётся после регистрации в serviceSessionIdGlobal

    @JsonIgnore
    public static String c = null; // Ключ подписи (секретный, задаётся после запроса от auth когда проверяется SSL сертификат), задаётся раз на всю жизнь процесса

    @JsonIgnore
    public static String serviceSessionIdGlobal; // PK сессии микросервиса, задаётся после полного завершения регистрации в auth


    public ProtectedRequest(T data) {
        this.data = data;

        serviceSessionId = serviceSessionIdGlobal;
        generateAlpha();
    }
    public ProtectedRequest(T data, String userSession) {
        this.data = data;
        this.userSession = userSession;

        serviceSessionId = serviceSessionIdGlobal;
        generateAlpha();
    }
    public ProtectedRequest() {

        serviceSessionId = serviceSessionIdGlobal;
        generateAlpha();
    }

    @JsonIgnore
    public void generateAlpha() {  
        try {
            if (c == null) {
                throw new IllegalStateException("Секретный ключ 'c' не установлен");
            }

            ObjectMapper mapper = new ObjectMapper();

            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true); // Сортировка по алфавиту
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Игнорировать null-поля
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);  // Игнорировать циклические ссылки

            String dataJson = (data != null) ? mapper.writeValueAsString(data) : "{Отсылочка дял тех, кто копается в этом коде}"; 

            // Включите в подпись все значимые поля
            String payload = String.join("|", 
                dataJson,
                serviceSessionId,
                userSession != null ? userSession : ""
            );

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(c.getBytes(), "HmacSHA256");
            hmac.init(key);
            byte[] signatureBytes = hmac.doFinal(payload.getBytes());
            this.alpha = bytesToHex(signatureBytes);
        }
        catch (Exception e) {
            throw new IllegalStateException("Не удалось сгенерировать цифровую подпись");
        }
    }

    @JsonIgnore
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
