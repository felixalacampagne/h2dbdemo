package com.felixalacampagne.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    /* the run method is called after application context is loaded but before the execution of the main method is complete. */
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner.");

        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
        }
        do {
           try {
              Thread.sleep(10000);
           } catch (InterruptedException e) {
              throw new RuntimeException(e);
           }
           LOG.info("I am still running");
        } while(true);
    }
}
