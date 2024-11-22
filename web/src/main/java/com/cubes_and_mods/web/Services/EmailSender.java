package com.cubes_and_mods.web.Services;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSender{

	 @Autowired
	 public JavaMailSender emailSender;

	    public void sendSimpleEmail(String toAddress, String subject, String message) {
	        System.out.println("Начато формирование письма");

	        MimeMessage mimeMessage = emailSender.createMimeMessage();
	        MimeMessageHelper helper;

	        try {
	            helper = new MimeMessageHelper(mimeMessage, true);
	            helper.setTo(toAddress);
	            helper.setSubject(subject);
	            helper.setText(message, false);
	            helper.setFrom("sergeypanz355@mail.ru", "Кубы и Моды");

	            System.out.println("Завершено формирование письма");

	            emailSender.send(mimeMessage);
	            System.out.println("Готово!");
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            System.out.println("Ошибка при отправке письма: " + e.getMessage());
	        } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	            System.out.println("Ошибка неподдерживаемой кодировки: " + e.getMessage());
			}
	    }
}
