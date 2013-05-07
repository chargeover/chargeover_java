import com.chargeover.chargeover_api.ChargeOver;
import java.util.List; // used by commented code
import java.util.Map;
import java.util.HashMap; // used by commented code

public class ChargeOver_Demo {

	/**
	 * Some ChargeOver java API demo code.
	 * 
	 * This class provides a simple demo of the ChargeOver APIs. Most sections
	 * are removed by comment. ChargeOver::create() and ChargeOver::update()
	 * will modify your ChargeOver instance!
	 * 
	 * The ChargeOver classes can be included in a project as libraries for
	 * talking to a ChargeOver server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * Fill these in with information from your chargeover API configuration
		 */
		String endpoint = "";
		String user = "";
		String pass = "";

		// true to use http basic authentication, false to user ChargeOver key
		// based authentication
		ChargeOver co = new ChargeOver(endpoint, user, pass, false);
		String last = "";

		// sections commented out to prevent accidental database modification

		/*
		 * // example find_all List<Map<String, Object>>all =
		 * co.find_all(ChargeOver.Target.CUSTOMER, 10, 0); if(null == all) {
		 * if((last = co.getLastError()) != null) { System.out.println(last); }
		 * } else { // success! co.prettyPrint(all); }
		 */

		/*
		 * // example find with where clause HashMap<String, String> where = new
		 * HashMap<String, String>(); //where.put("external_key", "12345");
		 * where.put("email", "javatest5@example.com");
		 * 
		 * List<Map<String, Object>>items = co.find(ChargeOver.Target.CUSTOMER,
		 * where, 3, 0); if(null == items) { if((last = co.getLastError()) !=
		 * null) { System.out.println(last); } } else { // success, with data!
		 * co.prettyPrint(items); }
		 */

		Map<String, Object> single = co
				.findById(ChargeOver.Target.CUSTOMER, 29);
		if (null == single) {
			if ((last = co.getLastError()) != null) {
				System.out.println(last);
			}
		} else {
			// success, with data!
			co.prettyPrint(single);
		}

		// a customer for create/update
		//HashMap<String, String> customer = new HashMap<String, String>();

		/*
		 * // example create customer.put("email", "javatest5@example.com");
		 * customer.put("company", "JavaTest5 Co."); int id = 0;
		 * 
		 * id = co.create(ChargeOver.Target.CUSTOMER, customer); if(id < 0){
		 * System.out.println(co.getLastError()); } else {
		 * System.out.println("Created Object id: " + id); }
		 */

		/*
		 * // example update customer.put("company", "JavaTest Co."); // using
		 * the id from our create() id = co.update(ChargeOver.Target.CUSTOMER,
		 * id, customer); if(id < 0){ System.out.println(co.getLastError()); }
		 * else { System.out.println("Updated Object id: " + id); }
		 */

		System.out.println("Done.");
	}
}