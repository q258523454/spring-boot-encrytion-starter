喜欢的点个star ⭐️⭐️⭐️
## 介绍
 SpringBoot控制器统一的响应体加密与请求体解密的注解处理方式，支持MD5/SHA/AES/DES/RSA。
## 加密解密支持
- 可进行加密的方式有：
    - - [x] MD5
    - - [x] SHA
    - - [x] AES
    - - [x] DES
    - - [ ] RSA (下个版本)
- 可进行解密的方式有：
    - - [x] AES
    - - [x] DES
    - - [ ] RSA (下个版本)
## 使用方法
#### 第一步:引入依赖
- 在`pom.xml`中引入依赖：
```xml
   <dependency>
        <groupId>LJ08</groupId>
        <artifactId>encrypt</artifactId>
        <version>1.0.0-RELEASE</version>
    </dependency>
```
- 在工程对应的`Application`类中增加@EnableEncryptBody注解 
```java
@SpringBootApplication
@EnableEncryptBody
public class SpringEncryptUseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringEncryptUseApplication.class, args);
    }
}
```
#### 第二步:参数配置
在项目的`application.yml`或`application.properties`文件中进行参数配置，例如：
```yaml
base:
  encrypt:
    aes-key: 'bfcs1ba4ds059352e135881ffxde211b' #AES密钥
    des-key: '12345678' # DES密钥
    open: true   # 开关
    debug: true  # 是否打印日志
```

#### 第三步:对控制器响应体进行加解密
```java
@RestController
//@DecryptBody(value = DecryptEnum.AES) 可以直接加到类上
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @PostMapping(value = "/encrypt")
    @ResponseBody
    @EncryptBody(value = EncryptEnum.AES, key = "bfcf1ba4d7059352613588effbfe8f0b")
    public String aes(HttpServletRequest request, @RequestBody String s) {
        logger.info("header key:" + request.getHeader("key"));
        logger.info("params:" + JSON.toJSONString(request.getParameterMap()));
        return s;
    }

    @GetMapping(value = "/decrypt")
    @DecryptBody(value = DecryptEnum.AES, key = "bfcf1ba4d7059352613588effbfe8f0b")
    // 解密串必须是 @RequestBody
    public String decrypt(HttpServletRequest request, @RequestBody String s) {
        logger.info("header key:" + request.getHeader("key"));
        logger.info("params:" + JSONObject.toJSONString(request.getParameterMap()));
        logger.info("body:" + s);
        BodyObject bodyObject = JSONObject.parseObject(s, BodyObject.class);
        logger.info("student is:" + JSON.toJSONString(bodyObject));
        return s;
    }

    @GetMapping(value = "/md5")
    @EncryptBody(value = EncryptEnum.MD5)
    public String md5() {
        return "zhang";
    }

}

```
