package com.yy.framework.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * CROS配置类与JwtFilter配合完成全站CROS配置
 *
 */
@Configuration
public class JsonWebMvcConfig extends WebMvcConfigurationSupport {

	/**
	 * 日期格式化
	 **/
	//private static final DateTimeFormatter FROMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"); //
	private static final String DEFAULT_DATE_FORMAT="yyyy-MM-dd";

	/**
	 * JSON处理器
	 *
	 * @author
	 **/
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
	}

	/**
	 * JSON转换器
	 *
	 * @author
	 **/
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);

		simpleModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
			@Override
			public void serialize(BigDecimal decimal, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeString(decimal.toPlainString());
			}
		});

		/*		simpleModule.addSerializer(Date.class, new JsonSerializer<Date>() {
			@Override
			public void serialize(Date time, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeString(new DateTime(time).toString(FROMAT));
			}
		});*/


		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.ALWAYS);
//		mapper.setSerializationInclusion(Include.NON_NULL);


/*		mapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
		mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
			@Override
			public Object findSerializer(Annotated annotatedMethod) {
				if(annotatedMethod instanceof AnnotatedMethod) {
					AnnotatedElement m = annotatedMethod.getAnnotated();
					JsonFormat jsonFormat = m.getAnnotation(JsonFormat.class);
					//DateTimeFormat dtm = m.getAnnotation(DateTimeFormat.class);
					if( jsonFormat != null) {
						if(!DEFAULT_DATE_FORMAT.equals(jsonFormat.pattern())) {
							return new JavaDateJsonSerializer(jsonFormat.pattern());
						}
					}
				}
				return super.findSerializer(annotatedMethod);
			}
		});*/

		mapper.registerModule(simpleModule);

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(mapper);
		return converter;
	}

}
