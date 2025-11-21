package nocomment.orato.domain.analysis.video.repository;

import nocomment.orato.domain.analysis.video.entity.VideoAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoAnalysisRepository extends JpaRepository<VideoAnalysis, Long> {
}
