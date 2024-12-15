package com.tunemate.be.domain.review.domain;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.user.domain.user.User;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private User user;
    private Album album;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
