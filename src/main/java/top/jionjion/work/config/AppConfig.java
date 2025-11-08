package top.jionjion.work.config;

import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Spring配置类
 *
 * @author Jion
 */
@Configuration
public class AppConfig {

    /**
     * 主界面路由
     */
    public static final String HOME_ROUTER = "layout-view.fxml";

    /**
     * 图标路径
     */
    public static final String ICON_PATH = "/icon/icon-1000.png";

    /**
     * 应用名称
     */
    public static final String APP_NAME = "个人工作台";


    /**
     * 获取应用样式表
     *
     * @return 样式表路径
     */
    public static String appStylesheet() {
        return Objects.requireNonNull(AppConfig.class.getResource("/styles/app.css")).toExternalForm();
    }

    /**
     * 获取主界面样式表
     *
     * @return 样式表路径
     */
    public static String mainStylesheet() {
        return Objects.requireNonNull(AppConfig.class.getResource("/styles/main.css")).toExternalForm();
    }
}