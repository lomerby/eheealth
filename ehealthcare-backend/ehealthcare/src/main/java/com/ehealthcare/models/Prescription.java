package com.ehealthcare.models;

import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.util.List;

/**
 * Prescription model.
 * Doctors issue digital prescriptions via the Doctor Portal.
 * Patients can view them through the Patient Portal.
 */
public class Prescription {

    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private ObjectId appointmentId;  // Linked to the appointment (optional for walk-in)
    private String patientName;
    private String doctorName;
    private List<PrescriptionItem> medications;
    private String diagnosis;
    private String instructions;
    private LocalDate issuedDate;
    private LocalDate expiryDate;

    public Prescription() {}

    public Prescription(ObjectId patientId, ObjectId doctorId, ObjectId appointmentId,
                        String patientName, String doctorName,
                        List<PrescriptionItem> medications, String diagnosis,
                        String instructions, LocalDate expiryDate) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.medications = medications;
        this.diagnosis = diagnosis;
        this.instructions = instructions;
        this.issuedDate = LocalDate.now();
        this.expiryDate = expiryDate;
    }

    // --- Getters & Setters ---

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getPatientId() { return patientId; }
    public void setPatientId(ObjectId patientId) { this.patientId = patientId; }

    public ObjectId getDoctorId() { return doctorId; }
    public void setDoctorId(ObjectId doctorId) { this.doctorId = doctorId; }

    public ObjectId getAppointmentId() { return appointmentId; }
    public void setAppointmentId(ObjectId appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public List<PrescriptionItem> getMedications() { return medications; }
    public void setMedications(List<PrescriptionItem> medications) { this.medications = medications; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    // --- Inner class for each medication line item ---

    public static class PrescriptionItem {
        private String medicationName;
        private String dosage;
        private String frequency;
        private int durationDays;

        public PrescriptionItem() {}

        public PrescriptionItem(String medicationName, String dosage,
                                 String frequency, int durationDays) {
            this.medicationName = medicationName;
            this.dosage = dosage;
            this.frequency = frequency;
            this.durationDays = durationDays;
        }

        public String getMedicationName() { return medicationName; }
        public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public int getDurationDays() { return durationDays; }
        public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    }
}
