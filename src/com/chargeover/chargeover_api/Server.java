package com.chargeover.chargeover_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;


public class Server 
{
	private String endpoint;
	private String user;
	private String pass;
	private boolean basic_auth;
		
	private String last_error;
	private String last_url;
	
	public Server(String endpoint, String username, String password, boolean basic_auth) 
	{
		this.endpoint = endpoint;
		this.user = username;
		this.pass = password;
		this.basic_auth = basic_auth;
		this.last_error = null;
		this.last_url = null;
	}
	
	public String request(ChargeOver.Target target)
	{
		String url_string = makeURL(target);
		
		URLConnection connection = prepareConnection(url_string);
		
		return doGET(connection);
	}
	
	public String request(ChargeOver.Target target, int id) 
	{
		String url_string = makeURL(target);
		
		url_string = url_string + "/" + id;
		
		URLConnection connection = prepareConnection(url_string);
		
		return doGET(connection);
	}
	
	public String request(ChargeOver.Target target, Map<String, String> where, int limit,
			int offset) {

		String url_string = makeURL(target);
		String headers = makeHeaders(where, limit, offset);
		
		url_string = url_string + "/" + headers;
		
		//System.out.println(url_string);
		
		URLConnection connection = prepareConnection(url_string);
		
		String res = doGET(connection);
		
		return res;
	}
	
	public String submit(ChargeOver.Target target, String data)
	{
		String url_string = makeURL(target);
		HttpURLConnection connection = (HttpURLConnection)prepareConnection(url_string);
		
		return doPOST(connection, data);
	}

	public String submit(ChargeOver.Target target, int obj_id, String data)
	{
		String url_string = makeURL(target);
		url_string = url_string + "/" + obj_id;
		
		HttpURLConnection connection = (HttpURLConnection) prepareConnection(url_string);
		
		return doPUT(connection, data);
	}
	
	public String getLastError()
	{
		return last_error;
	}
	
	public String getLastURL()
	{
		return last_url;
	}
	
	private String makeURL(ChargeOver.Target target)
	{
		String url_string = endpoint;
		
		switch(target) {
		case CUSTOMER:
			url_string = endpoint + "/customer";
			break;
		case INVOICE:
			url_string = endpoint + "/invoice";
			break;
		case TRANSACTION:
			url_string = endpoint + "/transaction";
			break;
		case BILLING_PACKAGE:
			url_string = endpoint + "/billing_package";
			break;
		}
		
		return url_string;
	}
	
	private String makeHeaders(Map<String, String> where, int limit, int offset) {
		String headers = "?";
		if (where != null) {
			headers = "?where=";
			for (String key : where.keySet()) {
				String value = where.get(key);
				String new_header = "";
				if(value == null) {
					new_header = key + ":IS:NULL";
				} else {
					new_header = key + ":EQUALS:" + value;
				}
				if (headers.endsWith("=")) {
					headers = headers + new_header;
				} else {
					headers = headers + "," + new_header;
				}
			}
			headers = headers + "&"; // for limit/offset
		}
		
		headers += "limit=" + limit;
		headers += "&offset=" + offset;
		
		return headers;
	}
	
	private URLConnection prepareConnection(String url_string) {
		
		last_error = "";
		String creds = this.user + ":" + this.pass;
		
		// Watch out for long (>76 char) strings breaking MIME (nginx doesn't seem to care)
		// http://stackoverflow.com/questions/469695/decode-base64-data-in-java/2054226#2054226
		String creds_encoded = DatatypeConverter.printBase64Binary(creds.getBytes());

		String prop_string = "Basic " + creds_encoded;
				
		try {
			URL con_url = new URL(url_string);
			last_url = con_url.toString();
			
			URLConnection connection = con_url.openConnection();
		
			if(this.basic_auth) {
				// http://stackoverflow.com/questions/7019997/preemptive-basic-auth-with-httpurlconnection
				connection.setRequestProperty("Authorization", prop_string);
			}
			return connection;
			
		} catch (IOException e) {
			last_error = new String("IO Exception while creating the URLConnection: " + e.getMessage());
			return null;
		}
	}
	
	private String doGET(URLConnection connection)
	{
		String data = "";
		InputStream response = null;
		String exception_message = null;
		
		try {
			response = connection.getInputStream();
		} catch (MalformedURLException e) {
			last_error = new String("Bad URL. " + e.getMessage());
			return null;
		} catch (IOException e) {
			//System.out.println(data);
			exception_message = e.getMessage();
			response = ((HttpURLConnection)connection).getErrorStream();
		}

		if(null == response) {
			last_error = new String("IOException communicating with server. " + exception_message);
			return null;
		}
		
		// Now read either the server response or the server's error stream.
		try {
			int x;
			while((x = response.read()) > 0) {
				data = data + (char)x;
			}
		} catch (IOException e) {
			last_error = new String("IOException communicating with server. " + e.getMessage());
			return null;
		}
		
		return data;
	}
	
	private String doPOST(HttpURLConnection connection, String data)
	{
		// setDoOutput implies POST
		connection.setDoOutput(true);
		connection.setRequestProperty("content-type", "application/json");

		return serverWrite(connection, data);
	}
	
	private String doPUT(HttpURLConnection connection, String data)
	{
		try {
			connection.setRequestMethod("PUT");
		} catch(ProtocolException e) {
			e.printStackTrace();
		}
		connection.setDoOutput(true);
		connection.setRequestProperty("content-type", "application/json");
		
		return serverWrite(connection, data);
	}
	
	private String serverWrite(HttpURLConnection connection, String data)
	{
		OutputStream output = null;
		
		try {
			
			output = connection.getOutputStream();
			output.write(data.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != output) {
				try {
					output.close();
				} catch(IOException e) {e.printStackTrace();}
			}
		}
		
		// now we read the result
		return doGET(connection);
	}
}
