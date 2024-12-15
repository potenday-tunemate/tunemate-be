package com.tunemate.be.domain.tag.domain;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
