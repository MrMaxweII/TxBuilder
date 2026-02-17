package TxBuild;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.DefaultComboBoxModel;
import org.json.JSONObject;
import FeeEstimate.LineDiagram;





/***************************************************************************************************  
 *   Version: V1.7											Letzte Änderung: 28.12.2025				*										
 *   Läd und speichert die TxBuilder.json Datei.													*
 *   Wenn die Datei nicht vorhanden ist, wird sie beim Programmende automatisch gespeichert.		*
 *   Diese Datei beinhaltet alle Einstellungen und Zustände im Programm, außer die Eingabe Felder!	*
 ***************************************************************************************************/



public class Config 
{
	
	final static String fileName = "TxBuilder.json";													// Name der Configurations Datei für diese. Programm
	final static String dateiID  = "90f6df7398f4c111905db99f1d0821696edbd4b38f1b018e8aef9989d94e2504";  // Die dateiID dient zur Identifizierung dieser json Datei.
	


/**	Läd die Programm Status Einstellungen aus der Datei: "TxBuilder.json" und schreibt sie in die Variablen.  **/
public static void load()
{
	File f = new File(fileName);
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
		//  String version = jo.getString("version");
			if(dateiID.equals(Config.dateiID)==false) 	throw new FileNotFoundException("The file "+fileName+" is an incorrect file!");
		//  if(version.equals(GUI.version)==false) 		throw new FileNotFoundException("The file "+fileName+" has the wrong version!");		
			GUI.posX 						= jo.getInt("posX");
			GUI.posY 						= jo.getInt("posY");	
			GUI.debugMode 					= jo.getInt("debugMode");
			GUI.btn_testNet					.setSelected(jo.getBoolean("testNet"));
			GUI.comboBoxList 				= new String[jo.getInt("comboBoxList")];
			GUI.countSearchBlocks 			= jo.getInt("countSearchBlocks");
			GUI_CoreSettings.txt_ip			.setText(jo.getString("coreIP"));
			GUI_CoreSettings.txt_port		.setText(jo.getString("corePort"));
			GUI_CoreSettings.txt_uName		.setText(jo.getString("coreUname"));
			GUI_CoreSettings.txt_pw			.setText(jo.getString("corePW"));
			GUI_CoreSettings.txt_timeOut	.setText(jo.getString("timeOut"));	
			GUI_CoreSettings.btn_auth		.setSelected(jo.getBoolean("coreAuthCookie"));
			GUI_CoreSettings.lbl_path		.setText(jo.getString("coreCookiePath"));
			GUI_TxSettings.txt_sequeHex		.setText(jo.getString("sequence"));	
			GUI_TxSettings.txt_sequence		.setText(GUI_TxSettings.hexToSequence(jo.getString("sequence")));
			GUI_TxSettings.txt_locktHex		.setText(jo.getString("locktime"));
			GUI_TxSettings.txt_locktime		.setText(GUI_TxSettings.hexToSequence(jo.getString("locktime")));
			GUI_FeeSettings.feeFrom			.setSelectedIndex(jo.getInt("feeFrom"));
			GUI_FeeSettings.sliderMax		.setSelectedIndex(jo.getInt("feeSliderMax"));
			GUI_FeeSettings.sliderMin		.setSelectedIndex(jo.getInt("feeSliderMin"));
			GUI_ImportCSV.lbl_file			.setText(jo.getString("fileNameAdressList"));
			GUI_PublishTx.lbl_file_sigTx	.setText(jo.getString("fileNameSigTx"));
			for(int i=0;i<GUI.comboBoxList.length;i++) {GUI.comboBoxList[i]=String.valueOf(i+1);}
			GUI.cBox_inCount				.setModel(new DefaultComboBoxModel(GUI.comboBoxList));	
			GUI.cBox_outCount				.setModel(new DefaultComboBoxModel(GUI.comboBoxList));	
			GUI.cBox_language				.setSelectedIndex(jo.getInt("languageIndex"));
			GUI_FeeSettings.maxAccaptFeeRate = jo.getInt("maxAccaptFeeRate");
			GUI_FeeSettings.estimateProfile  = jo.getInt("estimateProfile");
			LineDiagram.scalY				 = jo.getInt("FeeChart_scalY");
			LineDiagram.btnNr				 = jo.getInt("FeeChart_btnNr");
			GUI.toolTipEnabled				 = jo.getBoolean("toolTipEnabled");
			GUI.toolTipSetDismissDelay		 = jo.getInt("toolTipSetDismissDelay");
			GUI_FeeSettings.fallBackEstimaFeeRate=jo.getDouble("fallBackEstimaFeeRate");

		} 
		catch (Exception e) 
		{	
			GUI.txt_meld.setForeground(Color.red);
			GUI.txt_meld.setText(e.getMessage());
			e.printStackTrace();
		}
	}	
	else 
	{	
		Translate.ShowStartDialogg();
	}
}
	


public static void save()
{
	try 
	{
		JSONObject jo = new JSONObject();
		jo.put("dateiID", dateiID);   
		jo.put("progName", 	GUI.progName);
		jo.put("version", 	GUI.version);
		jo.put("autor", 	GUI.autor);
		jo.put("posX", 		GUI.frame.getX());
		jo.put("posY", 		GUI.frame.getY());
		jo.put("debugMode", GUI.debugMode);
		jo.put("testNet", 	GUI.btn_testNet.isSelected());
		jo.put("comboBoxList", GUI.comboBoxList.length);
		jo.put("countSearchBlocks", GUI.countSearchBlocks);
		jo.put("coreIP", 	GUI_CoreSettings.txt_ip.getText());
		jo.put("corePort", 	GUI_CoreSettings.txt_port.getText());
		jo.put("coreUname", GUI_CoreSettings.txt_uName.getText());
		jo.put("corePW", 	GUI_CoreSettings.txt_pw.getText());
		jo.put("timeOut", 	GUI_CoreSettings.txt_timeOut.getText());
		jo.put("coreAuthCookie", GUI_CoreSettings.btn_auth.isSelected());
		jo.put("coreCookiePath", GUI_CoreSettings.lbl_path.getText());
		jo.put("sequence",  GUI_TxSettings.txt_sequeHex.getText());
		jo.put("locktime",  GUI_TxSettings.txt_locktHex.getText());
		jo.put("feeFrom", 	GUI_FeeSettings.feeFrom.getSelectedIndex());
		jo.put("feeSliderMax", GUI_FeeSettings.sliderMax.getSelectedIndex());
		jo.put("feeSliderMin", GUI_FeeSettings.sliderMin.getSelectedIndex());
		jo.put("fileNameAdressList", GUI_ImportCSV.lbl_file.getText());
		jo.put("fileNameSigTx", GUI_PublishTx.lbl_file_sigTx.getText());
		jo.put("languageIndex", GUI.cBox_language.getSelectedIndex());
		jo.put("maxAccaptFeeRate",GUI_FeeSettings.maxAccaptFeeRate);
		jo.put("estimateProfile", GUI_FeeSettings.estimateProfile);
		jo.put("FeeChart_scalY", LineDiagram.scalY);
		jo.put("FeeChart_btnNr", LineDiagram.btnNr);
		jo.put("toolTipEnabled", GUI.toolTipEnabled);
		jo.put("toolTipSetDismissDelay", GUI.toolTipSetDismissDelay);
		jo.put("fallBackEstimaFeeRate", GUI_FeeSettings.fallBackEstimaFeeRate);
	
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		br.write(jo.toString(1));
		br.close();
	} 
	catch (Exception e) 
	{
		GUI.txt_meld.setForeground(Color.red);
		GUI.txt_meld.setText(e.getMessage());
		e.printStackTrace();
	}
}
}