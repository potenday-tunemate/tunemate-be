package com.tunemate.be.domain.artist.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ArtistDto {
    private int id;
    private String name;
    private String img;
    private Date debut_date;
    private Date created_at;

}
