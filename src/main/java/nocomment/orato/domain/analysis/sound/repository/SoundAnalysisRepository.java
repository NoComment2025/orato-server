package nocomment.orato.domain.analysis.sound.repository;

import nocomment.orato.domain.analysis.sound.entity.SoundAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoundAnalysisRepository extends JpaRepository<SoundAnalysis, Long> {
    
    // username으로 페이지네이션 조회
    Page<SoundAnalysis> findByUsername(String username, Pageable pageable);
    
    // 전체 페이지네이션 조회
    Page<SoundAnalysis> findAll(Pageable pageable);
}
