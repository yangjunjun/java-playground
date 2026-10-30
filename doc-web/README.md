# Java Playground 文档网站

直接读取仓库根目录与 `NN-topic` 文件夹中的 `README.md`、`readme.en.md`，生成中文、英文和中英对照的纯静态阅读页面。教程不需要复制或维护第二份。

## 本地使用

需要 Node.js 22 或更高版本。在仓库根目录执行：

```bash
cd doc-web
npm ci
npm test
npm run build
npm run preview
```

打开 http://localhost:4173 。产物在 `doc-web/dist`，可以部署到任意静态服务器；所有资源与页面均使用相对路径，支持 GitHub Pages 项目子目录。

## 自动部署到 GitHub Pages

仓库已提供 `.github/workflows/docs-pages.yml`：

1. 首次在 GitHub **Settings → Pages → Build and deployment → Source** 选择 **GitHub Actions**。
2. 此后每次推送到 `master` 都会执行安装依赖 → 测试 → 静态构建 → Pages 部署。
3. Pull Request 只测试和构建，不部署；Actions 页面也可以手动运行工作流。
4. 部署地址由 Actions 的 `github-pages` environment 显示，通常为 `https://yangjunjun.github.io/java-playground/`。

私有仓库使用 Pages 的资格及站点访问控制取决于 GitHub 账号套餐；私有仓库不代表 Pages 站点自动私有。工作流不会修改仓库可见性。只有在已确认需要的访问范围后才启用 Pages。

## 添加或更新教程

编辑原章节 Markdown 后推送即可自动重建。新增 `26-topic/README.md` 会自动加入导航，提供同目录 `readme.en.md` 即可支持英文。中英文保持相同标题层次时，双语以二级标题小节并排对照；小屏上下阅读。若两份结构不同则保留完整双栏原文，缺少英文时明确提示并显示中文，不会默默丢弃内容。

## 功能

- 章节目录、当前章节高亮、按中英文标题/编号筛选。
- 中文 / English / 中英对照；语言独立 URL，保留最近语言偏好。
- 文章二级/三级目录，滚动高亮；语言切换定位对应标题编号。
- Markdown 表格、语法高亮、代码复制、前后章和源文件链接。
- 无 CDN、无外部字体、无运行时 API；禁用 JS 仍可阅读与导航。
- 手机端折叠章节和页内目录；键盘焦点、跳转正文、打印样式。

## 文件

- `build.mjs`：发现源文档、解析与链接重写、三种模式 HTML。
- `assets/`：样式与小型增强脚本。
- `test/`：完整性、结构回退、链接及锚点验证。
- `SPEC.md`：需求、技术方案、验收结果。

正文来自仓库教程。本站展示原文，不重新评审或改写其中的 Java 技术结论。
