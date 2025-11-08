package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.jionjion.work.entity.DailyReport;

import java.time.LocalDate;
import java.util.List;

/**
 * 日报信息仓库
 *
 * @author Jion
 */
@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findByTitle(String title);

    /**
     * 根据日报日期查找
     */
    DailyReport findByReportDate(LocalDate reportDate);

    /**
     * 根据日期范围查询日报列表
     */
    List<DailyReport> findByReportDateBetweenOrderByReportDateAsc(LocalDate startDate, LocalDate endDate);

    /**
     * 根据日报日期删除
     */
    @Modifying
    @Query("DELETE FROM DailyReport d WHERE d.reportDate = ?1")
    void deleteByReportDate(LocalDate reportDate);
}
