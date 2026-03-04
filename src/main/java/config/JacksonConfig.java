package config;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class JacksonConfig {

    @Bean
    public Module objectIdModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ObjectId.class, ToStringSerializer.instance);
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder(Module objectIdModule) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(objectIdModule);
        return builder;
    }
}