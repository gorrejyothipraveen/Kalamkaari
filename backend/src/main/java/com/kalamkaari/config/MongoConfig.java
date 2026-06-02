package com.kalamkaari.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@ConditionalOnBean(MongoDatabaseFactory.class)
@EnableMongoAuditing
public class MongoConfig {
}
