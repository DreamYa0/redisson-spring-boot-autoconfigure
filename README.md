# redisson-spring-boot-starter

目前有很多项目还在使用jedis的 `setNx` 充当分布式锁,然而这个锁是有问题的,redisson是java支持redis的redlock的`唯一`实现, 集成该项目后只需要极少的配置.就能够使用redisson的全部功能. 目前支持 `集群模式`,`云托管模式`,`单Redis节点模式`,`哨兵模式`,`主从模式` 配置. 支持 `可重入锁`,`公平锁`,`联锁`,`红锁`,`读写锁` 锁定模式

## 介绍

1. 我们为什么需要`redisson`?

> `redisson`目前是官方唯一推荐的java版的分布式锁,他支持 `redlock`.具体请查看 [官方文档](https://redis.io/topics/distlock)

1. jedis为什么有问题?

> 目前jedis是只支持单机的.

> jedis setNx 和设置过期时间是不同步的,在某些极端的情况下会发生死锁.导致程序崩溃.如果没有设置value, 线程1可能会释放线程2的锁

## 软件架构

1. [redisson](https://github.com/redisson/redisson)
2. spring boot

## 安装教程

> .1. 引入 pom.xml

```
<dependency>
    <groupId>com.g7.framework</groupId>
    <artifactId>redisson-spring-boot-autoconfigure</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

1. 在 `apollo`配置中心 增加如下配置

```properties
#单Redis节点模式
spring.redisson.single.address=127.0.0.1:6379
spring.redisson.password=123456
```
2.  在Application启动类上加上 @EnableRedisson 注解来开启Redisson自动化配置组件

## 使用说明

### 如何使用`分布式锁`

在方法增加 `@DistributedLock` 注解

```
//支持 spel 表达式 如果后面需要接字符串的话请用`+`连接. 字符串一定要打`单引号`
@DistributedLock(keys = "#user.name+'locks'")
public String test(User user) {
    System.out.println("进来了test");
    return "test";
}
```

### 如何存储数据(目前实现了三个对象模板)

#### RedissonObject 这个是比较通用的模板,任何对象都可以存在这里面,在spring 容器中注入对象即可

```
    @Autowired
    private RedissonObject redissonObject;
```

示例：

```java
@Controller
public class ObjectController {

    @Autowired
    private RedissonObject redissonObject;

    /**
     * 设置值
     * @param user
     * @param request
     * @param response
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/object1")
    @ResponseBody
    public String object1(User user, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        User user1 = new User();
        user1.setName("test");
        user1.setAge("123");
        redissonObject.setValue("object1", user1,-1L);
        return "";
    }

    /**
     * 获取值
     * @param user
     * @param request
     * @param response
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/object2")
    @ResponseBody
    public Object object2(User user, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        return redissonObject.getValue("object1");
    }

    /**
     * 如果对象不存在则设置,否则不设置
     * @param user
     * @param request
     * @param response
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/object3")
    @ResponseBody
    public String object3(User user, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        return redissonObject.trySetValue("object1","object1-2")+"";
    }
}
```

#### RedissonBinary 这个是存储二进制的模板.可以存放图片之内的二进制文件,在spring 容器中注入对象即可

```java
    @Autowired
    private RedissonBinary redissonBinary;
```

示例：

```java
@Controller
public class BinaryController {

    @Autowired
    private RedissonBinary redissonBinary;

    /**
     * 存放图片
     * @param user
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/binary1")
    @ResponseBody
    public String binary1(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        redissonBinary.setValue("binary",new FileInputStream(new File("f:/1.png")));
        return "11";
    }

    /**
     * 获取图片
     * @param user
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/binary2")
    public void binary2(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        redissonBinary.getValue("binary",response.getOutputStream());

    }
}
```

#### RedissonCollection 这个是集合模板,可以存放`Map`,`List`,`Set`集合元素,在spring 容器中注入对象即可

```java
    @Autowired
    private RedissonCollection redissonCollection;
```

示例：

```java
@Controller
public class CollectionController {

    @Autowired
    private RedissonCollection redissonCollection;

    /**
     * map操作
     * @param user
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/collection1")
    @ResponseBody
    public String collection1(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,String> map=new HashMap<>();
        map.put("test1","test11");
        map.put("test2","test22");
        map.put("test3","test33");
        map.put("test4","test44");
        //设置值
        redissonCollection.setMapValues("test",map);

        //获取值
        RMap<String, String> test = redissonCollection.getMap("test");
        System.out.println(test);
        return "11";
    }

    /**
     * list操作
     * @param user
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/collection2")
    @ResponseBody
    public String collection2(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> list=new ArrayList<>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        list.add("test4");
        list.add("test5");
        //设置值
        redissonCollection.setListValues("list",list);

        //获取值
        RList<Object> list1 = redissonCollection.getList("list");
        System.out.println(list1);
        return "11";
    }
    /**
     * set操作
     * @param user
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/collection3")
    @ResponseBody
    public String collection3(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Set<String> set=new HashSet<>();
        set.add("test1");
        set.add("test2");
        set.add("test3");
        set.add("test4");
        set.add("test5");
        //设置值
        redissonCollection.setSetValues("set",set);

        //获取值
        RSet<Object> set1 = redissonCollection.getSet("set");
        System.out.println(set1);
        return "11";
    }
}
```

### 如何session集群

> 在启动类标注 `@EnableRedissonHttpSession` 即可

## 进阶篇

如何使用`redisson` 客户端实现自定义操作,只需要在spring 容器中注入redisson客户端就行,如下:

```
    @Autowired
    private RedissonClient redissonClient;
```

#### 如何集成spring cache [详细实例](https://gitee.com/ztp/redisson-spring-boot-starter/blob/1.0.2/readme/cache.md)

> 启动类加上 `@EnableCache(value = {"cache1","cache2"})` ,@Cacheable,@CachePut,@CacheEvict的value必须包含@EnableCache里面的value值,不然会报错

#### 如何使用消息队列MQ.(使用起来非常简单.两个注解即可完成操作)

消息队列分为 `生产者`以及`消费者`,`生产者`生产消息供`消费者`消费

#####  生产者

```java
@Controller
public class MQController {

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 发送方式一: 使用代码发送消息
     */
    @RequestMapping("testMq")
    @ResponseBody
    public void testMq(){
        RTopic testMq = redissonClient.getTopic("testMq");
        User message = new User();
        message.setAge("12");
        message.setName("的身份为");
        testMq.publish(message);
    }

    /**
     * 发送方式二:使用注解发送消息, 返回值就是需要发送的消息
     * @return
     */
    @RequestMapping("testMq1")
    @ResponseBody
    @MQProducer(name = "test")
    public User testMq1(){
        User user=new User();
        user.setName("garegarg");
        user.setAge("123");
        return user;
    }

}
```

##### 消费者

```java
//注入到spring容器中
@Component
public class MQConsumers {

    /**
     * 接受消息方式一:PRECISE精准的匹配 如:name="myTopic" 那么发送者的topic name也一定要等于myTopic (如果消息类型不明确,请使用Object 接收消息)
     * @param charSequence
     * @param o
     */
    @MQConsumer(name = "testMq")
    public void test1(CharSequence charSequence,User o,Object object){
        System.out.println("charSequence="+charSequence);
        System.out.println("收到消息2"+o);
    }

    /**
     *
     * 接收消息方式二: PATTERN模糊匹配 如: name="myTopic.*" 那么发送者的topic name 可以是 myTopic.name1 myTopic.name2.尾缀不限定
     * @param patten
     * @param charSequence
     * @param o
     */
    @MQListener(name = "test*",model = MQModel.PATTERN)
    public void test1(CharSequence patten,CharSequence charSequence,User o){
        System.out.println("patten="+patten);
        System.out.println("charSequence="+charSequence);
        System.out.println("test*="+o);
    }
}
```

> `生产者` 配置,发送消息有两种模式,二选一即可

> `消费者` 配置

1.启动类加上 `@EnableMQ` 开启消费者

2.使用注解`@MQListener(name = "testMq")`配置消费者

```
@MQListener(name = "testMq")
public void test1(CharSequence charSequence,User o,Object object){
    System.out.println("charSequence="+charSequence);
    System.out.println("收到消息2"+o);
}
```

#### 集群模式配置

> 集群模式除了适用于Redis集群环境，也适用于任何云计算服务商提供的集群模式，例如AWS ElastiCache集群版、Azure Redis Cache和阿里云（Aliyun）的云数据库Redis版。

```properties
#集群模式
spring.redisson.model=CLUSTER
#redis机器.一直累加下去
spring.redisson.multiple.node-addresses[0]=127.0.0.1:6379
spring.redisson.multiple.node-addresses[1]=127.0.0.1:6380
spring.redisson.multiple.node-addresses[2]=127.0.0.1:6381
```

#### 云托管模式

> 云托管模式适用于任何由云计算运营商提供的Redis云服务，包括亚马逊云的AWS ElastiCache、微软云的Azure Redis 缓存和阿里云（Aliyun）的云数据库Redis版

```properties
#云托管模式
spring.redisson.model=REPLICATED
#redis机器.一直累加下去
spring.redisson.multiple.node-addresses[0]=127.0.0.1:6379
spring.redisson.multiple.node-addresses[1]=127.0.0.1:6380
spring.redisson.multiple.node-addresses[2]=127.0.0.1:6381
```

#### 哨兵模式

```properties
spring.redisson.model=SENTINEL
#主服务器的名称是哨兵进程中用来监测主从服务切换情况的。
spring.redisson.multiple.master-name="mymaster"
#redis机器.一直累加下去
spring.redisson.multiple.node-addresses[0]=127.0.0.1:6379
spring.redisson.multiple.node-addresses[1]=127.0.0.1:6380
spring.redisson.multiple.node-addresses[2]=127.0.0.1:6381
```

#### 主从模式

```properties
spring.redisson.model=MASTERSLAVE
#第一台机器就是主库.其他的为从库
spring.redisson.multiple.node-addresses[0]=127.0.0.1:6379
spring.redisson.multiple.node-addresses[1]=127.0.0.1:6380
spring.redisson.multiple.node-addresses[2]=127.0.0.1:6381
```

#### 属性列表(分为 `公共参数`,`单例模式参数`,`集群模式参数`)

##### 公共参数

| 属性名                                             | 默认值                                 | 备注                                                         |
| -------------------------------------------------- | -------------------------------------- | ------------------------------------------------------------ |
| spring.redisson.password                           |                                        | 用于节点身份验证的密码。                                     |
| spring.redisson.attempt-timeout                    | 10000L                                 | 等待获取锁超时时间,-1则是一直等待 单位毫秒                   |
| spring.redisson.data-valid-time                    | 1000*60* 30L                           | 数据缓存时间 默认30分钟 -1永久缓存                           |
| spring.redisson.lock-model                         | 单个key默认`可重入锁`多个key默认`联锁` | 锁的模式.如果不设置, REENTRANT(可重入锁),FAIR(公平锁),MULTIPLE(联锁),REDLOCK(红锁),READ(读锁), WRITE(写锁) |
| spring.redisson.model                              | SINGLE                                 | 集群模式:SINGLE(单例),SENTINEL(哨兵),MASTERSLAVE(主从),CLUSTER(集群),REPLICATED(云托管) |
| spring.redisson.codec                              | org.redisson.codec.JsonJacksonCodec    | Redisson的对象编码类是用于将对象进行序列化和反序列化，以实现对该对象在Redis里的读取和存储 |
| spring.redisson.threads                            | 当前处理核数量 * 2                     | 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享。 |
| spring.redisson.netty-threads                      | 当前处理核数量 * 2                     | 这个线程池数量是在一个Redisson实例内，被其创建的所有分布式数据类型和服务，以及底层客户端所一同共享的线程池里保存的线程数量。 |
| spring.redisson.transport-mode                     | NIO                                    | TransportMode.NIO,TransportMode.EPOLL - 需要依赖里有netty-transport-native-epoll包（Linux） TransportMode.KQUEUE - 需要依赖里有 netty-transport-native-kqueue包（macOS） |
| spring.redisson.idle-connection-timeout            | 10000                                  | 如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒 |
| spring.redisson.connect-timeout                    | 10000                                  | 同任何节点建立连接时的等待超时。时间单位是毫秒。             |
| spring.redisson.timeout                            | 3000                                   | 等待节点回复命令的时间。该时间从命令发送成功时开始计时。     |
| spring.redisson.retry-attempts                     | 3                                      | 如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。 |
| spring.redisson.retry-interval                     | 1500                                   | 在一条命令发送失败以后，等待重试发送的时间间隔。时间单位是毫秒。 |
| spring.redisson.subscriptions-per-connection       | 5                                      | 每个连接的最大订阅数量。                                     |
| spring.redisson.client-name                        |                                        | 在Redis节点里显示的客户端名称。                              |
| spring.redisson.ssl-enable-endpoint-identification | true                                   | 开启SSL终端识别能力。                                        |
| spring.redisson.ssl-provider                       | JDK                                    | 确定采用哪种方式（JDK或OPENSSL）来实现SSL连接。              |
| spring.redisson.ssl-truststore                     |                                        | 指定SSL信任证书库的路径。                                    |
| spring.redisson.ssl-truststore-password            |                                        | 指定SSL信任证书库的密码。                                    |
| spring.redisson.ssl-keystore                       |                                        | 指定SSL钥匙库的路径。                                        |
| spring.redisson.ssl-keystore-password              |                                        | 指定SSL钥匙库的密码。                                        |
| spring.redisson.lock-watchdog-timeout              | 30000                                  | 监控锁的看门狗超时时间单位为毫秒。该参数只适用于分布式锁的加锁请求中未明确使用leaseTimeout参数的情况。如果该看门口未使用lockWatchdogTimeout去重新调整一个分布式锁的lockWatchdogTimeout超时，那么这个锁将变为失效状态。这个参数可以用来避免由Redisson客户端节点宕机或其他原因造成死锁的情况。 |
| spring.redisson.keep-pub-sub-order                 | true                                   | 通过该参数来修改是否按订阅发布消息的接收顺序出来消息，如果选否将对消息实行并行处理，该参数只适用于订阅发布消息的情况。 |

##### 单例模式参数

| 属性名                                                       | 默认值 | 备注                                                         |
| ------------------------------------------------------------ | ------ | ------------------------------------------------------------ |
| spring.redisson.single.address                               |        | 服务器地址,必填ip:port                                       |
| spring.redisson.single.database                              | 0      | 尝试连接的数据库编号。                                       |
| spring.redisson.single.subscription-connection-minimum-idle-size | 1      | 用于发布和订阅连接的最小保持连接数（长连接）。Redisson内部经常通过发布和订阅来实现许多功能。长期保持一定数量的发布订阅连接是必须的。 |
| spring.redisson.singlesubscription-connection-pool-size      | 50     | 用于发布和订阅连接的连接池最大容量。连接池的连接数量自动弹性伸缩。 |
| spring.redisson.single.connection-minimum-idle-size          | 32     | 最小保持连接数（长连接）。长期保持一定数量的连接有利于提高瞬时写入反应速度。 |
| spring.redisson.single.connection-pool-size                  | 64     | 连接池最大容量。连接池的连接数量自动弹性伸缩。               |
| spring.redisson.single.dns-monitoring-interval               | 5000   | 用来指定检查节点DNS变化的时间间隔。使用的时候应该确保JVM里的DNS数据的缓存时间保持在足够低的范围才有意义。用-1来禁用该功能。 |

##### 集群模式

| 属性名                                                       | 默认值                                                  | 备注                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------- | ------------------------------------------------------------ |
| spring.redisson.multiple.node-addresses                      |                                                         | 服务器节点地址.必填  redisson.multiple-server-config.node-addresses[0]=127.0.0.1:6379 redisson.multiple-server-config.node-addresses[1]=127.0.0.1:6380 redisson.multiple-server-config.node-addresses[2]=127.0.0.1:6381 |
| spring.redisson.multiple.load-balancer                       | org.redisson.connection.balancer.RoundRobinLoadBalancer | 在多Redis服务节点的环境里，可以选用以下几种负载均衡方式选择一个节点： org.redisson.connection.balancer.WeightedRoundRobinBalancer - 权重轮询调度算法 org.redisson.connection.balancer.RoundRobinLoadBalancer - 轮询调度算法 org.redisson.connection.balancer.RandomLoadBalancer - 随机调度算法 |
| spring.redisson.multiple.slave-connection-minimum-idle-size  | 32                                                      | 多从节点的环境里，每个 从服务节点里用于普通操作（非 发布和订阅）的最小保持连接数（长连接）。长期保持一定数量的连接有利于提高瞬时读取反映速度。 |
| spring.redisson.multiple-.slave-connection-pool-size         | 64                                                      | 多从节点的环境里，每个 从服务节点里用于普通操作（非 发布和订阅）连接的连接池最大容量。连接池的连接数量自动弹性伸缩。 |
| spring.redisson.multiple.master-connection-minimum-idle-size | 32                                                      | 多节点的环境里，每个 主节点的最小保持连接数（长连接）。长期保持一定数量的连接有利于提高瞬时写入反应速度。 |
| spring.redisson.multiple.master-connection-pool-size         | 64                                                      | 多主节点的环境里，每个 主节点的连接池最大容量。连接池的连接数量自动弹性伸缩。 |
| spring.redisson.multiple.read-mode                           | SLAVE                                                   | 设置读取操作选择节点的模式。 可用值为： SLAVE - 只在从服务节点里读取。 MASTER - 只在主服务节点里读取。 MASTER_SLAVE - 在主从服务节点里都可以读取。 |
| spring.redisson.multiple.subscription-mode                   | SLAVE                                                   | 设置订阅操作选择节点的模式。 可用值为： SLAVE - 只在从服务节点里订阅。 MASTER - 只在主服务节点里订阅。 |
| spring.redisson.multiple.subscription-connection-minimum-idle-size | 1                                                       | 用于发布和订阅连接的最小保持连接数（长连接）。Redisson内部经常通过发布和订阅来实现许多功能。长期保持一定数量的发布订阅连接是必须的。 redisson.multiple-server-config.subscriptionConnectionPoolSize |
| spring.redisson.multiple.dns-monitoring-interval             | 5000                                                    | 监测DNS的变化情况的时间间隔。                                |
| spring.redisson.multiple.scan-interval                       | 1000                                                    | (集群,哨兵,云托管模特特有) 对Redis集群节点状态扫描的时间间隔。单位是毫秒。 |
| spring.redisson.multiple.database                            | 0                                                       | (哨兵模式,云托管,主从模式特有)尝试连接的数据库编号。         |
| spring.redisson.multiple.master-name                         |                                                         | (哨兵模式特有)主服务器的名称是哨兵进程中用来监测主从服务切换情况的。 |