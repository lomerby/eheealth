package com.ehealthcare.models;

import org.bson.types.ObjectId;

/**
 * Base User model shared by both Patient and Doctor roles.
 * Stores core authentication and profile credentials.
 */
public class User {

    private ObjectId id;
    private String email;
    private String passwordHash;
    private String role; // "PATIENT" or "DOCTOR"
    private String firstName;
    private String lastName;
    private String phone;
    private boolean profileComplete;

    public User() {}

    public User(String email, String passwordHash, String role,
                String firstName, String lastName, String phone) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.profileComplete = false;
    }

    // --- Getters & Setters ---

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isProfileComplete() { return profileComplete; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
}
