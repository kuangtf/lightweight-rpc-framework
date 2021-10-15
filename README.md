# lightweight-rpc-framework



## 基本结构

![](https://gitee.com/kuangtf/blogImage/raw/master/img/rpc.jpg)

RPC 框架包含三个最重要的组件，分别是客户端、服务端和注册中心。在一次 RPC 调用流程中，这三个组件是这样交互的：

- 服务端在启动后，会将它提供的服务列表发布到注册中心，客户端向注册中心订阅服务地址；
- 客户端会通过本地代理模块 Proxy 调用服务端，Proxy 模块收到负责将方法、参数等数据转化成网络字节流；
- 客户端从服务列表中选取其中一个的服务地址，并将数据通过网络发送给服务端；
- 服务端接收到数据后进行解码，得到请求信息；
- 服务端根据解码后的请求信息调用对应的服务，然后将调用结果返回给客户端。


## 模块依赖

使用maven聚合工程

- lightweight-rpc-framework，父工程 
- consumer，服务消费者，是lightweight-rpc-framework的子工程，依赖于rpc-client-spring-boot-starter。
- provider，服务提供者，是lightweight-rpc-framework的子工程，依赖于rpc-server-spring-boot-starter。
- provider-api，服务提供者暴露的服务API，是lightweight-rpc-framework的子工程。
- rpc-client-spring-boot-starter，rpc客户端starter，封装客户端发起的请求过程（动态代理、网络通信）。
- rpc-core，rpc核心依赖，负载均衡策略、消息协议、协议编解码、序列化、请求响应实体、服务注册发现。
- rpc-server-spring-boot-starter，rpc服务端starter，负责发布 rpc 服务，接收和处理 rpc 请求，反射调用服务端。

## 如何使用
由上面的模块依赖可以知道 rpc 框架主要是就是以 rpc 开头的这几个模块，在使用的时候
- 消费者（consumer）需要依赖 `rpc-client-spring-boot-starter`。
- 服务提供者需要依赖 `rpc-server-spring-boot-starter`。这样基本就可以了，因为使用了spring boot自动配置，所以消费者和提供者启动的时候都会去加载starter里的spring.factories文件，会自动将需要的bean自动装配到IOC容器中。
- 注册中心使用 Zookeeper
- 消费者和服务提供者需要配置注册中心的地址（默认127.0.0.1:2181）以及服务启动端口，服务提供者还需要配置 rpc 监听端口。

## 发布服务和消费服务
对于发布的服务需要使用 @RpcService 注解标识，复合注解，基于 @Service

```java
@RpcService(interfaceType = HelloWordService.class, version = "1.0")
public class HelloWordServiceImpl implements HelloWordService {

    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, rpc 调用成功", name);
    }
}
```

费服务需要使用 @RpcAutowired 注解标识，复合注解，基于 @Autowired

```java
 @RpcAutowired(version = "1.0")
 private HelloWordService helloWordService;
```

## 项目中的组件
### 1.动态代理

基于jdk接口的动态代理，客户端不能切换（`rpc-client-spring-boot-starter`模块 proxy 包）原理是服务消费者启动的时候有个 `RpcClientProcessor` bean 的后置处理器，会扫描ioc容器中的bean,如果这个bean有属性被 @RpcAutowired 修饰，就给属性动态赋代理对象。

### 2.服务注册发现

本项目使用 zk 做的，实现在 `rpc-core` 模块，`com.ktf.rpc.core.discovery` 包下面是服务发现，`com.ktf.rpc.core.register` 包下面是服务注册。 服务提供者启动后，`RpcServerProvider` 会获取到被 @RpcService 修饰的bean，将服务元数据注册到zk上。  

### 3.负载均衡策略

负载均衡定义在`rpc-core`中，目前支持轮询（FullRoundBalance）和随机（RandomBalance），默认使用随机策略。由`rpc-client-spring-boot-starter`指定。

```java
 @Primary
 @Bean(name = "loadBalance")
 @ConditionalOnMissingBean
 @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "randomBalance", matchIfMissing = true)
 public LoadBalance randomBalance() {
     return new RandomBalance();
 }

 @Bean(name = "loadBalance")
 @ConditionalOnMissingBean
 @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "fullRoundBalance")
 public LoadBalance loadBalance() {
     return new FullRoundBalance();
 } 
```
可以在消费者中配置 `rpc.client.balance=fullRoundBalance` 替换，也可以自己定义，通过实现接口 `LoadBalance`，并将创建的类加入IOC容器即可。
```java
@Slf4j
@Component
public class FirstLoadBalance implements LoadBalance {

    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        log.info("---------FirstLoadBalance-----------------");
        return services.get(0);
    }
}
```

### 4.自定义消息协议、编解码。 

所谓协议，就是通信双方事先商量好规则，服务端知道发送过来的数据将如何解析。 

#### 4.1自定义消息协议 

+----------------------------------------------------------------------------+ 
| 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte | 
+----------------------------------------------------------------------------+ 
|       状态 1byte     |        消息 ID 32byte     |      数据长度 4byte         |
+----------------------------------------------------------------------------+ 
|                                   数据内容 （长度不定）                                     | 
+----------------------------------------------------------------------------+ 

 - 魔数：魔数是通信双方协商的一个暗号，通常采用固定的几个字节表示。魔数的作用是防止任何人随便向服务器的端口上发送数据。
      - 例如 java Class 文件开头就存储了魔数 0xCAFEBABE，在加载 Class 文件时首先会验证魔数的正确性
 - 协议版本号：随着业务需求的变化，协议可能需要对结构或字段进行改动，不同版本的协议对应的解析方法也是不同的。
 - 序列化算法：序列化算法字段表示数据发送方应该采用何种方法将请求的对象转化为二进制，以及如何再将二进制转化为对象，如 Protostuff、Kyro JSON、Hessian、Java 自带序列化等。
 - 报文类型： 在不同的业务场景中，报文可能存在不同的类型。RPC 框架中有请求、响应、心跳等类型的报文。
 - 状态： 状态字段用于标识请求是否正常（SUCCESS、FAIL）。
 - 消息ID： 请求唯一ID，通过这个请求ID将响应关联起来，也可以通过请求ID做链路追踪。
 - 数据长度： 标明数据的长度，用于判断是否是一个完整的数据包
 - 数据内容： 请求体内容

#### 4.2 编解码

编解码实现在 `rpc-core` 模块，在包 `com.ktf.rpc.core.codec`下。

**如何实现编解码？** 

- 编码利用 netty 的 MessageToByteEncoder 类实现。实现 encode 方法，MessageToByteEncoder 继承 ChannelOutboundHandlerAdapter 。 编码就是将请求数据写入到 ByteBuf 中。

- 解码是利用 netty 的 ByteToMessageDecoder 类实现。 实现 decode 方法，ByteToMessageDecoder 继承 ChannelInboundHandlerAdapter。 解码就是将 ByteBuf 中数据解析出请求的数据。 解码要注意 TCP 粘包和拆包问题。

**什么是TCP粘包和拆包问题？** 

- TCP 传输协议是面向流的，没有数据包界限，也就是说消息无边界。客户端向服务端发送数据时，可能将一个完整的报文拆分成多个小报文进行发送，也可能将多个报文合并成一个大的报文进行发送。 因此就有了拆包和粘包。在网络通信的过程中，每次可以发送的数据包大小是受多种因素限制的，如 MTU 传输单元大小、滑动窗口等。 所以如果一次传输的网络包数据大小超过传输单元大小，那么我们的数据可能会拆分为多个数据包发送出去。如果每次请求的网络包数据都很小，比如一共请求了 10000 次，TCP 并不会分别发送 10000 次。 TCP采用的 Nagle（批量发送，主要用于解决频繁发送小数据包而带来的网络拥塞问题） 算法对此作出了优化。

所以，网络传输会出现这样： 
<img src="https://gitee.com/kuangtf/blogImage/raw/master/img/tcp_package.png" style="zoom: 67%;" />

1. 服务端恰巧读到了两个完整的数据包 A 和 B，没有出现拆包/粘包问题；
2.  服务端接收到 A 和 B 粘在一起的数据包，服务端需要解析出 A 和 B； 
3. 服务端收到完整的 A 和 B 的一部分数据包 B-1，服务端需要解析出完整的 A，并等待读取完整的 B 数据包；
4.  服务端接收到 A 的一部分数据包 A-1，此时需要等待接收到完整的 A 数据包； 
5. 数据包 A 较大，服务端需要多次才可以接收完数据包 A。 

**如何解决？**

解决问题的根本手段：找出消息的边界：
- 消息长度固定 
    每个数据报文都需要一个固定的长度。当接收方累计读取到固定长度的报文后，就认为已经获得一个完整的消息。当发送方的数据小于固定长度时，则需要空位补齐。消息定长法使用非常简单，但是缺点也非常明显，无法很好设定固定长度的值，如果长度太大会造成字节浪费，长度太小又会影响消息传输，所以在一般情况下消息定长法不会被采用。
- 特定分隔符 
    在每次发送报文的尾部加上特定分隔符，接收方就可以根据特殊分隔符进行消息拆分。分隔符的选择一定要避免和消息体中字符相同，以免冲突。否则可能出现错误的消息拆分。比较推荐的做法是将消息进行编码，例如base64 编码，然后可以选择 64 个编码字符之外的字符作为特定分隔符
- 消息长度 + 消息内容 
    消息长度 + 消息内容是项目开发中最常用的一种协议，接收方根据消息长度来读取消息内容。    

<img src="https://gitee.com/kuangtf/blogImage/raw/master/img/tcpStickybagUnpacking.png" style="zoom: 60%;" />

本项目如何解决的？

- 使用的是**消息长度 + 消息内容**的形式。在解码器 RpcDecoder 中读取固定长度数据。

### 5.序列化和反序列化 

序列化和反序列化在 `rpc-core` 模块 `com.rrtv.rpc.core.serialization` 包下，提供了`HessianSerialization` 、 `JsonSerialization`、`ProtostuffSerialization`、`KryoSerialization` 序列化。 默认使用 `ProtostuffSerialization` 序列化，用户不可以自定义。

```java
  public static SerializationTypeEnum parseByName(String typeName) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.name().equalsIgnoreCase(typeName)) {
                return typeEnum;
            }
        }
        return PROTOSTUFF;
    }

    public static SerializationTypeEnum parseByType(byte type) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return PROTOSTUFF;
    }
```

序列化性能：
- 空间上 
    <img src="https://gitee.com/kuangtf/blogImage/raw/master/img/serialization_space.png" style="zoom: 67%;" />

- 时间上 
    <img src="https://gitee.com/kuangtf/blogImage/raw/master/img/serialization_time.png" style="zoom:67%;" />

### 6.网络传输，使用netty 

netty 代码固定的，值得注意的是 handler 的顺序不能弄错，编码是出站操作（可以放在入站后面），解码和收到响应都是入站操作，解码要在前面。

```java
bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
.handler(new ChannelInitializer<SocketChannel>() {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
            // 编码 是出站操作 将消息编写二进制
            .addLast(new RpcEncoder<>())
            // 解码 是入站操作 将二进制解码成消息
            .addLast(new RpcDecoder())
            // 接收响应 入站操作
            .addLast(handler);
    }
});
```

### 7.RPC 调用方式 

成熟的 RPC 框架一般会提供四种调用方式，分别为同步 Sync、异步 Future、回调 Callback和单向 Oneway。

- Sync 同步调用

    客户端线程发起 RPC 调用后，当前线程会一直阻塞，直至服务端返回结果或者处理超时异常。
    ![](https://gitee.com/kuangtf/blogImage/raw/master/img/sync.png)

- Future 异步调用 
    客户端发起调用后不会再阻塞等待，而是拿到 RPC 框架返回的 Future 对象，调用结果会被服务端缓存，客户端自行决定后续何时获取返回结果，当客户端主动获取结果时，该过程是阻塞等待的。
    ![](https://gitee.com/kuangtf/blogImage/raw/master/img/future.png)
    
- Callback 回调调用
    客户端发起调用时，将 Callback 对象传递给 RPC 框架，无须同步等待返回结果，直接返回。当获取到服务端响应结果或者超时异常后，再执行用户注册的 Callback 回调。
    ![](https://gitee.com/kuangtf/blogImage/raw/master/img/callback.png)

- Oneway 单向调用
    客户端发起请求之后直接返回，忽略返回结果。
    ![](https://gitee.com/kuangtf/blogImage/raw/master/img/oneway.png)

> 本项目使用的是第一种：客户端同步调用，其他的没有实现。逻辑在 RpcFuture 中，使用 CountDownLatch 实现阻塞等待（超时等待）。

## 流程
服务提供者启动 

1. 服务提供者 provider 会依赖 rpc-server-spring-boot-starter 
2. ProviderApplication 启动，根据springboot 自动装配机制，RpcServerAutoConfiguration 自动配置生效 
3. RpcServerProvider 是一个bean后置处理器，会发布服务，将服务元数据注册到ZK上 
4. RpcServerProvider.run 方法会开启一个 netty 服务 

服务消费者启动 

1. 服务消费者 consumer 会依赖 rpc-client-spring-boot-starter 
2. ConsumerApplication 启动，根据springboot 自动装配机制，RpcClientAutoConfiguration 自动配置生效
3. 将服务发现、负载均衡、代理等bean加入IOC容器 
4. 后置处理器 RpcClientProcessor 会扫描 bean ,将被 @RpcAutowired 修饰的属性动态赋值为代理对象 

调用过程 
  1. 服务消费者 发起请求 http://localhost:9090/hello?name=hello 
  2. 服务消费者 调用 helloWordService.sayHello() 方法，会被代理到执ClientStubInvocationHandler.invoke()方法 
  3. 服务消费者 通过ZK服务发现获取服务元数据，找不到报错404 
  4. 服务消费者 自定义协议，封装请求头和请求体 
  5. 服务消费者 通过自定义编码器 RpcEncoder 将消息编码 
  6. 服务消费者 通过服务发现获取到服务提供者的ip和端口， 通过Netty网络传输层发起调用 
  7. 服务消费者 通过 RpcFuture 进入返回结果（超时）等待
  8. 服务提供者 收到消费者请求
  9. 服务提供者 将消息通过自定义解码器 RpcDecoder 解码 
  10. 服务提供者 解码之后的数据发送到 RpcRequestHandler 中进行处理，通过反射调用执行服务端本地方法并获取结果
  11. 服务提供者 将执行的结果通过 编码器 RpcEncoder 将消息编码。（由于请求和响应的协议是一样，所以编码器和解码器可以用一套） 
  13. 服务消费者 将消息通过自定义解码器 RpcDecoder 解码
  14. 服务消费者 通过RpcResponseHandler将消息写入 请求和响应 池中，并设置 RpcFuture 的响应结果
  15. 服务消费者 获取到结果 

## 环境搭建

- 操作系统：Windows
- 集成开发工具：IntelliJ IDEA
- 项目技术栈：SpringBoot 2.5.2 + JDK 1.8 + Netty 4.1.42.Final
- 项目依赖管理工具：Maven 4.0.0
- 注册中心：Zookeeeper 3.7.0

## 项目测试

- 启动 Zookeeper 服务器：bin/zkServer.cmd
- 启动 provider 模块 ProviderApplication
- 启动 consumer 模块 ConsumerApplication
- 测试：浏览器输入 http://localhost:9090/hello?name=hello，成功返回 您好：hello, rpc 调用成功



> 参考：来源不明，如有侵权，请联系作者删除！