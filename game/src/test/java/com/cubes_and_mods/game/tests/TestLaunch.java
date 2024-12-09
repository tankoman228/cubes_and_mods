package com.cubes_and_mods.game.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cubes_and_mods.game.controller.RootController;
import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Tariff;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;
import com.cubes_and_mods.game.service.Config;
import com.cubes_and_mods.game.service.ServiceMinecraftServerObserver;

@ExtendWith(MockitoExtension.class)
class TestLaunch {

	@Test
	void test() {
		//fail("I am failed!");
	}


 	@Mock
    private ReposMineserver mineservers; 

 	@Mock
    private ReposTariff tariffs; 
 	
    @Mock
    private ServiceMinecraftServerObserver observers; 

    @InjectMocks
    private RootController controller;

    
    
    @Test
    void launch_serverExists_shouldReturnOk() {
    	
    	// Он 100% будет провален, если запускать не через компиляцию (скрипт sh)
    	// Проверь, в указанном пути конфига (сервера) должен быть server_2 с майном
    	
    	Config.INIT_CONFIG();
        Integer id = 2;
        Mineserver mineserver = new Mineserver(); 
        mineserver.setId(id);
        mineserver.setIdTariff(id);
        
        Tariff tariff = new Tariff();
        tariff.setCpuThreads((short)2);
        tariff.setHoursWorkMax(9999);
        tariff.setMemoryLimit(9999);
        tariff.setRam((short)4);
        
        when(mineservers.findById(id)).thenReturn(Optional.of(mineserver));
        lenient().when(tariffs.findById(id)).thenReturn(Optional.of(tariff));
        
        ResponseEntity<Void> response = controller.launch(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String errOutput = errContent.toString();
        System.out.println("Captured error output: " + errOutput);
        
        assertEquals(true, controller.is_alive(id).getBody());
    }    
    
    
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
       }
}
