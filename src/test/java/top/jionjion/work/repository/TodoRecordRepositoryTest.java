package top.jionjion.work.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import top.jionjion.work.BaseTest;
import top.jionjion.work.entity.TodoRecord;

/**
 * 待办记录数据访问测试
 *
 * @author Jion
 */
class TodoRecordRepositoryTest extends BaseTest {

    @Autowired
    private TodoRecordRepository todoRecordRepository;

    @Test
    void testSaveAndFind() {
        // 保存待办
        TodoRecord todoRecord = new TodoRecord("测试待办");
        TodoRecord saved = todoRecordRepository.save(todoRecord);

        assertNotNull(saved.getId());
        assertEquals("测试待办", saved.getContent());
        assertFalse(saved.getCompleted());
        assertNotNull(saved.getCreateTime());

        // 查询待办
        TodoRecord found = todoRecordRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getContent(), found.getContent());

        System.out.println("保存并查询待办成功: " + found);
    }

    @Test
    void testFindByCompleted() {
        // 保存未完成待办
        TodoRecord todo1 = new TodoRecord("未完成待办");
        todoRecordRepository.save(todo1);

        // 保存已完成待办
        TodoRecord todo2 = new TodoRecord("已完成待办");
        todo2.setCompleted(true);
        todoRecordRepository.save(todo2);

        // 查询未完成
        List<TodoRecord> activeTodos = todoRecordRepository.findByCompleted(false);
        assertFalse(activeTodos.isEmpty());
        assertTrue(activeTodos.stream().noneMatch(TodoRecord::getCompleted));

        // 查询已完成
        List<TodoRecord> completedTodos = todoRecordRepository.findByCompleted(true);
        assertFalse(completedTodos.isEmpty());
        assertTrue(completedTodos.stream().allMatch(TodoRecord::getCompleted));

        System.out.println("按完成状态查询成功，未完成: " + activeTodos.size() + ", 已完成: " + completedTodos.size());
    }

    @Test
    void testDeleteByCompleted() {
        // 保存多个待办
        TodoRecord todo1 = new TodoRecord("待办1");
        todoRecordRepository.save(todo1);

        TodoRecord todo2 = new TodoRecord("待办2");
        todo2.setCompleted(true);
        todoRecordRepository.save(todo2);

        TodoRecord todo3 = new TodoRecord("待办3");
        todo3.setCompleted(true);
        todoRecordRepository.save(todo3);

        // 删除已完成
        todoRecordRepository.deleteByCompleted(true);

        // 验证已完成的被删除
        List<TodoRecord> completedTodos = todoRecordRepository.findByCompleted(true);
        assertEquals(0, completedTodos.size());

        // 验证未完成的还在
        List<TodoRecord> activeTodos = todoRecordRepository.findByCompleted(false);
        assertFalse(activeTodos.isEmpty());

        System.out.println("按状态批量删除成功");
    }

    @Test
    void testOrderByCreateTime() throws InterruptedException {
        // 保存多个待办
        TodoRecord todo1 = new TodoRecord("待办1");
        todoRecordRepository.save(todo1);

        Thread.sleep(10); // 确保时间不同

        TodoRecord todo2 = new TodoRecord("待办2");
        todoRecordRepository.save(todo2);

        Thread.sleep(10);

        TodoRecord todo3 = new TodoRecord("待办3");
        todoRecordRepository.save(todo3);

        // 查询并验证顺序（倒序）
        List<TodoRecord> todos = todoRecordRepository.findAllByOrderByCreateTimeDesc();
        assertTrue(todos.size() >= 3);

        // 验证时间倒序
        for (int i = 0; i < todos.size() - 1; i++) {
            assertTrue(todos.get(i).getCreateTime().isAfter(todos.get(i + 1).getCreateTime())
                    || todos.get(i).getCreateTime().equals(todos.get(i + 1).getCreateTime()));
        }

        System.out.println("创建时间倒序查询成功");
    }

    @Test
    void testCountByCompleted() {
        // 保存多个待办
        TodoRecord todo1 = new TodoRecord("未完成1");
        todoRecordRepository.save(todo1);

        TodoRecord todo2 = new TodoRecord("未完成2");
        todoRecordRepository.save(todo2);

        TodoRecord todo3 = new TodoRecord("已完成");
        todo3.setCompleted(true);
        todoRecordRepository.save(todo3);

        // 统计数量
        Long activeCount = todoRecordRepository.countByCompleted(false);
        Long completedCount = todoRecordRepository.countByCompleted(true);

        assertTrue(activeCount >= 2);
        assertTrue(completedCount >= 1);

        System.out.println("统计待办数量成功，未完成: " + activeCount + ", 已完成: " + completedCount);
    }

    @Test
    void testUpdate() {
        // 保存待办
        TodoRecord todoRecord = new TodoRecord("待更新的待办");
        TodoRecord saved = todoRecordRepository.save(todoRecord);

        // 更新内容
        saved.setContent("更新后的内容");
        saved.setCompleted(true);
        TodoRecord updated = todoRecordRepository.save(saved);

        assertEquals("更新后的内容", updated.getContent());
        assertTrue(updated.getCompleted());

        System.out.println("更新待办成功: " + updated);
    }

    @Test
    void testDelete() {
        // 保存待办
        TodoRecord todoRecord = new TodoRecord("待删除的待办");
        TodoRecord saved = todoRecordRepository.save(todoRecord);
        Long id = saved.getId();

        // 删除待办
        todoRecordRepository.deleteById(id);

        // 验证已删除
        assertFalse(todoRecordRepository.findById(id).isPresent());

        System.out.println("删除待办成功: " + id);
    }
}
