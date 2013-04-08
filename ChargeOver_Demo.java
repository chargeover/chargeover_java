
import com.chargeover.chargeover_api.ChargeOver;

import java.util.HashMap;


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
		
		//co.find_all(ChargeOver.Target.CUSTOMER);
		
		//HashMap<String, String> where = new HashMap<String, String>();
		
		//where.put("external_key", null);
		//where.put("email", "foo@erida.local");
		
		//co.find(ChargeOver.Target.CUSTOMER, where, 10, 0);
		
		HashMap<String, String> customer = new HashMap<String, String>();
		
		customer.put("email", "javatest@erida.local");
		customer.put("company", "JavaTest2 Co.");
		int id = 62;
		co.update(ChargeOver.Target.CUSTOMER, id, customer);
		
		System.out.println("Done.");
	}
}