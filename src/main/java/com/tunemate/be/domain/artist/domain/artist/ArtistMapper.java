package com.tunemate.be.domain.artist.domain.artist;

import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface ArtistMapper {

    @Select("""
            SELECT *
                    FROM artist
                    WHERE id = #{id}
            """)
    Optional<Artist> findById(@Param("id") Long id);

    @Insert("""
            INSERT INTO artist (name, img, born_year)
                    VALUES (#{artist.name}, #{artist.img}, #{artist.bornYear})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "artist.id")
    void create(@Param("artist") Artist artist);
}
