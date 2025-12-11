package com.mypath.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/user")
public class UserController {

        @Autowired
        private UserRepository userRepository;

        @GetMapping("/getAll")
        public List<User> getAll() {
            return userRepository.findAll();
        }

}
