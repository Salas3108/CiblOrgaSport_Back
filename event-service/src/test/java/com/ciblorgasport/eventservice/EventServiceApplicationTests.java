package com.ciblorgasport.eventservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiert une base de données PostgreSQL démarrée") 
class EventServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
