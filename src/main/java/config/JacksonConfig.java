package config;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class JacksonConfig {

	@Bean
	ObjectMapper objectMapper() {  // sin 'public'
	    SimpleModule module = new SimpleModule();
	    module.addSerializer(ObjectId.class, ToStringSerializer.instance);
	    return JsonMapper.builder()
	            .addModule(module)
	            .build();
	}
}