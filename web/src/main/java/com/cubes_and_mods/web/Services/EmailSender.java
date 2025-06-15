package com.cubes_and_mods.web.Services;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSender{

	@Autowired
	public JavaMailSender emailSender;

	@Value("${spring.mail.username}")
    private String mailUsername;

	    public void sendSimpleEmail(String toAddress, String subject, String message, Boolean isHTML) {

	        MimeMessage mimeMessage = emailSender.createMimeMessage();
	        MimeMessageHelper helper;

	        try {
	            helper = new MimeMessageHelper(mimeMessage, true);
	            helper.setTo(toAddress);
	            helper.setSubject(subject);
	            helper.setText(message, isHTML);
	            helper.setFrom(mailUsername, "Кубы и Моды");

	            System.out.println("Завершено формирование письма");

	            emailSender.send(mimeMessage);
	            System.out.println("Готово!");
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            System.out.println("Ошибка при отправке письма: " + e.getMessage());
	        } catch (UnsupportedEncodingException e) {
				e.printStackTrace();
	            System.out.println("Ошибка неподдерживаемой кодировки: " + e.getMessage());
			}
	    }
}
