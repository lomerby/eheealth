package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.Prescription;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PrescriptionRepository handles all database operations on the 'prescriptions' collection.
 */
public class PrescriptionRepository {

    private final MongoCollection<Document> collection;

    public PrescriptionRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("prescriptions");
    }

    // --- Create ---

    public ObjectId save(Prescription prescription) {
        Document doc = toDocument(prescription);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public Prescription findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Prescription> findByPatientId(ObjectId patientId) {
        List<Prescription> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("patientId", patientId))
                                      .sort(Sorts.descending("issuedDate"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public List<Prescription> findByDoctorId(ObjectId doctorId) {
        List<Prescription> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("doctorId", doctorId))
                                      .sort(Sorts.descending("issuedDate"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public List<Prescription> findByAppointmentId(ObjectId appointmentId) {
        List<Prescription> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("appointmentId", appointmentId))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    // --- Mapping Helpers ---

    private Document toDocument(Prescription p) {
        Document doc = new Document();
        if (p.getId() != null) doc.append("_id", p.getId());
        doc.append("patientId", p.getPatientId());
        doc.append("doctorId", p.getDoctorId());
        doc.append("appointmentId", p.getAppointmentId());
        doc.append("patientName", p.getPatientName());
        doc.append("doctorName", p.getDoctorName());
        doc.append("diagnosis", p.getDiagnosis());
        doc.append("instructions", p.getInstructions());
        doc.append("issuedDate", p.getIssuedDate() != null ? p.getIssuedDate().toString() : null);
        doc.append("expiryDate", p.getExpiryDate() != null ? p.getExpiryDate().toString() : null);

        List<Document> medDocs = new ArrayList<>();
        if (p.getMedications() != null) {
            for (Prescription.PrescriptionItem item : p.getMedications()) {
                medDocs.add(new Document()
                    .append("medicationName", item.getMedicationName())
                    .append("dosage", item.getDosage())
                    .append("frequency", item.getFrequency())
                    .append("durationDays", item.getDurationDays()));
            }
        }
        doc.append("medications", medDocs);
        return doc;
    }

    @SuppressWarnings("unchecked")
    private Prescription fromDocument(Document doc) {
        Prescription p = new Prescription();
        p.setId(doc.getObjectId("_id"));
        p.setPatientId(doc.getObjectId("patientId"));
        p.setDoctorId(doc.getObjectId("doctorId"));
        p.setAppointmentId(doc.getObjectId("appointmentId"));
        p.setPatientName(doc.getString("patientName"));
        p.setDoctorName(doc.getString("doctorName"));
        p.setDiagnosis(doc.getString("diagnosis"));
        p.setInstructions(doc.getString("instructions"));
        String issued = doc.getString("issuedDate");
        if (issued != null) p.setIssuedDate(LocalDate.parse(issued));
        String expiry = doc.getString("expiryDate");
        if (expiry != null) p.setExpiryDate(LocalDate.parse(expiry));

        List<Document> medDocs = (List<Document>) doc.get("medications");
        if (medDocs != null) {
            List<Prescription.PrescriptionItem> items = new ArrayList<>();
            for (Document md : medDocs) {
                Prescription.PrescriptionItem item = new Prescription.PrescriptionItem();
                item.setMedicationName(md.getString("medicationName"));
                item.setDosage(md.getString("dosage"));
                item.setFrequency(md.getString("frequency"));
                item.setDurationDays(md.getInteger("durationDays", 0));
                items.add(item);
            }
            p.setMedications(items);
        }
        return p;
    }
}
