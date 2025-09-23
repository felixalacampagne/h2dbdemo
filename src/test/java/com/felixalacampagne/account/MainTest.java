package com.felixalacampagne.account;

import com.felixalacampagne.account.persistence.repository.RepositoryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootApplication
// @TestPropertySource(locations = {"classpath:application-test.properties"}) this is completely ignored
// @ActiveProfiles("test") this is completely ignored - only solution is to places in every test class.
public class MainTest {
     public static void main(String[] args) {
         SpringApplication.run(MainTest.class, args);
  }
}
