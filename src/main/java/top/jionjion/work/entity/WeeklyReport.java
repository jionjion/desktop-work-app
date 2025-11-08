package top.jionjion.work.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


/**
 * 周报信息
 *
 * @author Jion
 */
@Data
@Entity
@Table(name = "weekly_report")
public class WeeklyReport {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    @Lob
    private String content;

    /**
     * 周报开始日期 格式: yyyy-MM-dd
     */
    private LocalDate startDate;

    /**
     * 周报结束日期 格式: yyyy-MM-dd
     */
    private LocalDate endDate;

    /**
     * 年份
     */
    private Integer reportYear;

    /**
     * 第几周
     */
    private Integer weekOfYear;
}