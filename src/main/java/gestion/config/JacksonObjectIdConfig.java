package gestion.config;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonObjectIdConfig {

  @Bean
  public Module objectIdAsHexStringModule() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(ObjectId.class, new JsonSerializer<ObjectId>() {
      @Override
      public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers)
          throws IOException {
        gen.writeString(value == null ? null : value.toHexString());
      }
    });
    return module;
  }
}