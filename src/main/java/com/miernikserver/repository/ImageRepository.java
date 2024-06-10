package com.miernikserver.repository;

import com.miernikserver.domain.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Long> {

    @Query("select i.id from ImageModel i")
    List<Long> findAllIds();

    @Modifying
    @Query(nativeQuery = true, value = "insert into images_votes values (?1, ?2)")
    void addVoteToImage(Long imageId, Integer vote);
}
