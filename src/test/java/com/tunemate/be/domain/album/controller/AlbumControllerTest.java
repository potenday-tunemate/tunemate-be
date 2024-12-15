package com.tunemate.be.domain.album.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumMapper;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.domain.artist.ArtistMapper;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.domain.ReviewMapper;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.domain.user.UserMapper;
import com.tunemate.be.utils.StreamGobbler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {
    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("jwt.secret", () -> "thisIsASecretKeyThatShouldBeAtLeast32BytesLongOkWhynotWork");
        registry.add("jwt.access_token.time", () -> 24);
        registry.add("jwt.refresh_token.time", () -> 48);
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        Map<String, String> env = builder.environment();
        String mariadbUrl = String.format("%s:%s@tcp(%s:%d)/%s", mariaDBContainer.getUsername(), mariaDBContainer.getPassword(), mariaDBContainer.getHost(), mariaDBContainer.getMappedPort(3306), mariaDBContainer.getDatabaseName());
        System.out.println(mariadbUrl);
        env.put("GOOSE_MIGRATION_DIR", "./migrations");

        builder.command("goose", "mysql", mariadbUrl, "up");

        Process process = builder.start();


        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

        outputGobbler.start();
        errorGobbler.start();

        int exitCode = process.waitFor();

        outputGobbler.join();
        errorGobbler.join();

        if (exitCode != 0) {
            throw new RuntimeException("Migration failed with exit code: " + exitCode);
        }
    }

    @Test
    void testGetAlbumById() throws Exception {
        Artist artist = Artist.builder().
                name("Test Artist").
                img("artist_img.png").bornYear(1990).build();
        artistMapper.create(artist);
        Album album = Album.builder().title("Test Album").coverImg("cover_img.png").artist(artist).year(1990).build();
        albumMapper.create(album);
        Long albumId = album.getId(); // 생성된 앨범의 실제 ID를 가져옴

        // When & Then
        mockMvc.perform(get("/album/{id}", albumId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(albumId))
                .andExpect(jsonPath("$.data.title").value(album.getTitle()))
                .andExpect(jsonPath("$.data.coverImg").value(album.getCoverImg()))
                .andExpect(jsonPath("$.data.year").value(album.getYear()))
                .andExpect(jsonPath("$.data.artist.id").value(artist.getId()))
                .andExpect(jsonPath("$.data.artist.name").value(artist.getName()))
                .andExpect(jsonPath("$.data.artist.img").value(artist.getImg()))
                .andExpect(jsonPath("$.data.artist.bornYear").value(artist.getBornYear()));
        ;

    }

    @Test
    void testGetAlbumById_NotFound() throws Exception {
        // Given
        Long nonExistentAlbumId = 999L;

        // When & Then
        mockMvc.perform(get("/album/{id}", nonExistentAlbumId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // 에러 응답의 특정 필드 검증
                .andExpect(jsonPath("$.message").value("앨범을 찾지 못했습니다."))
                .andExpect(jsonPath("$.errorCode").value(5001));
    }

    @Test
    void testGetAlbumReviewList() throws Exception {
        User createdUser = User.builder().email("test@naver.com").password("test").nickname("test").build();
        userMapper.create(createdUser);
        Artist artist = Artist.builder().
                name("Test Artist").
                img("artist_img.png").bornYear(1990).build();
        artistMapper.create(artist);
        Album album = Album.builder().title("Test Album").coverImg("cover_img.png").artist(artist).year(1990).build();
        albumMapper.create(album);
        Long albumId = album.getId(); // 생성된 앨범의 실제 ID를 가져옴

        User user = userMapper.findByEmail(createdUser.getEmail()).orElse(null);
        Review review = Review.builder().user(user).album(album).content("test").build();
        reviewMapper.create(review);
        // When & Then
        mockMvc.perform(get("/album/{id}/review", albumId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        ;

    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

