package com.jaywant.rentify.controller;

import com.jaywant.rentify.models.Property;
import com.jaywant.rentify.models.User;
import com.jaywant.rentify.repository.PropertyRepository;
import com.jaywant.rentify.repository.UserRepository;
import com.jaywant.rentify.response.PropertyPaginatedResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/properties")
public class PropertyController {

	private final String UPLOAD_DIR = new ClassPathResource("static/images/").getFile().getAbsolutePath();

	public PropertyController() throws IOException
	{}
	

    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<Property> addProperty(@RequestParam("rent") Integer rent,
                                                @RequestParam("area") String area,
                                                @RequestParam("bedrooms") Integer bedrooms,
                                                @RequestParam("bathrooms") Integer bathrooms,
                                                @RequestParam("nearbySpots") String nearbySpots,
                                                @RequestParam("ownerId") Integer ownerId,
                                                @RequestParam("file") MultipartFile file) {
        try {
            // Ensure directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
         // Generate a new unique file name
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
           
         // Get the path to the destination file
            Path destinationPath = Paths.get(UPLOAD_DIR, newFileName);

            // Copy the file to the destination
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Create property object
            Property property = new Property();
            property.setRent(rent);
            property.setArea(area);
            property.setBedrooms(bedrooms);
            property.setBathrooms(bathrooms);
            property.setNearbySpots(nearbySpots);
            property.setImageUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/").path(newFileName).toUriString());
            // Assume you have a UserRepository to fetch the owner
            User owner = userRepository.findById(ownerId).orElse(null);
            property.setOwner(owner);

            // Save property
            Property savedProperty = propertyRepository.save(property);
            return ResponseEntity.ok(savedProperty);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    
    @PostMapping("/apply")
    public ResponseEntity<String> applyForProperty(@RequestParam("userId") Integer userId,
                                                   @RequestParam("propertyId") Integer propertyId) {
        User user = userRepository.findById(userId).orElse(null);
        Property property = propertyRepository.findById(propertyId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (property == null) {
            return ResponseEntity.status(404).body("Property not found");
        }

        if (property.getAppliedUsers().contains(userId)) {
            return ResponseEntity.status(400).body("Property already applied for");
        }
        
        property.getAppliedUsers().add(userId);
        propertyRepository.save(property);

        return ResponseEntity.ok("Property applied for successfully");
    }
    
    @GetMapping("/all")
    public ResponseEntity<PropertyPaginatedResponse> getAllProperties(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy
    ) {
        Pageable pageable = PageRequest.of(pageNo, 2);

        Page<Property> propertiesPage;
        
        if ("likes".equals(sortBy)) {
            propertiesPage = propertyRepository.findAllOrderByLikedUsersSize(pageable);
        } else {
            pageable = PageRequest.of(pageNo, 2, Sort.by(sortBy));
            propertiesPage = propertyRepository.findAll(pageable);
        }

        List<Property> properties = propertiesPage.getContent();
        Integer totalPages = propertiesPage.getTotalPages();
        
        PropertyPaginatedResponse propertyPaginatedResponse = new PropertyPaginatedResponse(properties,totalPages);
        
        return ResponseEntity.ok(propertyPaginatedResponse);
    }
    
    
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Load file as Resource
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG) // Change MediaType according to your image type
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    @GetMapping("/user/{ownerId}")
    public List<Property> getPropertiesByOwnerId(@PathVariable("ownerId") Integer ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }
    
    
    @PostMapping("/like")
    public ResponseEntity<String> likeProperty(@RequestParam("userId") Integer userId,
                                                   @RequestParam("propertyId") Integer propertyId) {
        User user = userRepository.findById(userId).orElse(null);
        Property property = propertyRepository.findById(propertyId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (property == null) {
            return ResponseEntity.status(404).body("Property not found");
        }
        
        property.getLikedUsers().add(userId);
        
        propertyRepository.save(property);

        return ResponseEntity.ok("Property liked successfully");
    }
    
    
    @PostMapping("/unlike")
    public ResponseEntity<String> unlikeProperty(@RequestParam("userId") Integer userId,
                                                   @RequestParam("propertyId") Integer propertyId) {
        User user = userRepository.findById(userId).orElse(null);
        Property property = propertyRepository.findById(propertyId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (property == null) {
            return ResponseEntity.status(404).body("Property not found");
        }
        
        property.getLikedUsers().remove(userId);
        
        propertyRepository.save(property);

        return ResponseEntity.ok("Property unliked successfully");
    }
    
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProperty(@RequestParam("propertyId") Integer propertyId) {
        Property property = propertyRepository.findById(propertyId).orElse(null);

        if (property == null) {
            return ResponseEntity.status(404).body("Property not found");
        }
        
        propertyRepository.deleteById(propertyId);

        return ResponseEntity.ok("Property liked successfully");
    }
    
    @PutMapping("/update")
    public ResponseEntity<Property> updateProperty(@RequestBody Property property) {
        Optional<Property> existingPropertyOpt = propertyRepository.findById(property.getId());

        if (existingPropertyOpt.isPresent()) {
            Property existingProperty = existingPropertyOpt.get();
            existingProperty.setRent(property.getRent());
            existingProperty.setArea(property.getArea());
            existingProperty.setBedrooms(property.getBedrooms());
            existingProperty.setBathrooms(property.getBathrooms());
            existingProperty.setNearbySpots(property.getNearbySpots());
            // Update other fields as necessary

            Property updatedProperty = propertyRepository.save(existingProperty);
            return ResponseEntity.ok(updatedProperty);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
    
}

