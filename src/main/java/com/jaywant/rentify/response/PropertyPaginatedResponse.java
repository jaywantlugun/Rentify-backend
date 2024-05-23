package com.jaywant.rentify.response;

import java.util.ArrayList;
import java.util.List;

import com.jaywant.rentify.Dto.PropertyDTO;
import com.jaywant.rentify.models.Property;

public class PropertyPaginatedResponse {
	
	List<PropertyDTO> properties = new ArrayList<>();
	Integer totalPages = 0;
	
	public PropertyPaginatedResponse() {}
	
	
	public PropertyPaginatedResponse(List<PropertyDTO> properties, Integer totalPages) {
		super();
		this.properties = properties;
		this.totalPages = totalPages;
	}


	public List<PropertyDTO> getProperties() {
		return properties;
	}


	public void setProperties(List<PropertyDTO> properties) {
		this.properties = properties;
	}


	public Integer getTotalPages() {
		return totalPages;
	}


	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	
	
	

}
