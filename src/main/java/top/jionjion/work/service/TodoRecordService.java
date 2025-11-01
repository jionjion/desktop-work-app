package top.jionjion.work.service;

import top.jionjion.work.entity.TodoRecord;

import java.util.List;

/**
 * 待办记录业务服务接口
 * 
 * @author Jion
 */
public interface TodoRecordService {

    /**
     * 添加新的待办记录
     * 
     * @param content 待办内容
     * @return 新增的待办记录
     */
    TodoRecord addTodoRecord(String content);

    /**
     * 切换待办的完成状态
     * 
     * @param id 待办ID
     * @return 更新后的待办记录
     */
    TodoRecord toggleComplete(Long id);

    /**
     * 删除指定待办记录
     * 
     * @param id 待办ID
     */
    void deleteTodoRecord(Long id);

    /**
     * 清空所有已完成待办
     * 
     * @return 清理的待办数量
     */
    Integer clearCompletedTodos();

    /**
     * 获取所有待办列表（按创建时间倒序）
     * 
     * @return 待办记录列表
     */
    List<TodoRecord> getAllTodos();

    /**
     * 获取未完成待办数量
     * 
     * @return 未完成待办数量
     */
    Long getActiveTodoCount();
}
