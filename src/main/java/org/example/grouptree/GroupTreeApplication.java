package org.example.grouptree;

import org.example.grouptree.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GroupTreeApplication implements CommandLineRunner {

    @Autowired
    private FileProcessorService fileProcessorService;

    public static void main(String[] args) {
        SpringApplication.run(GroupTreeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting group tree application");

        String filePath = "C:/Users/matheus.cabral/Documents/RP Info/grupos.txt";
        fileProcessorService.loadJsonFromFile(filePath);

        System.out.println("Finished group tree application");
    }

}
