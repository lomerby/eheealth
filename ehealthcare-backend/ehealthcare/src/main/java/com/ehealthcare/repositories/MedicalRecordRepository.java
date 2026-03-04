package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.MedicalRecord;
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
 * MedicalRecordRepository handles all database operations on the 'medicalRecords' collection.
 */
public class MedicalRecordRepository {

    private final MongoCollection<Document> collection;

    public MedicalRecordRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("medicalRecords");
    }

    // --- Create ---

    public ObjectId save(MedicalRecord record) {
        Document doc = toDocument(record);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public MedicalRecord findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    /** All records for a patient, sorted most recent first. */
    public List<MedicalRecord> findByPatientId(ObjectId patientId) {
        List<MedicalRecord> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("patientId", patientId))
                                      .sort(Sorts.descending("recordDate"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    /** All records created by a doctor. */
    public List<MedicalRecord> findByDoctorId(ObjectId doctorId) {
        List<MedicalRecord> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("doctorId", doctorId))
                                      .sort(Sorts.descending("recordDate"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    // --- Mapping Helpers ---

    private Document toDocument(MedicalRecord r) {
        Document doc = new Document();
        if (r.getId() != null) doc.append("_id", r.getId());
        doc.append("patientId", r.getPatientId());
        doc.append("doctorId", r.getDoctorId());
        doc.append("appointmentId", r.getAppointmentId());
        doc.append("doctorName", r.getDoctorName());
        doc.append("diagnosis", r.getDiagnosis());
        doc.append("treatment", r.getTreatment());
        doc.append("notes", r.getNotes());
        doc.append("recordDate", r.getRecordDate() != null ? r.getRecordDate().toString() : null);
        return doc;
    }

    private MedicalRecord fromDocument(Document doc) {
        MedicalRecord r = new MedicalRecord();
        r.setId(doc.getObjectId("_id"));
        r.setPatientId(doc.getObjectId("patientId"));
        r.setDoctorId(doc.getObjectId("doctorId"));
        r.setAppointmentId(doc.getObjectId("appointmentId"));
        r.setDoctorName(doc.getString("doctorName"));
        r.setDiagnosis(doc.getString("diagnosis"));
        r.setTreatment(doc.getString("treatment"));
        r.setNotes(doc.getString("notes"));
        String rd = doc.getString("recordDate");
        if (rd != null) r.setRecordDate(LocalDate.parse(rd));
        return r;
    }
}
