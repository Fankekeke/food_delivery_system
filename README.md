![](https://img.shields.io/badge/License-MIT-blue.svg)

![](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)

![](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)

# 智能点餐配送系统 (Smart Ordering & Delivery System)

一款全链路、多角色的现代化餐饮O2O解决方案。系统深度整合了用户点餐、商家接单、骑手配送与平台运营四大核心场景，支持实时位置追踪、积分会员体系及社区互动，致力于打造高效、智能、有温度的本地生活服务生态。


#### 安装环境
**️ 注意**：请确保您的开发环境已安装以下依赖。

JAVA 环境 

Node.js环境 [https://nodejs.org/en/] 选择14.17

Yarn 打开cmd， 输入npm install -g yarn !!!必须安装完毕nodejs

Mysql 数据库 一定要把账户和密码记住

redis

Idea 编译器

WebStorm OR VScode 编译器

#### 采用技术及功能

后端：SpringBoot、MybatisPlus、MySQL、Redis、
前端：Vue、Apex、Antd、Axios

平台前端：vue(框架) + vuex(全局缓存) + rue-router(路由) + axios(请求插件) + apex(图表)  + antd-ui(ui组件)

平台后台：springboot(框架) + redis(缓存中间件) + shiro(权限中间件) + mybatisplus(orm) + restful风格接口 + mysql(数据库)

开发环境：windows10 or windows7 ， vscode or webstorm ， idea + lambok

---

## 核心特性

- **多角色权限隔离**：完美支持管理员、商家、配送员、用户四种身份，数据与操作严格隔离。
- **实时配送追踪**：集成地图服务，支持配送员位置实时上报与用户端可视化追踪。
- **完整交易闭环**：涵盖在线支付、会员折扣、运费动态计算、骑手/商家提现等财务模块。
- **商家独立工作台**：为入驻商家提供专属仪表盘、客户沟通及独立会员管理体系。
- **社区互动模块**：内置帖子发布、收藏、评论与消息回复功能，增强用户粘性与平台活跃度。

---

## ️ 功能模块概览

### ️ 1. 平台管理员 (Admin)

*全局掌控，精细化运营平台资源与数据。*

- **数据看板**：系统仪表盘，实时掌握平台核心运营指标。
- **商家与员工管理**：商家入驻审核、商家会员管理、内部员工权限分配。
- **餐品与分类管理**：全局餐品库维护、餐品类型字典配置。
- **用户与地址管理**：用户账户管理、用户收货地址库。
- **订单与评价监管**：全局订单流转监控、订单评价审核与处理。
- **财务与积分体系**：积分兑换规则配置、物品积分管理、会员积分发放、商家/骑手提现记录审核。
- **内容与公告**：平台公告管理、帖子列表审核、消息回复管理。
- **配送调度**：配送员实时位置监控与调度。

### 2. 商家端 (Merchant)

*独立经营，高效处理店内订单与客户关系。*

- **经营看板**：订单仪表盘，今日营收、待处理订单一目了然。
- **店铺管理**：商家信息维护、餐品上下架与库存管理。
- **订单处理**：订单接单、状态流转、订单评价查看与回复。
- **客户维系**：客户在线沟通、商家专属会员管理、会员积分运营。

### 3. 配送员端 (Rider)

*智能接单，清晰记录配送轨迹与收益。*

- **接单看板**：正在派送仪表盘，实时显示待取餐与配送中订单。
- **订单操作**：快捷接单、配送状态更新、历史配送记录查询。
- **个人中心**：个人信息维护、收益提现申请。

### 4. 用户端 (User)

*极致体验，从搜索到售后的一站式服务。*

- **发现与选购**：商家搜索、餐品浏览与收藏、在线结算支付。
- **智能计算**：自动匹配会员折扣、动态计算配送费用。
- **订单管理**：我的订单追踪、支付成功通知、订单评价。
- **社区互动**：发布/查看帖子、餐品收藏、讨论交流、消息回复。
- **客户服务**：商家在线沟通、收货地址管理。
- **个人中心**：账户注册/登录、积分兑换、兑换记录查询、个人信息维护。

---

## 项目结构

```text
pharmacy-dispensing-system/
├── backend/            # 后端 API 服务
│   ├── src/
│   │   ├── controllers/  # 控制器
│   │   ├── models/       # 数据模型
│   │   ├── routes/       # 路由
│   │   └── services/     # 业务逻辑
│   └── package.json
├── frontend/           # 前端 Web 应用
│   ├── src/
│   │   ├── views/      # 页面视图
│   │   ├── components/ # 公共组件
│   │   └── store/      # 状态管理
│   └── package.json
├── docs/               # 项目文档与 API 接口说明
├── LICENSE
└── README.md
```
---


#### 前台启动方式
安装所需文件 yarn install 
运行 yarn run dev

#### 默认后台账户密码
[管理员]
admin
123456

[商家]
shangjia
123456

[配送员]
fkkk
123456

[用户]
fank
123456

#### 项目截图

|  |  |
|---------------------|---------------------|
| ![](./assets/image%20(1).png) | ![](./assets/image%20(2).png) |
| ![](./assets/image%20(3).png) | ![](./assets/image%20(4).png) |
| ![](./assets/image%20(5).png) | ![](./assets/image%20(6).png) |
| ![](./assets/image%20(7).png) | ![](./assets/image%20(8).png) |
| ![](./assets/image%20(9).png) | ![](./assets/image%20(10).png) |
| ![](./assets/image%20(11).png) | ![](./assets/image%20(12).png) |
| ![](./assets/image%20(13).png) | ![](./assets/image%20(14).png) |
| ![](./assets/image%20(15).png) | ![](./assets/image%20(16).png) |
| ![](./assets/image%20(17).png) | ![](./assets/image%20(18).png) |
| ![](./assets/image%20(19).png) | ![](./assets/image%20(20).png) |
| ![](./assets/image%20(21).png) | ![](./assets/image%20(22).png) |
| ![](./assets/image%20(23).png) | ![](./assets/image%20(24).png) |
| ![](./assets/image%20(25).png) | ![](./assets/image%20(26).png) |

#### 演示视频

暂无

#### 获取方式

Email: fan1ke2ke@gmail.com

WeChat: `Storm_Berserker`

`附带部署与讲解服务，因为要恰饭资源非免费，伸手党勿扰，谢谢理解😭`

> 1.项目纯原创，不做二手贩子 2.一次购买终身有效 3.项目讲解持续到答辩结束 4.非常负责的答辩指导 5.**黑奴价格**

> 项目部署调试不好包退！功能逻辑没讲明白包退！

<div align=center><img align="center" src="./assets/product_word.png" alt="fankekeke" /></div>

#### 其它资源
[2026年-答辩顺利通过-客户评价🀄](https://berserker287.github.io/2026/06/29/2026%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2025年-答辩顺利通过-客户评价🍜](https://berserker287.github.io/2025/06/18/2025%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2024年-答辩顺利通过-客户评价👻](https://berserker287.github.io/2024/06/06/2024%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2023年-答辩顺利通过-客户评价🐢](https://berserker287.github.io/2023/06/14/2023%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2022年-答辩通过率100%-客户评价🐣](https://berserker287.github.io/2022/05/25/%E9%A1%B9%E7%9B%AE%E4%BA%A4%E6%98%93%E8%AE%B0%E5%BD%95/)

[毕业答辩导师提问的高频问题](https://berserker287.github.io/2023/06/13/%E6%AF%95%E4%B8%9A%E7%AD%94%E8%BE%A9%E5%AF%BC%E5%B8%88%E6%8F%90%E9%97%AE%E7%9A%84%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98/)

[50个高频答辩问题-技术篇](https://berserker287.github.io/2023/06/13/50%E4%B8%AA%E9%AB%98%E9%A2%91%E7%AD%94%E8%BE%A9%E9%97%AE%E9%A2%98-%E6%8A%80%E6%9C%AF%E7%AF%87/)

[计算机毕设答辩时都会问到哪些问题？](https://www.zhihu.com/question/31020988)

[计算机专业毕业答辩小tips](https://zhuanlan.zhihu.com/p/145911029)

#### 接JAVAWEB毕设，纯原创，价格公道，诚信第一

`网站建设、小程序、H5、APP、各种系统 选题+开题报告+任务书+程序定制+安装调试+项目讲解+论文+答辩PPT`

More info: [悲伤的橘子树](https://berserker287.github.io/)

<div align="center"><img align="center" src="./assets/chat_qr_code.png" alt="fankekeke" width=250 /></div>

<p><img align="center" src="./assets/wechat_group.png" alt="fankekeke" /></p>

**感谢查看**
