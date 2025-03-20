package org.example.grouptree;

import org.example.grouptree.model.FileProcessor;
import org.example.grouptree.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GroupTreeApplication implements CommandLineRunner {

    private final FileProcessorService fileProcessorService;

    @Autowired
    public GroupTreeApplication(FileProcessorService fileProcessorService) {
        this.fileProcessorService = fileProcessorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(GroupTreeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting group tree application");
        // Carregar o JSON do arquivo ao iniciar o sistema
        fileProcessorService.loadJsonFromFile("C:/Users/matheus.cabral/Documents/RP Info/teste.txt");
        System.out.println("Finished group tree application");
    }

}
