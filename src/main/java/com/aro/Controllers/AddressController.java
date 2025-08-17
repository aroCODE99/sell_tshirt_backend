package com.aro.Controllers;

import com.aro.DTOs.AddressDto;
import com.aro.Services.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/address")
public class AddressController {

    private final AddressService userService;

    public AddressController(AddressService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAddress(@RequestHeader("Authorization") String authHeader) {
        return userService.getAddress(authHeader);
    }

    @PostMapping
    public ResponseEntity<?> saveAddress(@RequestBody AddressDto addressDto, @RequestHeader("Authorization") String authHeader) {
        System.out.println(addressDto);
        return userService.saveAddress(addressDto, authHeader);
    }
}
