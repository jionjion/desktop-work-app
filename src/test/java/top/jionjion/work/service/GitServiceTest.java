package top.jionjion.work.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.jionjion.work.BaseTest;
import top.jionjion.work.service.impl.GitServiceImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GitService 测试类
 *
 * @author Jion
 */
class GitServiceTest extends BaseTest {

    @Autowired
    GitServiceImpl gitService;

    @Test
    void getGitUserName() {
        // 执行测试
        String userName = gitService.getGitUserName();

        // 验证结果不为null且不为空
        assertNotNull(userName, "Git用户名不应为null");
        assertFalse(userName.isEmpty(), "Git用户名不应为空");

        System.out.println("Git用户名: " + userName);
    }

    @Test
    void getGitUserEmail() {
        // 执行测试
        String userEmail = gitService.getGitUserEmail();

        // 验证结果不为null
        assertNotNull(userEmail, "Git用户邮箱不应为null");

        System.out.println("Git用户邮箱: " + userEmail);
    }

    @Test
    void getTodayGitCommits() {
        // 准备测试数据
        String workingDir = ".";
        String targetDir = "W:\\ins-agent-back-master"; // 要检查的Git仓库路径

        // 执行测试
        String result = gitService.getTodayGitCommits(workingDir, targetDir);

        // 验证结果不为null
        assertNotNull(result, "脚本执行结果不应为null");

        System.out.println("今日提交记录:");
        System.out.println(result);
    }

    @Test
    void getTodayGitCommitsWithOnlyWorkingDir() {
        // Git仓库路径作为工作目录
        String workingDir = "W:\\ins-agent-back-master";

        // 执行测试
        String result = gitService.getTodayGitCommits(workingDir);

        // 验证结果不为null
        assertNotNull(result, "脚本执行结果不应为null");

        System.out.println("今日提交记录 (仅工作目录):");
        System.out.println(result);
    }
}
