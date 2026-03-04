package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.Appointment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentRepository handles all database operations on the 'appointments' collection.
 *
 * Core flow:
 *   Patient books -> status = PENDING (visible on Doctor Portal)
 *   Doctor accepts -> status = SCHEDULED (updated on Patient Portal)
 *   Doctor declines -> status = DECLINED
 *   Post-consultation -> status = COMPLETED
 */
public class AppointmentRepository {

    private final MongoCollection<Document> collection;

    public AppointmentRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("appointments");
    }

    // --- Create ---

    public ObjectId save(Appointment appointment) {
        Document doc = toDocument(appointment);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public Appointment findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    /** All appointments for a specific patient, sorted newest first. */
    public List<Appointment> findByPatientId(ObjectId patientId) {
        List<Appointment> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("patientId", patientId))
                                      .sort(Sorts.descending("createdAt"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    /** Patient's appointments filtered by status (e.g. PENDING, SCHEDULED). */
    public List<Appointment> findByPatientIdAndStatus(ObjectId patientId, String status) {
        List<Appointment> list = new ArrayList<>();
        for (Document doc : collection.find(
                Filters.and(Filters.eq("patientId", patientId), Filters.eq("status", status)))
                .sort(Sorts.ascending("appointmentDateTime"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    /** All appointments assigned to a doctor, sorted by appointment date. */
    public List<Appointment> findByDoctorId(ObjectId doctorId) {
        List<Appointment> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("doctorId", doctorId))
                                      .sort(Sorts.ascending("appointmentDateTime"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    /** Doctor's pending appointments - shown on Doctor Portal for acceptance. */
    public List<Appointment> findPendingByDoctorId(ObjectId doctorId) {
        List<Appointment> list = new ArrayList<>();
        for (Document doc : collection.find(
                Filters.and(Filters.eq("doctorId", doctorId),
                            Filters.eq("status", Appointment.Status.PENDING.name())))
                .sort(Sorts.ascending("createdAt"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    /** Doctor's scheduled (accepted) appointments. */
    public List<Appointment> findScheduledByDoctorId(ObjectId doctorId) {
        List<Appointment> list = new ArrayList<>();
        for (Document doc : collection.find(
                Filters.and(Filters.eq("doctorId", doctorId),
                            Filters.eq("status", Appointment.Status.SCHEDULED.name())))
                .sort(Sorts.ascending("appointmentDateTime"))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    // --- Update Status ---

    /**
     * Doctor accepts appointment: PENDING -> SCHEDULED.
     * This update is reflected immediately on the Patient Portal.
     */
    public void acceptAppointment(ObjectId appointmentId, String doctorNotes) {
        collection.updateOne(
            Filters.eq("_id", appointmentId),
            Updates.combine(
                Updates.set("status", Appointment.Status.SCHEDULED.name()),
                Updates.set("doctorNotes", doctorNotes),
                Updates.set("updatedAt", LocalDateTime.now().toString())
            )
        );
    }

    /**
     * Doctor declines appointment: PENDING -> DECLINED.
     */
    public void declineAppointment(ObjectId appointmentId, String reason) {
        collection.updateOne(
            Filters.eq("_id", appointmentId),
            Updates.combine(
                Updates.set("status", Appointment.Status.DECLINED.name()),
                Updates.set("doctorNotes", reason),
                Updates.set("updatedAt", LocalDateTime.now().toString())
            )
        );
    }

    /**
     * Mark appointment as completed after consultation.
     */
    public void completeAppointment(ObjectId appointmentId) {
        collection.updateOne(
            Filters.eq("_id", appointmentId),
            Updates.combine(
                Updates.set("status", Appointment.Status.COMPLETED.name()),
                Updates.set("updatedAt", LocalDateTime.now().toString())
            )
        );
    }

    /**
     * Patient cancels appointment.
     */
    public void cancelAppointment(ObjectId appointmentId) {
        collection.updateOne(
            Filters.eq("_id", appointmentId),
            Updates.combine(
                Updates.set("status", Appointment.Status.CANCELLED.name()),
                Updates.set("updatedAt", LocalDateTime.now().toString())
            )
        );
    }

    // --- Mapping Helpers ---

    private Document toDocument(Appointment appt) {
        Document doc = new Document();
        if (appt.getId() != null) doc.append("_id", appt.getId());
        doc.append("patientId", appt.getPatientId());
        doc.append("doctorId", appt.getDoctorId());
        doc.append("patientName", appt.getPatientName());
        doc.append("doctorName", appt.getDoctorName());
        doc.append("appointmentDateTime", appt.getAppointmentDateTime() != null
                ? appt.getAppointmentDateTime().toString() : null);
        doc.append("reason", appt.getReason());
        doc.append("status", appt.getStatus());
        doc.append("doctorNotes", appt.getDoctorNotes());
        doc.append("createdAt", appt.getCreatedAt() != null
                ? appt.getCreatedAt().toString() : null);
        doc.append("updatedAt", appt.getUpdatedAt() != null
                ? appt.getUpdatedAt().toString() : null);
        return doc;
    }

    private Appointment fromDocument(Document doc) {
        Appointment appt = new Appointment();
        appt.setId(doc.getObjectId("_id"));
        appt.setPatientId(doc.getObjectId("patientId"));
        appt.setDoctorId(doc.getObjectId("doctorId"));
        appt.setPatientName(doc.getString("patientName"));
        appt.setDoctorName(doc.getString("doctorName"));
        String adt = doc.getString("appointmentDateTime");
        if (adt != null) appt.setAppointmentDateTime(LocalDateTime.parse(adt));
        appt.setReason(doc.getString("reason"));
        appt.setStatus(doc.getString("status"));
        appt.setDoctorNotes(doc.getString("doctorNotes"));
        String cat = doc.getString("createdAt");
        if (cat != null) appt.setCreatedAt(LocalDateTime.parse(cat));
        String uat = doc.getString("updatedAt");
        if (uat != null) appt.setUpdatedAt(LocalDateTime.parse(uat));
        return appt;
    }
}
