package top.jionjion.work.service;

import top.jionjion.work.entity.FolderShortcut;

import java.util.List;
import java.util.Optional;

/**
 * 文件夹快捷访问服务接口
 * 提供文件夹快捷访问的核心业务逻辑
 *
 * @author Jion
 */
public interface FolderShortcutService {

    /**
     * 查询所有快捷路径，按访问次数降序排列
     * 
     * @return 排序后的快捷路径列表
     */
    List<FolderShortcut> getAllSortedByAccess();

    /**
     * 根据ID查询快捷路径
     * 
     * @param id 主键ID
     * @return 查询结果
     */
    Optional<FolderShortcut> getById(Long id);

    /**
     * 新增快捷路径
     * 
     * @param folderShortcut 快捷路径实体
     * @return 保存后的实体
     */
    FolderShortcut add(FolderShortcut folderShortcut);

    /**
     * 更新快捷路径
     * 
     * @param folderShortcut 快捷路径实体
     * @return 更新后的实体
     */
    FolderShortcut update(FolderShortcut folderShortcut);

    /**
     * 删除快捷路径
     * 
     * @param id 主键ID
     */
    void delete(Long id);

    /**
     * 检查路径是否已存在
     * 
     * @param path 文件夹路径
     * @return 存在返回true
     */
    boolean existsByPath(String path);

    /**
     * 检查路径是否已存在（排除指定ID）
     * 
     * @param path 文件夹路径
     * @param excludeId 要排除的ID
     * @return 存在返回true
     */
    boolean existsByPathExcludingId(String path, Long excludeId);

    /**
     * 打开文件夹并更新访问统计
     * 
     * @param folderShortcut 快捷路径实体
     * @throws Exception 打开失败时抛出异常
     */
    void openFolder(FolderShortcut folderShortcut);

    /**
     * 验证路径是否存在
     * 
     * @param path 文件夹路径
     * @return 存在返回true
     */
    boolean isPathValid(String path);
}
