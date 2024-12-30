package com.cubes_and_mods.admin.controller;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.RequestType;
import com.cubes_and_mods.admin.db.Machine;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class WebController {

   @GetMapping()
    public String apiPage(Model model) throws Exception {
	   
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/static/API.json");
        List<RequestType> requests = List.of(objectMapper.readValue(inputStream, RequestType[].class));
        model.addAttribute("requests", requests); 
        
        return "api"; 
    }
   

}
