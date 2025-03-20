# 文档编写指南

本指南旨在帮助团队成员了解如何编写和维护项目文档。良好的文档对于项目的可持续发展和新成员的加入至关重要。

## 文档类型

```
docs/
├── backend-guide.md
├── code-review-guide.md
├── documentation-guide.md
├── environment-setup.md
├── frontend-guide.md
├── git-guide.md
├── simplepaper.typ
├── start.public.md
├── target
│   └── use-case.pdf
├── testing-guide.md
├── use-case.private.md
└── use-case.typ
```


## 文档语言基础语法

### Markdown 语法

略，自行了解。

### Typst 语法

略，自行了解。

## 文档风格指南

为了保持文档一致性，请遵循以下风格指南：

1. **简洁明了**：使用简单、直接的语言表达
2. **结构化**：使用标题和子标题组织内容
3. **示例丰富**：提供代码示例和使用场景
4. **一致性**：保持术语和格式的一致性
5. **更新日期**：在文档开头注明最后更新日期

## 更新文档流程

文档的更新应遵循以下流程：

1. **创建分支**：从主分支创建一个新的文档更新分支
   ```bash
   $ git switch -c docs/update-description
   ```

2. **进行更改**：编辑或添加文档文件

3. **提交更改**：
   ```bash
   $ git add .
   $ git commit -m "docs: 更新项目描述文档"
   ```

4. **推送并创建 PR**：
   ```bash
   $ git push origin docs/update-description
   ```
   然后在 GitHub 上创建 Pull Request

5. **审查与合并**：文档更改也需要经过团队审查后才能合并
