package com.tunemate.be.domain.artist.domain.artist;

import java.util.Date;

import lombok.Data;

@Data
public class ArtistDto {
    private int id;
    private String name;
    private String img;
    private Date debut_date;
    private Date created_at;

}
