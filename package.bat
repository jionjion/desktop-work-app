@echo off
chcp 65001 >nul
echo ========================================
echo Work-App Windows 打包工具
echo ========================================
echo.

:: 设置 JAVA_HOME
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [1/2] Maven 构建项目 (使用生产环境配置)...
call mvn clean package -DskipTests -Dspring.profiles.active=prod
if %ERRORLEVEL% NEQ 0 (
    echo 错误: Maven 构建失败!
    pause
    exit /b 1
)

echo.
echo [2/2] jpackage 打包为 exe (需要 2-5 分钟,请耐心等待)...
if exist target\dist rd /s /q target\dist
"%JAVA_HOME%\bin\jpackage.exe" --type app-image --name "WorkApp" --app-version "1.0.0" --vendor "JionJion" --dest "target\dist" --input "target" --main-jar "work-app-1.0-SNAPSHOT.jar" --java-options "-Dfile.encoding=UTF-8" --java-options "-Dconsole.encoding=UTF-8" --java-options "-Dspring.profiles.active=prod"
if %ERRORLEVEL% NEQ 0 (
    echo 错误: jpackage 打包失败!
    pause
    exit /b 1
)

echo.
echo ========================================
echo 打包成功!
echo ========================================
echo 可执行文件: target\dist\WorkApp\WorkApp.exe
echo 应用目录: target\dist\WorkApp\
echo.
echo 提示: 整个 WorkApp 目录都是必需的,不能只复制 exe 文件
echo.
pause
