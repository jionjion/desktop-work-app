package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.jionjion.work.entity.TodoRecord;

import java.util.List;

/**
 * 待办记录数据访问接口
 * 
 * @author Jion
 */
@Repository
public interface TodoRecordRepository extends JpaRepository<TodoRecord, Long> {

    /**
     * 查询所有待办并按创建时间倒序排列
     * 
     * @return 待办记录列表
     */
    List<TodoRecord> findAllByOrderByCreateTimeDesc();

    /**
     * 根据完成状态查询待办
     * 
     * @param completed 完成状态
     * @return 待办记录列表
     */
    List<TodoRecord> findByCompleted(Boolean completed);

    /**
     * 删除指定完成状态的待办
     * 
     * @param completed 完成状态
     */
    void deleteByCompleted(Boolean completed);

    /**
     * 统计指定完成状态的待办数量
     * 
     * @param completed 完成状态
     * @return 待办数量
     */
    Long countByCompleted(Boolean completed);
}
