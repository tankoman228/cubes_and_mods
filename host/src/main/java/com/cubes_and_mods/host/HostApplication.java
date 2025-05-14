package com.cubes_and_mods.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import com.cubes_and_mods.host.service.Config;

@SpringBootApplication
public class HostApplication {

 public static void main(String[] args) {
  
  //Config.INIT();
  
  SpringApplication.run(HostApplication.class, args);
 }
}
