package com.cubes_and_mods.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//import com.cubes_and_mods.host.service.Config;

@SpringBootApplication
@EnableScheduling
public class HostApplication {

 public static void main(String[] args) {
  SpringApplication.run(HostApplication.class, args);
 }
}
