package top.jionjion.work.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.jionjion.work.entity.TodoRecord;
import top.jionjion.work.repository.TodoRecordRepository;
import top.jionjion.work.service.TodoRecordService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办记录业务服务实现类
 *
 * @author Jion
 */
@Service
@AllArgsConstructor
public class TodoRecordServiceImpl implements TodoRecordService {

    private static final Logger log = LoggerFactory.getLogger(TodoRecordServiceImpl.class);

    private final TodoRecordRepository todoRecordRepository;

    @Override
    public TodoRecord addTodoRecord(String content) {
        // 验证内容非空
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("待办内容不能为空");
        }

        // 验证长度限制
        if (content.length() > 500) {
            throw new IllegalArgumentException("待办内容不能超过500字符");
        }

        TodoRecord todoRecord = new TodoRecord(content.trim());
        TodoRecord saved = todoRecordRepository.save(todoRecord);
        log.info("添加待办记录: {}", saved);
        return saved;
    }

    @Override
    public TodoRecord toggleComplete(Long id) {
        TodoRecord todoRecord = todoRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("待办记录不存在: " + id));

        // 切换完成状态
        todoRecord.setCompleted(!todoRecord.getCompleted());

        // 设置完成时间
        if (todoRecord.getCompleted()) {
            todoRecord.setCompleteTime(LocalDateTime.now());
        } else {
            todoRecord.setCompleteTime(null);
        }

        TodoRecord updated = todoRecordRepository.save(todoRecord);
        log.info("切换待办状态: {}", updated);
        return updated;
    }

    @Override
    public void deleteTodoRecord(Long id) {
        if (!todoRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("待办记录不存在: " + id);
        }

        todoRecordRepository.deleteById(id);
        log.info("删除待办记录: {}", id);
    }

    @Override
    @Transactional
    public Integer clearCompletedTodos() {
        List<TodoRecord> completedTodos = todoRecordRepository.findByCompleted(true);
        int count = completedTodos.size();

        if (count > 0) {
            todoRecordRepository.deleteByCompleted(true);
            log.info("清空已完成待办: {} 条", count);
        }

        return count;
    }

    @Override
    public List<TodoRecord> getAllTodos() {
        return todoRecordRepository.findAllByOrderByCreateTimeDesc();
    }

    @Override
    public Long getActiveTodoCount() {
        return todoRecordRepository.countByCompleted(false);
    }
}
