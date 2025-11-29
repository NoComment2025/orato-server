package nocomment.orato.domain.analysis.video.repository;

import nocomment.orato.domain.analysis.video.entity.VideoAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoAnalysisRepository extends JpaRepository<VideoAnalysis, Long> {
    
    // username으로 페이지네이션 조회
    Page<VideoAnalysis> findByUsername(String username, Pageable pageable);

    Optional<VideoAnalysis> findByIdAndUsername(Long id, String username);
    
    // 전체 페이지네이션 조회
    Page<VideoAnalysis> findAll(Pageable pageable);
}
