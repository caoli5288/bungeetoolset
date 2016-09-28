# 服务器列表
很惭愧，最近忙于一些私人的事情，只对mc界做了一些微小的贡献，而这个插件是其中微小的贡献之一。
当Bungee定义了数百上千个服务器的时候，管理服务器列表是一件十分麻烦且伤眼睛的事情，而这个插件提供了一种更为灵活的管理方式。

# 定义
插件启动时会创建`server.list.d`文件夹，请将一个或者多个定义文件放入该文件夹。定义文件分为本地和远程两种，所定义的服务器
无法作为默认服务器，将在蹦极启动以及`greload`指令执行时被加载。

## 本地定义
一个本地定义文件可以是以`.list`为后缀的任意文件，并遵守如下示例的格式。
```
server tntrun1 localhost 20001
server tntrun2 localhost 20002
#server tntrun3 localhost 20003 <- 该服务器被注释而无效
server tntrun4 localhost 20004 restricted <- 该服务器被定义成restricted
```

## 远程定义
远程定义提供了一种更为灵活可伸缩的管理方式，一个远程定义文件以`.remote`为后缀，并遵守如下示例的格式。
不推荐设置多个远程定义，因为一个远程定义意味着一个数据库连接，多个远程定义可能造成较慢的重载速度。
```
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://127.0.0.1/server_list
table=server_list
user=root
password=wowsuchpassword
```

特别的，请在你所定义的数据库里面手动创建你所定义的数据表，参考语句如下。栏位的意义应该不用多解释。
```SQL
CREATE TABLE `server_list` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(32) NOT NULL,
  `host` char(15) NOT NULL,
  `port` smallint(5) unsigned NOT NULL,
  `restricted` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`)
)
```

你可以根据你的数据库实际情况调整数据类型，并且推荐使用一个自增的int类型的自增主键。
