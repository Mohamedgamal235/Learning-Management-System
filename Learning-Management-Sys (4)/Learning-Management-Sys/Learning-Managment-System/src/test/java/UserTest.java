import com.lms.Learning_Managment_System.LearningManagmentSystemApplication;
import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Controller.UserController;
import com.lms.Learning_Managment_System.Service.jwt;
import com.lms.Learning_Managment_System.Service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LearningManagmentSystemApplication.class)
public class UserTest {
    @Mock
    private jwt jwt;

    @Mock
    private EmailService emailService;

    @Spy
    @InjectMocks
    private UserController user;

    private User testUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up JWT mock behavior
        when(jwt.getRoleFromToken(anyString())).thenReturn("instructor");
        when(jwt.generateToken(anyString(), anyString())).thenReturn("dummy-token");

        // Set up test user
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Azzza");
        testUser.setLastName("ali");
        testUser.setEmail("44@gmail.com");
        testUser.setPassword("1234");
        testUser.setRole("instructor");

        // Mock getAllUsers to return our test user
        List<User> users = new ArrayList<>();
        users.add(testUser);
        doReturn(users).when(user).getAllUsers();

        doNothing().when(emailService).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void testRegister() {
        // For registration test, return empty list first
        doReturn(new ArrayList<User>()).when(user).getAllUsers();

        String response = user.registerUser(testUser);
        assertEquals("Successful Registration. Welcome To Learning Management System", response);
    }

    @Test
    void testLogin() {
        String response = user.loginUser("44@gmail.com", "1234");
        assertEquals("Successful Login, Hi Azzza, Token: dummy-token", response);

        String token = response.substring(response.indexOf("Token: ") + 7).trim();
        String role = jwt.getRoleFromToken(token);
        assertNotNull(role);
        assertTrue("instructor".equalsIgnoreCase(role));
    }
}