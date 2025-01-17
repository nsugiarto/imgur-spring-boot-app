package com.example.imgur_app;
import com.example.imgur_app.entity.User;
import com.example.imgur_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class H2DatabaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testH2DatabaseConnection() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        userRepository.save(user);

        Optional<User> fetchedUser = userRepository.findByUsername("testuser");
        assertNotNull(fetchedUser);
        User userFromDB = fetchedUser.get();
        assertEquals("testuser", userFromDB.getUsername());
    }
}

