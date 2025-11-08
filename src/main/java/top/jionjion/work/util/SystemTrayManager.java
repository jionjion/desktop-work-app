package top.jionjion.work.util;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * 系统托盘管理器 - 负责管理应用程序的系统托盘功能
 *
 * @author Jion
 */
@CommonsLog
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SystemTrayManager {

    /**
     * 系统托盘图标
     */
    private static TrayIcon trayIcon;

    /**
     * 初始化系统托盘
     *
     * @param stage    主窗口
     * @param iconPath 图标路径
     * @param appName  应用名称
     */
    public static void initSystemTray(Stage stage, String iconPath, String appName) {
        // 检查系统是否支持托盘
        if (!SystemTray.isSupported()) {
            log.warn("系统不支持托盘功能");
            return;
        }

        try {
            // 设置AWT使用系统外观和字符编码
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("sun.jnu.encoding", "UTF-8");

            // 获取系统托盘实例
            SystemTray systemTray = SystemTray.getSystemTray();

            // 加载托盘图标
            InputStream iconStream = SystemTrayManager.class.getResourceAsStream(iconPath);
            if (iconStream == null) {
                log.error("托盘图标加载失败: " + iconPath);
                return;
            }

            Image image = ImageIO.read(iconStream);

            // 创建弹出菜单
            PopupMenu popupMenu = createPopupMenu(stage);

            // 创建托盘图标
            trayIcon = new TrayIcon(image, appName, popupMenu);
            trayIcon.setImageAutoSize(true);

            // 添加双击显示窗口的事件监听
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                        // 双击左键显示窗口
                        Platform.runLater(() -> {
                            stage.show();
                            stage.toFront();
                        });
                    }
                }
            });

            // 将托盘图标添加到系统托盘
            systemTray.add(trayIcon);

            log.info("系统托盘初始化成功");

        } catch (IOException | AWTException e) {
            log.error("系统托盘初始化失败", e);
        }
    }

    /**
     * 创建托盘弹出菜单
     *
     * @param stage 主窗口
     * @return 弹出菜单
     */
    private static PopupMenu createPopupMenu(Stage stage) {
        PopupMenu popupMenu = new PopupMenu();

        // 添加"显示"菜单项
        MenuItem showItem = new MenuItem("Show");
        showItem.addActionListener(e -> Platform.runLater(() -> {
            stage.show();
            stage.toFront();
        }));

        // 添加"退出"菜单项
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> Platform.runLater(() -> {
            removeSystemTray();
            Platform.exit();
            System.exit(0);
        }));

        // 将菜单项添加到弹出菜单
        popupMenu.add(showItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
        return popupMenu;
    }

    /**
     * 隐藏窗口到托盘
     *
     * @param stage 主窗口
     */
    public static void hideToTray(Stage stage) {
        Platform.runLater(() -> {
            stage.hide();
            if (trayIcon != null) {
                trayIcon.displayMessage(
                        "应用已最小化到托盘",
                        "双击托盘图标或右键选择'显示'来恢复窗口",
                        TrayIcon.MessageType.INFO
                );
            }
        });
    }

    /**
     * 移除系统托盘图标
     */
    public static void removeSystemTray() {
        if (trayIcon != null) {
            SystemTray systemTray = SystemTray.getSystemTray();
            systemTray.remove(trayIcon);
            trayIcon = null;
            log.info("系统托盘图标已移除");
        }
    }
}
