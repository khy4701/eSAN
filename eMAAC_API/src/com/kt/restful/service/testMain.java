package com.kt.restful.service;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class testMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Client client = Client.create();

		String url = new String("http://localhost:5000/test");

		WebResource webResource = null;
		ClientResponse response = null;

	
		webResource = client.resource(url);
		
//		webResource.he
//				
//		webResource.header("akey", "123123123" );
//		webResource.header("channel", "esan" );
//		webResource.header("Content-type", "application/json;charset=UTF-8" );
		
		
		String body = "{ \"123\": \"12\"}";
					
		//response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
		 
		response = webResource.type(MediaType.APPLICATION_JSON).header("akey", "123123123").header("channel", "esan" ).post(ClientResponse.class, body);
		
					
		System.out.println(response.getResponseStatus());

	}

}
