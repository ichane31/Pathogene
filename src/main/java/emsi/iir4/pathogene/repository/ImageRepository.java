package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Image entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Page<Image> findAllByStadeId(Long id, Pageable pageable);
}
