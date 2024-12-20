package com.tunemate.be.domain.album.domain.album.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlbumDTO {
    private String title;
    private String coverImg;
    private Long artist;
    private Integer year;
}
