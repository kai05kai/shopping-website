测试账号
username:user1
password:user123

username:user2
password:user456

username:seller1
password:seller123

username:seller2
password:seller456

```
shopping-website
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
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
   │     │  │     ├─ default.jpg
   │     │  │     └─ product_9.jpg
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