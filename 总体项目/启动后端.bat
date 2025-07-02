@echo off
chcp 65001
echo.
echo ======================================
echo    OAA系统后端服务启动脚本
echo ======================================
echo.
echo 正在启动后端服务...
echo.

cd /d "%~dp0\后端\OAASystem\OAASystem"

echo 当前目录: %CD%
echo.

if not exist "pom.xml" (
    echo ❌ 错误：找不到pom.xml文件，请确认当前目录正确
    echo 预期路径：总体项目\后端\OAASystem\OAASystem\
    pause
    exit /b 1
)

echo ✅ 找到Maven项目文件
echo.
echo 🚀 启动Spring Boot应用...
echo 💡 提示：启动完成后，服务将运行在 http://localhost:9049
echo 💡 提示：按 Ctrl+C 可以停止服务
echo.

mvn spring-boot:run

echo.
echo 服务已停止
pause 