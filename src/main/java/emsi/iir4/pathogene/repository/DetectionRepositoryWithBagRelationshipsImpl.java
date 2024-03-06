package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Detection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;

/**
 * Utility repository to load bag relationships based on <a href="https://vladmihalcea.com/hibernate-multiplebagfetchexception/">...</a>
 */
public class DetectionRepositoryWithBagRelationshipsImpl implements DetectionRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Detection> fetchBagRelationships(List<Detection> detections) {
        return Optional.of(detections).map(this::fetchMaladies).orElse(Collections.emptyList());
    }

    Detection fetchMaladies(Detection result) {
        return entityManager
            .createQuery(
                "select detection from Detection detection left join fetch detection.maladies where detection is :detection",
                Detection.class
            )
            .setParameter("detection", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Detection> fetchMaladies(List<Detection> detections) {
        return entityManager
            .createQuery(
                "select distinct detection from Detection detection left join fetch detection.maladies where detection in :detections",
                Detection.class
            )
            .setParameter("detections", detections)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
