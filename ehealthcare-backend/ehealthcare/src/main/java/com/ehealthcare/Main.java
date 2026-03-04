package com.ehealthcare;

import com.ehealthcare.database.CollectionsInit;
import com.ehealthcare.database.DatabaseConnection;

/**
 * Application entry point.
 * Initialises the MongoDB collections and registers a shutdown hook.
 * The JavaFX UI layer will wire into the service classes:
 *   - AuthService    -> Login / Sign Up screens
 *   - PatientService -> Patient Portal screens
 *   - DoctorService  -> Doctor Portal screens
 *   - ResourceService -> Resource Tracker screen
 */
public class Main {
    public static void main(String[] args) {
        CollectionsInit.initializeCollections();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.close();
        }));

        System.out.println("E-HealthCare backend initialised successfully.");
        System.out.println("Services ready: AuthService | PatientService | DoctorService | ResourceService");
    }
}
