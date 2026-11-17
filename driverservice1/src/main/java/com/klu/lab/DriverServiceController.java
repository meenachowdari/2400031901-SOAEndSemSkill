package com.klu.lab;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableDiscoveryClient
@RequestMapping("display")
public class DriverServiceController {
	
	@GetMapping()
	public String order() {
		return "Driver Service from server 1";
	}
}
