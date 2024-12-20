package com.tunemate.be.domain.album.domain.album.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlbumDTO {
    private String title;
    private String coverImg;
    private Long artist;
    private List<String> genres;
    private Integer year;
}
