package top.jionjion.work.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Service;
import top.jionjion.work.exception.AppException;
import top.jionjion.work.service.CommandService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * 执行命令的服务
 *
 * @author Jion
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommandServiceImpl implements CommandService {

    /**
     * 执行 power shell 命令并返回结果
     *
     * @param command 命令
     * @return 命令执行结果
     */
    public String executeCommand(String command) {
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);

        // 捕获输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            log.error("执行脚本失败: " + e.getMessage(), e);
        }
        return outputStream.toString().trim();
    }


    /**
     * 在指定目录执行PowerShell脚本
     *
     * @param workingDir 工作目录（用于设置进程的工作目录）
     * @param scriptName 脚本名称
     * @param targetDir  要检查Git提交的目标目录（传递给脚本的参数）
     * @return 脚本执行结果
     */
    public String executePowerShellScript(String workingDir, String scriptName, String targetDir) {
        // 先尝试从类路径加载脚本到临时文件
        File tempScriptFile = copyScriptToTempFile(scriptName);

        // 构建命令行，使用临时文件
        String command = "powershell.exe -ExecutionPolicy Bypass -File \"" + tempScriptFile.getAbsolutePath() + "\"";
        // 如果提供了targetDir参数，则添加到命令中
        if (targetDir != null && !targetDir.isEmpty()) {
            command += " -TargetDir \"" + targetDir + "\"";
        }
        CommandLine cmdLine = CommandLine.parse(command);

        // 设置执行器
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);

        // 设置工作目录
        if (workingDir != null && !workingDir.isEmpty()) {
            executor.setWorkingDirectory(new File(workingDir));
        }

        // 捕获输出和错误流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        executor.setStreamHandler(streamHandler);

        try {
            executor.execute(cmdLine);
            // 删除临时文件
            Files.delete(tempScriptFile.toPath());
            return outputStream.toString().trim();
        } catch (Exception e) {
            String errorMsg = "执行脚本失败: " + e.getMessage()
                    + "\n错误输出: " + errorStream.toString().trim();
            throw new AppException(errorMsg, e);
        }
    }

    /**
     * 在指定目录执行PowerShell脚本（简化版本，不传递额外的目标目录参数）
     *
     * @param workingDir 工作目录
     * @param scriptName 脚本名称
     * @return 脚本执行结果
     */
    public String executePowerShellScript(String workingDir, String scriptName) {
        return executePowerShellScript(workingDir, scriptName, null);
    }

    /**
     * 将类路径下的脚本复制到临时文件
     *
     * @param scriptName 脚本名称
     * @return 临时文件
     */
    private File copyScriptToTempFile(String scriptName) {
        // 从类路径获取脚本
        InputStream scriptStream = getClass().getClassLoader().getResourceAsStream("script/" + scriptName);
        if (scriptStream == null) {
            // 如果在类路径中找不到，则尝试作为文件路径处理
            File scriptFile = new File(scriptName);
            if (scriptFile.exists()) {
                return scriptFile;
            }
            throw new AppException("找不到脚本文件: " + scriptName);
        }

        // 创建临时文件
        File tempFile;
        try {
            tempFile = File.createTempFile("script_", ".ps1");
            // 程序退出时自动删除
            tempFile.deleteOnExit();

            // 复制脚本内容到临时文件
            Files.copy(scriptStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AppException(e);
        }


        return tempFile;
    }
}
