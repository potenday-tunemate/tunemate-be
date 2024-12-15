package com.tunemate.be.domain.tag.service;

import com.tunemate.be.domain.tag.domain.CreateTagDTO;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.tag.domain.TagMapper;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagMapper tagMapper;

    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public List<Tag> getAllTags() {
        return tagMapper.findAllTags();
    }

    public void create(CreateTagDTO dto) {
        Tag tag = Tag.builder().name(dto.getName()).build();
        try {
            tagMapper.create(tag);
        } catch (Exception e) {
            throw new CustomException("태그 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 7001, "");
        }
    }
}
