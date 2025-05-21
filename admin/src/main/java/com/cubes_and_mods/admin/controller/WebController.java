package com.cubes_and_mods.admin.controller;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Returns index.html with API from API.json
*/
@Controller
public class WebController {

   @GetMapping()
    public String apiPage(Model model) throws Exception {
        return "login"; 
    }
}
