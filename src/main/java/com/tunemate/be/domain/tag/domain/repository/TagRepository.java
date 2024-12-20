package com.tunemate.be.domain.tag.domain.repository;

import com.tunemate.be.domain.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT tag FROM Tag tag")
    List<Tag> findAllTags();
}
