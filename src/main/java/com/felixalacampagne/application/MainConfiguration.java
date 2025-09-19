package com.felixalacampagne.application;

import com.felixalacampagne.account.persistence.repository.RepositoryConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// Entities are no longer under the Main package so must be referenced explicitly
// MainConfiguration IS under Main package so is auto-detected
@Configuration
@Import({RepositoryConfig.class})
public class MainConfiguration {
}
