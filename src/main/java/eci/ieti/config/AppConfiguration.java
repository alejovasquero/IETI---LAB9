package eci.ieti.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class AppConfiguration {

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://davinchi:Tnh7JPsxdSygSjYE@cluster0.8odye.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoClient mongoClient = new MongoClient(uri);
        return new SimpleMongoDbFactory( mongoClient, "myFirstDatabase");
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }

}