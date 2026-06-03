package com.kalamkaari.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

// Excluded from the "test" profile so @WebMvcTest slices don't try to resolve
// mongoMappingContext (which only exists when MongoDB auto-configuration runs).
@Configuration
@Profile("!test")
@EnableMongoAuditing
public class MongoConfig {
}
