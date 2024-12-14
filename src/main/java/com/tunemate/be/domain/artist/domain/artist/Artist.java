package com.tunemate.be.domain.artist.domain.artist;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    private Long id;
    private String name;
    private String img;
    private Integer bornYear;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
