package com.ehealthcare.models;

import org.bson.types.ObjectId;
import java.util.List;

/**
 * Doctor model.
 * Doctors can view pending appointments, accept/decline, and manage prescriptions.
 */
public class Doctor {

    private ObjectId id;
    private ObjectId userId;           // Reference to users collection
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private String licenseNumber;
    private List<String> availableDays; // e.g. ["MONDAY","WEDNESDAY","FRIDAY"]
    private String consultationStartTime; // e.g. "09:00"
    private String consultationEndTime;   // e.g. "17:00"

    public Doctor() {}

    public Doctor(ObjectId userId, String firstName, String lastName,
                  String email, String phone, String specialization,
                  String licenseNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
    }

    // --- Getters & Setters ---

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return "Dr. " + firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public List<String> getAvailableDays() { return availableDays; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }

    public String getConsultationStartTime() { return consultationStartTime; }
    public void setConsultationStartTime(String consultationStartTime) { this.consultationStartTime = consultationStartTime; }

    public String getConsultationEndTime() { return consultationEndTime; }
    public void setConsultationEndTime(String consultationEndTime) { this.consultationEndTime = consultationEndTime; }
}
