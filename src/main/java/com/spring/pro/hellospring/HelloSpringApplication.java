package com.spring.pro.hellospring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloSpringApplication {
	@Autowired
	private CatchDoctorInfoThread catchDoctorInfoThread;

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringApplication.class, args);
	}


	@GetMapping("/catch")
	public void catchInfo( @RequestParam("url") String url,
						   @RequestParam("name") String name) {
		catchDoctorInfoThread.startGetDoctorInfo(url,name);
	}
}
