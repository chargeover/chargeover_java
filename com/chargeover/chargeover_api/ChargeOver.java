package com.chargeover.chargeover_api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A class for interfacing with the ChargeOver API.
 * 
 * @author jon
 *
 */
public class ChargeOver
{
	private Server server;
	private Parser parser;
	private String last_error;
	
	public static enum Target
	{
		CUSTOMER,
		INVOICE,
		TRANSACTION,
		BILLING_PACKAGE
	}
	/**
	 * Create a ChargeOver object to interact with the ChargeOver server
	 * 
	 * @param endpoint the url of the API endpoint.
	 * @param username the basic auth username or signature public key.
	 * @param password the basic auth password or signature private key.
	 * @param basic_auth a boolean, True to use http basic authentication, false to use signature authentication
	 */
	public ChargeOver(String endpoint, String username, String password, Boolean basic_auth)
	{
		this.server = new Server(endpoint, username, password, basic_auth);
		this.parser = new Parser();
		this.last_error = "";
	}
	
	/**
	 * Find an object (customer, invoice, etc...) by id.
	 * 
	 * @param obj the type of object.
	 * @param id the object's id.
	 * 
	 * @return A Map of key(String)-value(Object) pairs representing the object.
	 */
	public Map<String, Object> findById(Target obj, int id)
	{
		String result = "";
		last_error = null;
		
		result = server.request(obj, id);
		if(null == result) {
			last_error = server.getLastError();
			return null;
		}
		
		// result has something
		ServerResponse response = parser.parseServerResponse(result);
		if(null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return null;
		} else if(response.code == 400) {
			// not found
			last_error = response.message;
			return null;
		} else if(response.response instanceof ArrayList) {
			// Got our list, hooray!
			return (Map<String, Object>)response.response;
		} else {
			// didn't give a null but didn't give a list result...
			last_error = "Weird json error.";
			return null;
		}
	}
	
	public List<Map<String, Object>> find_all(Target obj)
	{
		String result = "";
		
		result = server.request(obj);
		if(null == result) {
			System.out.println(server.getLastError());
			return null;
		}

		
		ServerResponse response = parser.parseServerResponse(result);
		if(null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return null;
		} else if(response.code == 400) {
			// not found
			last_error = response.message;
			return null;
		} else if(response.response instanceof ArrayList) {
			// Got our list, hooray!
			return (List<Map<String, Object>>) response.response;
		} else {
			// didn't give a null but didn't give a list result...
			last_error = "Weird json error.";
			return null;
		}
	}
	
	public List<Map<String, Object>> find(Target obj, Map<String, String> where, int limit, int offset)
	{
		String result = "";
		last_error = "";
		
		// request from the server
		result = server.request(obj, where, limit, offset);
		if(null == result) {
			last_error = server.getLastError();
			return null;
		}
		
		// parse the response
		ServerResponse response = parser.parseServerResponse(result);
		if(null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return null;
		} else if(response.code == 400) {
			// not found
			last_error = response.message;
			return null;
		} else if(response.response instanceof ArrayList) {
			// Got our list, hooray!
			return (List<Map<String, Object>>) response.response;
		} else {
			// didn't give a null but didn't give a list result...
			last_error = "Weird json error.";
			return null;
		}
	}
	
	public int create(Target obj, Map<String, String> data)
	{
		String server_data = parser.generateServerData(data);
		
		String result = server.submit(obj, server_data);
		
		System.out.println(result);
		
		return 0;
	}
	
	public int update(Target obj, int id, Map<String, String> data)
	{
		String server_data = parser.generateServerData(data);
		
		String result = server.submit(obj, id, server_data);
		
		System.out.println(result);
		
		return 0;
	}
	
	public void prettyPrint(ArrayList<Map<String, Object>> l) {
		for (Map<String, Object> item : l) {
			System.out.println("--------------------------");
			prettyPrint(item); // not recursive!
		}
	}
	
	public void prettyPrint(Map<String, Object> m)
	{
		for(Map.Entry<String, Object> entry : m.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
