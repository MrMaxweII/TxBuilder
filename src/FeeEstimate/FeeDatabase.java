package FeeEstimate;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.JSONObject;
import TxBuild.GUI;



/********************************************************************************************************************************************
 *   Läd und speichert die Dateien: feeDataMain.json oder feeDataTest.json																	*
 *   Es Handelt sich um die Daten der Transaktions-Gebühren-Rate aus der Vergangenheit. Die im LinienDiagramm als FeeRate angezeigt werden.	*
 *   Es gibt zwei Datein, eine für MainNet und eine für TestNet																				*
 *   Wenn die Datei nicht vorhanden ist, wird sie beim Frame-Close automatisch gespeichert.													*
 ********************************************************************************************************************************************/



public class FeeDatabase 
{
	
	final static String dateiID  = "b6973d13d9e1273ccc937b3792f5b59438144d5df710ae8ac44fdc04e4e2c75d";  		// Die dateiID dient zur Identifizierung dieser json Datei.
	public static JSONObject data = new JSONObject();															// Der gesamte Datensatz alse JSON-Object




/**	Läd die Datei, wenn die Datei vorhanden ist. Wenn nicht, passiert nichts und die default Werte werden verwendet.  **/
public static void load()
{
	File f;
	if(GUI.btn_testNet.isSelected()) 	f = new File("feeDataTest.json");
	else								f = new File("feeDataMain.json");
	
	if(f.exists())
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			String str = "";
			while(br.ready()) str = str +br.readLine();		
			br.close();
	
			JSONObject jo = new JSONObject(str);				
			String dateiID = jo.getString("dateiID");
			String version = jo.getString("version");
			if(dateiID.equals(FeeDatabase.dateiID)==false) 	throw new FileNotFoundException("The file "+f.getName()+" is an incorrect file!");
			if(version.equals(GUI.version)==false) 			throw new FileNotFoundException("The file "+f.getName()+" has the wrong version!");		
			data = jo.getJSONObject("data");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			GUI_FeeChart.txt_meld.setText(e.getMessage());
		}
	}	
	else {}
}
	


public static void save()
{
	File f;
	if(GUI.btn_testNet.isSelected()) 	f = new File("feeDataTest.json");
	else								f = new File("feeDataMain.json");
	
	try 
	{
		JSONObject jo = new JSONObject();
		jo.put("dateiID", dateiID);   
		jo.put("progName", 	GUI.progName);
		jo.put("version", 	GUI.version);
		jo.put("autor", 	GUI.autor);	
		jo.put("data", data);

		BufferedWriter br = new BufferedWriter(new FileWriter(f));
		br.write(jo.toString());
		br.close();	
	} 
	catch (Exception e) 
	{
		e.printStackTrace();
		GUI_FeeChart.txt_meld.setText(e.getMessage());
	}
}
}