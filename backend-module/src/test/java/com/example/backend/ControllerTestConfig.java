package com.example.backend;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Configuração mínima para testes do controller.
 * Exclui JPA, EJB e BackendApplication.
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@ComponentScan(
    basePackages = "com.example.backend",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.example.backend.config.EjbConfig.class,
                com.example.backend.BackendApplication.class
            }
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.example\\.backend\\.repository\\..*"
        )
    }
)
public class ControllerTestConfig {
}
