# 在Spring Boot中实现自定义参数解析器

>在Spring Boot应用程序中，处理请求时经常需要解析并处理传入的参数。**Spring MVC提供了强大的参数解析机制**，但有时候我们可能需要定制自己的参数解析器以满足特定的需求。**本文将介绍如何在Spring Boot中自定义参数解析器**。

![](https://files.mdnice.com/user/7954/9d8f0de6-2eba-47a3-9eac-66cf182d8a78.png)




##  1.参数解析器介绍

参数解析器属于spring-web包中提供的组件，springmvc框架中对应提供了很多参数解析器。例如我们开发的Controller代码如下：
```java
@PostMapping("/save")
//此处request对象就是通过Springmvc提供的参数解析器帮我们注入的
public String saveTutorial(HttpServletRequest request){
    return "success";
}

```

在上面的`saveTutorial`方法中，我们声明了一个类型为`HttpServletRequest`的参数，这个对象就是通过springmvc提供的`ServletRequestMethodArgumentResolver`这个参数解析器帮我们注入的。**同样如果我们需要使用HttpServletResponse对象，也可以直接在方法上加入这个参数即可，此时springmvc会通过ServletResponseMethodArgumentResolver这个参数解析器帮我们注入**。

在项目开发中我们也可以根据需要自定义参数解析器，需要实现`HandlerMethodArgumentResolver`接口：
```java
public interface HandlerMethodArgumentResolver {
    boolean supportsParameter(MethodParameter var1);

    @Nullable
    Object resolveArgument(MethodParameter var1, 
                            @Nullable ModelAndViewContainer var2, 
                            NativeWebRequest var3, 
                            @Nullable WebDataBinderFactory var4) throws Exception;
}

```
可以看到此接口包含两个接口方法：`supportsParameter`和`resolveArgument`。

supportsParameter方法:，我们可以判断是否支持解析特定类型的参数。
当`supportsParameter`方法返回true时，才会调用`resolveArgument`方法。`resolveArgument`方法可以解析HTTP请求中的JSON数据。

## 2.参数解析器入门案例
本案例要实现的功能为：修改前端传递到在Controller的方法参数类型为`Tutorial`值
项目结果如下:

![](https://files.mdnice.com/user/7954/281fca58-b071-4d50-b2b8-34e29cfb744c.png)

### 【步骤一】:创建maven工程spring-boot-parse-master并配置pom.xml文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <artifactId>spring-boot-starter-parent</artifactId>
        <groupId>org.springframework.boot</groupId>
        <version>2.7.15</version>
    </parent>


    <groupId>com.zbbmeta</groupId>
    <artifactId>spring-boot-parse-master</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>


        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.20</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
```

### 【步骤二】:创建application.yml
```yml
server:
  port: 8899
```

### 【步骤三】:创建Tutorial
```java
@Data
public class Tutorial implements Serializable {
    private Long id;

    private String title;

    private String description;

    private Integer published;

    private static final long serialVersionUID = 1L;
}
```
### 【步骤四】:创建TutorialController
```java

@RestController
public class TutorialController {




    @PostMapping("/save")
    //此处request对象就是通过Springmvc提供的参数解析器帮我们注入的
    public String saveTutorial(HttpServletRequest request){
        return "success";
    }



    @GetMapping("/tutorial")
    public Tutorial getTutorial(Tutorial tutorial){


        return tutorial;
    }
}
```
### 【步骤五】:创建启动类
```java
@SpringBootApplication
public class ParseMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParseMasterApplication.class,args);
    }
}
```

此时可以启动项目并使用PostMan发送请求访问：http://localhost:8899/tutorial


![](https://files.mdnice.com/user/7954/7c50636f-06b9-4a1e-83fe-4901051b8538.png)
可以发现虽然能够访问成功，但是`Tutorial`对象的属性都是空的。

### 【步骤六】:创建参数解析器类，需要实现HandlerMethodArgumentResolver接口
```java
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //如果Controller的方法参数类型为Tutorial，则返回true
        return parameter.getParameterType().equals(Tutorial.class);


    }
//当supportsParameter方法返回true时执行此方法
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
     System.out.println("参数解析器...");
        Tutorial tutorial = new Tutorial();
        tutorial.setPublished(1);
        tutorial.setTitle("test");
        tutorial.setDescription("test");
        return tutorial;
    }
}

```

### 【步骤七】:创建配置类，用于注册自定义参数解析器
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    public CurrentUserMethodArgumentResolver getCurrentUserMethodArgumentResolver(){
        return new CurrentUserMethodArgumentResolver();
    }

    @Override
    //注册自定义参数解析器
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(getCurrentUserMethodArgumentResolver());
    }
}

```


重新启动项目访问，发现`tutorial`对象的属性已经有值了，这是因为在我们访问Controller的方法时Spring框架会调用我们自定义的参数解析器的`supportsParameter`方法来判断是否执行`resolveArgument`方法，如果Controller方法的参数类型为`Tutorial`则执行resolverArgument方法，此方法的返回结果将赋值给我们的Controller方法中声明的`tutorial`参数，即完成了参数绑定

![](https://files.mdnice.com/user/7954/f35f30f1-d36e-4984-b17f-a85d2c805ee0.png)

![](https://files.mdnice.com/user/7954/9d8f0de6-2eba-47a3-9eac-66cf182d8a78.png)