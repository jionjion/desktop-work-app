package top.jionjion.work.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办记录实体类
 * 
 * @author Jion
 */
@Data
@Entity
@Table(name = "todo_record")
public class TodoRecord {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 待办内容
     */
    @Column(nullable = false, length = 500)
    private String content;

    /**
     * 完成状态
     */
    @Column(nullable = false)
    private Boolean completed = false;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    /**
     * 构造方法
     */
    public TodoRecord() {
        this.createTime = LocalDateTime.now();
        this.completed = false;
    }

    /**
     * 带内容的构造方法
     */
    public TodoRecord(String content) {
        this();
        this.content = content;
    }
}
