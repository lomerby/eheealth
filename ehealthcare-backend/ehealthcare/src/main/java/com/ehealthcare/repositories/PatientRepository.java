package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.MedicalHistory;
import com.ehealthcare.models.Patient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PatientRepository handles all database operations on the 'patients' collection.
 */
public class PatientRepository {

    private final MongoCollection<Document> collection;

    public PatientRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("patients");
    }

    // --- Create ---

    public ObjectId save(Patient patient) {
        Document doc = toDocument(patient);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public Patient findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public Patient findByUserId(ObjectId userId) {
        Document doc = collection.find(Filters.eq("userId", userId)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public Patient findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        for (Document doc : collection.find()) {
            patients.add(fromDocument(doc));
        }
        return patients;
    }

    // --- Update ---

    /**
     * Saves the patient's medical history and marks the history as complete.
     * Called immediately after account creation.
     */
    public void saveMedicalHistory(ObjectId patientId, MedicalHistory history) {
        Document historyDoc = medicalHistoryToDocument(history);
        collection.updateOne(
            Filters.eq("_id", patientId),
            Updates.combine(
                Updates.set("medicalHistory", historyDoc),
                Updates.set("medicalHistoryComplete", true)
            )
        );
    }

    public void updateProfile(Patient patient) {
        collection.updateOne(
            Filters.eq("_id", patient.getId()),
            Updates.combine(
                Updates.set("firstName", patient.getFirstName()),
                Updates.set("lastName", patient.getLastName()),
                Updates.set("phone", patient.getPhone()),
                Updates.set("address", patient.getAddress())
            )
        );
    }

    // --- Delete ---

    public void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    // --- Mapping Helpers ---

    private Document toDocument(Patient patient) {
        Document doc = new Document();
        if (patient.getId() != null) doc.append("_id", patient.getId());
        doc.append("userId", patient.getUserId());
        doc.append("firstName", patient.getFirstName());
        doc.append("lastName", patient.getLastName());
        doc.append("email", patient.getEmail());
        doc.append("phone", patient.getPhone());
        if (patient.getDateOfBirth() != null) {
            doc.append("dateOfBirth", patient.getDateOfBirth().toString());
        }
        doc.append("gender", patient.getGender());
        doc.append("address", patient.getAddress());
        doc.append("medicalHistoryComplete", patient.isMedicalHistoryComplete());
        if (patient.getMedicalHistory() != null) {
            doc.append("medicalHistory", medicalHistoryToDocument(patient.getMedicalHistory()));
        }
        return doc;
    }

    private Patient fromDocument(Document doc) {
        Patient patient = new Patient();
        patient.setId(doc.getObjectId("_id"));
        patient.setUserId(doc.getObjectId("userId"));
        patient.setFirstName(doc.getString("firstName"));
        patient.setLastName(doc.getString("lastName"));
        patient.setEmail(doc.getString("email"));
        patient.setPhone(doc.getString("phone"));
        String dob = doc.getString("dateOfBirth");
        if (dob != null) patient.setDateOfBirth(LocalDate.parse(dob));
        patient.setGender(doc.getString("gender"));
        patient.setAddress(doc.getString("address"));
        patient.setMedicalHistoryComplete(Boolean.TRUE.equals(doc.getBoolean("medicalHistoryComplete")));
        Document histDoc = (Document) doc.get("medicalHistory");
        if (histDoc != null) {
            patient.setMedicalHistory(medicalHistoryFromDocument(histDoc));
        }
        return patient;
    }

    private Document medicalHistoryToDocument(MedicalHistory h) {
        return new Document()
            .append("allergies", h.getAllergies())
            .append("currentMedications", h.getCurrentMedications())
            .append("pastConditions", h.getPastConditions())
            .append("surgeries", h.getSurgeries())
            .append("bloodType", h.getBloodType())
            .append("smoker", h.isSmoker())
            .append("drinker", h.isDrinker())
            .append("additionalNotes", h.getAdditionalNotes());
    }

    @SuppressWarnings("unchecked")
    private MedicalHistory medicalHistoryFromDocument(Document doc) {
        MedicalHistory h = new MedicalHistory();
        h.setAllergies((List<String>) doc.get("allergies"));
        h.setCurrentMedications((List<String>) doc.get("currentMedications"));
        h.setPastConditions((List<String>) doc.get("pastConditions"));
        h.setSurgeries((List<String>) doc.get("surgeries"));
        h.setBloodType(doc.getString("bloodType"));
        h.setSmoker(Boolean.TRUE.equals(doc.getBoolean("smoker")));
        h.setDrinker(Boolean.TRUE.equals(doc.getBoolean("drinker")));
        h.setAdditionalNotes(doc.getString("additionalNotes"));
        return h;
    }
}
