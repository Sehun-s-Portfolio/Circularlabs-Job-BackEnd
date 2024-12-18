package com.job.circularlabs_job_backend;

import org.quartz.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class CircularLabsJobBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircularLabsJobBackEndApplication.class, args);
		System.out.println("잡(JOB) 어플리케이션 실행 ~~~~~~~~~~~~~~~~~~~~");
		System.out.println("JDK 버전 17");
	}

}
