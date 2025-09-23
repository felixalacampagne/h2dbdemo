package com.felixalacampagne.account.persistence;

import com.felixalacampagne.account.MainTest;
import com.felixalacampagne.account.persistence.entities.Account;
import com.felixalacampagne.account.persistence.entities.Transaction;
import com.felixalacampagne.account.persistence.repository.AccountJpaRepository;
import com.felixalacampagne.account.persistence.repository.TransactionJpaRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MainTest.class})
@Configuration
@ActiveProfiles("test")
public class H2SeqDemoIntegrationTest {

private static final Logger log = LoggerFactory.getLogger(H2SeqDemoIntegrationTest.class);


@Autowired
AccountJpaRepository accountJpaRepository;

@Autowired
TransactionJpaRepository transactionJpaRepository;

   @Test
   void addAccountAfterPopulationFromDataSQL()
   {
      Account acc = new Account();

      acc.setAccAddr("testAddr");
      acc.setAccCode("testCode");
      acc.setAccCurr("EUR");
      acc.setAccFmt("#,###.00");

      acc = accountJpaRepository.save(acc);
      accountJpaRepository.flush();

      log.info("addAccountAfterPopulationFromDataSQL: new acc: {}", acc);

      List<Account> all = accountJpaRepository.findAll();
      log.info("addAccountAfterPopulationFromDataSQL: Account count: {}", all.size());
   }

   @Test
   void addTransaction()
   {
      long txncount = transactionJpaRepository.countByAccountId(1);

      Transaction tosave = new Transaction();

      List<Account> accs = accountJpaRepository.findAll();

      Account acc = accs.getFirst(); // acountJpaRepository.findAll().getFirst();

      tosave.setAccount(acc);
      tosave.setDate(LocalDate.now());
      tosave.setType("TEST");
      tosave.setComment("This is a test");
      tosave.setDebit(BigDecimal.valueOf(2.34));
      tosave.setBalance(BigDecimal.valueOf(1000.00));

      Transaction saved = transactionJpaRepository.save(tosave);

      assertEquals((txncount+1), transactionJpaRepository.countByAccountId(1));


      long newid = saved.getSequence();
      System.out.println("new id is " + newid);
      log.info("testSave: transaction saved");
   }

   @Test
   void transactionFindByAccountId()
   {

      Account acc = this.accountJpaRepository.findAll().getLast();
      assertTrue(transactionJpaRepository.countByAccountId(acc.getId()) > 0, "Should be some transaction for account: " + acc.getId());

      List<Transaction> txns = transactionJpaRepository.findByAccountIdOrderByDateAscSequenceAsc(acc.getId());
      log.info("transactionFindByAccountId: account:{}", acc);
      log.info("transactionFindByAccountId: first txn:{}", txns.getFirst());
      log.info("transactionFindByAccountId: account in txn:{}", txns.getFirst().getAccount());

   }
}