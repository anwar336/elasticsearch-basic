package com.es.api.esdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EntityScan(basePackageClasses = {
		EsdbApplication.class
})
//@EnableAutoConfiguration(exclude={ElasticsearchRestClientAutoConfiguration.class})
public class EsdbApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(EsdbApplication.class, args);
	}
        
        @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EsdbApplication.class);
	}

}
