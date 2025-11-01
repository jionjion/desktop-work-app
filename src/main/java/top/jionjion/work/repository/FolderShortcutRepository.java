package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.jionjion.work.entity.FolderShortcut;

import java.util.List;
import java.util.Optional;

/**
 * 文件夹快捷访问数据访问接口
 * 提供基础CRUD操作和自定义查询方法
 *
 * @author Jion
 */
@Repository
public interface FolderShortcutRepository extends JpaRepository<FolderShortcut, Long> {

    /**
     * 查询所有快捷路径，按访问次数降序排列，次数相同时按最后访问时间降序排列
     * 
     * @return 排序后的快捷路径列表
     */
    List<FolderShortcut> findAllByOrderByAccessCountDescLastAccessTimeDesc();

    /**
     * 检查指定路径是否已存在
     * 
     * @param path 文件夹路径
     * @return 存在返回true，否则返回false
     */
    boolean existsByPath(String path);

    /**
     * 根据路径查询记录
     * 
     * @param path 文件夹路径
     * @return 查询结果
     */
    Optional<FolderShortcut> findByPath(String path);
}
