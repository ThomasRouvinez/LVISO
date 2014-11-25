package com.project.livefeed.Objects;

/**
 * A tool class to store data to perform a HTTP Post.
 * @author Thomas Rouvinez
 * @creation date: 2014.11.25
 * @last modified: 2014.11.25
 * 
 */
public class HTTPData {
	
	// -----------------------------------------------------------------------
	// Variables.
	// -----------------------------------------------------------------------
	
	private String address;
	private ForwardData data;
	
	// -----------------------------------------------------------------------
	// Constructor.
	// -----------------------------------------------------------------------
	
	public HTTPData(){}

	// -----------------------------------------------------------------------
	// Getters - Setters.
	// -----------------------------------------------------------------------
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ForwardData getData() {
		return data;
	}

	public void setData(ForwardData data) {
		this.data = data;
	}
}