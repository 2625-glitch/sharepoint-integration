package com.example.shareapp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.shareapp.service.SharePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShareappApplication {

	@Autowired
	private SharePointService sharePointService;

	public static void main(String[] args) {
		SpringApplication.run(ShareappApplication.class, args);
	}

//	@PostConstruct
//	public void initializeSubscriptions() {
//		// Call the subscription creation method
//		sharePointService.initializeSubscription();
//	}

}
