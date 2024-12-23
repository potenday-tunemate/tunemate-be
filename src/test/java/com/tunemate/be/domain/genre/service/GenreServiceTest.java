package com.tunemate.be.domain.genre.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.genre.domain.repository.GenreRepository;
import com.tunemate.be.global.exceptions.CustomException;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGenresBySortType_WhenSortTypeIsNew_ShouldReturnGenreList() {
        // Arrange
        Long genreId = 1L;
        String sortType = "new";
        
        Album mockAlbum = new Album();
        mockAlbum.setTitle("Test Album");
        mockAlbum.setCoverImg("test_cover.jpg");
        mockAlbum.setYear(2024);
        
        Object[] row = new Object[]{mockAlbum, "Test Artist"};
        List<Object[]> mockResults = Collections.singletonList(row);
        
        when(genreRepository.findAllGenresByNew(genreId)).thenReturn(mockResults);
        
        // Act
        List<Map<String, Object>> result = genreService.getGenresBySortType(genreId, sortType);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Album", ((CreateAlbumDTO) result.get(0).get("album")).getTitle());
        assertEquals("Test Artist", result.get(0).get("artistName"));
        
        // Verify
        verify(genreRepository, times(1)).findAllGenresByNew(genreId);
    }


}
