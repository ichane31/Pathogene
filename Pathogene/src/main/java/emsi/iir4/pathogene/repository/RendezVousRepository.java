package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.RendezVous;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RendezVous entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    Set<RendezVous> findByPatientUserId(Long id);

    Set<RendezVous> findByMedecinUserId(Long id);

    List<RendezVous> findAllByPatientUserId(Long id);
    List<RendezVous> findAllByMedecinUserId(Long id);

    List<RendezVous> findAllByMedecinId(Long id);
}
