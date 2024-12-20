package com.tunemate.be.domain.tag.service;

import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.tag.domain.repository.TagRepository;
import com.tunemate.be.domain.tag.dto.CreateTagDTO;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAllTags();
    }

    public Tag findById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new CustomException("태그를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 10001, ""));
    }

    public void create(CreateTagDTO dto) {
        Tag tag = Tag.builder().name(dto.getName()).build();
        try {
            tagRepository.save(tag);
        } catch (Exception e) {
            throw new CustomException("태그 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 7001, "");
        }
    }
}
