package site.randomvideo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.randomvideo.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
