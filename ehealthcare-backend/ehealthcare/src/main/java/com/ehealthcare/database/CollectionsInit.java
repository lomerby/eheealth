package com.ehealthcare.database;

import com.mongodb.client.MongoDatabase;

public class CollectionsInit {

    public static void initializeCollections() {
        MongoDatabase db = DatabaseConnection.getDatabase();

        String[] collections = {
            "users",
            "patients",
            "doctors",
            "doctorAvailability",
            "medicalRecords",
            "appointments",
            "prescriptions",
            "medications",
            "resources"
        };

        for (String name : collections) {
            boolean exists = false;
            for (String col : db.listCollectionNames()) {
                if (col.equals(name)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                db.createCollection(name);
                System.out.println("Created collection: " + name);
            } else {
                System.out.println("Collection already exists: " + name);
            }
        }
    }
}
