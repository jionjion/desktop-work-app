package top.jionjion.work.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件夹快捷访问实体
 * 表示一个文件夹快捷访问记录，包含文件夹元信息和访问统计数据
 *
 * @author Jion
 */
@Entity
@Table(name = "folder_shortcut")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderShortcut {

    /**
     * 主键ID，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件夹显示名称
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * 文件夹完整路径，唯一约束
     */
    @Column(nullable = false, length = 500, unique = true)
    private String path;

    /**
     * 访问次数统计，默认为0
     */
    @Column(nullable = false)
    private Integer accessCount = 0;

    /**
     * 记录创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /**
     * 最后访问时间
     */
    @Column
    private LocalDateTime lastAccessTime;

    /**
     * 创建时自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (accessCount == null) {
            accessCount = 0;
        }
    }

    /**
     * 更新访问统计
     * 递增访问次数并更新最后访问时间
     */
    public void incrementAccessCount() {
        this.accessCount++;
        this.lastAccessTime = LocalDateTime.now();
    }
}
