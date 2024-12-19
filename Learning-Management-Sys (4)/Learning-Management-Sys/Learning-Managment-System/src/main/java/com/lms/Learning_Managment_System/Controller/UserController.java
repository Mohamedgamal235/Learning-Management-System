package com.lms.Learning_Managment_System.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.User;

import lombok.Getter;
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

    private Map<String, Integer> loggedInStudents = new HashMap<>();
    private Map<String, Integer> loggedInInstructors = new HashMap<>();
    private Map<String, Integer> loggedInAdmins = new HashMap<>();

    public UserController() {
        loadUsersFromFile();
        initCounter();
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

        return "Successful Registration. Welcome To Learning Management System";
    }

    // User login
    @GetMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password) {
        for (User user : getAllUsers()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                if ("student".equalsIgnoreCase(user.getRole())) {
                    loggedInStudents.put(email, user.getId());
                } else if ("instructor".equalsIgnoreCase(user.getRole())) {
                    loggedInInstructors.put(email, user.getId());
                } else if ("admin".equalsIgnoreCase(user.getRole())) {
                    loggedInAdmins.put(email, user.getId());
                }
                return "Successful Login, Hi " + user.getFirstName();
            }
        }
        return "Login Failed, Invalid email or password.";
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
                return Arrays.asList(objectMapper.readValue(file, User[].class));
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
}

