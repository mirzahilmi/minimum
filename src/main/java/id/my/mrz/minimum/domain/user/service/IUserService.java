package id.my.mrz.minimum.domain.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import id.my.mrz.minimum.domain.user.dto.UserResourceResponse;
import id.my.mrz.minimum.domain.user.dto.UserSignupRequest;

public sealed interface IUserService extends UserDetailsService permits UserService {
    public UserResourceResponse create(UserSignupRequest credential);
}
