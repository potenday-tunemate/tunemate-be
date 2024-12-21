package com.tunemate.be.domain.artist.domain.artist.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateArtistDTO {
    private String name;
    private String img;
    private Integer born_year;
}