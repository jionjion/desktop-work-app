package top.jionjion.work.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;
import top.jionjion.work.WorkApplication;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * 模板渲染引擎 - 类似Vue的组件化渲染系统
 * 支持FXML模板加载和数据绑定
 *
 * @author Jion
 */
@Component
public class TemplateRenderer {

    private static final String TEMPLATE_PREFIX = "/templates/";

    /**
     * 渲染FXML模板到指定容器
     *
     * @param container    目标容器
     * @param templateName 模板名称
     * @param dataBinding  数据绑定回调
     */
    public void render(StackPane container, String templateName, Consumer<Object> dataBinding) {
        try {
            // 清空容器（遵循JavaFX页面切换规范）
            container.getChildren().clear();

            // 加载FXML模板
            Node template = loadTemplate(templateName);

            // 添加到容器
            container.getChildren().add(template);

            // 执行数据绑定
            if (dataBinding != null) {
                FXMLLoader loader = getLoaderFromTemplate(templateName);
                if (loader != null && loader.getController() != null) {
                    dataBinding.accept(loader.getController());
                }
            }

        } catch (Exception e) {
            renderError(container, "模板加载失败: " + templateName, e);
        }
    }

    /**
     * 渲染FXML模板（简化版本，直接通过模板名渲染）
     *
     * @param container    目标容器
     * @param templateName 模板名称（对应FXML文件名，不含.fxml后缀）
     */
    public void renderTemplate(StackPane container, String templateName) {
        render(container, templateName, null);
    }

    /**
     * 渲染纯Java代码生成的组件
     *
     * @param container        目标容器
     * @param componentFactory 组件工厂方法
     */
    public void renderComponent(StackPane container, ComponentFactory componentFactory) {
        try {
            // 清空容器
            container.getChildren().clear();

            // 创建组件
            Node component = componentFactory.create();

            // 添加到容器
            container.getChildren().add(component);

        } catch (Exception e) {
            renderError(container, "组件渲染失败", e);
        }
    }

    private Node loadTemplate(String templateName) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        // 设置Spring控制器工厂
        if (WorkApplication.getApplicationContext() != null) {
            loader.setControllerFactory(WorkApplication.getApplicationContext()::getBean);
        }

        // 构建模板路径
        String templatePath = TEMPLATE_PREFIX + templateName + ".fxml";
        URL resource = getClass().getResource(templatePath);

        if (resource == null) {
            throw new IllegalArgumentException("模板文件未找到: " + templatePath);
        }

        loader.setLocation(resource);
        return loader.load();
    }

    private FXMLLoader getLoaderFromTemplate(String templateName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            if (WorkApplication.getApplicationContext() != null) {
                loader.setControllerFactory(WorkApplication.getApplicationContext()::getBean);
            }

            String templatePath = TEMPLATE_PREFIX + templateName + ".fxml";
            URL resource = getClass().getResource(templatePath);
            if (resource != null) {
                loader.setLocation(resource);
                loader.load();
                return loader;
            }
        } catch (IOException e) {
            // 忽略异常，返回null
        }
        return null;
    }

    private void renderError(StackPane container, String message, Exception e) {
        container.getChildren().clear();
        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                message + "\n" + (e != null ? e.getMessage() : "")
        );
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        container.getChildren().add(errorLabel);
    }

    @FunctionalInterface
    public interface ComponentFactory {
        Node create();
    }
}