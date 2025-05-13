package com.kravitzsurf.models;

public class SurfClass {
    private String id;
    private String title;
    private String description;
    private String instructor;
    private long dateTime;
    private int duration; // in minutes
    private int capacity;
    private double price;
    private String type; // group, private, parent_child
    private String location;
    
    public SurfClass() {
        // Required for Firebase
    }
    
    public SurfClass(String title, String description, String instructor, long dateTime, 
                     int duration, int capacity, double price, String type, String location) {
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.dateTime = dateTime;
        this.duration = duration;
        this.capacity = capacity;
        this.price = price;
        this.type = type;
        this.location = location;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
    
    public long getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}
