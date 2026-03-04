package com.ehealthcare.services;

import com.ehealthcare.models.Doctor;
import com.ehealthcare.models.Patient;
import com.ehealthcare.models.User;
import com.ehealthcare.repositories.DoctorRepository;
import com.ehealthcare.repositories.PatientRepository;
import com.ehealthcare.repositories.UserRepository;
import com.ehealthcare.utils.AuthResult;
import com.ehealthcare.utils.PasswordUtil;
import com.ehealthcare.utils.ValidationUtil;
import org.bson.types.ObjectId;

import java.time.LocalDate;

/**
 * AuthService handles all authentication operations:
 *   - Patient registration (creates User + Patient records in DB)
 *   - Doctor registration (creates User + Doctor records in DB)
 *   - Login (validates credentials, returns AuthResult with the User)
 *   - Password update
 *
 * After a patient signs up, medicalHistoryComplete = false.
 * The PatientService must be called next to collect medical history
 * before the patient can access the full portal.
 */
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
        this.patientRepository = new PatientRepository();
        this.doctorRepository = new DoctorRepository();
    }

    // -------------------------------------------------------------------------
    // PATIENT REGISTRATION
    // -------------------------------------------------------------------------

    /**
     * Registers a new patient.
     * Saves to 'users' and 'patients' collections.
     * medicalHistoryComplete is set to false — patient must complete it before
     * accessing the portal (enforced in PatientService).
     *
     * @return AuthResult with the newly created User on success
     */
    public AuthResult registerPatient(String firstName, String lastName,
                                      String email, String password,
                                      String phone, String dateOfBirthStr,
                                      String gender, String address) {
        // --- Validation ---
        if (!ValidationUtil.isValidEmail(email)) {
            return AuthResult.failure("Invalid email address.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return AuthResult.failure("Password must be at least 8 characters and include a letter and a digit.");
        }
        if (!ValidationUtil.isNotBlank(firstName) || !ValidationUtil.isNotBlank(lastName)) {
            return AuthResult.failure("First and last name are required.");
        }
        if (userRepository.existsByEmail(email)) {
            return AuthResult.failure("An account with this email already exists.");
        }

        // --- Create User record ---
        String passwordHash = PasswordUtil.hash(password);
        User user = new User(email, passwordHash, "PATIENT", firstName, lastName, phone);
        ObjectId userId = userRepository.save(user);
        user.setId(userId);

        // --- Create Patient record ---
        LocalDate dob = null;
        if (ValidationUtil.isNotBlank(dateOfBirthStr)) {
            try { dob = LocalDate.parse(dateOfBirthStr); } catch (Exception ignored) {}
        }
        Patient patient = new Patient(userId, firstName, lastName, email, phone, dob, gender, address);
        patientRepository.save(patient);

        System.out.println("Patient registered: " + email);
        return AuthResult.success(user, "Registration successful! Please complete your medical history to continue.");
    }

    // -------------------------------------------------------------------------
    // DOCTOR REGISTRATION
    // -------------------------------------------------------------------------

    /**
     * Registers a new doctor.
     * Saves to 'users' and 'doctors' collections.
     *
     * @return AuthResult with the newly created User on success
     */
    public AuthResult registerDoctor(String firstName, String lastName,
                                     String email, String password,
                                     String phone, String specialization,
                                     String licenseNumber) {
        // --- Validation ---
        if (!ValidationUtil.isValidEmail(email)) {
            return AuthResult.failure("Invalid email address.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return AuthResult.failure("Password must be at least 8 characters and include a letter and a digit.");
        }
        if (!ValidationUtil.isNotBlank(specialization)) {
            return AuthResult.failure("Specialization is required for doctor registration.");
        }
        if (!ValidationUtil.isNotBlank(licenseNumber)) {
            return AuthResult.failure("Medical license number is required.");
        }
        if (userRepository.existsByEmail(email)) {
            return AuthResult.failure("An account with this email already exists.");
        }

        // --- Create User record ---
        String passwordHash = PasswordUtil.hash(password);
        User user = new User(email, passwordHash, "DOCTOR", firstName, lastName, phone);
        user.setProfileComplete(true); // Doctors don't have the post-registration questionnaire
        ObjectId userId = userRepository.save(user);
        user.setId(userId);

        // --- Create Doctor record ---
        Doctor doctor = new Doctor(userId, firstName, lastName, email, phone, specialization, licenseNumber);
        doctorRepository.save(doctor);

        System.out.println("Doctor registered: " + email);
        return AuthResult.success(user, "Doctor account created successfully. Welcome, Dr. " + lastName + ".");
    }

    // -------------------------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------------------------

    /**
     * Authenticates a user (patient or doctor) by email and password.
     *
     * @return AuthResult with the User on success, or a failure message
     */
    public AuthResult login(String email, String password) {
        if (!ValidationUtil.isValidEmail(email)) {
            return AuthResult.failure("Invalid email address.");
        }
        if (!ValidationUtil.isNotBlank(password)) {
            return AuthResult.failure("Password is required.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return AuthResult.failure("No account found with this email address.");
        }

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            return AuthResult.failure("Incorrect password. Please try again.");
        }

        String welcomeMessage;
        if ("PATIENT".equals(user.getRole())) {
            Patient patient = patientRepository.findByUserId(user.getId());
            if (patient != null && !patient.isMedicalHistoryComplete()) {
                welcomeMessage = "Welcome back! Please complete your medical history before proceeding.";
            } else {
                welcomeMessage = "Welcome back, " + user.getFirstName() + "!";
            }
        } else {
            welcomeMessage = "Welcome back, Dr. " + user.getLastName() + "!";
        }

        System.out.println("Login successful: " + email + " [" + user.getRole() + "]");
        return AuthResult.success(user, welcomeMessage);
    }

    // -------------------------------------------------------------------------
    // PASSWORD MANAGEMENT
    // -------------------------------------------------------------------------

    /**
     * Updates a user's password after verifying the old one.
     */
    public AuthResult changePassword(ObjectId userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) return AuthResult.failure("User not found.");

        if (!PasswordUtil.verify(oldPassword, user.getPasswordHash())) {
            return AuthResult.failure("Current password is incorrect.");
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            return AuthResult.failure("New password must be at least 8 characters with a letter and digit.");
        }

        userRepository.updatePassword(userId, PasswordUtil.hash(newPassword));
        return AuthResult.success(user, "Password updated successfully.");
    }
}
