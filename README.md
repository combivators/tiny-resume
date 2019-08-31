## Tiny boot: 一个极小精简的Java应用初始启动以及配置的全新框架
## 设计目的
 - 支持对application的YAML,HOSN,JSON,Properties属性配置文件处理。
 - 支持对Web系统,云计算和StandaloneJVM的部署。
 - 使用@Config注解将属性值绑定到结构化的实体中。
 - 以运行库方式提供，编译不需要依存此Java包。
 - 不依存任何第三方Java包。

##Usage

###1. Simple Use
```java
java net.tiny.boot.Main --help
      -p --profile The profile name.
      -f --file    Configuration file.
                   Default: 'application-{profile}.yml'
      -i --pid     Process id file (/var/run/pid).
      -v --verbose On debug mode.
      -h --help    This help message
```


###2. Application configuration file with profile
```properties
Configuration file : application-{profile}.[yml, json, conf, properties]

main = ${launcher}
shutdown = ${hook}
daemon = true
executor = ${pool}
callback = ${consumer}
launcher.class = x.y.Launcher
hook.class = x.y.ShutdownHook
pool.class = x.y.ExecutorService
pool.size = 5
pool.max = 10
consumer.class = x.y.Callable
```


###3. Configuration java
```java
@Config("app.sample")
public class SampleConfig {
    private LocalDate date;
    private LocalDate day;
    private List<String> array;
    // Getter and Setter methods

}
```

###4. Configuration file
```properties
app.sample.date = 2016/09/16
app.sample.day = ${app.sample.date}
app.sample.array = [${${a.b}.c}, ${x.y.z}]
```

##More Detail, See The Samples

---

Email   : wuweibg@gmail.com
