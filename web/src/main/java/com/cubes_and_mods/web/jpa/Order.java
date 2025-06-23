package com.cubes_and_mods.web.jpa;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Order {

    private String code;

    private LocalDateTime madeAt;

    private Boolean confirmed;

    private LocalDateTime closedAt;
    
    private Client clientOrder; 
    
    private Server serverOrder; 
    
    private Tariff tariffOrder;

	private Host hostOrder;
}
