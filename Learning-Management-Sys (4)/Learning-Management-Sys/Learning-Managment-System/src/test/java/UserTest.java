package com.lms.Learning_Managment_System;
import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Controller.userController;
import com.lms.Learning_Managment_System.Service.jwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {
    private userController user;
    @BeforeEach
    void setup(){
        user = new userController();
    }
    @Test
    void testRegister(){
        User newUser = new User();
        newUser.setFirstName("Azzza"); // must add new data to test register
        newUser.setLastName("ali");
        newUser.setEmail("azzaa12@gmail.com");
        newUser.setPassword("1234");
        newUser.setRole("Student");

        String response = user.registerUser(newUser);
        assertEquals("Successful Registration. Welcome To Learning Management System",response);
        List<User> users = user.getAllUsers();
        assertTrue(users.stream().anyMatch(user -> user.getEmail().equals("dina12@gmail.com")));
    }
    /*@Test
    void test_login(){ // before adding authentication
        String response = user.loginUser("leo@gmail.com", "pass4321");
        assertEquals("Successful Login, Hi Leo", response);
        List<User> loggedIn_users = user.getAllUsers();
        assertTrue(loggedIn_users.stream().anyMatch(user -> user.getEmail().equals("leo@gmail.com")));
    }*/
    @Autowired
    private jwt jwt;
    @Test
    void testLogin() { // after adding authentication
        String response = user.loginUser("leo@gmail.com", "pass4321");
        assertTrue(response.startsWith("Successful Login, Hi Leo"));
        assertTrue(response.contains("Token: "));

        String token = response.substring(response.indexOf("Token: ") + 7).trim();
        String role = jwt.getRoleFromToken(token);
        assertNotNull(role);
        assertTrue("instructor".equalsIgnoreCase(role));
        assertTrue(user.getLoggedInInstructors().containsKey("leo@gmail.com"));
    }
}
