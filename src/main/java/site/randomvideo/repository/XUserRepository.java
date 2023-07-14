package site.randomvideo.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import site.randomvideo.domain.XUser;

import java.util.Optional;

/**
 * Spring Data JPA repository for the XUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface XUserRepository extends JpaRepository<XUser, Long> {
    Optional<XUser> findOneByInternalUserId(Long id);
}
