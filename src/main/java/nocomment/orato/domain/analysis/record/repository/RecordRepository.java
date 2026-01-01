package nocomment.orato.domain.analysis.record.repository;

import nocomment.orato.domain.analysis.record.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    // username으로 페이지네이션 조회
    Page<Record> findByUsername(String username, Pageable pageable);
    
    // 전체 페이지네이션 조회
    Page<Record> findAll(Pageable pageable);
}
