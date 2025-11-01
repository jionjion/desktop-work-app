package top.jionjion.work;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 *
 * @author Jion
 */
@Rollback
@Transactional
@SpringBootTest
public abstract class BaseTest {

    protected static final String USER_HOME = System.getProperty("user.home");
    protected static final String USER_DIR = System.getProperty("user.dir");
    protected static final String USER_NAME = System.getProperty("user.name");

    @BeforeEach
    protected void setUp() {
        System.out.println("开始测试");
        System.out.println("用户目录: " + USER_HOME);
        System.out.println("项目目录: " + USER_DIR);
        System.out.println("用户名: " + USER_NAME);
        System.out.println("\n\n\n\n\n\n");
    }
}
