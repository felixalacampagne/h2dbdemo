package com.felixalacampagne.account;

import com.felixalacampagne.account.persistence.repository.RepositoryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RepositoryConfig.class})
public class MainTest {
     public static void main(String[] args) {
         SpringApplication.run(MainTest.class, args);
  }
}
