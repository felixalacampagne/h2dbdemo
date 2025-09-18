package com.felixalacampagne.account.persistence;

import com.felixalacampagne.account.MainTest;
import com.felixalacampagne.account.persistence.entities.Account;
import com.felixalacampagne.account.persistence.repository.AccountJpaRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@SpringBootTest(classes = {MainTest.class})
@Configuration

public class H2SeqDemoIntegrationTest {

private static final Logger log = LoggerFactory.getLogger(H2SeqDemoIntegrationTest.class);


@Autowired
AccountJpaRepository acountJpaRepository;

   @Test
   void addAccountAfterPopulationFromDataSQL()
   {
      Account acc = new Account();

      acc.setAccAddr("testAddr");
      acc.setAccCode("testCode");
      acc.setAccCurr("EUR");
      acc.setAccFmt("#,###.00");

      acc = acountJpaRepository.save(acc);
      acountJpaRepository.flush();

      log.info("addAccountAfterPopulationFromDataSQL: new acc: {}", acc);

      List<Account> all = acountJpaRepository.findAll();
      log.info("addAccountAfterPopulationFromDataSQL: Account count: {}", all.size());
   }

}