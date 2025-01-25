package com.sweetme.back.auth.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("---------------loadUserByEmail--------------");

        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        UserDTO userDTO = new UserDTO(
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getLoginType(),
                user.getStatus(),
                user.getRole()
        );

        log.info(userDTO);

        return userDTO;
    }
}
