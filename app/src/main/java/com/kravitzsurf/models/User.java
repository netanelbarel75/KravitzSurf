package com.kravitzsurf.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private int age;
    private String gender;
    private Map<String, Boolean> enrolledClasses;
    
    public User() {
        // Required for Firebase
        this.enrolledClasses = new HashMap<>();
    }
    
    public User(String name, String email, int age, String gender) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.enrolledClasses = new HashMap<>();
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public Map<String, Boolean> getEnrolledClasses() {
        return enrolledClasses;
    }
    
    public void setEnrolledClasses(Map<String, Boolean> enrolledClasses) {
        this.enrolledClasses = enrolledClasses;
    }
}
