package top.jionjion.work;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import top.jionjion.work.config.AppConfig;
import top.jionjion.work.util.RouterManager;
import top.jionjion.work.util.SystemTrayManager;

import java.io.InputStream;

/**
 * 启动类
 *
 * @author Jion
 */
@SpringBootApplication
public class WorkApplication {

    @Getter
    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        // 设置JavaFX线程为守护线程，避免DevTools重启时的问题
        System.setProperty("javafx.platform.implicitExit", "false");

        // 首先启动 JavaFX 应用
        Application.launch(JavaFxApp.class, args);
    }

    /**
     * 配置JavaFX应用Bean
     */
    @Bean
    public JavaFxApp javaFxApp() {
        return new JavaFxApp();
    }

    public static class JavaFxApp extends Application {
        private ConfigurableApplicationContext springContext;

        @Override
        public void init() {
            // 在 JavaFX 应用初始化时启动 Spring 上下文
            springContext = new SpringApplicationBuilder(WorkApplication.class)
                    .headless(false)
                    .run(getParameters().getRaw().toArray(new String[0]));

            // 设置应用程序上下文
            WorkApplication.applicationContext = springContext;
        }

        @Override
        public void start(Stage primaryStage) {
            // 禁用隐式退出，防止所有窗口关闭时自动退出应用
            javafx.application.Platform.setImplicitExit(false);

            RouterManager.setStage(primaryStage);

            // 添加图标
            InputStream iconStream = WorkApplication.class.getResourceAsStream(AppConfig.ICON_PATH);
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }

            // 初始化系统托盘
            SystemTrayManager.initSystemTray(primaryStage, AppConfig.ICON_PATH, AppConfig.APP_NAME);

            // 拦截窗口关闭事件，最小化到托盘而不是退出
            primaryStage.setOnCloseRequest(event -> {
                // 阻止默认关闭行为
                event.consume();
                SystemTrayManager.hideToTray(primaryStage);
            });

            // 添加 F5 刷新,到主页面
            primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F5) {
                    RouterManager.switchTo(AppConfig.HOME_ROUTER);
                }
            });

            // 主页
            RouterManager.switchTo(AppConfig.HOME_ROUTER);
        }

        @Override
        public void stop() {
            // 移除系统托盘图标
            SystemTrayManager.removeSystemTray();
            springContext.close();
        }
    }
}