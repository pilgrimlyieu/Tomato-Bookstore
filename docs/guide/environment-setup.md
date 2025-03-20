# 环境设置指南

本文档详细介绍了番茄商城项目的开发环境设置步骤。为确保开发体验一致，请所有团队成员按照此指南配置环境。

## 前端环境设置

### 安装 Node.js

1. 访问 [Node.js 官网](https://nodejs.org/)，下载并安装 v22+ LTS 版本。
2. 安装完成后，在命令行验证安装：
   ```bash
   $ node --version
   v22.14.0
   ```

### 安装 Bun

[Bun](https://bun.sh/) 是我们项目使用的包管理器和运行时环境。

1. 使用以下命令安装 Bun：
   ```bash
   # Windows（通过 PowerShell）
   $ powershell -c "irm bun.sh/install.ps1 | iex"
   ```

2. 安装完成后，在命令行验证安装：
   ```bash
   $ bun --version
   1.2.5
   ```

### 前端项目设置

1. 克隆项目仓库后，进入前端目录：
   ```bash
   $ cd TomatoBookstore/client
   ```

2. 安装项目依赖：
   ```bash
   $ bun install
   ```

3. 启动开发服务器：
   ```bash
   $ bun dev
   # 或 bun run dev
   ```

## 后端环境设置

### 安装 JDK

#### Windows（使用 Scoop）

1. 安装 [Scoop](https://scoop.sh/)（Windows 包管理器）：
   - 打开 PowerShell 并运行：
   ```powershell
   # 设置 PowerShell 执行策略
   $ Set-ExecutionPolicy RemoteSigned -Scope CurrentUser

   # 安装 Scoop
   $ irm get.scoop.sh | iex
   ```

2. 使用 Scoop 安装 JDK 21：
   ```powershell
   # 添加 extras bucket
   $ scoop bucket add java

   # 安装 OpenJDK 21
   $ scoop install java/openjdk21
   ```

   Scoop 会自动设置必要的环境变量（`JAVA_HOME` 和 `PATH` 等）。

## 数据库设置

### 数据库设计

项目使用的主要表结构：

1. **用户表（users）**：存储用户信息
2. **书籍表（books）**：存储书籍信息
3. **分类表（categories）**：存储书籍分类
4. **订单表（orders）**：存储订单信息
5. **订单项表（order_items）**：存储订单中的商品
6. **购物车表（cart_items）**：存储用户购物车
