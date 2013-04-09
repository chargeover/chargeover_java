
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
		
		String endpoint = "http://erida.local/saas/signup/api/v3.php";
		String user = "gYow6p85Vb0vcfzEH9shRQTrjy2niuLl";
		String pass = "FBste2Hw9PovbnY6hNuGVmjgA5RDdqXS";
		
		ChargeOver co = new ChargeOver(endpoint, user, pass, true);
		String last = "";
		
		List<Map<String, Object>>all = co.find_all(ChargeOver.Target.CUSTOMER, 10, 0);
		if(null == all)
		{
			if((last = co.getLastError()) != null) {
				System.out.println(last);
			} else {
				System.out.println("find_all found nothing.");
			}
		} else {
			co.prettyPrint(all);
		}
		//HashMap<String, String> where = new HashMap<String, String>();
		
		//where.put("external_key", null);
		//where.put("email", "foo@erida.local");
		
		//co.find(ChargeOver.Target.CUSTOMER, where, 10, 0);
		
		HashMap<String, String> customer = new HashMap<String, String>();
		
		// example create
		customer.put("email", "javatest@erida.local");
		customer.put("company", "JavaTest4 Co.");
		int id = 0;
		
//		id = co.create(ChargeOver.Target.CUSTOMER, customer);
		if(id < 0){
			System.out.println(co.getLastError());
		} else {
			System.out.println("Created Object id: " + id);
		}
		
		// test update
//		customer.put("company", "JavaTest Co.");
		// using the id from our create()
		id = co.update(ChargeOver.Target.CUSTOMER, id, customer);
		if(id < 0){
			System.out.println(co.getLastError());
		} else {
			System.out.println("Updated Object id: " + id);
		}

		System.out.println("Done.");
	}
}