package com.aro.Services;

import com.aro.DTOs.AddressDto;
import com.aro.DTOs.ErrorResponse;
import com.aro.Entity.Addresses;
import com.aro.Entity.AppUsers;
import com.aro.Exceptions.ResourceNotFoundException;
import com.aro.Repos.AddressRepo;
import com.aro.Repos.AuthRepo;
import com.nimbusds.openid.connect.sdk.claims.Address;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AuthRepo authRepo;

    private final JwtService jwtService;

    private final AddressRepo addressRepo;

    public AddressService(AuthRepo authRepo, JwtService jwtService, AddressRepo addressRepo) {
        this.authRepo = authRepo;
        this.jwtService = jwtService;
        this.addressRepo = addressRepo;
    }

    // now this is going to be doing {} things:
    public ResponseEntity<?> getAddress(String authHeader) {
        Long userId = jwtService.getUserId(authHeader);
        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("user not in db")
        );

        return ResponseEntity.ok().body(user.getAddresses());
    }

    public ResponseEntity<?> saveAddress(AddressDto addressDto, String authHeader) {
        Long userId = jwtService.getUserId(authHeader);

        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("user not in db")
        );

        Set<Addresses> existOtherTypeAddress = user.getAddresses().stream()
            .filter(a -> Objects.equals(a.getAddressType(), addressDto.getAddressType()))
            .collect(Collectors.toSet());

        if (existOtherTypeAddress.isEmpty()) {
            final Addresses address = getAddresses(addressDto);
            user.addAddress(address);

            return ResponseEntity.ok().body(authRepo.save(user));
        }  else {
            return ResponseEntity.badRequest().body(new ErrorResponse("ALREADY_"+addressDto.getAddressType()+"ADDRESS EXISTS", "address already there",
                LocalDateTime.now().toString()));
        }
    }

    private Addresses getAddresses(AddressDto addressDto) {
        Addresses address = new Addresses();
        address.setAddressType(addressDto.getAddressType());
        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setLandmark(addressDto.getLandmark());
        address.setPostalCode(addressDto.getPostalCode());
        address.setStreetName(addressDto.getStreetName());
        address.setName(addressDto.getName());
        address.setPhoneNumber(addressDto.getPhoneNumber());
        return address;
    }

}
