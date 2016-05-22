# LaunchRedPacket
一个会发红包的android控件
[地址](http://blog.csdn.net/xuan_xiaofeng/article/details/50586848)

###
#包结构(app module下)
```
-core(存放全局缓存，全局http，全局mvp基类)  
  -cache(全局缓存)
  -http (全局http)
  -mvp  (mvp相关基类)
  -application
-login(业务模块-登录，其他业务模块类似划分)
  -bean(实体类)
  -http(跟http即网络请求相关的)
  -dao(数据库增删改查)
  -service(复杂业务可以加一层service，抽出presenter中的复杂逻辑)
  -mvp(存放mvp相关的类，如LoginActivity, LoginContract, LoginPresenter...)
-main(业务模块-主页，其他业务模块类似划分)
  -bean(实体类)
  -http(跟http即网络请求相关的)
  -dao(数据库增删改查)
  -service(复杂业务可以加一层service，抽出presenter中的复杂逻辑)
  -mvp(存放mvp相关的类，如MainActivity, MainContract, MainPresenter...)
-walllet(业务模块-钱包，其他业务模块类似划分)
  -bean(实体类)
  -http(跟http即网络请求相关的)
  -dao(数据库增删改查)
  -service(复杂业务可以加一层service，抽出presenter中的复杂逻辑)
  -mvp(存放mvp相关的类，如wallletActivity, wallletContract, wallletPresenter...)
...(其他业务模块的包划分类似)
```
***
