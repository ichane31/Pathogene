package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Stade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Stade entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StadeRepository extends JpaRepository<Stade, Long> {
    Page<Stade> findAllByMaladieId(Long id, Pageable pageable);
}
