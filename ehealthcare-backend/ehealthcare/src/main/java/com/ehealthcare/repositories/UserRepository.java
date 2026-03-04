package com.ehealthcare.repositories;

import com.ehealthcare.database.DatabaseConnection;
import com.ehealthcare.models.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * UserRepository handles all database operations on the 'users' collection.
 */
public class UserRepository {

    private final MongoCollection<Document> collection;

    public UserRepository() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        this.collection = db.getCollection("users");
    }

    // --- Create ---

    public ObjectId save(User user) {
        Document doc = toDocument(user);
        collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    // --- Read ---

    public User findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public User findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    public boolean existsByEmail(String email) {
        return collection.countDocuments(Filters.eq("email", email)) > 0;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (Document doc : collection.find()) {
            users.add(fromDocument(doc));
        }
        return users;
    }

    // --- Update ---

    public void updateProfileComplete(ObjectId userId, boolean complete) {
        collection.updateOne(
            Filters.eq("_id", userId),
            Updates.set("profileComplete", complete)
        );
    }

    public void updatePassword(ObjectId userId, String newPasswordHash) {
        collection.updateOne(
            Filters.eq("_id", userId),
            Updates.set("passwordHash", newPasswordHash)
        );
    }

    // --- Delete ---

    public void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    // --- Mapping Helpers ---

    private Document toDocument(User user) {
        Document doc = new Document();
        if (user.getId() != null) doc.append("_id", user.getId());
        doc.append("email", user.getEmail());
        doc.append("passwordHash", user.getPasswordHash());
        doc.append("role", user.getRole());
        doc.append("firstName", user.getFirstName());
        doc.append("lastName", user.getLastName());
        doc.append("phone", user.getPhone());
        doc.append("profileComplete", user.isProfileComplete());
        return doc;
    }

    private User fromDocument(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id"));
        user.setEmail(doc.getString("email"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setRole(doc.getString("role"));
        user.setFirstName(doc.getString("firstName"));
        user.setLastName(doc.getString("lastName"));
        user.setPhone(doc.getString("phone"));
        user.setProfileComplete(Boolean.TRUE.equals(doc.getBoolean("profileComplete")));
        return user;
    }
}
