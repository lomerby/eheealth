package com.ehealthcare.services;

import com.ehealthcare.models.Resource;
import com.ehealthcare.repositories.ResourceRepository;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * ResourceService manages hospital resource tracking (beds, staff, equipment).
 * Corresponds to the Resource Tracker specified in the project roadmap.
 */
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService() {
        this.resourceRepository = new ResourceRepository();
    }

    public Resource addResource(String name, String type, int totalCount,
                                 String ward, String description) {
        Resource resource = new Resource(name, type, totalCount, ward, description);
        ObjectId id = resourceRepository.save(resource);
        resource.setId(id);
        System.out.println("Resource added: " + name + " (" + ward + ")");
        return resource;
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<Resource> getResourcesByType(String type) {
        return resourceRepository.findByType(type);
    }

    public List<Resource> getResourcesByWard(String ward) {
        return resourceRepository.findByWard(ward);
    }

    public void updateAvailability(ObjectId resourceId, int newAvailableCount) {
        Resource resource = resourceRepository.findById(resourceId);
        if (resource == null) throw new IllegalArgumentException("Resource not found.");
        if (newAvailableCount < 0 || newAvailableCount > resource.getTotalCount()) {
            throw new IllegalArgumentException("Available count must be between 0 and " + resource.getTotalCount());
        }
        resourceRepository.updateAvailableCount(resourceId, newAvailableCount);
    }
}
