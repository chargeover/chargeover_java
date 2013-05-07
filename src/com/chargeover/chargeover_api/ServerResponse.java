package com.chargeover.chargeover_api;

/**
 * Class for encapsulating a response from the ChargeOver server.
 * 
 * The ChargeOver server returns the fields in this class as JSON, and the JSON
 * parser returns objects of this class.
 * 
 * @author jon
 * 
 */
public class ServerResponse {
	public int code;
	public String status;
	public String message;
	public Object response;
}
