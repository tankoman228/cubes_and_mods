package com.cubes_and_mods.order.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"order\"")  // Экранирование имени таблицы из-за использования зарезервированного слова
public class Order {

    @Id
    @Column(name = "code", nullable = false, length = 128)
    private String code;

    @Column(name = "made_at", nullable = false, columnDefinition = "timestamp DEFAULT now()")
    private LocalDateTime madeAt;

    @Column(name = "confirmed", nullable = false)
    private Boolean confirmed;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false, referencedColumnName = "id")
    private Client clientOrder; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_server", nullable = false, referencedColumnName = "id")
    private Server serverOrder; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tariff", nullable = false, referencedColumnName = "id")
    private Tariff tariffOrder;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_host", nullable = true, referencedColumnName = "id")
	private Host hostOrder;
}
