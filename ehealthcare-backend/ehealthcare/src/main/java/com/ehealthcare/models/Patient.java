package com.ehealthcare.models;

import org.bson.types.ObjectId;
import java.time.LocalDate;

/**
 * Patient model.
 * After account creation, medical history and allergies are collected
 * before the patient can access the portal.
 */
public class Patient {

    private ObjectId id;
    private ObjectId userId;       // Reference to users collection
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private MedicalHistory medicalHistory;
    private boolean medicalHistoryComplete; // Gate: must complete before portal access

    public Patient() {}

    public Patient(ObjectId userId, String firstName, String lastName,
                   String email, String phone, LocalDate dateOfBirth,
                   String gender, String address) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.medicalHistoryComplete = false;
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

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public MedicalHistory getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
        this.medicalHistoryComplete = true;
    }

    public boolean isMedicalHistoryComplete() { return medicalHistoryComplete; }
    public void setMedicalHistoryComplete(boolean complete) { this.medicalHistoryComplete = complete; }
}
