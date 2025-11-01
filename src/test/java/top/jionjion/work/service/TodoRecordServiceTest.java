package top.jionjion.work.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import top.jionjion.work.BaseTest;
import top.jionjion.work.entity.TodoRecord;

/**
 * 待办记录服务测试
 *
 * @author Jion
 */
class TodoRecordServiceTest extends BaseTest {

    @Autowired
    private TodoRecordService todoRecordService;

    @Test
    void testAddTodoRecord() {
        // 测试添加有效待办
        String content = "完成单元测试";
        TodoRecord todoRecord = todoRecordService.addTodoRecord(content);

        assertNotNull(todoRecord);
        assertNotNull(todoRecord.getId());
        assertEquals(content, todoRecord.getContent());
        assertFalse(todoRecord.getCompleted());
        assertNotNull(todoRecord.getCreateTime());
        assertNull(todoRecord.getCompleteTime());

        System.out.println("添加待办成功: " + todoRecord);
    }

    @Test
    void testAddTodoRecordWithEmptyContent() {
        // 测试添加空内容待办
        assertThrows(IllegalArgumentException.class, () -> todoRecordService.addTodoRecord(""));

        assertThrows(IllegalArgumentException.class, () -> todoRecordService.addTodoRecord("   "));

        assertThrows(IllegalArgumentException.class, () -> todoRecordService.addTodoRecord(null));
    }

    @Test
    void testAddTodoRecordWithLongContent() {
        // 测试添加超长内容待办
        String longContent = "a".repeat(501);
        assertThrows(IllegalArgumentException.class, () -> todoRecordService.addTodoRecord(longContent));
    }

    @Test
    void testToggleComplete() {
        // 添加待办
        TodoRecord todoRecord = todoRecordService.addTodoRecord("测试切换完成状态");
        Long id = todoRecord.getId();

        // 切换为已完成
        TodoRecord updated = todoRecordService.toggleComplete(id);
        assertTrue(updated.getCompleted());
        assertNotNull(updated.getCompleteTime());

        // 再次切换为未完成
        updated = todoRecordService.toggleComplete(id);
        assertFalse(updated.getCompleted());
        assertNull(updated.getCompleteTime());

        System.out.println("切换完成状态成功: " + updated);
    }

    @Test
    void testToggleCompleteWithInvalidId() {
        // 测试切换不存在的待办
        assertThrows(IllegalArgumentException.class, () -> todoRecordService.toggleComplete(999999L));
    }

    @Test
    void testDeleteTodoRecord() {
        // 添加待办
        TodoRecord todoRecord = todoRecordService.addTodoRecord("测试删除");
        Long id = todoRecord.getId();

        // 删除待办
        todoRecordService.deleteTodoRecord(id);

        // 验证已删除
        assertThrows(IllegalArgumentException.class, () -> todoRecordService.toggleComplete(id));

        System.out.println("删除待办成功: " + id);
    }

    @Test
    void testDeleteTodoRecordWithInvalidId() {
        // 测试删除不存在的待办
        assertThrows(IllegalArgumentException.class, () -> todoRecordService.deleteTodoRecord(999999L));
    }

    @Test
    void testClearCompletedTodos() {
        // 添加多个待办
        todoRecordService.addTodoRecord("待办1");
        TodoRecord todo2 = todoRecordService.addTodoRecord("待办2");
        TodoRecord todo3 = todoRecordService.addTodoRecord("待办3");

        // 标记部分为已完成
        todoRecordService.toggleComplete(todo2.getId());
        todoRecordService.toggleComplete(todo3.getId());

        // 清空已完成
        Integer count = todoRecordService.clearCompletedTodos();
        assertEquals(2, count);

        // 验证只剩未完成的
        List<TodoRecord> todos = todoRecordService.getAllTodos();
        assertTrue(todos.stream().noneMatch(TodoRecord::getCompleted));

        System.out.println("清空已完成待办成功，清理数量: " + count);
    }

    @Test
    void testClearCompletedTodosWhenNone() {
        // 清空时没有已完成待办
        Integer count = todoRecordService.clearCompletedTodos();
        assertEquals(0, count);
    }

    @Test
    void testGetAllTodos() throws InterruptedException {
        // 添加多个待办
        todoRecordService.addTodoRecord("待办A");
        Thread.sleep(10); // 确保时间不同
        todoRecordService.addTodoRecord("待办B");
        Thread.sleep(10);
        todoRecordService.addTodoRecord("待办C");

        // 查询所有待办
        List<TodoRecord> todos = todoRecordService.getAllTodos();
        assertTrue(todos.size() >= 3);

        // 验证按创建时间倒序
        for (int i = 0; i < todos.size() - 1; i++) {
            assertTrue(todos.get(i).getCreateTime().isAfter(todos.get(i + 1).getCreateTime())
                    || todos.get(i).getCreateTime().equals(todos.get(i + 1).getCreateTime()));
        }

        System.out.println("查询所有待办成功，数量: " + todos.size());
    }

    @Test
    void testGetActiveTodoCount() {
        // 添加多个待办
        todoRecordService.addTodoRecord("未完成1");
        TodoRecord completed = todoRecordService.addTodoRecord("已完成");
        todoRecordService.addTodoRecord("未完成2");

        // 标记一个为已完成
        todoRecordService.toggleComplete(completed.getId());

        // 查询未完成数量
        Long count = todoRecordService.getActiveTodoCount();
        assertTrue(count >= 2);

        System.out.println("未完成待办数量: " + count);
    }
}
