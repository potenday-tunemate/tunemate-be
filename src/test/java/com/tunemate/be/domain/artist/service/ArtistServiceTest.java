package com.tunemate.be.domain.artist.service;

import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.domain.artist.dto.CreateArtistDTO;
import com.tunemate.be.domain.artist.domain.artist.repository.ArtistRepository;
import com.tunemate.be.global.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void testGetArtistById_WhenArtistExists() {
        // Given
        Long artistId = 1L;
        Artist mockArtist = new Artist();
        mockArtist.setId(artistId);
        mockArtist.setName("Test Artist");
        mockArtist.setImg("artist_img.png");
        mockArtist.setBornYear(1990);
        mockArtist.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mockArtist.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(mockArtist));

        // When
        Artist result = artistService.getArtistById(artistId);

        // Then
        assertNotNull(result, "Resulting artist should not be null");
        assertEquals(artistId, result.getId(), "Artist ID should match");
        assertEquals("Test Artist", result.getName(), "Artist name should match");
        assertEquals("artist_img.png", result.getImg(), "Artist image should match");
        assertEquals(1990, result.getBornYear(), "Artist born year should match");
        verify(artistRepository, times(1)).findById(artistId);
    }

    @Test
    void testGetArtistById_WhenArtistDoesNotExist() {
        // Given
        Long artistId = 2L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.getArtistById(artistId);
        }, "Expected getArtistById to throw CustomException");

        assertEquals("아티스트를 찾지 못했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "HTTP status should be NOT_FOUND");
        assertEquals(6001, exception.getErrorCode(), "Error code should match");
        verify(artistRepository, times(1)).findById(artistId);
    }

    @Test
    void testCreateArtist_Success() {
        // Given
        CreateArtistDTO dto = new CreateArtistDTO();
        dto.setName("Test Artist");
        dto.setImg("artist_img.png");
        dto.setBorn_year(1990);

        // Then
        assertDoesNotThrow(() -> artistService.createArtist(dto), "createArtist should not throw an exception");

        // Verify that artistMapper.create was called once with the given artist
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    void testCreateArtist_Failure() {
        // Given
        CreateArtistDTO dto = new CreateArtistDTO();
        dto.setName("Test Artist");
        dto.setImg("artist_img.png");
        dto.setBorn_year(1990);

        // Mocking artistMapper.create to throw an exception
        doThrow(new RuntimeException("Database error")).when(artistRepository).save(any(Artist.class));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.createArtist(dto);
        }, "Expected createArtist to throw CustomException");

        assertEquals("아티스트 생성에 실패했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode(),
                "HTTP status should be INTERNAL_SERVER_ERROR");
        assertEquals(6002, exception.getErrorCode(), "Error code should match");

        // Verify that artistMapper.create was called once with the given artist
        verify(artistRepository, times(1)).save(any(Artist.class));
    }
}
