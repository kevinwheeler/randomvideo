package site.randomvideo.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import site.randomvideo.domain.VideoList;

/**
 * Spring Data JPA repository for the VideoList entity.
 *
 * When extending this class, extend VideoListRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface VideoListRepository extends VideoListRepositoryWithBagRelationships, JpaRepository<VideoList, Long> {
    default Optional<VideoList> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<VideoList> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<VideoList> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    List<VideoList> findAllWithEagerRelationshipsByxUserId(Long id);
    List<VideoList> findAllByxUserId(Long id);


    Optional<VideoList> findOneWithEagerRelationshipsBySlug(String slug);
}
