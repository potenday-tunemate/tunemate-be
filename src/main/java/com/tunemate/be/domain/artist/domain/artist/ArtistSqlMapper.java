package com.tunemate.be.domain.artist.domain.artist;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ArtistSqlMapper {
    public void registArtistInfoProcess(ArtistDto dto);

}
