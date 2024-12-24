package com.tunemate.be.domain.genre.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumGenre;
import com.tunemate.be.domain.album.domain.album.dto.AlbumGenreDto;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.domain.repository.GenreRepository;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.global.exceptions.CustomException;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllTags() {
        return genreRepository.findeAllGenres();
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new CustomException("장르를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 10001, ""));
    }

    public List<Map<String, Object>> getGenresBySortType(Long genreId, String sortType) {

        if ("new".equalsIgnoreCase(sortType)) {
            List<Object[]> resultNew = genreRepository.findAllGenresByNew(genreId);

            return resultNew.stream().map(row -> {
                Album album = (Album) row[0];
                String artistName = (String) row[1];

                // CreateAlbumDTO 생성 및 필드 매핑
                CreateAlbumDTO dto = new CreateAlbumDTO();
                dto.setTitle(album.getTitle());
                dto.setCover_img(album.getCoverImg());
                dto.setArtist(album.getArtist() != null ? album.getArtist().getId() : null);
                dto.setYear(album.getYear());

                Map<String, Object> map = new HashMap<>();
                map.put("album", dto);
                map.put("artistName", artistName);

                return map;
            }).collect(Collectors.toList());
        }
        // 추후에 할꺼임
        else if ("popular".equalsIgnoreCase(sortType)) {
            List<Object[]> resultPopular = genreRepository.findAllGenresByPopular(genreId);
            return resultPopular.stream().map(row -> {
                Album album = (Album) row[0];
                String artistName = (String) row[1];
                Long reviewCount = (Long) row[2];
                Long genreID = (Long) row[3];
                String albumGenre = (String) row[4];

               
                // CreateAlbumDTO 생성 및 필드 매핑
                CreateAlbumDTO dto = new CreateAlbumDTO();
                dto.setTitle(album.getTitle());
                dto.setCover_img(album.getCoverImg());
                dto.setArtist(album.getArtist() != null ? album.getArtist().getId() : null);
                dto.setYear(album.getYear());
                dto.setGenre(genreID);

                Map<String, Object> map = new HashMap<>();
                map.put("album", dto);
                map.put("artistName", artistName);
                map.put("reviewCount", reviewCount);
                map.put("albumGenre", albumGenre);

                return map;
            }).collect(Collectors.toList());
        } else {
            throw new CustomException("앨범 정렬에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 7001, "");
        }

    }
}
