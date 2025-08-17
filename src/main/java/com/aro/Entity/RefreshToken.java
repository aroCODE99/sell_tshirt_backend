package com.aro.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor @Setter @Getter @ToString
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long token_id;

    @Column(unique = true)
    private String token;

    private LocalDateTime expiryDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @ToString.Exclude
    private AppUsers user;


    public RefreshToken(String token, AppUsers user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusDays(1);
    }

}

