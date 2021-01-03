# Easy Mapper Processor

--- 一个基于MyBatis的注解扩展 ---

## 目前支持的功能
* JSON字段映射，自动生成JSON字段TypeHandler。

## 使用手册
### 编译安装
下载源码后执行maven命令：
```shell script
mvn clean install
```

### 配置依赖
```xml
    <dependency>
        <groupId>com.iakuil</groupId>
        <artifactId>ez-mapper-processor</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```
### 配置注解参数
和Lombok等注解处理器类似的配置：
```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.iakuil</groupId>
                <artifactId>ez-mapper-processor</artifactId>
                <version>0.0.1-SNAPSHOT</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```
### 配置JSON字段
```java
@JsonEntity
public class Bar {
    private String name;
    private String addr;
    // getters and setters
}
```
重新编译后，target/classes目录下自动生成的TypeHandler：
```java
@MappedTypes({Bar.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class BarTypeHandler extends AbstractJsonTypeHandler<Bar> {
    public BarTypeHandler() {
    }
}
```
### MyBatis配置文件
```yaml
mybatis:
  type-handlers-package: com.yourdomain.sample.yourpackage
```

### 局限性
如果是多模块的Maven工程，整个工程中不能有包名+类名完全一致的JSON映射对象。  
比如：A模块有一个org.sample.Foo，B模块也有一个org.sample.Foo，并且两个类都添加了`@JsonEntity`注解，编译时就会报错：`Too many Classes: org.sample.Foo`。

-- THE END --