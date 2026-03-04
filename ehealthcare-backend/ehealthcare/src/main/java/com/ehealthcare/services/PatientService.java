package com.ehealthcare.services;

import com.ehealthcare.models.Appointment;
import com.ehealthcare.models.Doctor;
import com.ehealthcare.models.MedicalHistory;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * PatientService contains all business logic for the Patient Portal:
 *
 *  1. completeMedicalHistory() — Required gate after account creation.
 *     Collects allergies, conditions, medications, and lifestyle info.
 *
 *  2. bookAppointment()        — Creates a PENDING appointment visible
 *     on the Doctor Portal. Patient sees it as "Pending Appointments".
 *
 *  3. getPendingAppointments() — Lists appointments awaiting doctor acceptance.
 *
 *  4. getScheduledAppointments() — Lists doctor-accepted appointments.
 *     Status changes from PENDING -> SCHEDULED when doctor accepts.
 *
 *  5. getMedicalRecords()      — Patient views full consultation history.
 *
 *  6. getPrescriptions()       — Patient views prescriptions issued by doctors.
 *
 *  7. cancelAppointment()      — Patient cancels a pending/scheduled appointment.
 *
 *  8. updateProfile()          — Patient updates personal details.
 */
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PatientService() {
        this.patientRepository = new PatientRepository();
        this.appointmentRepository = new AppointmentRepository();
        this.doctorRepository = new DoctorRepository();
        this.medicalRecordRepository = new MedicalRecordRepository();
        this.prescriptionRepository = new PrescriptionRepository();
    }

    // -------------------------------------------------------------------------
    // MEDICAL HISTORY — Required after account creation
    // -------------------------------------------------------------------------

    /**
     * Saves the patient's medical history after they sign up.
     * Until this is called, the patient cannot access the full portal.
     *
     * @param userId  The logged-in user's ID
     * @param history The completed MedicalHistory object
     */
    public void completeMedicalHistory(ObjectId userId, MedicalHistory history) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient profile not found for user ID: " + userId);
        }
        patientRepository.saveMedicalHistory(patient.getId(), history);
        System.out.println("Medical history saved for patient: " + patient.getFullName());
    }

    /**
     * Checks whether the patient has completed their medical history.
     * The UI should redirect to the medical history form if this returns false.
     */
    public boolean isMedicalHistoryComplete(ObjectId userId) {
        Patient patient = patientRepository.findByUserId(userId);
        return patient != null && patient.isMedicalHistoryComplete();
    }

    // -------------------------------------------------------------------------
    // PATIENT PROFILE
    // -------------------------------------------------------------------------

    public Patient getPatientProfile(ObjectId userId) {
        return patientRepository.findByUserId(userId);
    }

    public void updateProfile(ObjectId userId, String phone, String address) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) throw new IllegalArgumentException("Patient not found.");
        patient.setPhone(phone);
        patient.setAddress(address);
        patientRepository.updateProfile(patient);
    }

    // -------------------------------------------------------------------------
    // APPOINTMENT BOOKING
    // -------------------------------------------------------------------------

    /**
     * Books an appointment with a selected doctor.
     * The appointment is saved with status = PENDING and becomes immediately
     * visible on the Doctor Portal.
     *
     * @param userId              The patient's user ID
     * @param doctorId            The selected doctor's ObjectId
     * @param appointmentDateTime The requested date and time
     * @param reason              The reason for visit
     * @return The newly created Appointment
     */
    public Appointment bookAppointment(ObjectId userId, ObjectId doctorId,
                                       LocalDateTime appointmentDateTime, String reason) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) throw new IllegalArgumentException("Patient profile not found.");

        if (!patient.isMedicalHistoryComplete()) {
            throw new IllegalStateException("Please complete your medical history before booking an appointment.");
        }

        if (!ValidationUtil.isNotBlank(reason)) {
            throw new IllegalArgumentException("Please provide a reason for your appointment.");
        }
        if (appointmentDateTime == null || appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date must be in the future.");
        }

        Doctor doctor = doctorRepository.findById(doctorId);
        if (doctor == null) throw new IllegalArgumentException("Selected doctor not found.");

        Appointment appointment = new Appointment(
            patient.getId(),
            doctorId,
            patient.getFullName(),
            doctor.getFullName(),
            appointmentDateTime,
            reason
        );

        ObjectId id = appointmentRepository.save(appointment);
        appointment.setId(id);

        System.out.println("Appointment booked: " + patient.getFullName()
            + " -> " + doctor.getFullName() + " on " + appointmentDateTime + " [PENDING]");
        return appointment;
    }

    /**
     * Returns all PENDING appointments for a patient.
     * Displayed on the Patient Portal as "Pending Appointments".
     */
    public List<Appointment> getPendingAppointments(ObjectId userId) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) return List.of();
        return appointmentRepository.findByPatientIdAndStatus(
            patient.getId(), Appointment.Status.PENDING.name());
    }

    /**
     * Returns all SCHEDULED appointments for a patient.
     * These were PENDING and were accepted by the doctor.
     * Displayed on the Patient Portal as "Scheduled Appointments".
     */
    public List<Appointment> getScheduledAppointments(ObjectId userId) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) return List.of();
        return appointmentRepository.findByPatientIdAndStatus(
            patient.getId(), Appointment.Status.SCHEDULED.name());
    }

    /**
     * Returns ALL appointments for a patient across all statuses.
     */
    public List<Appointment> getAllAppointments(ObjectId userId) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) return List.of();
        return appointmentRepository.findByPatientId(patient.getId());
    }

    /**
     * Patient cancels a pending or scheduled appointment.
     */
    public void cancelAppointment(ObjectId userId, ObjectId appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId);
        if (appt == null) throw new IllegalArgumentException("Appointment not found.");

        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null || !appt.getPatientId().equals(patient.getId())) {
            throw new SecurityException("You can only cancel your own appointments.");
        }

        String currentStatus = appt.getStatus();
        if (Appointment.Status.COMPLETED.name().equals(currentStatus) ||
            Appointment.Status.CANCELLED.name().equals(currentStatus)) {
            throw new IllegalStateException("This appointment cannot be cancelled.");
        }

        appointmentRepository.cancelAppointment(appointmentId);
        System.out.println("Appointment cancelled: " + appointmentId);
    }

    // -------------------------------------------------------------------------
    // MEDICAL RECORDS & PRESCRIPTIONS
    // -------------------------------------------------------------------------

    /**
     * Returns all medical records for the patient, sorted most recent first.
     */
    public List<MedicalRecord> getMedicalRecords(ObjectId userId) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) return List.of();
        return medicalRecordRepository.findByPatientId(patient.getId());
    }

    /**
     * Returns all prescriptions issued to the patient, sorted newest first.
     */
    public List<Prescription> getPrescriptions(ObjectId userId) {
        Patient patient = patientRepository.findByUserId(userId);
        if (patient == null) return List.of();
        return prescriptionRepository.findByPatientId(patient.getId());
    }

    // -------------------------------------------------------------------------
    // DOCTOR BROWSING
    // -------------------------------------------------------------------------

    /**
     * Returns all registered doctors — used to let patients choose
     * a doctor when booking an appointment.
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Returns doctors by specialization — for filtered appointment booking.
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }
}
