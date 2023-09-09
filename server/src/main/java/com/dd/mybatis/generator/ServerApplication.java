package com.dd.mybatis.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String dir = "config";
        String fileName = "sqlite3.db";
        File file = new File(dir);
        if (!file.exists()) {
            boolean _b = file.mkdir();
        }
        File sqlite3 = new File(String.join("/", dir, fileName));
        if (!sqlite3.exists()) {
            boolean _b = sqlite3.createNewFile();
        }
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().create();
    }
}
