package top.jionjion.work.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jionjion.work.entity.TodoRecord;
import top.jionjion.work.service.TodoRecordService;
import top.jionjion.work.supper.component.DialogService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 待办记录控制器
 * 
 * @author Jion
 */
@Component
public class TodoRecordController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TodoRecordController.class);

    @FXML
    private Label titleLabel;

    @FXML
    private TextField todoInputField;

    @FXML
    private ListView<TodoRecord> todoListView;

    @FXML
    private Label todoCountLabel;

    @FXML
    private Button clearCompletedBtn;

    @Autowired
    private TodoRecordService todoRecordService;

    @Autowired
    private DialogService dialogService;

    private ObservableList<TodoRecord> todoList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("初始化待办记录界面");

        // 初始化数据列表
        todoList = FXCollections.observableArrayList();
        todoListView.setItems(todoList);

        // 设置自定义单元格渲染
        todoListView.setCellFactory(param -> createTodoListCell());

        // 绑定输入框回车事件
        todoInputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleAddTodo();
            }
        });

        // 绑定清空已完成按钮
        clearCompletedBtn.setOnAction(event -> handleClearCompleted());

        // 加载待办列表
        loadTodoList();
    }

    /**
     * 处理添加待办
     */
    private void handleAddTodo() {
        String content = todoInputField.getText();

        if (content == null || content.trim().isEmpty()) {
            dialogService.info("提示", "待办内容不能为空");
            return;
        }

        try {
            TodoRecord todoRecord = todoRecordService.addTodoRecord(content);
            todoList.add(0, todoRecord); // 添加到列表顶部
            todoInputField.clear();
            updateTodoCount();
            log.info("成功添加待办: {}", content);
        } catch (Exception e) {
            log.error("添加待办失败", e);
            dialogService.error("错误", "添加待办失败: " + e.getMessage());
        }
    }

    /**
     * 处理切换完成状态
     */
    private void handleToggleComplete(Long id) {
        try {
            todoRecordService.toggleComplete(id);
            loadTodoList(); // 重新加载列表以刷新显示
            updateTodoCount();
        } catch (Exception e) {
            log.error("切换待办状态失败", e);
            dialogService.error("错误", "切换状态失败: " + e.getMessage());
        }
    }

    /**
     * 处理删除待办
     */
    private void handleDeleteTodo(Long id) {
        try {
            todoRecordService.deleteTodoRecord(id);
            todoList.removeIf(todo -> todo.getId().equals(id));
            updateTodoCount();
            log.info("成功删除待办: {}", id);
        } catch (Exception e) {
            log.error("删除待办失败", e);
            dialogService.error("错误", "删除待办失败: " + e.getMessage());
        }
    }

    /**
     * 处理清空已完成待办
     */
    private void handleClearCompleted() {
        try {
            Integer count = todoRecordService.clearCompletedTodos();
            if (count > 0) {
                dialogService.info("成功", "已清空 " + count + " 条已完成待办");
                loadTodoList();
                updateTodoCount();
            } else {
                dialogService.info("提示", "没有已完成的待办");
            }
        } catch (Exception e) {
            log.error("清空已完成待办失败", e);
            dialogService.error("错误", "清空失败: " + e.getMessage());
        }
    }

    /**
     * 加载待办列表
     */
    private void loadTodoList() {
        try {
            List<TodoRecord> todos = todoRecordService.getAllTodos();
            todoList.clear();
            todoList.addAll(todos);
            updateTodoCount();
            log.info("加载待办列表: {} 条", todos.size());
        } catch (Exception e) {
            log.error("加载待办列表失败", e);
            dialogService.error("错误", "加载待办列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新待办统计信息
     */
    private void updateTodoCount() {
        Long activeCount = todoRecordService.getActiveTodoCount();
        todoCountLabel.setText("未完成: " + activeCount);
    }

    /**
     * 创建自定义列表单元格
     */
    private ListCell<TodoRecord> createTodoListCell() {
        return new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label contentLabel = new Label();
            private final Button deleteButton = new Button("删除");
            private final HBox container = new HBox(10);

            {
                // 设置容器布局
                container.setAlignment(Pos.CENTER_LEFT);
                
                // 设置内容标签自动增长
                HBox.setHgrow(contentLabel, Priority.ALWAYS);
                contentLabel.setMaxWidth(Double.MAX_VALUE);
                contentLabel.setStyle("-fx-font-size: 14px;");
                
                // 设置删除按钮样式
                deleteButton.getStyleClass().addAll("btn", "btn-danger");
                deleteButton.setStyle("-fx-font-size: 12px;");
                deleteButton.setVisible(false); // 默认隐藏
                
                // 添加悬停事件
                container.setOnMouseEntered(event -> deleteButton.setVisible(true));
                container.setOnMouseExited(event -> deleteButton.setVisible(false));
                
                // 添加子元素
                container.getChildren().addAll(checkBox, contentLabel, deleteButton);
            }

            @Override
            protected void updateItem(TodoRecord item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // 设置复选框状态
                    checkBox.setSelected(item.getCompleted());
                    checkBox.setOnAction(event -> handleToggleComplete(item.getId()));

                    // 设置内容文本
                    contentLabel.setText(item.getContent());

                    // 根据完成状态设置样式
                    if (item.getCompleted()) {
                        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #adb5bd; -fx-opacity: 0.6; -fx-strikethrough: true;");
                    } else {
                        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #212529;");
                    }

                    // 设置删除按钮事件
                    deleteButton.setOnAction(event -> handleDeleteTodo(item.getId()));

                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }
}
