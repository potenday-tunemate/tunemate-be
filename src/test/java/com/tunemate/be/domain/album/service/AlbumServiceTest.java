package com.tunemate.be.domain.album.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.global.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
public class AlbumServiceTest {
    @Mock
    private ArtistService artistService;

    @Mock
    private AlbumRepository albumRepository;

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

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(mockAlbum));

        // When
        Album result = albumService.getAlbumById(albumId);

        // Then
        assertNotNull(result, "Resulting album should not be null");
        assertEquals(albumId, result.getId(), "Album ID should match");
        assertEquals("Test Album", result.getTitle(), "Album title should match");
        assertEquals("cover_img.png", result.getCoverImg(), "Album cover image should match");
        assertNotNull(result.getArtist(), "Artist should not be null");
        assertEquals("Test Artist", result.getArtist().getName(), "Artist name should match");
        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    void testGetAlbumById_WhenAlbumDoesNotExist() {
        // Given
        Long albumId = 2L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            albumService.getAlbumById(albumId);
        }, "Expected getAlbumById to throw CustomException");

        assertEquals("앨범을 찾지 못했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "HTTP status should be NOT_FOUND");
        assertEquals(5001, exception.getErrorCode(), "Error code should match");
        verify(albumRepository, times(1)).findById(albumId);
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
        verify(albumRepository, times(1)).save(albumCaptor.capture());

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
        doThrow(new RuntimeException("Database error")).when(albumRepository).save(any(Album.class));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            albumService.createAlbum(dto);
        }, "Expected createAlbum to throw CustomException");

        assertEquals("앨범 생성에 실패했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode(),
                "HTTP status should be INTERNAL_SERVER_ERROR");
        assertEquals(5002, exception.getErrorCode(), "Error code should match");
        verify(albumRepository, times(1)).save(any(Album.class));
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
        verify(albumRepository, never()).save(any(Album.class));
    }

}
