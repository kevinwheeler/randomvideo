package site.randomvideo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import site.randomvideo.domain.VideoList;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class VideoListRepositoryWithBagRelationshipsImpl implements VideoListRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<VideoList> fetchBagRelationships(Optional<VideoList> videoList) {
        return videoList.map(this::fetchVideos);
    }

    @Override
    public Page<VideoList> fetchBagRelationships(Page<VideoList> videoLists) {
        return new PageImpl<>(fetchBagRelationships(videoLists.getContent()), videoLists.getPageable(), videoLists.getTotalElements());
    }

    @Override
    public List<VideoList> fetchBagRelationships(List<VideoList> videoLists) {
        return Optional.of(videoLists).map(this::fetchVideos).orElse(Collections.emptyList());
    }

    VideoList fetchVideos(VideoList result) {
        return entityManager
            .createQuery(
                "select videoList from VideoList videoList left join fetch videoList.videos where videoList.id = :id",
                VideoList.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<VideoList> fetchVideos(List<VideoList> videoLists) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, videoLists.size()).forEach(index -> order.put(videoLists.get(index).getId(), index));
        List<VideoList> result = entityManager
            .createQuery(
                "select videoList from VideoList videoList left join fetch videoList.videos where videoList in :videoLists",
                VideoList.class
            )
            .setParameter("videoLists", videoLists)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
