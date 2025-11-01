package top.jionjion.work.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * 代码目录
 *
 * @author Jion
 */
@Data
@Entity(name = "code_directory")
public class CodeDirectory {

    public static final String TABLE_NAME = "code_directory";

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String TYPE = "type";

    public static final String PATH = "path";


    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 路径
     */
    private String path;
}
