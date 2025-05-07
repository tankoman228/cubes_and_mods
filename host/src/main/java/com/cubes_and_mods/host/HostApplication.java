package com.cubes_and_mods.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cubes_and_mods.host.service.Config;

@SpringBootApplication(scanBasePackages = {
    "com.cubes_and_mods.host",
    "com.cubes_and_mods.host.service"
})
public class HostApplication {

 public static void main(String[] args) {
  
  Config.INIT();
  
  SpringApplication.run(HostApplication.class, args);
 }
}
