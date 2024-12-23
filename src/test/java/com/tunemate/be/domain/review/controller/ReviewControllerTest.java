package com.tunemate.be.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunemate.be.domain.review.dto.request.UpdateReviewDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.domain.tag.service.TagService;
import com.tunemate.be.global.jwt.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService; // 🛠️ MockBean 추가

    @MockBean
    private ReviewService reviewService; // 🛠️ MockBean 추가

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

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
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 1. 리뷰 리스트 조회 테스트
     */
    @Test
    void getAlbumList_ShouldReturnRecentReviews() throws Exception {
        // Arrange
        List<ReviewResponseRepositoryDTO> mockReviews = List.of(
                new ReviewResponseRepositoryDTO(1L, "User1", "Artist1", "Album1", "cover1.jpg", "Great Album"),
                new ReviewResponseRepositoryDTO(2L, "User2", "Artist2", "Album2", "cover2.jpg", "Awesome Song"));

        when(reviewService.findReviewByRecent(10, 0)).thenReturn(mockReviews);

        // Act & Assert
        mockMvc.perform(get("/review")
                .param("limit", "10")
                .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nickname").value("User1"))
                .andExpect(jsonPath("$.data[1].nickname").value("User2"));

        verify(reviewService, times(1)).findReviewByRecent(10, 0);
    }

    /**
     * 2. 리뷰 삭제 테스트
     */
    @Test
    void deleteReview_ShouldDeleteReviewSuccessfully() throws Exception {
        // Arrange
        Long reviewId = 1L;
        String userId = "123"; // 가짜 사용자 ID

        doNothing().when(reviewService).deleteReview(Long.parseLong(userId), reviewId);

        // Act & Assert
        mockMvc.perform(delete("/review/{id}", reviewId)
                .header("Authorization", "Bearer mock-jwt-token") // Mock JWT 토큰 추가
                .requestAttr("userID", userId) // @UserID가 사용된다면 이렇게 전달
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 3. 리뷰 수정 테스트
     */
    // @Test
    // void updateReview_ShouldUpdateReviewSuccessfully() throws Exception {
    //     // Arrange
    //     Long userId = 1L;
    //     Long reviewId = 1L;
    //     UpdateReviewDTO dto = new UpdateReviewDTO();
    //     dto.setContent("Updated Review Content");
    //     dto.setTags(Arrays.asList(1L, 2L)); // 실제 태그 ID 값 추가

    //     doNothing().when(reviewService).updateReview(userId, reviewId, dto);

    //     // Act & Assert
    //     mockMvc.perform(put("/review/{id}", reviewId)
    //             .header("Authorization", "Bearer mock-jwt-token") // 인증 헤더 추가
    //             .requestAttr("userID", String.valueOf(userId)) // @UserID 매핑
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(new ObjectMapper().writeValueAsString(dto)));

    //     // Verify
    //     verify(reviewService, times(1)).updateReview(userId, reviewId, dto);
    // }
}
