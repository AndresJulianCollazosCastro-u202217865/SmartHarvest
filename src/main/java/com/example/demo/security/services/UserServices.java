package com.example.demo.security.services;

import com.example.demo.security.dtos.UserDto;
import com.example.demo.security.entities.User;
import com.example.demo.security.interfaces.IUserServices;
import com.example.demo.security.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServices implements IUserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;


    public UserDto addUser(UserDto user) {
        User newUser = modelMapper.map(user, User.class);
        newUser = userRepository.save(newUser);
        return modelMapper.map(newUser, UserDto.class);
    }

    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findUserByUserId(userDto.getId());
        modelMapper.map(userDto, user);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    public Integer insertUserRol(Long userId, Long roleId) {
        Integer result = 0;
        userRepository.addUserRole(userId,roleId);
        return 1;
    }

}
