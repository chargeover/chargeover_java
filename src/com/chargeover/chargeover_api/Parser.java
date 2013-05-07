package com.chargeover.chargeover_api;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parser for ChargeOver JSON
 * 
 * Utility class for translating JSON to a hash and back
 * 
 * @author jon adelson
 * 
 */
public class Parser {
	public static enum ExpectedStruct {
		ARRAY, DICTIONARY
	}

	private ObjectMapper mapper;

	private String last_error;

	public Parser() {
		mapper = new ObjectMapper();
		last_error = "";
	}

	/**
	 * Parse the top level json object returned by the server
	 * 
	 * @params server_response - String containing json response from server
	 * 
	 * @return - A correctly filled out ServerResponse object
	 * 
	 */
	public ServerResponse parseServerResponse(String server_response) {
		ServerResponse response = null;

		try {
			response = mapper.readValue(server_response, ServerResponse.class);
		} catch (IOException e) {
			// the json is bad in some way, which we will not try to determine
			last_error = "Parser: json string unreadable.";
			return null;
		}
		last_error = "";
		return response;
	}

	/**
	 * Generate JSON for the data in 'data'
	 * 
	 * @param data
	 * @return String of JSON
	 */
	public String generateServerData(Map<String, String> data) {
		String server_data = "";

		try {
			server_data = mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			last_error = "Parser/Generator: couldn't build json from map.";
			return null;
		}

		return server_data;
	}

	public String getLastError() {
		return last_error;
	}
}
