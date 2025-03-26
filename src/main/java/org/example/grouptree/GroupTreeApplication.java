package org.example.grouptree;

import org.example.grouptree.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

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

        // Usa o Scanner para ler o caminho do arquivo(Ex de caminho: C:/Users/matheus.cabral/Documents/RP Info/grupos.txt)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o caminho do arquivo: ");
        String filePath = scanner.nextLine(); // Captura o caminho do arquivo informado pelo usuário

        // Lê a mascara
        System.out.println("Informe a máscara(deve ser utilizado apenas o delimitador '.'): ");
        String mascara = scanner.nextLine(); // Captura a mascara a ser aplicada na classificacao

        // Carrega o JSON com o caminho fornecido pelo usuário
        fileProcessorService.loadJsonFromFile(filePath, mascara);

        System.out.println("Finished group tree application");
    }

}
