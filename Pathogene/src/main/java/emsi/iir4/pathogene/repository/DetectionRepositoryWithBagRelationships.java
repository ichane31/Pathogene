package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Detection;
import java.util.List;

public interface DetectionRepositoryWithBagRelationships {
    List<Detection> fetchBagRelationships(List<Detection> detections);
}
