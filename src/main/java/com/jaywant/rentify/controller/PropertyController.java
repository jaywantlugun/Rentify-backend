package com.jaywant.rentify.controller;

import com.jaywant.rentify.Dto.PropertyDTO;
import com.jaywant.rentify.models.Property;
import com.jaywant.rentify.models.User;
import com.jaywant.rentify.repository.PropertyRepository;
import com.jaywant.rentify.repository.UserRepository;
import com.jaywant.rentify.response.PropertyPaginatedResponse;

import org.apache.tomcat.util.codec.binary.Base64;
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

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin("https://rentify-frontend-git-master-jaywantlugun2000s-projects.vercel.app")
@RestController
@RequestMapping("/properties")
public class PropertyController {

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


            // Create property object
            Property property = new Property();
            property.setRent(rent);
            property.setArea(area);
            property.setBedrooms(bedrooms);
            property.setBathrooms(bathrooms);
            property.setNearbySpots(nearbySpots);

            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            property.setPropertyImage(photoBlob);

            // Assume you have a UserRepository to fetch the owner
            User owner = userRepository.findById(ownerId).orElse(null);
            property.setOwner(owner);

            // Save property
            Property savedProperty = propertyRepository.save(property);
            return ResponseEntity.ok(savedProperty);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    ) throws SQLException {
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

        List<PropertyDTO> propertiesDTO = new ArrayList<>();
        for(Property property:properties){
            Blob photoBlob = property.getPropertyImage();
            byte[] photoBytes = photoBlob.getBytes(1,(int) photoBlob.length());
            String base64Photo = Base64.encodeBase64String(photoBytes);

            PropertyDTO propertyDTO = new PropertyDTO(
                    property.getId(),
                    property.getRent(),
                    property.getArea(),
                    property.getBedrooms(),
                    property.getBathrooms(),
                    property.getNearbySpots(),
                    base64Photo,
                    property.getOwner(),
                    property.getAppliedUsers(),
                    property.getLikedUsers()
                    );

            propertiesDTO.add(propertyDTO);
        }
        
        PropertyPaginatedResponse propertyPaginatedResponse = new PropertyPaginatedResponse(propertiesDTO,totalPages);
        
        return ResponseEntity.ok(propertyPaginatedResponse);
    }
    
    
    @GetMapping("/user/{ownerId}")
    public List<PropertyDTO> getPropertiesByOwnerId(@PathVariable("ownerId") Integer ownerId) throws SQLException {
        List<Property> properties = propertyRepository.findByOwnerId(ownerId);

        List<PropertyDTO> propertiesDTO = new ArrayList<>();
        for(Property property:properties){
            Blob photoBlob = property.getPropertyImage();
            byte[] photoBytes = photoBlob.getBytes(1,(int) photoBlob.length());
            String base64Photo = Base64.encodeBase64String(photoBytes);

            PropertyDTO propertyDTO = new PropertyDTO(
                    property.getId(),
                    property.getRent(),
                    property.getArea(),
                    property.getBedrooms(),
                    property.getBathrooms(),
                    property.getNearbySpots(),
                    base64Photo,
                    property.getOwner(),
                    property.getAppliedUsers(),
                    property.getLikedUsers()
            );

            propertiesDTO.add(propertyDTO);
        }

        return propertiesDTO;

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

