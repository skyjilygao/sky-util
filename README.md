# util
工具类
部分内容是根据网上内容进行整理

如何使用：直接依赖和添加jitpack.io仓库地址（参考demo-m3u8如何依赖）
参考：https://jitpack.io/#skyjilygao/sky-util/2.0.0

pom.xml依赖：

**依赖1：**
使用maven中央仓库的repositories
```xml
<dependencies>
    <dependency>
  	    <groupId>com.github.skyjilygao</groupId>
  	    <artifactId>sky-util</artifactId>
  	    <version>2.0.0</version>
    </dependency>
</dependencies>
```
如果以上依赖失败，可用参数下面的方法。

**依赖2：**
使用jitpack.io的repositories
```xml
<!-- 依赖视频转换工具类，需要加入jitpack.io的repositories -->

<dependencies>
    <dependency>
  	    <groupId>com.github.skyjilygao</groupId>
  	    <artifactId>sky-util</artifactId>
  	    <version>2.0.0</version>
    </dependency>
</dependencies>
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```
