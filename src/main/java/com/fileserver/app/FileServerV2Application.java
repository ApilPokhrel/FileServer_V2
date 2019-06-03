package com.fileserver.app;

import com.fileserver.app.works.user.UserDao;
import com.fileserver.app.works.user.UserDaoRepository;
import com.mongodb.MongoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;

@ComponentScan(basePackages = { "com.fileserver.app" })
@SpringBootApplication
public class FileServerV2Application {




	@Bean
	public MongoClient mongo() {
		return new MongoClient("localhost");
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), "fileServer");
	}

	public static void main(String[] args) {
		SpringApplication.run(FileServerV2Application.class, args);
	}

}
