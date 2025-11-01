package top.jionjion.work.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kordamp.bootstrapfx.BootstrapFX;
import top.jionjion.work.WorkApplication;
import top.jionjion.work.config.AppConfig;

import java.io.IOException;
import java.net.URL;

/**
 * 路由管理器 - 负责管理JavaFX应用程序中的页面跳转和视图切换
 *
 * @author Jion
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RouterManager {

    private static final Log log = LogFactory.getLog(RouterManager.class);

    /**
     * FXML文件路径前缀，指向resources/views目录
     */
    private static final String PREFIX = "/views/";

    /**
     * 主应用程序窗口，通过setter方法注入
     */
    @Setter
    @Getter
    private static Stage stage;


    /**
     * 页面跳转方法 - 根据FXML文件名切换视图
     *
     * @param fxmlFile fxml文件名（不包含路径前缀）
     */
    public static void switchTo(String fxmlFile) {
        try {
            // 创建FXML加载器用于加载FXML文件
            FXMLLoader loader = new FXMLLoader();

            // 设置控制器工厂，使用Spring上下文来创建控制器实例以支持依赖注入
            if (WorkApplication.getApplicationContext() != null) {
                loader.setControllerFactory(WorkApplication.getApplicationContext()::getBean);
            }

            // 构建FXML文件的完整路径并获取资源URL
            URL resource = RouterManager.class.getResource(PREFIX + fxmlFile);

            // 记录资源位置日志
            log.info("资源位置" + resource);

            // 设置FXML加载器的资源位置
            loader.setLocation(resource);

            // 验证FXML文件是否存在，如果不存在则抛出异常
            if (loader.getLocation() == null) {
                throw new IllegalArgumentException("FXML file 未找到: /fxml/" + fxmlFile);
            }

            // 创建场景对象，加载FXML文件并设置窗口宽高
            Scene scene = new Scene(loader.load(), 1600, 900);

            // 应用BootstrapFX样式，提供现代化的UI组件样式
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

            // 加载自定义CSS样式文件
            scene.getStylesheets().add(AppConfig.appStylesheet());
            scene.getStylesheets().add(AppConfig.mainStylesheet());

            // 将场景设置到主窗口
            stage.setScene(scene);

            // 设置窗口标题，如果控制器实现了 Initializable 接口则使用类名作为标题，否则使用默认标题
            if (loader.getController() instanceof Initializable initializable) {
                stage.setTitle(initializable.getClass().getSimpleName());
            } else {
                stage.setTitle("个人工作台");
            }

            // 添加F5刷新到主页功能
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F5), () -> RouterManager.switchTo(AppConfig.HOME_ROUTER));

            // 显示窗口
            stage.show();
        } catch (IOException exception) {
            // 记录页面跳转失败的错误日志
            log.error("跳转页面失败: {}", exception);
        }
    }
}