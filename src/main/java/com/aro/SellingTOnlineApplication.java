package com.aro;

import com.aro.Services.CloudinaryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SellingTOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SellingTOnlineApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(CloudinaryService cloudinaryService) {
		return args -> {
			System.out.println("Application started .....");
		};
	}

}

// 🧱 Core Features:
// Homepage with banners, categories, featured products,
//
// Products List Page with filtering (category, price, etc.)
//
// Products Details Page with images, sizes, stock info
//
// Shopping Cart (add/remove items, quantity control)
//
// Checkout with shipping details & payment
//
// User Accounts (login/register, orders)
//
// Admin Panel to manage products & orders

// now we are getting the latest order from here by using the pagination method because jpa doesnt
// supports the LIMIT query so getting the first page and firstData that's smart
// Orders latestOrder = ordersRepo.findLatestOrderByUserId(user.getId(),
// 	PageRequest.of(0, 1)).stream().findFirst().orElseThrow(
// 	() -> new UsernameNotFoundException("User has not Ordered Anything")
// );


// now let's work with the placing the order
// how will we make this happen
// so i think that the order should be created when the user is placing the order
