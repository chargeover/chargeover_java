package com.chargeover.chargeover_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


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
		if(null == connection) {
			return null;
		}
		
		return doGET(connection);
	}
	
	public String request(ChargeOver.Target target, int id) 
	{
		String url_string = makeURL(target);
		
		url_string = url_string + "/" + id;
		
		URLConnection connection = prepareConnection(url_string);
		if(null == connection) {
			return null;
		}
		
		return doGET(connection);
	}
	
	public String request(ChargeOver.Target target, Map<String, String> where, int limit,
			int offset) {

		String url_string = makeURL(target);
		String headers = makeHeaders(where, limit, offset);
		
		url_string = url_string + "/" + headers;
		
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

		if(!this.basic_auth) {
			String sig = makeSig(connection.getURL().toString(), null);
			System.out.println(sig);
			connection.setRequestProperty("Authorization", sig);
		}
		
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
	
	private String makeSig(String url, String data) {
        	
		ArrayList<Character> tmp = new ArrayList<Character>(36);
		for(char c = 'a'; c <= 'z'; c += 1) {
			tmp.add(c);
		}
		for(char c = '0'; c <= '9'; c += 1) {
			tmp.add(c);
		}
		Collections.shuffle(tmp);
		String nonce = new String();
		
		for(Character c : tmp.subList(0, 8)) {
			nonce += c;
		}
		
		int client_time = (int)(System.currentTimeMillis() / 1000);
		String delim = "||";
		String msg = this.user + delim + url.toLowerCase() + delim + nonce + delim + client_time + delim;
		System.out.println(msg);
		if(null != data) {
			msg = msg += data;
		}
		
		Mac mac = null;
		try {
			SecretKeySpec key = new SecretKeySpec(this.pass.getBytes("UTF-8"), "HmacSHA256");
			mac = Mac.getInstance("HmacSHA256");
			mac.init(key);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] digest = mac.doFinal(msg.getBytes());
		String sig = "";
		String hex = "";
		for(byte b : digest) {
			// need one byte, so the last two hex digits of the Integer
			hex = Integer.toHexString(0xff & b);
			// we get no leading zeros!
			if(hex.length() == 1) {
				sig += "0";
			}
			sig += hex;
		}
		System.out.println(sig);
		// header = 'ChargeOver co_public_key="%s" co_nonce="%s" co_timestamp="%s" co_signature_method="HMAC-SHA256" 
		// co_version="1.0" co_signature="%s"' %(self._user, nonce, client_time, sig)
		String auth_header = "ChargeOver co_public_key=\"" + this.user + "\" co_nonce=\"" + nonce + 
							 "\" co_timestamp=\"" + client_time + 
							 "\" co_signature_method=\"HMAC-SHA256\" co_version=\"1.0\" co_signature=\"" + sig + "\"";
		System.out.println(auth_header);
		return auth_header;
	}
}
