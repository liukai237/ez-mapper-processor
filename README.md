# Easy Mybatis

-- 基于mybatis-spring-boot-starter的扩展

## 目前支持的功能
* JSON字段映射，自动生成JSON字段TypeHandler。

## 后续规划加入的内容
* 主键生成策略。
* 支持JPA注解，自动生成Mapper。
* 类似JPA的通用CRUD方法。
* 字段自动填充插件。
* 枚举字段映射。
* 逻辑删除。
* 乐观锁。
> PS. 分页可能直接采用PageHelper，不再重复造轮子。

## 使用手册
### 编译安装
下载源码后执行maven命令：
```shell script
mvn clean install
```
> 目前构件存放在私服，后续会上传到中央仓库。
### 配置依赖
```xml
    <dependency>
        <groupId>com.iakuil</groupId>
        <artifactId>ez-mapper-processor</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```
### 配置注解参数
```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
            <annotationProcessors>
                <annotationProcessor>
                    com.iakuil.ezm.processor.AnnotationProcessor
                </annotationProcessor>
            </annotationProcessors>
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
target/classes目录下自动生成的TypeHandler：
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
-- THE END --