package nocomment.orato.domain.analysis.sound.repository;

import nocomment.orato.domain.analysis.sound.entity.SoundAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoundAnalysisRepository extends JpaRepository<SoundAnalysis, Long> {
}
