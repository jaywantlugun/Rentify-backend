package com.jaywant.rentify.response;

import java.util.ArrayList;
import java.util.List;

import com.jaywant.rentify.models.Property;

public class PropertyPaginatedResponse {
	
	List<Property> properties = new ArrayList<>();
	Integer totalPages = 0;
	
	public PropertyPaginatedResponse() {}
	
	
	public PropertyPaginatedResponse(List<Property> properties, Integer totalPages) {
		super();
		this.properties = properties;
		this.totalPages = totalPages;
	}


	public List<Property> getProperties() {
		return properties;
	}


	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}


	public Integer getTotalPages() {
		return totalPages;
	}


	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	
	
	

}
