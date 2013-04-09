
import com.chargeover.chargeover_api.ChargeOver;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ChargeOver_Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Tester_URLLib.connect_with_URLlib();
		
		String endpoint = "";
		String user = "";
		String pass = "";
		
		ChargeOver co = new ChargeOver(endpoint, user, pass, true);
		String last = "";
		
		/*
		// example find_all
 		List<Map<String, Object>>all = co.find_all(ChargeOver.Target.CUSTOMER, 10, 0);
		if(null == all)
		{
			if((last = co.getLastError()) != null) {
				System.out.println(last);
			}
		} else {
			// success!
			co.prettyPrint(all);
		}
		*/

		/*
		// example find with where clause
		HashMap<String, String> where = new HashMap<String, String>();
		//where.put("external_key", "12345");
		where.put("email", "javatest5@example.com");
		
		List<Map<String, Object>>items = co.find(ChargeOver.Target.CUSTOMER, where, 3, 0);
		if(null == items) {
			if((last = co.getLastError()) != null) {
				System.out.println(last);
			}
		} else {
			// success, with data!
			co.prettyPrint(items);
		}
		*/
		
		Map<String, Object>single = co.findById(ChargeOver.Target.CUSTOMER, 29);
		if(null == single) {
			if((last = co.getLastError()) != null) {
				System.out.println(last);
			}
		} else {
			// success, with data!
			co.prettyPrint(single);
		}
		
		// a customer for create/update
		HashMap<String, String> customer = new HashMap<String, String>();

		/*
		// example create
		customer.put("email", "javatest5@example.com");
		customer.put("company", "JavaTest5 Co.");
		int id = 0;
		
		id = co.create(ChargeOver.Target.CUSTOMER, customer);
		if(id < 0){
			System.out.println(co.getLastError());
		} else {
			System.out.println("Created Object id: " + id);
		}
		*/
		
		/*
		// example update
		customer.put("company", "JavaTest Co.");
		// using the id from our create()
		id = co.update(ChargeOver.Target.CUSTOMER, id, customer);
		if(id < 0){
			System.out.println(co.getLastError());
		} else {
			System.out.println("Updated Object id: " + id);
		}
		*/
		
		System.out.println("Done.");
	}
}