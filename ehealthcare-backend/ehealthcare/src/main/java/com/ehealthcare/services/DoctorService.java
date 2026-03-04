package com.ehealthcare.services;

import com.ehealthcare.models.Appointment;
import com.ehealthcare.models.Doctor;
import com.ehealthcare.models.MedicalRecord;
import com.ehealthcare.models.Patient;
import com.ehealthcare.models.Prescription;
import com.ehealthcare.repositories.AppointmentRepository;
import com.ehealthcare.repositories.DoctorRepository;
import com.ehealthcare.repositories.MedicalRecordRepository;
import com.ehealthcare.repositories.PatientRepository;
import com.ehealthcare.repositories.PrescriptionRepository;
import com.ehealthcare.utils.ValidationUtil;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;

/**
 * DoctorService contains all business logic for the Doctor Portal:
 *
 *  1. getPendingAppointments()   — Shows all PENDING appointments awaiting action.
 *
 *  2. acceptAppointment()        — Doctor accepts a PENDING appointment.
 *     Status changes PENDING -> SCHEDULED, immediately visible on Patient Portal.
 *
 *  3. declineAppointment()       — Doctor declines with an optional reason.
 *     Status changes PENDING -> DECLINED.
 *
 *  4. completeAppointment()      — Marks the appointment as completed post-consultation.
 *
 *  5. issuePrescription()        — Doctor issues a digital prescription linked
 *     to a patient and optionally an appointment.
 *
 *  6. createMedicalRecord()      — Doctor creates a consultation record for a patient.
 *
 *  7. getPatientHistory()        — Doctor views a patient's full medical history,
 *     records, and prescriptions.
 *
 *  8. updateAvailability()       — Doctor sets available days and consultation hours.
 */
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DoctorService() {
        this.doctorRepository = new DoctorRepository();
        this.appointmentRepository = new AppointmentRepository();
        this.patientRepository = new PatientRepository();
        this.medicalRecordRepository = new MedicalRecordRepository();
        this.prescriptionRepository = new PrescriptionRepository();
    }

    // -------------------------------------------------------------------------
    // DOCTOR PROFILE
    // -------------------------------------------------------------------------

    public Doctor getDoctorProfile(ObjectId userId) {
        return doctorRepository.findByUserId(userId);
    }

    /**
     * Updates the doctor's available days and consultation hours.
     * Used to populate the DoctorAvailability collection.
     */
    public void updateAvailability(ObjectId userId, List<String> availableDays,
                                   String startTime, String endTime) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) throw new IllegalArgumentException("Doctor not found.");
        doctorRepository.updateAvailability(doctor.getId(), availableDays, startTime, endTime);
        System.out.println("Availability updated for: " + doctor.getFullName());
    }

    // -------------------------------------------------------------------------
    // APPOINTMENT MANAGEMENT
    // -------------------------------------------------------------------------

    /**
     * Returns all PENDING appointments for this doctor.
     * These are appointments booked by patients awaiting the doctor's decision.
     * Shown prominently on the Doctor Portal dashboard.
     */
    public List<Appointment> getPendingAppointments(ObjectId userId) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) return List.of();
        return appointmentRepository.findPendingByDoctorId(doctor.getId());
    }

    /**
     * Returns all SCHEDULED (accepted) appointments for this doctor.
     */
    public List<Appointment> getScheduledAppointments(ObjectId userId) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) return List.of();
        return appointmentRepository.findScheduledByDoctorId(doctor.getId());
    }

    /**
     * Returns all appointments for this doctor across all statuses.
     */
    public List<Appointment> getAllAppointments(ObjectId userId) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) return List.of();
        return appointmentRepository.findByDoctorId(doctor.getId());
    }

    /**
     * Doctor ACCEPTS a pending appointment.
     *
     * Status: PENDING -> SCHEDULED
     *
     * This update is persisted to MongoDB and the Patient Portal
     * will immediately reflect the change:
     *   - Appointment moves from "Pending" to "Scheduled" on patient's view.
     *
     * @param userId        The doctor's user ID (for ownership verification)
     * @param appointmentId The appointment to accept
     * @param notes         Optional doctor notes / confirmation message
     */
    public Appointment acceptAppointment(ObjectId userId, ObjectId appointmentId, String notes) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) throw new IllegalArgumentException("Doctor profile not found.");

        Appointment appt = appointmentRepository.findById(appointmentId);
        if (appt == null) throw new IllegalArgumentException("Appointment not found.");

        if (!appt.getDoctorId().equals(doctor.getId())) {
            throw new SecurityException("You can only manage your own appointments.");
        }
        if (!Appointment.Status.PENDING.name().equals(appt.getStatus())) {
            throw new IllegalStateException("Only PENDING appointments can be accepted.");
        }

        appointmentRepository.acceptAppointment(appointmentId, notes);
        appt.setStatus(Appointment.Status.SCHEDULED.name());
        appt.setDoctorNotes(notes);

        System.out.println("Appointment ACCEPTED by " + doctor.getFullName()
            + " for patient: " + appt.getPatientName()
            + " on " + appt.getAppointmentDateTime());
        return appt;
    }

    /**
     * Doctor DECLINES a pending appointment.
     *
     * Status: PENDING -> DECLINED
     *
     * @param userId        The doctor's user ID
     * @param appointmentId The appointment to decline
     * @param reason        Reason for declining (shown to patient)
     */
    public Appointment declineAppointment(ObjectId userId, ObjectId appointmentId, String reason) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) throw new IllegalArgumentException("Doctor profile not found.");

        Appointment appt = appointmentRepository.findById(appointmentId);
        if (appt == null) throw new IllegalArgumentException("Appointment not found.");

        if (!appt.getDoctorId().equals(doctor.getId())) {
            throw new SecurityException("You can only manage your own appointments.");
        }
        if (!Appointment.Status.PENDING.name().equals(appt.getStatus())) {
            throw new IllegalStateException("Only PENDING appointments can be declined.");
        }

        appointmentRepository.declineAppointment(appointmentId, reason);
        appt.setStatus(Appointment.Status.DECLINED.name());
        appt.setDoctorNotes(reason);

        System.out.println("Appointment DECLINED by " + doctor.getFullName()
            + " for patient: " + appt.getPatientName());
        return appt;
    }

    /**
     * Marks an appointment as COMPLETED after the consultation.
     */
    public void completeAppointment(ObjectId userId, ObjectId appointmentId) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) throw new IllegalArgumentException("Doctor profile not found.");

        Appointment appt = appointmentRepository.findById(appointmentId);
        if (appt == null) throw new IllegalArgumentException("Appointment not found.");

        if (!appt.getDoctorId().equals(doctor.getId())) {
            throw new SecurityException("You can only manage your own appointments.");
        }
        if (!Appointment.Status.SCHEDULED.name().equals(appt.getStatus())) {
            throw new IllegalStateException("Only SCHEDULED appointments can be completed.");
        }

        appointmentRepository.completeAppointment(appointmentId);
        System.out.println("Appointment COMPLETED: " + appointmentId);
    }

    // -------------------------------------------------------------------------
    // PATIENT HISTORY (Doctor view)
    // -------------------------------------------------------------------------

    /**
     * Doctor views a patient's full profile including their medical history.
     * Called when the doctor reviews a patient before/during consultation.
     */
    public Patient getPatientProfile(ObjectId patientId) {
        return patientRepository.findById(patientId);
    }

    /**
     * Doctor retrieves a patient's medical records (all past consultations).
     */
    public List<MedicalRecord> getPatientMedicalRecords(ObjectId patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    /**
     * Doctor retrieves a patient's prescription history.
     */
    public List<Prescription> getPatientPrescriptions(ObjectId patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    // -------------------------------------------------------------------------
    // MEDICAL RECORDS
    // -------------------------------------------------------------------------

    /**
     * Doctor creates a consultation record after completing an appointment.
     * This record is visible to the patient via the Patient Portal.
     */
    public MedicalRecord createMedicalRecord(ObjectId userId, ObjectId patientId,
                                              ObjectId appointmentId, String diagnosis,
                                              String treatment, String notes) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) throw new IllegalArgumentException("Doctor profile not found.");

        if (!ValidationUtil.isNotBlank(diagnosis)) {
            throw new IllegalArgumentException("Diagnosis is required.");
        }

        MedicalRecord record = new MedicalRecord(
            patientId, doctor.getId(), appointmentId,
            doctor.getFullName(), diagnosis, treatment, notes
        );

        ObjectId id = medicalRecordRepository.save(record);
        record.setId(id);
        System.out.println("Medical record created for patient: " + patientId + " by " + doctor.getFullName());
        return record;
    }

    // -------------------------------------------------------------------------
    // PRESCRIPTIONS
    // -------------------------------------------------------------------------

    /**
     * Doctor issues a digital prescription to a patient.
     * Saved to the 'prescriptions' collection and immediately visible
     * to the patient on their portal.
     *
     * @param userId         The doctor's user ID
     * @param patientId      The patient to prescribe to
     * @param appointmentId  The linked appointment (can be null)
     * @param medications    List of PrescriptionItems
     * @param diagnosis      The diagnosed condition
     * @param instructions   General instructions (e.g. "Take with food")
     * @param expiryDate     When the prescription expires
     */
    public Prescription issuePrescription(ObjectId userId, ObjectId patientId,
                                           ObjectId appointmentId,
                                           List<Prescription.PrescriptionItem> medications,
                                           String diagnosis, String instructions,
                                           LocalDate expiryDate) {
        Doctor doctor = doctorRepository.findByUserId(userId);
        if (doctor == null) throw new IllegalArgumentException("Doctor profile not found.");

        Patient patient = patientRepository.findById(patientId);
        if (patient == null) throw new IllegalArgumentException("Patient not found.");

        if (medications == null || medications.isEmpty()) {
            throw new IllegalArgumentException("At least one medication is required.");
        }
        if (!ValidationUtil.isNotBlank(diagnosis)) {
            throw new IllegalArgumentException("Diagnosis is required.");
        }

        Prescription prescription = new Prescription(
            patientId, doctor.getId(), appointmentId,
            patient.getFullName(), doctor.getFullName(),
            medications, diagnosis, instructions, expiryDate
        );

        ObjectId id = prescriptionRepository.save(prescription);
        prescription.setId(id);
        System.out.println("Prescription issued by " + doctor.getFullName()
            + " to " + patient.getFullName());
        return prescription;
    }
}
