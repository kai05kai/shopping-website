# 购物网站项目 (Shopping Website)

## 快速开始
在线演示
应用地址: http://8.134.253.254:8080
```bash
 git clone https://github.com/kai05kai/shopping-website.git
cd shopping-website
```

## 项目简介
这是一个基于Spring Boot 3.1.5构建的简单购物网站项目，实现了用户认证授权、商品管理、购物车等核心电商功能。项目采用分层架构设计，使用Spring Security进行权限控制，Thymeleaf作为模板引擎，MySQL/MariaDB作为数据库。

## 使用说明
点击左上角logo可返回主页
用户:
可以添加商品到购物车，
光标移动到右上角用户名可以查看购物车和个人资料
在购物车页面可以增减商品数量，结算商品只是模拟运行，商品库存数量会减少，无其他影响
个人资料页面，可以更改个人资料，修改密码，更改头像

商家:
光标移动到右上角用户名可以查看购物车、个人资料、商家后台
个人资料页面也可以查看商品重定向到后台
商品后台可以增加、删除、修改商品内容

## 技术栈
- **后端框架**: Spring Boot 3.1.5
- **安全框架**: Spring Security 6
- **数据库**: MySQL / MariaDB
- **ORM框架**: Spring Data JPA
- **模板引擎**: Thymeleaf
- **前端框架**: Bootstrap 5
- **构建工具**: Maven
- **Java版本**: JDK 17+
- **其他依赖**: Lombok、Validation、DevTools

## 功能特性

### 用户管理
- 用户注册、登录、注销
- 个人资料编辑
- 密码修改
- 多角色权限控制（用户、商家、管理员）

### 商品管理
- 商品列表展示
- 商品搜索功能
- 商家可添加、编辑、删除商品
- 商品图片上传与管理

### 购物车功能
- 添加商品到购物车
- 查看购物车
- 修改购物车商品数量
- 结算功能（模拟）

### 权限控制
- 基于角色的访问控制
- 普通用户：浏览商品、管理购物车
- 商家：管理自己的商品
- 管理员：系统管理

### 项目核心目录结构

- **项目根目录 (shopping-website/)**
  - `pom.xml` - Maven项目配置文件
  - `README.md` - 项目说明文档
  - `LICENSE` - MIT许可证文件
  
- **源代码目录 (src/main/java/com/example/shopping_website/)**
  - `config/` - 配置类
    - `SecurityConfig.java` - Spring Security安全配置
    - `DataInitializer.java` - 数据初始化
    - `WebConfig.java` - Web配置
  - `controller/` - 控制器层
    - `AuthController.java` - 用户认证
    - `ProductController.java` - 商品管理
    - `CartController.java` - 购物车管理
  - `dto/` - 数据传输对象
  - `model/` - 实体类
  - `repository/` - 数据访问层
  - `service/` - 业务逻辑层
  - `util/` - 工具类
  - `ShoppingWebsiteApplication.java` - 应用启动类
  
- **资源目录 (src/main/resources/)**
  - `application.properties` - Spring Boot配置文件
  - `templates/` - Thymeleaf模板
    - `auth/` - 认证相关页面
    - `product/` - 商品相关页面
    - `cart/` - 购物车页面
  - `static/` - 静态资源
    - `css/` - 样式文件
    - `js/` - JavaScript文件
    - `product_images/` - 商品图片
    - `user/` - 用户相关资源

## 系统架构
┌─────────────────────────────────────────────────────────┐
│ 表示层 (Presentation) │
│ Thymeleaf模板 + Bootstrap 5 │
├─────────────────────────────────────────────────────────┤
│ 控制器层 (Controller) │
│ Spring MVC Controllers │
├─────────────────────────────────────────────────────────┤
│ 业务逻辑层 (Service) │
│ Spring Service Components │
├─────────────────────────────────────────────────────────┤
│ 数据访问层 (Repository) │
│ Spring Data JPA Repositories │
├─────────────────────────────────────────────────────────┤
│ 数据存储层 (Database) │
│ MySQL/MariaDB + JPA │
└─────────────────────────────────────────────────────────┘

## 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+ 或 MariaDB 10.5+


## 测试账户
商家1：seller1 / seller123
商家2：seller2 / seller456
用户1：user1 / user123
用户2：user2 / user456


## 项目完整结构
```
shopping-website
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ LICENSE
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ README.md
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ example
   │  │        └─ shopping_website
   │  │           ├─ config
   │  │           │  ├─ DataInitializer.java
   │  │           │  ├─ SecurityConfig.java
   │  │           │  └─ WebConfig.java
   │  │           ├─ controller
   │  │           │  ├─ AuthController.java
   │  │           │  ├─ CartController.java
   │  │           │  ├─ HomeController.java
   │  │           │  ├─ ProductController.java
   │  │           │  ├─ SellerController.java
   │  │           │  └─ TestController.java
   │  │           ├─ dto
   │  │           │  ├─ ChangePasswordDto.java
   │  │           │  ├─ UserRegistrationDto.java
   │  │           │  └─ UserUpdateDto.java
   │  │           ├─ model
   │  │           │  ├─ CartItem.java
   │  │           │  ├─ Permission.java
   │  │           │  ├─ Product.java
   │  │           │  ├─ Role.java
   │  │           │  └─ User.java
   │  │           ├─ repository
   │  │           │  ├─ CartItemRepository.java
   │  │           │  ├─ ProductRepository.java
   │  │           │  ├─ RoleRepository.java
   │  │           │  └─ UserRepository.java
   │  │           ├─ service
   │  │           │  ├─ CartService.java
   │  │           │  ├─ impl
   │  │           │  │  └─ UserServiceImpl.java
   │  │           │  ├─ ProductService.java
   │  │           │  ├─ UserDetailsServiceImpl.java
   │  │           │  └─ UserService.java
   │  │           ├─ ShoppingWebsiteApplication.java
   │  │           └─ util
   │  │              ├─ FileUploadUtil.java
   │  │              └─ ImageNameValidator.java
   │  └─ resources
   │     ├─ application.properties
   │     ├─ static
   │     │  ├─ css
   │     │  │  ├─ bootstrap.min.css
   │     │  │  └─ style.css
   │     │  ├─ js
   │     │  │  └─ bootstrap.bundle.min.js
   │     │  ├─ product_images
   │     │  │  └─ uploads
   │     │  │     └─ default.jpg
   │     │  └─ user
   │     │     └─ images
   │     │        └─ avatar.jpg
   │     └─ templates
   │        ├─ auth
   │        │  ├─ login.html
   │        │  └─ register.html
   │        ├─ cart
   │        │  └─ cart.html
   │        ├─ error
   │        │  └─ 403.html
   │        ├─ fragments
   │        │  ├─ footer.html
   │        │  ├─ head.html
   │        │  └─ navbar.html
   │        ├─ layout
   │        │  └─ base.html
   │        ├─ product
   │        │  ├─ add.html
   │        │  ├─ edit.html
   │        │  ├─ list.html
   │        │  └─ my-products.html
   │        ├─ test
   │        │  ├─ products.html
   │        │  └─ simple.html
   │        ├─ test-cart.html
   │        ├─ test.html
   │        └─ user
   │           ├─ change-password.html
   │           ├─ profile-edit.html
   │           └─ profile.html
   └─ test
      └─ java
         └─ com
            └─ example
               └─ shopping_website

```

## 开发者信息
学号: 202330450531
姓名: 胡泓鑫
课程: 《网络应用》课程作业

## 版权声明
本购物网站系统为《[网络应用]》课程作业项目。
遵循 MIT 开源许可证，允许自由使用、修改、分发。

仅供学习交流，不用于商业用途。