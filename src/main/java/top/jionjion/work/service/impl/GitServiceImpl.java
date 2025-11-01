package top.jionjion.work.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.jionjion.work.service.GitService;

/**
 * 读取Git相关内容
 *
 * @author Jion
 */
@Service
@AllArgsConstructor
public class GitServiceImpl implements GitService {

    private final CommandServiceImpl commandService;

    /**
     * 获取当前Git用户名
     *
     * @return 用户名
     */
    public String getGitUserName() {
        return commandService.executeCommand("git config user.name");
    }

    /**
     * 获取当前Git用户邮箱
     *
     * @return 用户邮箱
     */
    public String getGitUserEmail() {
        return commandService.executeCommand("git config user.email");
    }


    /**
     * 执行git-today.ps1脚本
     *
     * @param workingDir 工作目录
     * @param targetDir  目标Git仓库目录
     * @return 脚本执行结果
     */
    public String getTodayGitCommits(String workingDir, String targetDir) {
        return commandService.executePowerShellScript(workingDir, "git-today.ps1", targetDir);
    }

    /**
     * 执行git-today.ps1脚本（使用当前工作目录）
     *
     * @param workingDir 工作目录
     * @return 脚本执行结果
     */
    public String getTodayGitCommits(String workingDir) {
        return commandService.executePowerShellScript(workingDir, "git-today.ps1");
    }

}