package top.jionjion.work.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.jionjion.work.BaseTest;
import top.jionjion.work.entity.FolderShortcut;
import top.jionjion.work.repository.FolderShortcutRepository;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件夹快捷访问Service测试
 *
 * @author Jion
 */
class FolderShortcutServiceTest extends BaseTest {

    @Autowired
    private FolderShortcutService folderShortcutService;

    @Autowired
    private FolderShortcutRepository folderShortcutRepository;

    @Override
    @BeforeEach
    protected void setUp() {
        // 清空测试数据
        folderShortcutRepository.deleteAll();
    }

    @Test
    void testGetAllSortedByAccess() {
        // 创建测试数据
        createFolderShortcut("文件夹A", "C:\\A", 10);
        createFolderShortcut("文件夹B", "C:\\B", 5);
        createFolderShortcut("文件夹C", "C:\\C", 15);

        // 查询
        List<FolderShortcut> result = folderShortcutService.getAllSortedByAccess();

        // 验证
        assertEquals(3, result.size());
        assertEquals("文件夹C", result.getFirst().getName());
        assertEquals(15, result.getFirst().getAccessCount());
    }

    @Test
    void testAddFolderShortcut() {
        // 创建实体
        FolderShortcut folder = new FolderShortcut();
        folder.setName("新文件夹");
        folder.setPath("C:\\New");
        folder.setAccessCount(0);
        folder.setCreateTime(LocalDateTime.now());

        // 添加
        FolderShortcut saved = folderShortcutService.add(folder);

        // 验证
        assertNotNull(saved.getId());
        assertEquals("新文件夹", saved.getName());
    }

    @Test
    void testUpdateFolderShortcut() {
        // 创建测试数据
        FolderShortcut folder = createFolderShortcut("原名称", "C:\\Old", 0);

        // 更新
        folder.setName("新名称");
        folder.setPath("C:\\New");
        FolderShortcut updated = folderShortcutService.update(folder);

        // 验证
        assertEquals("新名称", updated.getName());
        assertEquals("C:\\New", updated.getPath());
    }

    @Test
    void testDeleteFolderShortcut() {
        // 创建测试数据
        FolderShortcut folder = createFolderShortcut("测试", "C:\\Test", 0);
        Long id = folder.getId();

        // 删除
        folderShortcutService.delete(id);

        // 验证
        Optional<FolderShortcut> result = folderShortcutService.getById(id);
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByPath() {
        // 创建测试数据
        createFolderShortcut("测试", "C:\\Test", 0);

        // 验证
        assertTrue(folderShortcutService.existsByPath("C:\\Test"));
        assertFalse(folderShortcutService.existsByPath("C:\\NotExist"));
    }

    @Test
    void testExistsByPathExcludingId() {
        // 创建测试数据
        FolderShortcut folder1 = createFolderShortcut("文件夹1", "C:\\Test1", 0);
        FolderShortcut folder2 = createFolderShortcut("文件夹2", "C:\\Test2", 0);

        // 验证（排除自己，不存在重复）
        assertFalse(folderShortcutService.existsByPathExcludingId("C:\\Test1", folder1.getId()));

        // 验证（排除自己，存在其他重复）
        assertTrue(folderShortcutService.existsByPathExcludingId("C:\\Test2", folder2.getId()));
    }

    @Test
    void testIsPathValid() {
        // 测试有效路径（系统临时目录一定存在）
        String tempDir = System.getProperty("java.io.tmpdir");
        assertTrue(folderShortcutService.isPathValid(tempDir));

        // 测试无效路径
        assertFalse(folderShortcutService.isPathValid("C:\\ThisPathDoesNotExist12345"));

        // 测试空路径
        assertFalse(folderShortcutService.isPathValid(""));
        assertFalse(folderShortcutService.isPathValid(null));
    }

    @Test
    void testOpenFolderWithInvalidPath() {
        // 创建无效路径的快捷方式
        FolderShortcut folder = new FolderShortcut();
        folder.setName("无效路径");
        folder.setPath("C:\\InvalidPath12345");
        folder.setAccessCount(0);

        // 验证打开失败
        assertThrows(Exception.class, () -> {
            folderShortcutService.openFolder(folder);
        });
    }

    @Test
    void testOpenFolderSuccess() {
        // 创建临时测试目录
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "test-folder-shortcut");
        tempDir.mkdirs();

        try {
            // 创建快捷方式
            FolderShortcut folder = new FolderShortcut();
            folder.setName("测试临时目录");
            folder.setPath(tempDir.getAbsolutePath());
            folder.setAccessCount(0);
            folder.setCreateTime(LocalDateTime.now());
            folder = folderShortcutRepository.save(folder);

            // 记录初始访问次数
            int initialCount = folder.getAccessCount();

            // 打开文件夹
            folderShortcutService.openFolder(folder);

            // 验证访问次数增加
            Optional<FolderShortcut> updated = folderShortcutRepository.findById(folder.getId());
            assertTrue(updated.isPresent());
            assertEquals(initialCount + 1, updated.get().getAccessCount());
            assertNotNull(updated.get().getLastAccessTime());

        } finally {
            // 清理测试目录
            tempDir.delete();
        }
    }

    /**
     * 辅助方法：创建文件夹快捷方式
     */
    private FolderShortcut createFolderShortcut(String name, String path, int accessCount) {
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
