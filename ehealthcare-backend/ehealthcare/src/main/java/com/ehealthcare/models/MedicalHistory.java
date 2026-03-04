package com.ehealthcare.models;

import java.util.List;

/**
 * MedicalHistory captures a patient's health background.
 * Collected immediately after account creation as per project spec.
 */
public class MedicalHistory {

    private List<String> allergies;
    private List<String> currentMedications;
    private List<String> pastConditions;
    private List<String> surgeries;
    private String bloodType;
    private boolean smoker;
    private boolean drinker;
    private String additionalNotes;

    public MedicalHistory() {}

    public MedicalHistory(List<String> allergies, List<String> currentMedications,
                          List<String> pastConditions, List<String> surgeries,
                          String bloodType, boolean smoker, boolean drinker,
                          String additionalNotes) {
        this.allergies = allergies;
        this.currentMedications = currentMedications;
        this.pastConditions = pastConditions;
        this.surgeries = surgeries;
        this.bloodType = bloodType;
        this.smoker = smoker;
        this.drinker = drinker;
        this.additionalNotes = additionalNotes;
    }

    // --- Getters & Setters ---

    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getCurrentMedications() { return currentMedications; }
    public void setCurrentMedications(List<String> currentMedications) { this.currentMedications = currentMedications; }

    public List<String> getPastConditions() { return pastConditions; }
    public void setPastConditions(List<String> pastConditions) { this.pastConditions = pastConditions; }

    public List<String> getSurgeries() { return surgeries; }
    public void setSurgeries(List<String> surgeries) { this.surgeries = surgeries; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public boolean isSmoker() { return smoker; }
    public void setSmoker(boolean smoker) { this.smoker = smoker; }

    public boolean isDrinker() { return drinker; }
    public void setDrinker(boolean drinker) { this.drinker = drinker; }

    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
}
