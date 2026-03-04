package com.ehealthcare.models;

import org.bson.types.ObjectId;
import java.time.LocalDate;

/**
 * MedicalRecord stores a single consultation record.
 * Created by a doctor after a completed appointment.
 * Patients can view all their records via the Patient Portal.
 */
public class MedicalRecord {

    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private ObjectId appointmentId;
    private String doctorName;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDate recordDate;

    public MedicalRecord() {}

    public MedicalRecord(ObjectId patientId, ObjectId doctorId, ObjectId appointmentId,
                         String doctorName, String diagnosis, String treatment, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;
        this.recordDate = LocalDate.now();
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

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
}
