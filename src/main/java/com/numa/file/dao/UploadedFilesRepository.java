package com.numa.file.dao;

import com.numa.file.entity.UploadedFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFilesRepository extends JpaRepository<UploadedFiles, Long> {

    @Query("SELECT u FROM UploadedFiles u " +
            "WHERE (:type IS NULL OR :type = '' OR LOWER(TRIM(u.type)) = LOWER(TRIM(:type))) " +
            "AND (:name IS NULL OR :name = '' OR LOWER(TRIM(u.name)) = LOWER(TRIM(:name)))")
    List<UploadedFiles> findByTypeAndName(@Param("type") String type, @Param("name") String name);

}
