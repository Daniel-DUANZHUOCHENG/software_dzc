@echo off
echo.
echo ========================================
echo    Coze API 代理服务器启动脚本
echo ========================================
echo.

REM 检查Node.js是否安装
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Node.js
    echo 请先安装 Node.js: https://nodejs.org/
    pause
    exit /b 1
)

echo ✅ Node.js 已安装

REM 检查是否存在package.json，如果不存在则创建
if not exist package.json (
    echo 📦 创建 package.json...
    echo {> package.json
    echo   "name": "coze-proxy-server",>> package.json
    echo   "version": "1.0.0",>> package.json
    echo   "description": "Coze API代理服务器",>> package.json
    echo   "main": "coze-proxy-server.js",>> package.json
    echo   "scripts": {>> package.json
    echo     "start": "node coze-proxy-server.js">> package.json
    echo   },>> package.json
    echo   "dependencies": {>> package.json
    echo     "express": "^4.18.2",>> package.json
    echo     "cors": "^2.8.5">> package.json
    echo   }>> package.json
    echo }>> package.json
)

REM 检查依赖是否安装
if not exist node_modules (
    echo 📦 安装依赖包...
    npm install
    if %errorlevel% neq 0 (
        echo ❌ 依赖安装失败
        pause
        exit /b 1
    )
    echo ✅ 依赖安装完成
)

echo.
echo 🚀 启动代理服务器...
echo.
echo 📋 使用说明:
echo    - 代理服务器地址: http://localhost:9049
echo    - 按 Ctrl+C 停止服务器
echo    - 保持此窗口打开以维持服务运行
echo.
echo ========================================
echo.

REM 启动服务器
node coze-proxy-server.js

echo.
echo 🛑 代理服务器已停止
pause 