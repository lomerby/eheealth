package com.ehealthcare.models;

import org.bson.types.ObjectId;

/**
 * Resource model tracks hospital beds and staff availability.
 * Used by the Resource Tracker as specified in the project roadmap.
 */
public class Resource {

    public enum ResourceType {
        BED, STAFF, EQUIPMENT
    }

    private ObjectId id;
    private String name;
    private String type;         // Uses ResourceType enum values as strings
    private int totalCount;
    private int availableCount;
    private String ward;         // e.g. "ICU", "General", "Paediatrics"
    private String description;

    public Resource() {}

    public Resource(String name, String type, int totalCount, String ward, String description) {
        this.name = name;
        this.type = type;
        this.totalCount = totalCount;
        this.availableCount = totalCount;
        this.ward = ward;
        this.description = description;
    }

    // --- Getters & Setters ---

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }

    public int getOccupiedCount() { return totalCount - availableCount; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
