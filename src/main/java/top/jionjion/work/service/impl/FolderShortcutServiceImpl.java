package top.jionjion.work.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.jionjion.work.entity.FolderShortcut;
import top.jionjion.work.exception.AppException;
import top.jionjion.work.repository.FolderShortcutRepository;
import top.jionjion.work.service.FolderShortcutService;
import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * 文件夹快捷访问服务实现类
 *
 * @author Jion
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FolderShortcutServiceImpl implements FolderShortcutService {

    private final FolderShortcutRepository folderShortcutRepository;

    @Override
    public List<FolderShortcut> getAllSortedByAccess() {
        return folderShortcutRepository.findAllByOrderByAccessCountDescLastAccessTimeDesc();
    }

    @Override
    public Optional<FolderShortcut> getById(Long id) {
        return folderShortcutRepository.findById(id);
    }

    @Override
    @Transactional
    public FolderShortcut add(FolderShortcut folderShortcut) {
        return folderShortcutRepository.save(folderShortcut);
    }

    @Override
    @Transactional
    public FolderShortcut update(FolderShortcut folderShortcut) {
        return folderShortcutRepository.save(folderShortcut);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        folderShortcutRepository.deleteById(id);
    }

    @Override
    public boolean existsByPath(String path) {
        return folderShortcutRepository.existsByPath(path);
    }

    @Override
    public boolean existsByPathExcludingId(String path, Long excludeId) {
        Optional<FolderShortcut> existing = folderShortcutRepository.findByPath(path);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }

    @Override
    @Transactional
    public void openFolder(FolderShortcut folderShortcut) {
        // 验证路径是否存在
        if (!isPathValid(folderShortcut.getPath())) {
            throw new AppException("文件夹路径无效或不可访问: " + folderShortcut.getPath());
        }

        try {
            // 调用系统命令打开文件夹
            openFolderByOs(folderShortcut.getPath());

            // 更新访问统计
            folderShortcut.incrementAccessCount();
            folderShortcutRepository.save(folderShortcut);

            log.info("成功打开文件夹: {}, 当前访问次数: {}", folderShortcut.getName(), folderShortcut.getAccessCount());
        } catch (Exception e) {
            log.error("打开文件夹失败: {}", folderShortcut.getPath(), e);
            throw new AppException("打开文件夹失败: " + folderShortcut.getPath());
        }
    }

    @Override
    public boolean isPathValid(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        try {
            Path p = Paths.get(path);
            return Files.exists(p) && Files.isDirectory(p) && Files.isReadable(p);
        } catch (Exception e) {
            log.warn("路径验证失败: {}", path, e);
            return false;
        }
    }

    /**
     * 根据操作系统类型打开文件夹
     *
     * @param path 文件夹路径
     * @throws IOException 打开失败时抛出异常
     */
    private void openFolderByOs(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("文件夹不存在或不可访问: " + path);
        }

        // 优先使用跨平台 Desktop API
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(dir);
                log.debug("使用Desktop API打开文件夹: {}", path);
                return;
            }
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            log.warn("Desktop API不可用，回退到系统命令: {}", e.getMessage());
        } catch (SecurityException e) {
            throw new IOException("没有权限访问该文件夹", e);
        }

        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                // Windows: 直接启动 explorer，不等待退出码
                new ProcessBuilder("explorer.exe", path).start();
                log.debug("使用Windows explorer打开文件夹(不等待退出码): {}", path);
            } else if (osName.contains("mac")) {
                // macOS
                new ProcessBuilder("open", path).start();
                log.debug("使用macOS open命令打开文件夹: {}", path);
            } else if (osName.contains("nix") || osName.contains("nux")) {
                // Linux
                new ProcessBuilder("xdg-open", path).start();
                log.debug("使用Linux xdg-open命令打开文件夹: {}", path);
            } else {
                throw new IOException("不支持的操作系统: " + osName);
            }
        } catch (SecurityException e) {
            throw new IOException("没有权限访问该文件夹", e);
        } catch (IOException e) {
            throw new IOException("打开文件夹命令执行失败: " + e.getMessage(), e);
        }
    }
}
