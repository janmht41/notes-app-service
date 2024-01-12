package com.assignment;

import com.assignment.index.Indexer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotesServiceApplication {
	// TODO add tests.
	public static void main(String[] args) {
		SpringApplication.run(NotesServiceApplication.class, args);
	}

	@Bean
	public ApplicationRunner buildIndex(Indexer indexer) throws Exception {
		return (ApplicationArguments args) -> {
			indexer.indexPersistedData("com.assignment.entity.Note");
		};
	}

}
