package top.jionjion.work.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.jionjion.work.BaseTest;
import top.jionjion.work.entity.FolderShortcut;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件夹快捷访问Repository测试
 *
 * @author Jion
 */
class FolderShortcutRepositoryTest extends BaseTest {

    @Autowired
    private FolderShortcutRepository folderShortcutRepository;

    @BeforeEach
    @Override
    protected void setUp() {
        // 清空测试数据
        folderShortcutRepository.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        // 创建测试数据
        FolderShortcut folder = new FolderShortcut();
        folder.setName("测试文件夹");
        folder.setPath("C:\\Test");
        folder.setAccessCount(0);
        folder.setCreateTime(LocalDateTime.now());

        // 保存
        FolderShortcut saved = folderShortcutRepository.save(folder);

        // 验证
        assertNotNull(saved.getId());
        assertEquals("测试文件夹", saved.getName());

        // 查询
        Optional<FolderShortcut> found = folderShortcutRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("C:\\Test", found.get().getPath());
    }

    @Test
    void testFindAllOrderByAccessCount() {
        // 创建测试数据（不同访问次数）
        createFolderShortcut("文件夹A", "C:\\A", 10);
        createFolderShortcut("文件夹B", "C:\\B", 5);
        createFolderShortcut("文件夹C", "C:\\C", 15);

        // 查询
        List<FolderShortcut> result = folderShortcutRepository.findAllByOrderByAccessCountDescLastAccessTimeDesc();

        // 验证排序（按访问次数降序）
        assertEquals(3, result.size());
        assertEquals("文件夹C", result.get(0).getName()); // 15次
        assertEquals("文件夹A", result.get(1).getName()); // 10次
        assertEquals("文件夹B", result.get(2).getName()); // 5次
    }

    @Test
    void testExistsByPath() {
        // 创建测试数据
        createFolderShortcut("测试", "C:\\Test", 0);

        // 验证存在性
        assertTrue(folderShortcutRepository.existsByPath("C:\\Test"));
        assertFalse(folderShortcutRepository.existsByPath("C:\\NotExist"));
    }

    @Test
    void testFindByPath() {
        // 创建测试数据
        createFolderShortcut("测试", "C:\\Test", 0);

        // 查询
        Optional<FolderShortcut> result = folderShortcutRepository.findByPath("C:\\Test");

        // 验证
        assertTrue(result.isPresent());
        assertEquals("测试", result.get().getName());
    }

    @Test
    void testDeleteById() {
        // 创建测试数据
        FolderShortcut folder = createFolderShortcut("测试", "C:\\Test", 0);

        // 删除
        folderShortcutRepository.deleteById(folder.getId());

        // 验证
        Optional<FolderShortcut> result = folderShortcutRepository.findById(folder.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateAccessCount() {
        // 创建测试数据
        FolderShortcut folder = createFolderShortcut("测试", "C:\\Test", 5);

        // 更新访问次数
        folder.incrementAccessCount();
        folderShortcutRepository.save(folder);

        // 验证
        Optional<FolderShortcut> result = folderShortcutRepository.findById(folder.getId());
        assertTrue(result.isPresent());
        assertEquals(6, result.get().getAccessCount());
        assertNotNull(result.get().getLastAccessTime());
    }

    /**
     * 辅助方法：创建文件夹快捷方式
     */
    FolderShortcut createFolderShortcut(String name, String path, int accessCount) {
        FolderShortcut folder = new FolderShortcut();
        folder.setName(name);
        folder.setPath(path);
        folder.setAccessCount(accessCount);
        folder.setCreateTime(LocalDateTime.now());
        if (accessCount > 0) {
            folder.setLastAccessTime(LocalDateTime.now());
        }
        return folderShortcutRepository.save(folder);
    }
}
