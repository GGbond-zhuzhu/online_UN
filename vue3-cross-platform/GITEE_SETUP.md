# Gitee 仓库配置指南

## SSH 公钥

您的 SSH 公钥已生成，请按照以下步骤添加到 Gitee：

1. 复制下面的公钥内容
2. 登录 Gitee，进入：设置 -> SSH公钥 -> 添加公钥
3. 粘贴公钥并保存

## 公钥内容

```
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCrZEPfu4ZIIcKDACoqItIJ0SKvFr8CeGS07o6prHYcdUNt0TazNb0rgX61z7ZSlKMqcNiuoUkFlRUF211Bc5GQBqUQ3QeGUKoE8tOq0JHZ4Q00S91k9Jvh2VMTugMZpSsL/Xnx6yl84V1hzRsUYoCtMfzjp+tQjW0V9OnmmQK3VrbGr13JchgwNCxCpKjwq9KxDmtzMidHKQ6ZoP5QfbxfHdDA9l24VJC43LQBMnfkdeGAMeG8j+G4mhWj/jI2+p2SuVcNSfZu6msfOKcf3ZGKUY8I08jLxTAnWgf18NMXmu63dKjL+CPNT8RjqHve7KdoCI/Z2l5/mSwT5qHfPVBoVcvR68R7P4gpJIDDzxXDQq/I+/6fPCUObE8GMwI3JE5GedmWtc5IqMgi3a4rki1yX7TIasq2nCz92Xz8fKSeshzttmutsMgsv3PqUefHDhMjwXz2wcwZLivGwWfIVlXKGOWqOVGZ0fhrl2kuntTotfXYYqaebGD4wckL7UQEsqk= 462764857@qq.com
```

## 推送代码

代码已提交到本地仓库，使用以下命令推送到 Gitee：

```bash
git push -u origin master
```

## 配置 Gitee Pages（让前端可以通过网址访问）

1. 进入 Gitee 仓库页面
2. 点击"服务" -> "Gitee Pages"
3. 选择分支：`master`
4. 选择目录：`packages/web/dist`（需要先构建前端）
5. 点击"启动"按钮

## 构建前端

在推送代码前，需要先构建前端：

```bash
cd packages/web
npm install
npm run build
```

构建完成后，dist 目录就是前端静态文件，可以用于 Gitee Pages。

