package com.tunemate.be.domain.album.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.AlbumReviewRequest;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.domain.artist.repository.ArtistRepository;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.domain.repository.ReviewRepository;
import com.tunemate.be.domain.review.dto.request.CreateReviewDTO;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.domain.tag.dto.CreateTagDTO;
import com.tunemate.be.domain.tag.service.TagService;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;


    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private TagService tagService;

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

    @Test
    void testGetAlbumById() throws Exception {
        Artist artist = Artist.builder().
                name("Test Artist").
                img("artist_img.png").bornYear(1990).build();
        artistRepository.save(artist);
        Album album = Album.builder().title("Test Album").coverImg("cover_img.png").artist(artist).year(1990).build();
        albumRepository.save(album);
        Long albumId = album.getId(); // 생성된 앨범의 실제 ID를 가져옴

        // When & Then
        mockMvc.perform(get("/album/{id}", albumId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(albumId))
                .andExpect(jsonPath("$.data.title").value(album.getTitle()))
                .andExpect(jsonPath("$.data.cover_img").value(album.getCoverImg()))
                .andExpect(jsonPath("$.data.year").value(album.getYear()))
                .andExpect(jsonPath("$.data.artist.id").value(artist.getId()))
                .andExpect(jsonPath("$.data.artist.name").value(artist.getName()))
                .andExpect(jsonPath("$.data.artist.img").value(artist.getImg()))
                .andExpect(jsonPath("$.data.artist.born_year").value(artist.getBornYear()));
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
                .andExpect(jsonPath("$.error_code").value(5001));
    }


    @Test
    void testGetAlbumReviewList() throws Exception {
        User createdUser = User.builder().email("test@naver.com").password("test").nickname("test").build();
        userRepository.save(createdUser);
        Artist artist = Artist.builder().
                name("Test Artist").
                img("artist_img.png").bornYear(1990).build();
        artistRepository.save(artist);
        Album album = Album.builder().title("Test Album").coverImg("cover_img.png").artist(artist).year(1990).build();
        albumRepository.save(album);
        Long albumId = album.getId(); // 생성된 앨범의 실제 ID를 가져옴

        User user = userRepository.findByEmail(createdUser.getEmail()).orElse(null);
        Review review = Review.builder().user(user).album(album).content("test").build();
        reviewRepository.save(review);

        System.out.println(albumId);
        // When & Then
        mockMvc.perform(get("/album/{id}/review", albumId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        ;

    }


    @Test
    void registAlbumReview_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        User createdUser = User.builder().email("test@naver.com").password("test").nickname("test").build();
        userRepository.save(createdUser);
        Artist artist = Artist.builder().
                name("Test Artist").
                img("artist_img.png").bornYear(1990).build();
        artistRepository.save(artist);
        Album album = Album.builder().title("Test Album").coverImg("cover_img.png").artist(artist).year(1990).build();
        albumRepository.save(album);

        CreateReviewDTO dto = CreateReviewDTO.builder()
                .content("This is a great album!")
                .build();

        CreateTagDTO tag1 = CreateTagDTO.builder().name("test1").build();
        CreateTagDTO tag2 = CreateTagDTO.builder().name("test2").build();
        CreateTagDTO tag3 = CreateTagDTO.builder().name("test3").build();
        tagService.create(tag1);
        tagService.create(tag2);
        tagService.create(tag3);

        List<Integer> selectedTags = Arrays.asList(1, 2, 3);

        AlbumReviewRequest albumReviewRequest = new AlbumReviewRequest();
        albumReviewRequest.setContent(dto.getContent());
        albumReviewRequest.setSelectedTags(selectedTags);

        mockMvc.perform(post("/album/{id}/review", album.getId()) // album ID 설정
                        .header("Authorization", "Bearer some-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumReviewRequest)) // JSON 직렬화
                        .requestAttr("authenticatedUserID", createdUser.getId().toString())) // 인증된 사용자 ID 설정
                .andExpect(status().isOk()); // 상태 코드 검증
    }


    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

