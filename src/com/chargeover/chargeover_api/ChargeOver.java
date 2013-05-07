package com.chargeover.chargeover_api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for interfacing with the ChargeOver API.
 * 
 * @see http://chargeover.com/features/api-webhooks/
 * @see http://chargeover.com/documentation/integration/rest-api/
 * 
 * @author jon adelson
 * 
 *         Create a ChargeOver object, giving it (endpoint url, username,
 *         password, true) or (endpoint url, public key, private key, false)
 *         which will use HTTP basic auth or not, respectively.
 * 
 *         use findById(), findAll(), and find() to retrieve data from the
 *         ChargeOver API.
 * 
 *         use create() and update() to modify data through the ChargeOver API.
 */
public class ChargeOver {
	private Server server;
	private Parser parser;
	private String last_error;

	public static enum Target {
		CUSTOMER, INVOICE, TRANSACTION, BILLING_PACKAGE
	}

	/**
	 * Create a ChargeOver object to interact with the ChargeOver server
	 * 
	 * @param endpoint
	 *            the url of the API endpoint.
	 * @param username
	 *            the basic auth username or signature public key.
	 * @param password
	 *            the basic auth password or signature private key.
	 * @param basic_auth
	 *            a boolean, True to use http basic authentication, false to use
	 *            signature authentication
	 */
	public ChargeOver(String endpoint, String username, String password,
			Boolean basic_auth) {
		this.server = new Server(endpoint, username, password, basic_auth);
		this.parser = new Parser();
		this.last_error = "";
	}

	/**
	 * Find an object (customer, invoice, etc...) by id.
	 * 
	 * @param obj
	 *            the type of object.
	 * @param id
	 *            the object's id.
	 * 
	 * @return A Map of key(String)-value(Object) pairs representing the object.
	 */
	public Map<String, Object> findById(Target obj, int id) {
		String result = "";
		last_error = null;

		result = server.request(obj, id);
		if (null == result) {
			last_error = server.getLastError();
			return null;
		}

		// result has something
		ServerResponse response = parser.parseServerResponse(result);
		if (null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return null;
		} else if (response.code == 400) {
			// not found
			last_error = response.message;
			return null;
		} else if (response.response instanceof Map) {
			// Got our list, hooray!
			return (Map<String, Object>) response.response;
		} else {
			// didn't give a null but didn't give a list result...
			last_error = "Error: " + response.code + " - " + response.message
					+ " | " + response.status + " | " + response.response;
			return null;
		}
	}

	/**
	 * Find all objects of a given type (customer, invoice, etc...)
	 * 
	 * @param obj
	 *            the type of object.
	 * @param limit
	 *            maximum number to return
	 * @param offset
	 *            offset into the results for pagination. if the offset is zero,
	 *            the first 'limit' results will be returned. If the offset is
	 *            1, the second item up through 'limit' number of items will be
	 *            returned.
	 * 
	 * @return A List<> of Maps of key(String)-value(Object) pairs representing
	 *         the objects.
	 */
	public List<Map<String, Object>> find_all(Target obj, int limit, int offset) {
		String result = "";

		result = server.request(obj, null, limit, offset);
		if (null == result) {
			System.out.println(server.getLastError());
			return null;
		}

		ServerResponse response = parser.parseServerResponse(result);
		if (null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return null;
		} else if (response.code == 400) {
			// not found
			last_error = response.message;
			return null;
		} else if (response.response instanceof ArrayList) {
			// Got our list, hooray!
			return (List<Map<String, Object>>) response.response;
		} else {
			// didn't give a null but didn't give a list result...
			last_error = "Error: " + response.code + " - " + response.message
					+ " | " + response.status + " | " + response.response;
			return null;
		}
	}

	/**
	 * Find all objects of a given type (customer, invoice, etc...) that match a
	 * criteria.
	 * 
	 * @see http://chargeover.com/documentation/integration/rest-api/
	 * 
	 * @param obj
	 *            the type of object.
	 * @param where
	 *            where clause criteria for filtering find results. See the
	 *            above link for details. This is given as a Map of <String,
	 *            String> where the key is something like 'email' and the value
	 *            is, in this case, the email address of the customer in the
	 *            ChargeOver database.
	 * 
	 * @param limit
	 *            maximum number to return
	 * @param offset
	 *            offset into the results for pagination. if the offset is zero,
	 *            the first 'limit' results will be returned. If the offset is
	 *            1, the second item up through 'limit' number of items will be
	 *            returned.
	 * 
	 * @return A List<> of Maps of key(String)-value(Object) pairs representing
	 *         the objects.
	 */
	public List<Map<String, Object>> find(Target obj,
			Map<String, String> where, int limit, int offset) {
		String result = "";
		last_error = "";

		// request from the server
		result = server.request(obj, where, limit, offset);
		if (null == result) {
			last_error = server.getLastError();
			return null;
		}

		// parse the response
		ServerResponse response = parser.parseServerResponse(result);
		if (null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return null;
		} else if (response.code == 400) {
			// not found
			last_error = response.message;
			return null;
		} else if (response.response instanceof ArrayList) {
			// Got our list, hooray!
			return (List<Map<String, Object>>) response.response;
		} else {
			// didn't give a null but didn't give a list result...
			last_error = "Error: " + response.code + " - " + response.message;
			return null;
		}
	}

	/**
	 * Create a new object in the ChargeOver system.
	 * 
	 * @param obj
	 *            the type of object
	 * @param data
	 *            a Map<String, String> of keys and values that represent the
	 *            object.
	 * @return The id of the newly created object, or -1 for an error.
	 */
	public int create(Target obj, Map<String, String> data) {
		String server_data = parser.generateServerData(data);

		String result = server.submit(obj, server_data);

		ServerResponse response = parser.parseServerResponse(result);
		if (null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return -1;
		} else if (response.code < 200 && response.code >= 300) {
			// not found
			last_error = response.message;
			return -1;
		} else if (response.response instanceof HashMap) {
			// Got our Map, hooray!
			return (int) ((Map<String, Object>) response.response).values()
					.toArray()[0];
		} else {
			// didn't give a null but didn't give a Map result...
			last_error = "Error: " + response.code + " - " + response.message;
			return -1;
		}
	}

	/**
	 * Update an object in the ChargeOver system.
	 * 
	 * @param obj
	 *            The type of object to update
	 * @param id
	 *            The object's id
	 * @param data
	 *            The object data.
	 * @return The id of the modified object or -1 for error
	 */
	public int update(Target obj, int id, Map<String, String> data) {
		String server_data = parser.generateServerData(data);

		String result = server.submit(obj, id, server_data);
		System.out.println(data);

		ServerResponse response = parser.parseServerResponse(result);
		if (null == response) {
			// no response
			last_error = "Couldn't parse server response: " + result;
			return -1;
		} else if (response.code < 200 && response.code >= 300) {
			// not found
			last_error = response.message;
			return -1;
		} else if (response.response instanceof HashMap) {
			// Got our Map, hooray!
			return (int) ((Map<String, Object>) response.response).values()
					.toArray()[0];
		} else {
			// didn't give a null but didn't give a Map result...
			last_error = "Error: " + response.code + " - " + response.message;
			return -1;
		}
	}

	/**
	 * Retreve the last error thrown
	 * 
	 * @return The text of the last error.
	 */
	public String getLastError() {
		return last_error;
	}

	/**
	 * Utility function for Printing out to the console a List of Map<String, Object>
	 * 
	 * @param l
	 *            Object to be printed
	 */
	public void prettyPrint(List<Map<String, Object>> l) {
		if (l.isEmpty()) {
			System.out.println("[Empty List]");
		}
		for (Map<String, Object> item : l) {
			System.out.println("--------------------------");
			prettyPrint(item); // not recursive!
		}
	}

	/**
	 * Utility function for printing out a Map<String, Object>
	 * 
	 * @param m
	 */
	public void prettyPrint(Map<String, Object> m) {
		for (Map.Entry<String, Object> entry : m.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
