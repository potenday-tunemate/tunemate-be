package com.tunemate.be.domain.album.domain.album.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateAlbumDTO {
    private String title;
    private String cover_img;
    private Long artist;
    private Integer year;
    private Long genre;
}
