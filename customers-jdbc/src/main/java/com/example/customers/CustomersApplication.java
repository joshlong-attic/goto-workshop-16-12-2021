package com.example.customers;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

    @Bean
    ApplicationRunner runnable(CustomerService customerService) {
        return (args) -> customerService.getCustomers().forEach(System.out::println);
    }

}

record Customer(Integer id, String name) {
}

@Service
class CustomerService {

    private final JdbcTemplate jdbcTemplate;

    CustomerService(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }

    @Transactional
    public Collection<Customer> getCustomers() {
        return jdbcTemplate.query("select * from customers",
                (rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name")));
    }
}