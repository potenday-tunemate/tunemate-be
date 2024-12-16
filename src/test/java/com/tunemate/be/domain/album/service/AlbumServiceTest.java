package com.tunemate.be.domain.album.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumMapper;
import com.tunemate.be.domain.album.domain.album.AlbumReviewTagDTO;
import com.tunemate.be.domain.album.domain.album.CreateAlbumDTO;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.domain.review.domain.ReviewMapper;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.global.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private ArtistService artistService;

    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void testGetAlbumById_WhenAlbumExists() {
        // Given
        Long albumId = 1L;
        Artist mockArtist = Artist.builder()
                .id(10L)
                .name("Test Artist")
                .img("artist_img.png")
                .bornYear(1990)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        Album mockAlbum = Album.builder()
                .id(albumId)
                .title("Test Album")
                .coverImg("cover_img.png")
                .artist(mockArtist)
                .year(2020)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        when(albumMapper.findById(albumId)).thenReturn(Optional.of(mockAlbum));

        // When
        Album result = albumService.getAlbumById(albumId);

        // Then
        assertNotNull(result, "Resulting album should not be null");
        assertEquals(albumId, result.getId(), "Album ID should match");
        assertEquals("Test Album", result.getTitle(), "Album title should match");
        assertEquals("cover_img.png", result.getCoverImg(), "Album cover image should match");
        assertNotNull(result.getArtist(), "Artist should not be null");
        assertEquals("Test Artist", result.getArtist().getName(), "Artist name should match");
        verify(albumMapper, times(1)).findById(albumId);
    }

    @Test
    void testGetAlbumById_WhenAlbumDoesNotExist() {
        // Given
        Long albumId = 2L;

        when(albumMapper.findById(albumId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            albumService.getAlbumById(albumId);
        }, "Expected getAlbumById to throw CustomException");

        assertEquals("앨범을 찾지 못했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "HTTP status should be NOT_FOUND");
        assertEquals(5001, exception.getErrorCode(), "Error code should match");
        verify(albumMapper, times(1)).findById(albumId);
    }

    @Test
    void testCreateAlbum_Success() {
        // Given
        Long artistId = 1L;
        String title = "Test Album";
        Integer year = 2023;
        String coverImg = "cover_img.png";

        // Create a mock Artist
        Artist mockArtist = Artist.builder()
                .id(artistId)
                .name("Test Artist")
                .img("artist_img.png")
                .bornYear(1990)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Create a CreateAlbumDTO
        CreateAlbumDTO dto = CreateAlbumDTO.builder()
                .artist(artistId)
                .title(title)
                .year(year)
                .coverImg(coverImg)
                .build();

        // Mocking ArtistService to return the mock Artist
        when(artistService.getArtistById(artistId)).thenReturn(mockArtist);

        // When
        albumService.createAlbum(dto);

        // Then
        // Capture the Album object passed to albumMapper.create
        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        verify(albumMapper, times(1)).create(albumCaptor.capture());

        Album capturedAlbum = albumCaptor.getValue();
        assertNotNull(capturedAlbum, "Captured Album should not be null");
        assertEquals(title, capturedAlbum.getTitle(), "Album title should match");
        assertEquals(year, capturedAlbum.getYear(), "Album year should match");
        assertEquals(coverImg, capturedAlbum.getCoverImg(), "Album cover image should match");
        assertEquals(mockArtist, capturedAlbum.getArtist(), "Album artist should match");
    }

    @Test
    void testCreateAlbum_AlbumCreationFails() {
        // Given
        Long artistId = 1L;
        String title = "Test Album";
        Integer year = 2023;
        String coverImg = "cover_img.png";

        // Create a mock Artist
        Artist mockArtist = Artist.builder()
                .id(artistId)
                .name("Test Artist")
                .img("artist_img.png")
                .bornYear(1990)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Create a CreateAlbumDTO
        CreateAlbumDTO dto = CreateAlbumDTO.builder()
                .artist(artistId)
                .title(title)
                .year(year)
                .coverImg(coverImg)
                .build();

        // Mocking ArtistService to return the mock Artist
        when(artistService.getArtistById(artistId)).thenReturn(mockArtist);

        // Mocking AlbumMapper.create to throw an exception
        doThrow(new RuntimeException("Database error")).when(albumMapper).create(any(Album.class));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            albumService.createAlbum(dto);
        }, "Expected createAlbum to throw CustomException");

        assertEquals("앨범 생성에 실패했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode(),
                "HTTP status should be INTERNAL_SERVER_ERROR");
        assertEquals(5002, exception.getErrorCode(), "Error code should match");
        verify(albumMapper, times(1)).create(any(Album.class));
    }

    @Test
    void testCreateAlbum_ArtistNotFound() {
        // Given
        Long artistId = 2L;
        String title = "Another Album";
        Integer year = 2024;
        String coverImg = "another_cover.png";

        // Create a CreateAlbumDTO
        CreateAlbumDTO dto = CreateAlbumDTO.builder()
                .artist(artistId)
                .title(title)
                .year(year)
                .coverImg(coverImg)
                .build();

        // Mocking ArtistService to throw CustomException
        when(artistService.getArtistById(artistId))
                .thenThrow(new CustomException("아티스트를 찾지 못했습니다.", HttpStatus.NOT_FOUND, 6001, ""));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            albumService.createAlbum(dto);
        }, "Expected createAlbum to throw CustomException when artist is not found");

        assertEquals("아티스트를 찾지 못했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "HTTP status should be NOT_FOUND");
        assertEquals(6001, exception.getErrorCode(), "Error code should match");

        verify(artistService, times(1)).getArtistById(artistId);
        verify(albumMapper, never()).create(any(Album.class));
    }

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void registAlbumReviewTag_ShouldRegisterTags_WhenSelectedTagsAreValid() {
        // Given
        int reviewId = 1;
        List<Integer> selectedTags = List.of(100, 200, 300);
        // When
        reviewService.registAlbumReviewTag(reviewId, selectedTags);

        // Then
        ArgumentCaptor<AlbumReviewTagDTO> captor = ArgumentCaptor.forClass(AlbumReviewTagDTO.class);
        verify(reviewMapper, times(3)).registReviewTag(captor.capture()); // 호출 횟수 검증 (태그 3개)

        List<AlbumReviewTagDTO> capturedDtos = captor.getAllValues();
        assertEquals(3, capturedDtos.size()); // DTO 개수 검증

        // 태그 데이터 검증
        assertEquals(100, capturedDtos.get(0).getTag_id());
        assertEquals(reviewId, capturedDtos.get(0).getReview_id());

        assertEquals(200, capturedDtos.get(1).getTag_id());
        assertEquals(reviewId, capturedDtos.get(1).getReview_id());

        assertEquals(300, capturedDtos.get(2).getTag_id());
        assertEquals(reviewId, capturedDtos.get(2).getReview_id());
    }
}
