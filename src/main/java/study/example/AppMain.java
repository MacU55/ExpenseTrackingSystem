package study.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AppMain {


    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }

}

