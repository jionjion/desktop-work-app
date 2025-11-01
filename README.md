# 打包说明

## 🚀 快速打包

### 一键打包(推荐)

双击项目根目录下的 **`package.bat`** 文件,等待 2-5 分钟。

**打包配置**: 使用 **生产环境配置** (`application-prod.properties`)

---

## 📦 手动打包

### 使用生产环境配置打包(默认)

```powershell
# 1. 构建项目
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
mvn clean package -DskipTests -Dspring.profiles.active=prod

# 2. 打包为 exe (2-5分钟)
& "C:\Program Files\Java\jdk-25\bin\jpackage.exe" `
    --type app-image `
    --name "WorkApp" `
    --app-version "1.0.0" `
    --vendor "JionJion" `
    --dest "target\dist" `
    --input "target" `
    --main-jar "work-app-1.0-SNAPSHOT.jar" `
    --java-options "-Dfile.encoding=UTF-8" `
    --java-options "-Dconsole.encoding=UTF-8" `
    --java-options "-Dspring.profiles.active=prod"
```

### 使用开发环境配置打包

如需使用开发环境配置打包,修改配置参数:

```powershell
# Maven 构建时
mvn clean package -DskipTests -Dspring.profiles.active=dev

# jpackage 打包时
--java-options "-Dspring.profiles.active=dev"
```

---

## 📁 打包结果

```
target\dist\WorkApp\
├── WorkApp.exe          ← 可执行文件
├── app\                 ← 应用JAR
└── runtime\             ← 内嵌JRE (约150MB)
```

**运行**: 双击 `target\dist\WorkApp\WorkApp.exe`

---

## 🔧 环境配置说明

### 配置文件位置
- **开发环境**: `src/main/resources/application-dev.properties`
- **生产环境**: `src/main/resources/application-prod.properties`

### 运行时配置检查

打包后的应用启动时会显示当前使用的配置环境。

---

## 💡 提示

- ✅ 打包默认使用**生产环境配置**
- ✅ 整个 `WorkApp` 目录都是必需的,不能只复制 exe
- ✅ 打包后的应用可在任何 Windows 电脑运行,无需安装 Java
- ⚠️ 修改配置需要重新打包

---

## 🐛 常见问题

**Q: 如何确认打包使用的是哪个配置?**  
A: 启动应用后查看控制台或日志输出,会显示 `active profiles: prod`

**Q: 打包后想换配置怎么办?**  
A: 需要修改 `package.bat` 中的配置参数并重新打包

**Q: 能否打包时包含多个配置?**  
A: 所有配置文件都会打包进去,只是启动时通过 `-Dspring.profiles.active` 指定使用哪个

---

祝你打包顺利! 🎉
