#  LearnSpringBoot-SpringBoot整合-ElasticSearch检索

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1 检索

- 我们的应用经常需要添加检索功能，开源的 ElasticSearch 是目前**全文搜索引擎**的首选。他可以快速的存储、搜索和分析海量数据。Spring Boot通过整合Spring Data ElasticSearch为我们提供了非常便捷的检索功能支持
- **Elasticsearch**是一个分布式搜索服务，**提供Restful API，底层基于Lucene**，采用 多shard（分片）的方式保证数据安全，并且提供自动resharding的功能，github 等大型的站点也是采用了ElasticSearch作为其搜索服务
    - **Lucene**是一套用于[全文检索](https://baike.baidu.com/item/全文检索/8028630)和搜寻的开源程式库，由[Apache](https://baike.baidu.com/item/Apache)软件基金会支持和提供。Lucene提供了一个简单却强大的应用程式接口，能够做全文索引和搜寻。在Java开发环境里Lucene是一个成熟的免费[开源](https://baike.baidu.com/item/开源/246339)工具。就其本身而言，Lucene是当前以及最近几年最受欢迎的免费Java信息检索程序库。

## 2 ElasticSearch相关概念

- 以员工文档的形式存储为例：一个文档代表一个员工数据。存储数据到 ElasticSearch 的行为叫做**索引** ，但在索引一个文档之前，需要确定将文档存储在哪里。

- 一个 ElasticSearch 集群可以 包含多个 **索引** ，相应的每个索引可以包含多 个 **类型** 。 这些不同的类型存储着多个 **文档** ，每个文档又有 多个属性 。

- ![5vjv852](https://i.imgur.com/5vjv852.png)

- 用数据库术语对比：索引-数据库；类型-表；文档-表中的记录； 属性-列

- 例如，对于员工目录：

    - [索引员工文档 | Elasticsearch: 权威指南 | Elastic](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_indexing_employee_documents.html)

    - 对于员工目录，我们将做如下操作：

        - 每个员工索引一个文档，文档包含该员工的所有信息。
        - 每个文档都将是 `employee` *类型* 。
        - 该类型位于 *索引* `megacorp` 内。
        - 该索引保存在我们的 Elasticsearch 集群中。

    - 对应的RESTful请求：`PUT /megacorp/employee/1`

    - ![oPzeJQz](https://i.imgur.com/oPzeJQz.png)

    - ![ggSgQr5](https://i.imgur.com/ggSgQr5.png)

    - 全文搜索会根据相关度评分给结果排序：

        ```
        GET /megacorp/employee/_search
        {
            "query" : {
                "match" : {
                    "about" : "rock climbing"
                }
            }
        }
        ```

        - ![PkhIMQc](https://i.imgur.com/PkhIMQc.png)

    - 短语匹配搜索：

        ```
        GET /megacorp/employee/_search
        {
            "query" : {
                "match_phrase" : {
                    "about" : "rock climbing"
                }
            }
        }
        ```

## 3 SpringBoot整合ElasticSearch

- 1、docker中安装ElasticSearch, 运行时最好限制其内存大小`docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 elasticsearch:7.6.2`

    - ElasticSearch有两个端口：9200是web管理端口，9300是通信端口
    - ![QgGXp7i](https://i.imgur.com/QgGXp7i.png)

    > 5.6.0版本的docker镜像按此命令启动不会闪退。

- 2、创建SpringBoot时勾选ElasticSearch和Web
- 3、自动配置原理:
    - `ElaticsearchAutoConfiguration`
    - 1）、Client 节点信息clusterNodes；clusterName
    - 2）、`ElasticsearchTemplate` 操作ES
    - 3）、编写一个 ElasticsearchRepository 的子接口来操作ES；
- SpringBoot默认支持两种技术来和ES交互:
    - Jest（默认不生效）:需要导入jest的工具包（io.searchbox.client.JestClient）
    - SpringData ElasticSearch【需要匹配ES版本和SpringBoot的版本】
### 3.1 SpringBoot整合Jest操作ElasticSearch(已过时)
- Jest is a Java HTTP Rest client for ElasticSearch.

```xml
<!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
<dependency>
    <groupId>io.searchbox</groupId>
    <artifactId>jest</artifactId>
    <version>6.3.1</version>
</dependency>
```
- 1、在主配置文件中配置jest
    ```properties
      spring.elasticsearch.jest.uris=http://118.24.44.169:9200
    ```
- 2、@Autowired自动注入JestClient操作ES
- 3、编写JavaBean，使用@JestId标注主键
- 4、测试操作功能
    ```java
    @SpringBootTest
    public class Springboot03ElasticApplicationTests {
    
        @Autowired
        JestClient jestClient;

        //测试给ES中索引（保存）一个文档；  
        @Test
        public void contextLoads() {
            Article article = new Article();
            article.setId(1);
            article.setTitle("好消息");
            article.setAuthor("zhangsan");
            article.setContent("Hello World");
    
            //构建一个索引功能
            Index index = new Index.Builder(article).index("atguigu").type("news").build();
    
            try {
                jestClient.execute(index);//执行
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        //测试搜索
        @Test
        public void search(){
            //JSON格式的查询表达式
            String json ="{\n" +
                    "    \"query\" : {\n" +
                    "        \"match\" : {\n" +
                    "            \"content\" : \"hello\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
    
            //更多操作：https://github.com/searchbox-io/Jest/tree/master/jest
            //构建搜索功能(传入查询表达式)
            Search search = new Search.Builder(json).addIndex("atguigu").addType("news").build();
    
            //执行
            try {
                SearchResult result = jestClient.execute(search);
                System.out.println(result.getJsonString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    ```

### 3.2 SpringBoot整合SpringData操作ElasticSearch(已过时)
- 1、在主配置文件中配置SpringData整合ES：
    - ![BjqtFlP](https://i.imgur.com/BjqtFlP.png)
    ```properties
    spring.data.elasticsearch.cluster-name=docker-cluster
    spring.data.elasticsearch.cluster-nodes=127.0.0.1:9300
    ```
    - ![VRCJCm2](https://i.imgur.com/VRCJCm2.png)
    > 有可能因为SpringBoot和ES的版本不匹配导致报错，本机运行时的环境是： Spring Boot(v2.2.6.RELEASE)、elasticsearch:6.5.0
- 2、编写一个Repository接口，继承自ElasticsearchRepository接口，范型使用<要搜索的数据封装类， 主键类型>
    - 自定义Repository接口支持写自定义方法:
    - 例如需要模糊查询书名，只需要在接口中声明一个名字为`findByBookNameLike`的方法即可（无需实现）
    - 方法的命名需要满足要求，可以参见：[Spring Data Elasticsearch - Reference Documentation](https://docs.spring.io/spring-data/elasticsearch/docs/3.2.6.RELEASE/reference/html/#elasticsearch.query-methods)
    - 同时**也支持在方法声明上使用@Query注解来设置查询时用到的ES查询表达式**
        ```java
        // 范型<要搜索的数据封装类， 主键类型>
        public interface BookRepository extends ElasticsearchRepository<Book, Integer> {
        
            // 按照书名模糊查询（需要方法名匹配命名规范）
            public List<Book> findByBookNameLike(String bookName);
        
        }
        ```
    - ![cRuFRyb](https://i.imgur.com/cRuFRyb.png)
    
- 3、在需要操作ES的地方@Autowired注入自定义的ElasticsearchRepository接口
    ```java
    @SpringBootTest
    class Springboot17ElasticsearchApplicationTests {
    
        @Autowired
        BookRepository bookRepository;
    
        // 测试在es中保存一个BOOK对象
        @Test
        void contextLoads() {
    
            Book book = new Book();
            book.setId(2);
            book.setAuthor("吴承恩");
            book.setBookName("西游记");
            //book.setAuthor("我也不知道谁写的");
            //book.setBookName("西厢记");
    
            bookRepository.index(book);
        }
    
        // 测试findByBookNameLike
        @Test
        public void test1(){
            for (Book book : bookRepository.findByBookNameLike("西")) {
                System.out.println(book);
            }
        }
    
    }
    ```





## 参考资料

- [ElasticSearch启动报错，bootstrap checks failed_JavaScript_feng12345zi的博客-CSDN博客](https://blog.csdn.net/feng12345zi/article/details/80367907)
- [索引员工文档 | Elasticsearch: 权威指南 | Elastic](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_indexing_employee_documents.html)
- [spring-projects/spring-data-elasticsearch: Provide support to increase developer productivity in Java when using Elasticsearch. Uses familiar Spring concepts such as a template classes for core API usage and lightweight repository style data access.](https://github.com/spring-projects/spring-data-elasticsearch)
- [Jest/jest at master · searchbox-io/Jest](https://github.com/searchbox-io/Jest/tree/master/jest)






