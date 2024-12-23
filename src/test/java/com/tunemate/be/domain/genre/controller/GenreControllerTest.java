package com.tunemate.be.domain.genre.controller;

import com.tunemate.be.domain.genre.service.GenreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    private List<Map<String, Object>> mockGenreList;

    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mariaDBContainer::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        mockGenreList = new ArrayList<>();
        Map<String, Object> genre1 = new HashMap<>();
        genre1.put("id", 1L);
        genre1.put("name", "Rock");
        genre1.put("new", 85);

        Map<String, Object> genre2 = new HashMap<>();
        genre2.put("id", 2L);
        genre2.put("name", "Pop");
        genre2.put("new", 90);

        mockGenreList.add(genre1);
        mockGenreList.add(genre2);
    }

    @Test
    void getGenres_Success() throws Exception {
        Long mockId = 1L;
        String sortType = "new";

        // Mocking Service
        when(genreService.getGenresBySortType(mockId, sortType)).thenReturn(mockGenreList);

        // Perform Request
        mockMvc.perform(get("/genre/{id}/{sortType}", mockId, sortType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Rock")))
                .andExpect(jsonPath("$[0].new", is(85)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Pop")))
                .andExpect(jsonPath("$[1].new", is(90)));

        // Verify Service Method Call
        verify(genreService, times(1)).getGenresBySortType(mockId, sortType);
    }
}