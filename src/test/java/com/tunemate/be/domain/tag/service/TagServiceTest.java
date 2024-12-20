package com.tunemate.be.domain.tag.service;


import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.tag.domain.repository.TagRepository;
import com.tunemate.be.domain.tag.dto.CreateTagDTO;
import com.tunemate.be.global.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void testGetAllTags() {
        // Arrange
        List<Tag> mockTags = Arrays.asList(
                Tag.builder().id(1L).name("Tag1").build(),
                Tag.builder().id(2L).name("Tag2").build()
        );
        when(tagRepository.findAllTags()).thenReturn(mockTags);

        // Act
        List<Tag> result = tagService.getAllTags();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The size of the tag list should be 2");
        assertEquals("Tag1", result.get(0).getName(), "First tag name should be 'Tag1'");
        assertEquals("Tag2", result.get(1).getName(), "Second tag name should be 'Tag2'");
        verify(tagRepository, times(1)).findAllTags();
    }

    @Test
    void testCreateTagSuccess() {
        CreateTagDTO dto = new CreateTagDTO();
        dto.setName("NewTag");

        tagService.create(dto);

        ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository, times(1)).save(tagCaptor.capture());
        Tag capturedTag = tagCaptor.getValue();
        assertNotNull(capturedTag, "Captured tag should not be null");
        assertEquals("NewTag", capturedTag.getName(), "Tag name should be 'NewTag'");
    }

    @Test
    void testCreateTagFailure() {
        // Arrange
        CreateTagDTO dto = new CreateTagDTO();
        dto.setName("FailTag");
        doThrow(new RuntimeException("Database error")).when(tagRepository).save(any(Tag.class));

        CustomException exception = assertThrows(CustomException.class, () -> {
            tagService.create(dto);
        }, "Expected create() to throw CustomException");

        assertEquals("태그 생성에 실패했습니다.", exception.getMessage(), "Exception message should match");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode(), "HTTP status should be 500");
        assertEquals(7001, exception.getErrorCode(), "Exception code should be 7001");
        assertEquals("", exception.getErrorMessage(), "Exception details should be empty");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }
}
