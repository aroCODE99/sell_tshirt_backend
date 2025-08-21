package com.aro;

import com.aro.Entity.*;
import com.aro.Enums.Categories;
import com.aro.Enums.Size;
import com.aro.Repos.CategoryRepo;
import com.aro.Repos.ProductsRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class SellingTOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SellingTOnlineApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("Application Started....");
		};
	}

	public void AddingTShirt(CategoryRepo categoryRepo, ProductsRepo productsRepo) {
		Category category = categoryRepo.findByType(Categories.oversized.name());

		Products tShirt = new Products(
			"Solo-leveling t-shirt",
			new BigDecimal(59),
			"""
                FIT TYPE: Oversized Fit; Main material: 100% Cotton , 180GSM (Bio-Washed & Pre-Shrunk for Minimum shrinkage).
                HIGH DEFINITION PRINT: We are using the High Quality Print Technology to ensure our products with sharp cuts and durable for long time.
            """,
			"Something",
			"Black",
			"http://demopath.com"
		);

		Set<ProductVariant> productVariants = Set.of(
			new ProductVariant(
				tShirt,
				Size.SM,
				50
			),

			new ProductVariant(
				tShirt,
				Size.XL,
				50
			),

			new ProductVariant(
				tShirt,
				Size.M,
				50
			),

			new ProductVariant(
				tShirt,
				Size.LG,
				50
			)
		);

		tShirt.setProductVariants(productVariants);
		Products savedTShirt = productsRepo.save(tShirt);

		System.out.println(savedTShirt);
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