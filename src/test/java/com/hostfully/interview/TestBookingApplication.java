package com.hostfully.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestBookingApplication {

	public static void main(String[] args) {
		SpringApplication.from(BookingApplication::main).with(TestBookingApplication.class).run(args);
	}

}
