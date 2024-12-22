package com.tunemate.be.domain.user.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tunemate.be.domain.user.domain.user.Follow;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.dto.response.ResponseUserDTO;

public interface FollowRepository extends JpaRepository<Follow,Long> {

    @Query("SELECT new com.tunemate.be.domain.user.dto.response.ResponseUserDTO(u.nickname) FROM Follow f JOIN f.follower u where f.following.id = :followingId")
    List<ResponseUserDTO> findFollowingList(@Param("followingId")Long followingId);

    @Query("SELECT new com.tunemate.be.domain.user.dto.response.ResponseUserDTO(u.nickname) FROM Follow f JOIN f.following u where f.follower.id = :followerId")
    List<ResponseUserDTO> findFollowerList(@Param("followerId")Long followerId);


}
