package com.desafio.remessa.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JdbiConfigTest {

    @Autowired
    private Jdbi jdbi;

    @Test
    void deveConectarAoBancoComJdbi() {
        Integer resultado = jdbi.withHandle(handle ->
                handle.createQuery("SELECT 1")
                        .mapTo(Integer.class)
                        .one()
        );

        assertEquals(1, resultado);
    }
}