#项目启动

```jshelllanguage
java -jar vertx-blueprint-kue-core.jar -cluster -ha
java -jar vertx-blueprint-kue-example.jar -cluster -ha

```
#指定配置文件启动
```jshelllanguage
java -jar vertx-blueprint-kue-core.jar -cluster -ha -conf config.json
java -jar vertx-blueprint-kue-example.jar -cluster -ha -conf config.json

```
- json 文件格式
```json
{
  "redis.host": "127.0.0.1",
  "redis.port":6379
}

```