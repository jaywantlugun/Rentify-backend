package com.jaywant.rentify.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jaywant.rentify.models.Property;

public interface PropertyRepository extends JpaRepository<Property,Integer> {
	
	List<Property> findByOwnerId(Integer ownerId);
	
	@Query("SELECT p FROM Property p LEFT JOIN p.likedUsers lu GROUP BY p.id ORDER BY COUNT(lu) DESC")
    Page<Property> findAllOrderByLikedUsersSize(Pageable pageable);

}
