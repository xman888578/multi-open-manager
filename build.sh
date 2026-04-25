#!/bin/bash

# 🍀 多开管理器 - 编译脚本
# 需要在有 Android SDK 的电脑上运行

set -e

echo "🍀 开始编译多开管理器..."

# 检查 Gradle
if ! command -v gradle &> /dev/null; then
    echo "⚠️ 未检测到 Gradle，尝试使用 gradlew..."
    if [ -f "./gradlew" ]; then
        ./gradlew assembleDebug
    else
        echo "❌ 请安装 Gradle 或使用 Android Studio 编译"
        exit 1
    fi
else
    gradle assembleDebug
fi

echo ""
echo "✅ 编译完成！"
echo "📦 APK 位置：app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "下一步："
echo "1. 将 APK 传到小米 17 Pro Max"
echo "2. 安装并授予必要权限"
echo "3. 创建工作资料空间"
echo ""