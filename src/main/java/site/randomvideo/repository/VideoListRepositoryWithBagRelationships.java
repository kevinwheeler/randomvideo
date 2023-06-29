package site.randomvideo.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import site.randomvideo.domain.VideoList;

public interface VideoListRepositoryWithBagRelationships {
    Optional<VideoList> fetchBagRelationships(Optional<VideoList> videoList);

    List<VideoList> fetchBagRelationships(List<VideoList> videoLists);

    Page<VideoList> fetchBagRelationships(Page<VideoList> videoLists);
}
