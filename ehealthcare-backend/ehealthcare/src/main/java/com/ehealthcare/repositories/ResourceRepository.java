package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.Resource;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * ResourceRepository handles the database-driven Resource Tracker
 * for hospital beds and staff, as specified in the project roadmap.
 */
public class ResourceRepository {

    private final MongoCollection<Document> collection;

    public ResourceRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("resources");
    }

    // --- Create ---

    public ObjectId save(Resource resource) {
        Document doc = toDocument(resource);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public Resource findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public List<Resource> findAll() {
        List<Resource> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public List<Resource> findByType(String type) {
        List<Resource> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("type", type))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    public List<Resource> findByWard(String ward) {
        List<Resource> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("ward", ward))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    // --- Update ---

    public void updateAvailableCount(ObjectId resourceId, int availableCount) {
        collection.updateOne(
            Filters.eq("_id", resourceId),
            Updates.set("availableCount", availableCount)
        );
    }

    // --- Delete ---

    public void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    // --- Mapping Helpers ---

    private Document toDocument(Resource r) {
        Document doc = new Document();
        if (r.getId() != null) doc.append("_id", r.getId());
        doc.append("name", r.getName());
        doc.append("type", r.getType());
        doc.append("totalCount", r.getTotalCount());
        doc.append("availableCount", r.getAvailableCount());
        doc.append("ward", r.getWard());
        doc.append("description", r.getDescription());
        return doc;
    }

    private Resource fromDocument(Document doc) {
        Resource r = new Resource();
        r.setId(doc.getObjectId("_id"));
        r.setName(doc.getString("name"));
        r.setType(doc.getString("type"));
        r.setTotalCount(doc.getInteger("totalCount", 0));
        r.setAvailableCount(doc.getInteger("availableCount", 0));
        r.setWard(doc.getString("ward"));
        r.setDescription(doc.getString("description"));
        return r;
    }
}
