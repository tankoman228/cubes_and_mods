package com.cubes_and_mods.admin;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class WebController {

   @GetMapping("/api")
    public String apiPage(Model model) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/static/API.json");
        List<RequestType> requests = List.of(objectMapper.readValue(inputStream, RequestType[].class));
        model.addAttribute("requests", requests); // Передаем список запросов в модель
        return "api";  // Имя Thymeleaf шаблона без расширения .html
    }

    @GetMapping("/api/requests")
    public List<RequestType> getRequests() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/static/API.json");
        return List.of(objectMapper.readValue(inputStream, RequestType[].class));
    }
}
