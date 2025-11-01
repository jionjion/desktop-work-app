package top.jionjion.work.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 百炼平台调用日志
 *
 * @author Jion
 */
@Data
@Entity
@Table(name = "bai_lian_log")
public class BaiLianLog {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 用户输入提示词
     */
    @Lob
    private String prompt;

    /**
     * 调用结果
     */
    @Lob
    private String result;

    /**
     * 是否调用成功
     */
    private Boolean success;

    /**
     * 调用时间
     */
    private LocalDateTime invokeTime;

    /**
     * 错误信息（如果有的话）
     */
    private String errorMessage;
}
