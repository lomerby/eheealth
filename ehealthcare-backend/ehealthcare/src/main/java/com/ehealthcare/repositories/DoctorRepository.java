package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.Doctor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * DoctorRepository handles all database operations on the 'doctors' collection.
 */
public class DoctorRepository {

    private final MongoCollection<Document> collection;

    public DoctorRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("doctors");
    }

    // --- Create ---

    public ObjectId save(Doctor doctor) {
        Document doc = toDocument(doctor);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public Doctor findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public Doctor findByUserId(ObjectId userId) {
        Document doc = collection.find(Filters.eq("userId", userId)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public Doctor findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        for (Document doc : collection.find()) {
            doctors.add(fromDocument(doc));
        }
        return doctors;
    }

    public List<Doctor> findBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("specialization", specialization))) {
            doctors.add(fromDocument(doc));
        }
        return doctors;
    }

    // --- Update ---

    public void updateAvailability(ObjectId doctorId, List<String> availableDays,
                                   String startTime, String endTime) {
        collection.updateOne(
            Filters.eq("_id", doctorId),
            Updates.combine(
                Updates.set("availableDays", availableDays),
                Updates.set("consultationStartTime", startTime),
                Updates.set("consultationEndTime", endTime)
            )
        );
    }

    // --- Delete ---

    public void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    // --- Mapping Helpers ---

    @SuppressWarnings("unchecked")
    private Document toDocument(Doctor doctor) {
        Document doc = new Document();
        if (doctor.getId() != null) doc.append("_id", doctor.getId());
        doc.append("userId", doctor.getUserId());
        doc.append("firstName", doctor.getFirstName());
        doc.append("lastName", doctor.getLastName());
        doc.append("email", doctor.getEmail());
        doc.append("phone", doctor.getPhone());
        doc.append("specialization", doctor.getSpecialization());
        doc.append("licenseNumber", doctor.getLicenseNumber());
        doc.append("availableDays", doctor.getAvailableDays());
        doc.append("consultationStartTime", doctor.getConsultationStartTime());
        doc.append("consultationEndTime", doctor.getConsultationEndTime());
        return doc;
    }

    @SuppressWarnings("unchecked")
    private Doctor fromDocument(Document doc) {
        Doctor doctor = new Doctor();
        doctor.setId(doc.getObjectId("_id"));
        doctor.setUserId(doc.getObjectId("userId"));
        doctor.setFirstName(doc.getString("firstName"));
        doctor.setLastName(doc.getString("lastName"));
        doctor.setEmail(doc.getString("email"));
        doctor.setPhone(doc.getString("phone"));
        doctor.setSpecialization(doc.getString("specialization"));
        doctor.setLicenseNumber(doc.getString("licenseNumber"));
        doctor.setAvailableDays((List<String>) doc.get("availableDays"));
        doctor.setConsultationStartTime(doc.getString("consultationStartTime"));
        doctor.setConsultationEndTime(doc.getString("consultationEndTime"));
        return doctor;
    }
}
