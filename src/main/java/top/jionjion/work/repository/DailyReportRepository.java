package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.jionjion.work.entity.DailyReport;

import java.util.List;

/**
 * 日报信息仓库
 *
 * @author Jion
 */
@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findByTitle(String title);
}
