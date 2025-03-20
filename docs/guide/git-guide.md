# Git 工作流指南

本文档详细介绍了番茄商城项目的 Git 工作流程和规范。

## 基本概念

Git 是一个分布式版本控制系统，用于跟踪源代码在开发过程中的变化。

**核心概念**：
- **仓库（Repository）**：存储项目代码和版本历史的地方
- **分支（Branch）**：代码的独立开发线
- **提交（Commit）**：对代码进行一次更改并记录
- **推送（Push）**：将本地更改上传到远程仓库
- **拉取（Pull）**：从远程仓库获取更新
- **合并（Merge）**：将一个分支的更改集成到另一个分支
- **拉取请求（Pull Request）**：提出将你的更改集成到项目主分支的请求

## 安装与配置

### 安装 Git

1. 访问 [Git 官网](https://git-scm.com/) 下载适合你操作系统的 Git 安装包
2. 按照安装向导完成安装

### 初始配置

设置你的用户名和邮箱（这会出现在你的提交记录中）：

```bash
git config --global user.name "你的名字"
git config --global user.email "你的邮箱@example.com"
```

### 获取项目代码

克隆项目仓库到本地：

```bash
git clone <仓库URL>
cd TomatoBookstore
```

## 分支命名规范

我们使用以下分支命名规范：

- **主分支**：`main`
- **功能分支**：`feature/<功能名称>`（例如：`feature/user-login`）
- **修复分支**：`fix/<问题描述>`（例如：`fix/login-button-error`）
- **发布分支**：`release/v<版本号>`（例如：`release/v1.0.0`）

## 提交信息规范

良好的提交信息让代码变更历史更加清晰。提交信息应遵循以下格式（即[约定式提交](https://www.conventionalcommits.org/zh-hans/v1.0.0/)）：

```
<类型>(<范围>): <简短描述>

<详细描述（可选）>
```

**类型**:
- `feat`：新功能
- `fix`：修复 bug
- `docs`：文档更新
- `style`：代码格式调整（不影响代码运行的变动）
- `refactor`：代码重构（即不是新增功能，也不是修改 bug 的代码变动）
- `test`：添加或修改测试代码
- `chore`：构建过程或辅助工具的变动

`<范围>` 是可选的，可以是模块、组件、文件等。

**示例**：
```
feat: 添加用户登录功能

实现了用户登录表单和后端API验证逻辑
```

## 工作流程

我们采用基于 Feature Branch 的工作流程：

### 更新本地主分支

在开始新工作前，确保你的本地主分支是最新的：

```bash
$ git switch main
$ git pull origin main
```

### 创建新的功能分支

为你要开发的功能创建一个新分支：

```bash
$ git switch -c feature/your-feature-name
```

### 编写代码并提交

在你的分支上进行开发，完成后提交更改：

```bash
# 添加所有更改到暂存区
$ git add .

# 或者选择性添加特定文件
$ git add file1.js file2.js

# $ 提交更改
git commit -m "feat: 添加用户登录功能"
```

### 定期推送到远程

定期将你的更改推送到远程仓库，避免本地工作丢失：

```bash
git push origin feature/your-feature-name
```

### 创建 Pull Request

当功能开发完成后：

1. 访问项目的 GitHub 仓库页面
2. 点击 Compare & pull request 按钮
3. 填写 PR 描述，说明你的更改内容
4. 请求代码审查
5. 根据反馈进行调整

### 合并到主分支

一旦 PR 被批准，它将被合并到主分支。

## 常见问题与解决

### 合并冲突

当你的更改与主分支有冲突时：

```bash
# 首先更新主分支
$ git swtich main
$ git pull origin main

# 切回你的分支
$ git switch feature/your-feature-name

# 合并主分支更改
$ git merge main

# 解决冲突后
$ git add .
$ git commit -m "fix: 解决合并冲突"
$ git push origin feature/your-feature-name
```

### 撤销本地更改

如果你想放弃所有本地修改：

```bash
$ git restore .
```

### 撤销上一次提交

如果你需要修改最后一次提交：

```bash
# 修改最后一次提交（修改提交信息或添加更多修改）
$ git commit --amend

# 如果已经推送到远程，需要强制推送（慎用）
$ git push origin feature/your-feature-name --force
```

### 查看提交历史

查看提交历史以了解项目的变更：

```bash
# 查看简洁的提交历史
$ git log --oneline

# 查看详细的提交历史
$ git log
```

## 进阶 Git 技巧

### 暂存工作

如果你需要临时切换到另一个分支而不想提交当前更改：

```bash
# 暂存当前更改
$ git stash

# 切换分支并完成其他工作
$ git checkout other-branch
# 做一些其他工作…

# 回到原分支
$ git checkout feature/your-feature-name

# 恢复暂存的更改
$ git stash pop
```

### 交互式暂存

选择性地暂存文件的部分内容：

```bash
$ git add -p
```

这会进入交互模式，你可以逐块选择要添加的更改。

### Git GUI 工具

如果命令行操作不够直观，可以考虑使用 GUI 工具：
- VS Code 的 Git 集成
- JetBrains IDE 的 Git 集成
