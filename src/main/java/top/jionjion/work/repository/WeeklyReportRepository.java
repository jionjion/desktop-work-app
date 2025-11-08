package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.jionjion.work.entity.WeeklyReport;

import java.time.LocalDate;
import java.util.List;

/**
 * 周报信息仓库
 *
 * @author Jion
 */
@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    List<WeeklyReport> findByTitle(String title);

    /**
     * 根据年份和周数查找
     */
    WeeklyReport findByReportYearAndWeekOfYear(Integer year, Integer weekOfYear);

    /**
     * 根据开始日期查找
     */
    WeeklyReport findByStartDate(LocalDate startDate);

    /**
     * 根据年份和周数删除
     */
    @Modifying
    @Query("DELETE FROM WeeklyReport w WHERE w.reportYear = ?1 AND w.weekOfYear = ?2")
    void deleteByReportYearAndWeekOfYear(Integer reportYear, Integer weekOfYear);
}
