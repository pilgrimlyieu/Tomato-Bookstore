# 番茄商城项目贡献指南

本文档旨在帮助团队成员了解如何参与番茄商城项目的开发工作。

> 仅作为开发前的技术准备和规范参考，不代表最终的开发规范与技术实现。

## 项目架构概览

番茄商城是一个线上图书销售平台，采用前后端分离架构：

- **前端**：Vue 3 + TypeScript + Bun
- **后端**：Spring Boot + Java + MySQL

项目结构：
```
TomatoBookstore/
├── client/      # 前端代码
├── server/      # 后端代码
├── docs/        # 项目文档
└── ...
```

详细架构信息请参阅[前端开发指南](docs/guide/frontend-guide.md)和[后端开发指南](docs/guide/backend-guide.md)。

## 开发环境设置

在开始开发前，请确保已安装以下工具：

- [Node.js](https://nodejs.org/) (v22+)
- [Bun](https://bun.sh/) (v1.0+)
- [OpenJDK](https://jdk.java.net/archive/) (v21)
- [MySQL](https://www.mysql.com/) (v9.0+)

详细的环境设置步骤请查看[环境设置指南](docs/guide/environment-setup.md)。

## Git 工作流程

我们采用基于 Feature Branch 的 Git 工作流程。详细的 Git 操作指南请查看 [Git 工作流指南](docs/guide/git-guide.md)。

## 文档编写指南

项目文档采用 Markdown/Typst 格式编写。详细的文档编写规范请查看[文档编写指南](docs/guide/documentation-guide.md)。

## 前端开发指南

前端开发规范、组件设计模式及最佳实践请查看[前端开发指南](docs/guide/frontend-guide.md)。

## 后端开发指南

后端架构、API 设计规范及数据库操作规范请查看[后端开发指南](docs/guide/backend-guide.md)。

## 代码审查流程

所有代码提交都需要经过代码审查才能合并到主分支。详细的代码审查流程请查看[代码审查指南](docs/guide/code-review-guide.md)。

## 测试指南

项目的单元测试、集成测试和端到端测试规范请查看[测试指南](docs/guide/testing-guide.md)。

## 问题反馈与帮助

如有任何问题，请在 Issue 提出。
