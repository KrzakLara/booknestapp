package com.example.booknestapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BooknestappApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verify that the context loads without issues
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void mainMethodTest() {
        // Verify that the application starts properly
        BooknestappApplication.main(new String[] {});
        assertThat(applicationContext).isNotNull();
    }
}
