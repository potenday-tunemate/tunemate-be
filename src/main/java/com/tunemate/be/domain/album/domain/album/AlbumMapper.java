package com.tunemate.be.domain.album.domain.album;

import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface AlbumMapper {

    @Select("""
            SELECT
            a.id AS album_id,
                       a.title AS album_title,
                       a.cover_img AS album_cover_img,
                       a.year AS album_year,
                       a.created_at AS album_created_at,
                       a.updated_at AS album_updated_at,
                       ar.id AS artist_id,
                       ar.name AS artist_name,
                       ar.img AS artist_img,
                       ar.born_year AS artist_born_year,
                       ar.created_at AS artist_created_at,
                       ar.updated_at AS artist_updated_at
            FROM album AS a
            JOIN artist as ar ON a.artist = ar.id
            WHERE a.id = #{id} 
            """)
    Optional<Album> findById(@Param("id") Long id);

    @Insert("""
            INSERT INTO album (title, cover_img, artist, year)
                    VALUES (#{album.title}, #{album.coverImg}, #{album.artist.id}, #{album.year})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "album.id")
    void create(@Param("album") Album album);

}
