package com.example.company.model;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsEnhanced implements UserDetails {

    String username;

    String password;

    boolean accountNonExpired;

    boolean accountNonLocked;

    boolean credentialsNonExpired;

    boolean enabled;

    Long managedBrandId;

    Collection<? extends GrantedAuthority> authorities;
}
