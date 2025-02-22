package com.cubes_and_mods.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.cubes_and_mods.host.controller.RootController;
import com.cubes_and_mods.host.service.Config;
import com.cubes_and_mods.host.service.ServiceHandlers;
import com.cubes_and_mods.host.service.ServiceMinecraftServerObserver;
import com.cubes_and_mods.host.service.mineserver_process.MinecraftHandler;

@ExtendWith(MockitoExtension.class)
class TestLaunch {
/*
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

    @Mock
    private ServiceHandlers handlers; 

    @InjectMocks
    private RootController controller;
    
    
    @Test
    void launch_serverExists_shouldReturnOk() {
    	
    	Config.PATH_TO_SERVERS = "test/";
        Integer id = 2;
        Mineserver mineserver = new Mineserver(); 
        mineserver.setId(id);
        mineserver.setIdTariff(id);
        
        Tariff tariff = new Tariff();
        tariff.setCpuThreads((short)2);
        tariff.setHoursWorkMax(9999);
        tariff.setMemoryLimit((long)9999);
        tariff.setRam((short)4);
        
        lenient().when(handlers.get(2)).thenReturn(new MinecraftHandler(mineserver, tariff));
        lenient().when(mineservers.findById(id)).thenReturn(Optional.of(mineserver));
        lenient().when(tariffs.findById(id)).thenReturn(Optional.of(tariff));

        // Test data files (emit minecraft)
        File file = new File("test/server_2");
        file.mkdirs();
        file = new File("test/server_2/run.sh");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
        	try {         
				file.createNewFile();
	        	FileWriter f = new FileWriter(file);       	
	        	f.append("while true; do echo 123; done");
	        	f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        file = new File("test/server_2/server.properties");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
        	try {         
				file.createNewFile();
	        	FileWriter f = new FileWriter(file);       	
	        	f.append("max-players=2\nserver-port=25565\nquery.port=25565");
	        	f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        ResponseEntity<Void> response = controller.launch(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        try {
			Thread.sleep(200);
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
    }*/
}
