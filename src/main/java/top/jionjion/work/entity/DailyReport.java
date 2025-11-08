package top.jionjion.work.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


/**
 * 日报信息
 *
 * @author Jion
 */
@Data
@Entity
@Table(name = "daily_report")
public class DailyReport {

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
     * 日报日期 格式: yyyy-MM-dd
     */
    private LocalDate reportDate;
}
