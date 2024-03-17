package RPC;
import org.json.JSONArray;
import org.json.JSONObject;


/************************************************************
 * Hier können Temporär RPC-Funktionen getestet werden.		*
 * Oder allgemeine  Tests									*
 ************************************************************/



public class Test 
{

	public static void main(String[] args) throws Exception 
	{
		

	}
	
	


	

	
	
	public static void showMinMaxFeeRate() throws Exception
	{
	
		String 	ip 	 = "192.168.178.31";
		int 	port = 18333;
		String 	name = "";
		String 	pw 	 = "";
		ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
		
		String str = peer.get("getmempoolinfo", null);
		JSONObject jo = new JSONObject(str).getJSONObject("result");
		System.out.println(jo.toString(2));
		
		double min = jo.getDouble("mempoolminfee")*100000;
		System.out.println("mempoolminfee = "+min+"\n\n");

	// -------------------------------- High ---------------------------------
		
		
		str = peer.get("estimatesmartfee", new JSONArray().put(1));
		jo = new JSONObject(str).getJSONObject("result");
		System.out.println(jo.toString(2));
		double feerate = jo.getDouble("feerate")*100000;
		System.out.println("feerate = "+feerate+"\n\n");


	}
	
}
