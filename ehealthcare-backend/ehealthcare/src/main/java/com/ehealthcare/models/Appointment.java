package com.ehealthcare.models;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;

/**
 * Appointment model.
 *
 * Status lifecycle:
 *   PENDING  -> Doctor sees it on portal and accepts/declines
 *   SCHEDULED -> Doctor accepted; patient sees it as Scheduled
 *   DECLINED  -> Doctor declined; patient can re-book
 *   COMPLETED -> Consultation done
 *   CANCELLED -> Patient cancelled before appointment
 */
public class Appointment {

    public enum Status {
        PENDING,
        SCHEDULED,
        DECLINED,
        COMPLETED,
        CANCELLED
    }

    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDateTime;
    private String reason;           // Patient's reason for visit
    private String status;           // Uses Status enum values as strings
    private String doctorNotes;      // Added by doctor after accepting/completing
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Appointment() {}

    public Appointment(ObjectId patientId, ObjectId doctorId,
                       String patientName, String doctorName,
                       LocalDateTime appointmentDateTime, String reason) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
        this.status = Status.PENDING.name();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getPatientId() { return patientId; }
    public void setPatientId(ObjectId patientId) { this.patientId = patientId; }

    public ObjectId getDoctorId() { return doctorId; }
    public void setDoctorId(ObjectId doctorId) { this.doctorId = doctorId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
