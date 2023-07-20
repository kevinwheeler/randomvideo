package site.randomvideo.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import site.randomvideo.domain.Video;

import java.util.List;

/**
 * Spring Data JPA repository for the Video entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByxUserId(Long userId);
}
