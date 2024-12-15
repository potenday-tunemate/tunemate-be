package com.tunemate.be.domain.tag.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> findAllTags();

    void create(Tag tag);
}
