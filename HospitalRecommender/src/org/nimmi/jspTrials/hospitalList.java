package org.nimmi.jspTrials;

public class hospitalList {
	
	private String name;
	private String addr;
	private String city;
	private String county;
	private String state;
	private String zip;
	private String rating1;
	private String rating2;
	private String rating3;
	private String lat;
	private String lon;

	hospitalList(String name, String addr, String city, String county, String state, String zip, String rating1, String rating2, String rating3,String lat, String lon)
	{
		this.name = name;
		this.addr=addr;
		this.city=city;
		this.county=county;
		this.state=state;
		this.zip=zip;
		this.rating1=rating1;
		this.rating2=rating2;
		this.rating3=rating3;
		this.lat = lat;
		this.lon = lon;
		
	}


}
