param(
    [string]$TargetDir = ""
)

# 如果提供了目录参数，则切换到该目录
if ($TargetDir -ne "") {
    if (Test-Path $TargetDir) {
        Set-Location $TargetDir
    } else {
        Write-Host "❌ 指定的目录不存在: $TargetDir" -ForegroundColor Red
        exit 1
    }
}

# 检查是否在 Git 仓库中
if (-not (git rev-parse --git-dir 2>$null)) {
    Write-Host "❌ 当前目录不是 Git 仓库！" -ForegroundColor Red
    exit 1
}

# 获取今天的日期（格式：YYYY-MM-DD）
$today = Get-Date -Format "yyyy-MM-dd"
$userName = git config user.name

Write-Host "📅 查询 $userName 今天 ($today) 的 Git 提交记录：" -ForegroundColor Green
Write-Host "--------------------------------------------------"

# 查询今天的提交
$logs = git log --since="$today 00:00:00" --until="$today 23:59:59" `
                --author="$userName" `
                --pretty=format:"%h - %s (%ad)" `
                --date=short

if ($logs) {
    $logs
} else {
    Write-Host "📭 未找到今天的提交记录。" -ForegroundColor Yellow
}
