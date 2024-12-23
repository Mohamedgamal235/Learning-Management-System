package com.lms.Learning_Managment_System.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.lms.Learning_Managment_System.Service.jwt;
import com.lms.Learning_Managment_System.Service.EmailService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private List<User> students = new ArrayList<>();
    private List<User> instructors = new ArrayList<>();
    private List<User> admins = new ArrayList<>();
    private static final String STUDENTS_FILE = "students.json";
    private static final String INSTRUCTORS_FILE = "instructors.json";
    private static final String ADMINS_FILE = "admins.json";
    private ObjectMapper objectMapper = new ObjectMapper();
    private int id_counter = 1;

    private List<Integer> IDs_Instructor = new ArrayList<>();

    private Map<String, Integer> loggedInStudents = new HashMap<>();
    private Map<String, Integer> loggedInInstructors = new HashMap<>();
    private Map<String, Integer> loggedInAdmins = new HashMap<>();
    @Autowired
    private EmailService emailService;
    @Autowired
    private jwt jwt;

    public UserController() {
        loadUsersFromFile();
        initCounter();
    }

    public List<Integer> getInstructorsId(){
        return IDs_Instructor;
    }

    private void initCounter(){
        int maxID = getAllUsers().stream().mapToInt(User::getId).max().orElse(0);
        id_counter = maxID+1;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        // Check if email already exists in any list
        if (isEmailExists(user.getEmail())) {
            return "Registration Failed, A user with this email address already exists";
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getRole() == null || user.getRole().isEmpty()) {
            return "Registration Failed: Missing required fields";
        }
        // Add user to the appropriate list based on role
        user.setId(id_counter++);
        if ("student".equalsIgnoreCase(user.getRole())) {
            students.add(user);
            saveUsersToFile(STUDENTS_FILE, students);
        } else if ("instructor".equalsIgnoreCase(user.getRole())) {
            instructors.add(user);
            saveUsersToFile(INSTRUCTORS_FILE, instructors);
        } else if ("admin".equalsIgnoreCase(user.getRole())) {
            admins.add(user);
            saveUsersToFile(ADMINS_FILE, admins);
        } else {
            return "Registration Failed, Invalid role specified. Role must be 'student', 'instructor', or 'admin'.";
        }
        String MailBody ="Dear "+user.getFirstName()+" "+user.getLastName()+"\n\nWelcome to our Learning-Managment-System! We are thrilled to have you on board and excited to support your learning journey.\nHere’s what you can do next to make the most of our platform:\n" +
                "\n" +
                "1. Explore Courses: Browse through the available courses and enroll in the ones that match your interests.\n" +
                "2. Track Your Progress: Stay organized and monitor your achievements through our easy-to-use dashboard.\n" +
                "3. Stay Engaged: Participate in discussions, quizzes, and assignments to deepen your understanding.\nIf you have any questions or need assistance, feel free to contact us.We're here to help!\n\n" +
                "Best regards";
        String MailSubject = "Welcome to our leaning management system platform";
        emailService.sendMail(user.getEmail(),MailSubject,MailBody);
        return "Successful Registration. Welcome To Learning Management System";
    }

    // User login
    @GetMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password) {
        for (User user : getAllUsers()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                String token = jwt.generateToken(user.getEmail(),user.getRole());

                if ("student".equalsIgnoreCase(user.getRole())) {
                    loggedInStudents.put(email, user.getId());
                } else if ("instructor".equalsIgnoreCase(user.getRole())) {
                    loggedInInstructors.put(email, user.getId());
                    IDs_Instructor.add(user.getId());
                } else if ("admin".equalsIgnoreCase(user.getRole())) {
                    loggedInAdmins.put(email, user.getId());
                }
                return "Successful Login, Hi " + user.getFirstName() + ", Token: " + token;
                // should generate token for user logged in
            }
        }
        return "Login Failed, Invalid email or password.";
    }

    ///  Just for testing will be deleted if testing was successful
    /// To test it using postman:
    /// http://localhost:8080/user/login?email=hok.doe@example.com&password=password123
    /// http://localhost:8080/user/course/create
    @PostMapping("/course/create")
    public ResponseEntity<String> createCourse(@RequestHeader("Authorization") String token) {
        // Extract role from the JWT token
        String role = jwt.getRoleFromToken(token);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Invalid token.");
        }
        if ("INSTRUCTOR".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Course Created");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Unauthorized Access");
        }
    }

    public Map<String, Integer> getLoggedInStudents() {
        return loggedInStudents;
    }

    public Map<String, Integer> getLoggedInInstructors() {
        return loggedInInstructors;
    }

    public Map<String, Integer> getLoggedInAdmins() {
        return loggedInAdmins;
    }

    // Get all users (combined list)
    @GetMapping
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(students);
        allUsers.addAll(instructors);
        allUsers.addAll(admins);
        return allUsers;
    }

    // Check if email already exists in any list
    private boolean isEmailExists(String email) {
        return students.stream().anyMatch(user -> user.getEmail().equals(email)) ||
                instructors.stream().anyMatch(user -> user.getEmail().equals(email)) ||
                admins.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    // Load users from the JSON files
    private void loadUsersFromFile() {
        students = loadUsersFromFile(STUDENTS_FILE);
        instructors = loadUsersFromFile(INSTRUCTORS_FILE);
        admins = loadUsersFromFile(ADMINS_FILE);
    }

    private List<User> loadUsersFromFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, User[].class)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    private void saveUsersToFile(String fileName, List<User> userList) {
        try {
            objectMapper.writeValue(new File(fileName), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getInstructorNameById(int id){
        for (User user : instructors) {
            if (user.getId() == id) {
                return user.getFirstName() + " " + user.getLastName();
            }
        }
        return null;
    }
    public String getStudentNameById(int id){
        for (User user : students) {
            if (user.getId() == id) {
                return user.getFirstName() + " " + user.getLastName();
            }
        }
        return null;
    }
    public String getEmailById(int id){
        for (User user : students) {
            if (user.getId() == id) {
                return user.getEmail();
            }
        }
        return null;
    }
}