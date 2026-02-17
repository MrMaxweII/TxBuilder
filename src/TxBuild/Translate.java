package TxBuild;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



	/************************************************************************************************************************************
	 *	Translate V1.1                                            Mr.Maxwell                                             31.01.2026		*
	 *	Hier wird die Sprachumschaltung für verschiedene Sprachen implementiert.														*
	 *																																	*
	 *	Bedienung:																														*
	 *	1.: public static Translate t = new Translate(); 																				*
	 *	2.: System.out.println(t.t("Hello World"));																						*
	 *																																	*
	 *	Achtung. Die Klasse ist NICHT static, es muss der Konsruktor verwendet werden!													*
	 *	Es werden keine Texte übersetzt die an einen Logger geschickt werden. 															*										
	 ***********************************************************************************************************************************/



public class Translate
{

	public static String[] 	languages = {"English", "German"};						// Die Verfügbaren Sprachen
	private JSONObject 		englishTextJSON = new JSONObject(); 					// Enthält die Englischen Texte als JSON-Object
	private JSONObject 		germanTextJSON = new JSONObject(); 						// Enthält die Deutschen  Texte als JSON-Object

	



	// Konstruktor. Muss beim Start des Programms aufgerufen werden, Legt die Sprache fest
 	Translate()					
 	{ 		
 		try
 		{
 			setEnglishJSON();
 	 		setGermanJSON(); 	 		
 		}
 		catch(JSONException e){e.printStackTrace();}
 	}
 	
 	
 	
 	
 	// Gibt ein Dialog-Feld am Bildschirm aus, in dem die Sprache augsgewählt werden soll.
 	// Wird von der Klasse "Config" aufgerufen, wenn das Programm das erste mal starten. (Wenn die TxBuilder.json nicht vorhanen ist)
 	public static void ShowStartDialogg()
 	{
		JDialog dialog = new JDialog();   
		dialog.setLocationRelativeTo(null);  // Dialog im Mittelpunkt des Bildschirms anzeigen
        dialog.setLayout(null);
        dialog.setSize(500, 320); 
		dialog.setModal(true);
		dialog.setTitle("TxBuilder");
		dialog.setIconImage(MyIcons.bitcoinLogoMain.getImage());	
		JComboBox cb = new JComboBox();
		JButton btn = new JButton();
		btn.setBounds(150, 240, 200, 30);
		btn.setText("OK");
		cb.setBounds(150, 80, 200, 50);
		cb	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Choose language", TitledBorder.LEADING, TitledBorder.TOP, GUI.font3, GUI.color3));
		cb	.setModel(new DefaultComboBoxModel(languages));
		cb	.setForeground(GUI.color3);
		cb	.setBackground(GUI.color1);
		dialog.add(cb);
		dialog.add(btn);	
		btn.addActionListener(new ActionListener() 
		{
		    public void actionPerformed(ActionEvent e) 
		    {
				GUI.cBox_language.setSelectedIndex(cb.getSelectedIndex());	
		    	dialog.dispose();  
		    }
		});
		// Close Button wird abgefangen und hier selbst verarbeitet.
		dialog.addWindowListener(new java.awt.event.WindowAdapter() 
		{
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {System.exit(0);}
		});
		dialog.setVisible(true);
 	}
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 
	/**	Gibt den gewünschten Text in der eingestellen Sprache zurück. **/
 	public String t(String str)
 	{
 		try
 		{
 	 		if(GUI.cBox_language.getSelectedIndex()==0) return englishTextJSON.getString(str);
 	 		if(GUI.cBox_language.getSelectedIndex()==1) return germanTextJSON.getString(str);
 	 		throw new Exception("Sprache mit dem Index = "+GUI.cBox_language.getSelectedIndex()+" wurde nicht gefunden!");
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 			GUI.txt_meld.setForeground(Color.red);
 			GUI.txt_meld.setText(e.getMessage());
 			return "<Translate-Error!>";	
 		}
 	}
 	
 	
	/**	Gibt ein Text-Array in der eingestellten Sprache zurück, welcher dem Schlüssel "key" entspricht.. 
	 Diese Variante wird z.B. für ComboBoxen verwendet. 
 	 @param arraySize Die Array-länge des zu erwartenden Arrays, muss zwingend hier angegeben werden!	**/
 	public String[] getTextArray(String key, int arraySize)
 	{
 		try
 		{
 			if(GUI.cBox_language.getSelectedIndex()==0) 
 			{
 	 			JSONArray jaEN = englishTextJSON.getJSONArray(key);
 	 			String[] strEN=new String[jaEN.length()];
 				for(int i=0; i<strEN.length; i++) {strEN[i]=jaEN.getString(i);}
 				return strEN;
 			}
 	 		if(GUI.cBox_language.getSelectedIndex()==1) 
 	 		{
 	 			JSONArray jaDE =  germanTextJSON.getJSONArray(key);
 	 			String[] strDE=new String[jaDE.length()];
 				for(int i=0; i<strDE.length; i++) {strDE[i]=jaDE.getString(i);}
 				return strDE;
 	 		}
 	 		throw new Exception("Sprache mit dem Index = "+GUI.cBox_language.getSelectedIndex()+" wurde nicht gefunden!");
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 			GUI.txt_meld.setForeground(Color.red);
 			GUI.txt_meld.setText(e.getMessage());
 			String[] strError = new String[arraySize];
 			for(int i=0; i<arraySize; i++) {strError[i] = "<Translate-Error!>";}	
 			return strError;	
 		}
 	}
 	
 	
 
 	
 	// -------------------------------------------------------- Ein Java Programm zum sortieren von diesen Texten ist im Projekt "Test" "als Klasse: SortJsonPuts.java"
 	//----------------------------------------------------------------------------------- sotiert: --------------------------------------------------------
 	
 	
 	private void setEnglishJSON() throws JSONException
 	{
 		
 		
 		englishTextJSON.put("Fee"							,"Fee");
 		englishTextJSON.put("Info"							,"Info");
 		englishTextJSON.put("Value"							,"Value (BTC)");
 		englishTextJSON.put("cancel"						,"cancel");
 		englishTextJSON.put("1 week"						,"1 week");
 		englishTextJSON.put("4 week"						,"4 week");
 		englishTextJSON.put("QR-Code"						,"View QR code from Tx");
 		englishTextJSON.put("open Tx"						,"load Tx");
 		englishTextJSON.put("show Tx"						,"show Tx");
 		englishTextJSON.put("send Tx"						,"send Tx");
 		englishTextJSON.put("Settings"						,"Settings");
 		englishTextJSON.put("TestNet3"						,"TestNet3");
 		englishTextJSON.put("Language"						,"Language");
 		englishTextJSON.put("Fee rate"						,"Fee rate (sat/vB)");
 		englishTextJSON.put("Password"						,"Password");
 		englishTextJSON.put("Functions"						,"Functions");
 		englishTextJSON.put("Fee (BTC)"						,"Fee (BTC)");
 		englishTextJSON.put("Rejection"						,"Rejection");
 		englishTextJSON.put("User abort"					,"User abort");
 		englishTextJSON.put("View Tx Raw"					,"View Tx Raw");
 		englishTextJSON.put("Input count"					,"Input count");
 		englishTextJSON.put("BTC-Address"					,"BTC-Address");
 		englishTextJSON.put("fees = Null"					,"fees = Null");
 		englishTextJSON.put("Last output"					,"Last output");
 		englishTextJSON.put("Last blocks"					,"Last blocks");
 		englishTextJSON.put("feerate low"					,"feerate low");
 		englishTextJSON.put("Output count"					,"Output count");
 		englishTextJSON.put("Virtual size"					,"Virtual size (vB)");
 		englishTextJSON.put("Fee settings"					,"Fee settings");
 		englishTextJSON.put("First output"					,"First output");
 		englishTextJSON.put("scan QR code"					,"scan QR");
 		englishTextJSON.put("feerate high"					,"feerate high");
 		englishTextJSON.put("show QR code"					,"show QR code");
 		englishTextJSON.put("No connect!\n"					,"No connect!\n");
 		englishTextJSON.put("mempoolminfee"					,"mempoolminfee");
 		englishTextJSON.put("open csv file"					,"open csv file");
 		englishTextJSON.put("Source address"				,"Source address   (Tx-Input)");
 		englishTextJSON.put("Fee slider max"				,"Fee slider max");
 		englishTextJSON.put("Fee slider min"				,"Fee slider min");
 		englishTextJSON.put("open crypt file"				,"open crypt file");
 		englishTextJSON.put("feerate average"				,"feerate average");
 		englishTextJSON.put("View Transaction"				,"View Transaction");
 		englishTextJSON.put("Save Transaction"				,"Save Transaction");
 		englishTextJSON.put("No input address"				,"No input address");
 		englishTextJSON.put(" sat/vB\nIgnore?"				," sat/vB\nIgnore?");
 		englishTextJSON.put("Check connection"				,"Connect");
 		englishTextJSON.put("encrypt csv file"				,"encrypt csv file");
 		englishTextJSON.put("programm-infoText"				,GUI_InfoText.infoTextEN);
 		englishTextJSON.put("Total value input"				,"Total value input (BTC)");
 		englishTextJSON.put("No output address"				,"No output address");
 		englishTextJSON.put("As source address"				,"As source address");
 		englishTextJSON.put("Total Value (BTC)"				,"Total Value (BTC)");
 		englishTextJSON.put("Total value output"			,"Total value output (BTC)");
 		englishTextJSON.put("Get Signature Hash"			,"Get Signature Hash");
 		englishTextJSON.put("Bitcoin Core Path:"			,"Bitcoin-Core data path");
 		englishTextJSON.put("accept max feerate"			,"Maximum accepted fee rate");
 		englishTextJSON.put("accept min feerate"			,"Minimum accepted fee rate");
 		englishTextJSON.put("Destination address"			,"Destination address   (Tx-Output)");	
 		englishTextJSON.put("Bitcoin Address List"			,"Open my Bitcoin Address List");
 		englishTextJSON.put("Transaction settings"			,"Transaction settings");
 		englishTextJSON.put("Scan input address: "			,"Scan input address: ");
 		englishTextJSON.put("Input Transaction ID"			,"Input Transaction ID");
 		englishTextJSON.put("Scan output address: "			,"Scan output address: ");
 		englishTextJSON.put("As destination address"		,"As destination address");
 		englishTextJSON.put("Send signed transaction"		,"Send signed transaction");
 		englishTextJSON.put("View historical fee rate"		,"View historical fee rate");
 		englishTextJSON.put("Total Value Input = Null"		,"Total Value Input = Null");
 		englishTextJSON.put("Only .txn files allowed!"		, "Only .txn files allowed!");
 		englishTextJSON.put("sliderMax_Language-Array",new JSONArray(new String[]{"Estimated in 1 blocks", "Estimated in 2 blocks", "Estimated in 3 blocks", "Estimated in 4 blocks", "Estimated in 5 blocks", "Estimated in 6 blocks", "Estimated in 7 blocks", "Estimated in 8 blocks", "Estimated in 9 blocks", "Estimated in 10 blocks"}));
 		englishTextJSON.put("sliderMin_Language-Array",new JSONArray(new String[]{"mempoolminfee", "Estimated in 1000 blocks", "Estimated in   900 blocks", "Estimated in   800 blocks", "Estimated in   700 blocks", "Estimated in   600 blocks", "Estimated in   500 blocks", "Estimated in   400 blocks", "Estimated in   300 blocks", "Estimated in   200 blocks", "Estimated in   100 blocks","0"}));
 		englishTextJSON.put("acceptMax_Language-Array",new JSONArray(new String[]{"Highest Estimated Fee Rate", "(Highest Fee Rate) * 2", "(Highest Fee Rate) * 3", "(Highest Fee Rate) * 4", "(Highest Fee Rate) * 5", "(Highest Fee Rate) * 6", "(Highest Fee Rate) * 7", "(Highest Fee Rate) * 8", "(Highest Fee Rate) * 9", "(Highest Fee Rate) * 10", "absolute maximum"}));
 		englishTextJSON.put("Get JSON from transaction"		,"Convert transaction to JSON");
 		englishTextJSON.put("show address List as CSV?"		,"Should the Bitcoin address list also be displayed as a CSV file?\n- Yes: All your addresses will be displayed in CSV format. Also the addresses without credit.\n- No: Only the addresses with credit are imported, displayed and all amounts are added up.");
 		englishTextJSON.put("Passwort false!   Passwort:"	,"Wrong password!   Password:");
 		englishTextJSON.put("Fees must be greater than 0!"	,"Fees must be greater than 0!");
 		englishTextJSON.put("feerate min (mempool accept)"	,"feerate min (mempool accept)");
 		englishTextJSON.put("Authentication Method: Cookie"	,"Authentication Method: Cookie");
 		englishTextJSON.put("load Tx-inputs from Blockchain","↺  load Tx-inputs from Blockchain");
 		englishTextJSON.put("cancel loading from Blockchain","✖  cancel loading from Blockchain");
 		englishTextJSON.put("Create an unsigned transaction","Create an unsigned transaction");
 		englishTextJSON.put("Get transaction from blockchain","Get transaction from blockchain");
 		englishTextJSON.put("BitcoinCore connection settings","BitcoinCore connection settings");	
 		englishTextJSON.put("Creates a QR code from any text.","Creates a QR code from any text.\nE.g. Bitcoin address, transaction, or anything else from which a QR code should be created.");
 		englishTextJSON.put("Password to be used for encryption:","Password to be used for encryption:");
 		englishTextJSON.put("Tx Error: No correct transaction!\n","Tx Error: Invalid transaction!\n");
 		englishTextJSON.put("Success! Transaction has been send!\n","Success! Transaction has been send!\n");
 		englishTextJSON.put("FallBack estimatesmartfee Data used","\nFallBack data is used.\nThe estimatesmartfee-FallBack data can be adjusted in the config.");
 		englishTextJSON.put("Average Fee Rate could not be loaded!\n","Average Fee Rate could not be loaded!\n");
 		englishTextJSON.put("Which output should the fee be deducted","Which output should the fee be deducted");
 		englishTextJSON.put("No address with an available amount found.","No address with an available amount found.");
 		englishTextJSON.put("Insert signed transactions into this field","Drag and drop the transaction or enter it in hexa");
 		englishTextJSON.put("The Fees is higher than 0,001 BTC \nIgnore?","The Fees is higher than 0,001 BTC \nIgnore?");
 		englishTextJSON.put("This is a test network. Coins have no value!","This is a test network. Coins have no value!");
 		englishTextJSON.put("Calculate Signature Hash\nInput RAW transaction:","Calculate Signature Hash\nInput RAW transaction:");
 		englishTextJSON.put("Connected, but password or username are wrong!\n","Connected, but password or username are wrong!\n");
 		englishTextJSON.put("With OK, the transaction will be send irreversibly!","With OK, the transaction will be send irreversibly!");
 		englishTextJSON.put("Convert transaction to JSON format\nInput RAW transaction:","Convert transaction to JSON format\\nInput RAW transaction:");
 		englishTextJSON.put("The minimum and maximum values \nof the fee slider are set here.","The minimum and maximum values \nof the fee slider are set here.");
 		englishTextJSON.put("FeeRate exceeds the maximum allowable amount.\nMaximum FeeRate = ","FeeRate exceeds the maximum allowable amount.\nMaximum FeeRate = ");
 		englishTextJSON.put("FeeRate falls below the minimum allowed amount.\nMinimum FeeRate = ","FeeRate falls below the minimum allowed amount.\nMinimum FeeRate = ");
 		englishTextJSON.put("Mark one or more lines and select them as the input or output address.","Mark one or more lines and select them as the input or output address.");
 		englishTextJSON.put("Tx input has been changed or is empty.\n Load Tx-Inputs from Blockchain!","Tx input has been changed or is empty.\n Load Tx-Inputs from Blockchain!");
 		englishTextJSON.put("The FeeRate is less than the network minimum FeeRate. \nMinimum FeeRate = ","The FeeRate is less than the network minimum FeeRate. \nMinimum FeeRate = ");
 		englishTextJSON.put("The accepted fee rate that is allowed at all.\nThis setting will not be saved!","The accepted fee rate that is allowed at all.\nThis setting will not be saved!");
 		englishTextJSON.put(" sat/vB \nTransaction will not be created!\nIn the FeeSettings you can set this."," sat/vB \nTransaction will not be created!\nIn the FeeSettings you can set this.");
 		englishTextJSON.put("The number of transaction exits is redefined.\nThis action deletes all previous exits!","The number of transaction exits is redefined.\nThis action deletes all previous exits!");
 		englishTextJSON.put("Specifies the time at which the transaction should be executed.\nAt: \"00000000\", immediately.","A set lock time indicates that the transaction is only valid after a certain block height.\nIf: \"00000000\" the transaction is valid immediately.\nDefault: \"00000000\"");
 		englishTextJSON.put("The number of transaction entries will be redefined.\nThis action will delete all previous entries!","The number of transaction entries will be redefined.\nThis action will delete all previous entries!");
 		englishTextJSON.put("The FeeRate is disproportionately high!\nHighest estimated FeeRate for confirmation in the next block is: ","The FeeRate is disproportionately high!\nHighest estimated FeeRate for confirmation in the next block is: ");
 		englishTextJSON.put("If sequence number is < 0xFFFFFFFF: Makes the transaction input Replace-By-Fee. Default:\"4294967295\" hex: \"ffffffff\"","If sequence number is < 0xFFFFFFFF: Makes the transaction input Replace-By-Fee. (BIP-125)\nDefault:\"4294967295\" hex: \"ffffffff\"");
 		englishTextJSON.put("Authentication method: Sign-in\nYou have to enter the RPC credentials from the bitcoin.conf file of the Bitcoin core here.","Authentication method: Sign-in\nYou have to enter the RPC credentials from the bitcoin.conf file of the Bitcoin core here.");
 		englishTextJSON.put("Change the default values only if you know exactly what you're doing!\nIncorrect values can lead to the loss of the coins!","Change the default values only if you know exactly what you're doing!\nIncorrect values can lead to the loss of the coins!");
 		englishTextJSON.put("No marked addresses.\nMark the desired addresses!\nUse the keyboard shortcuts: <Ctrl+a> or <Ctrl+select> or<Shift+select> etc.","No marked addresses.\nMark the desired addresses!\nUse the keyboard shortcuts: <Ctrl+a> or <Ctrl+select> or<Shift+select> etc.");
 		englishTextJSON.put("Signed transactions can be sent to the network here.\nThis action can never be reprimanded.\nCheck the transaction very carefully!","Signed transactions can be sent to the network here.\nThis action is irreversible\nCheck the transaction very carefully!");
 		englishTextJSON.put("This program must be connected to a Bitcoin-Core.\nYou can find the connection data in the Bitcoin-Core directory, in the bitcoin.conf file.","This program must be connected to a Bitcoin-Core.\nThis allows you to establish an RPC connection to your Bitcoin core.\nYou can find the connection data in the Bitcoin-Core directory, in the bitcoin.conf file.");
 		englishTextJSON.put("Bitcoin-Core is not running, or the file path to the Bitcoin directory is not correct!\nSpecify the file path where the configuration file (bitcoin.conf) is stored.","The \"cookie\" file was not found!\nThis may be due to the following reasons:\n- Bitcoin Core is not running. (The .cookie file is only created when the Bitcoin Core is started.)\n- The .cookie file is not in the specified directory\n- The Bitcoin Core is configured incorrectly\nSpecify the correct directory where the .cookie file is located.\nUsually it is the main Bitcoin directory.");
 		englishTextJSON.put("This is where the csv file is imported, which contains your own Bitcoin addresses.\n Once imported, the total amount of all controlled Bitcoin addresses will be displayed.\n They can directly choose from which addresses they want to send or receive.","This is where the csv file is imported, which contains your own Bitcoin addresses.\nOnce imported, the total amount of all controlled Bitcoin addresses will be displayed.\nThey can directly choose from which addresses they want to send or receive.");
 	
 		
 	 	// -------------------------------------------------------------------------- ToolTip Texte ------------------------------------------------------------------------------------------------------------------------------
	
 		englishTextJSON.put("ToolTipText_btn_loadTx"	,"<html>Searches the blockchain for all Tx inputs.<br>Must be done on every change of Tx inputs!<br>This process takes a while.</html>");
 		englishTextJSON.put("ToolTipText_btn_QR"		,"Scan QR code of a Bitcoin address");
 		englishTextJSON.put("ToolTipText_cBox_inCount"	,"Set the number of transaction inputs");
 		englishTextJSON.put("ToolTipText_cBox_outCount"	,"Set the number of transaction outputs");
 		englishTextJSON.put("ToolTipText_pnl_fee"		,"<html>Sets the transaction fee.<br>The transaction fee is deducted from the output marked in yellow.</html>");
 		englishTextJSON.put("ToolTipText_txt_inAdr"		,"Sender from which coins are sent");
 		englishTextJSON.put("ToolTipText_txt_outAdr"	,"Destination address where coins are sent");
 		englishTextJSON.put("ToolTipText_txt_outValue"	,"<html>Amount in BTC<br>All Character must be filled in.<br>e.g.: “0000000.01230000”</html>");
 		englishTextJSON.put("ToolTipText_btn_auth"		,"<html>Cookie-based authentication is used when no RPC password is provided.<br>The Bitcoin Core creates a \".cookie\" file in the main directory when it starts.<br>This file must exist and the file path to it must be specified.<br>In this case, the IP address is always 127.0.0.1</html>");
 		englishTextJSON.put("ToolTipText_lbl_path"		,"<html>The Bitcoin Core file path is specified in which the file: \"cookie\" must be located.<br>Normally this is the main directory of Bitcoin Core.</html>");
 		englishTextJSON.put("ToolTipText_txt_timeOut"	,"<html>If after this time (in seconds)<br> no connection is established,<br>it will be aborted.</html>");
 		englishTextJSON.put("ToolTipText_txt_ip"		,"<html>If Bitcoin Core is located on the same PC, the IP address is always: 127.0.0.1<br>Otherwise, the local IP address of the PC on which Bitcoin Core is running must be entered here.</html>");
 		englishTextJSON.put("ToolTipText_txt_port"		,"<html>The Bitcoin RPC port is usually:<br>8332 for Main-Net, and<br>18332 for Testnet 3.<br>The correct port is in the bitcoin.conf file under \"rpcport=\"</html>");
 		englishTextJSON.put("ToolTipText_txt_uName"		,"<html>The correct \"User name\" is in the bitcoin.conf file under \"rpcuser=\"</html>");
 		englishTextJSON.put("ToolTipText_txt_pw"		,"<html>The correct password is located in the bitcoin.conf file under \"rpcpassword=\"<br>The password can be changed there.<br>It must match this password!</html>");
 		englishTextJSON.put("ToolTipText_txt_btn_cTest"	,"<html>Tests the RPC connection and connects to the Bitcoin core.<br>The connected Bitcoin core determines the network.<br>If the connected core is on the testnet,<br>this program will also run on the testnet.</html>");
 		englishTextJSON.put("ToolTipText_feeFrom"		,"<html>The transaction fee can be deducted from the first<br>or last output.</html>");
 		englishTextJSON.put("ToolTipText_sliderMax"		,"<html>This is where the highest, i.e. top value, of the transaction fee slider is set.<br>Example: If “Estimated in 5 blocks” is selected,<br>this means that the top value of the slider sets a transaction fee that will confirm the transaction in 5 blocks (estimated).</html>");
 		englishTextJSON.put("ToolTipText_sliderMin"		,"<html>This is where the smallest, i.e. lowest value, of the transaction fee slider is set.<br>Example: If “Estimated in 100 blocks” is selected,<br>this means that the smallest value of the slider sets a transaction fee that will confirm the transaction in 100 blocks (estimated).<br>If “0” is selected, the smallest value of the slider is 0 sat/vB.");
 		englishTextJSON.put("ToolTipText_acceptMax"		,"<html>Here, the highest permitted transaction fee rate is set.<br>Example: If \"(Highest Ø fee rate) * 2\" is selected, this means:<br>that twice the value of the highest average fee rate,<br>currently determined in the network,<br>is set as the maximum limit for a transfer.<br>Even by manually changing the issue amount,<br>no transaction will be accepted, which would include a higher transaction fee.</html>");
 		englishTextJSON.put("ToolTipText_acceptMin"		,"<html>In principle, the smallest permitted transaction fee rate is set here.<br>Example: If \"Minimum allowed in Mempool\" is selected, this means:<br>that the minimum transaction fee rate currently determined in the network,<br>is set as the minimum limit for a transfer.<br>Even by manually changing the issue amount,<br>no transaction will be accepted that would include a lower transaction fee rate.<br>If 0 is selected there is no lower limit.</html>");
 		englishTextJSON.put("ToolTipText_btn_testNet"	,"<html>This setting is determined by the connected Bitcoin core.</html>");
 		englishTextJSON.put("ToolTipText_btn_open"		,"<html>Imports an unencrypted CSV file with Bitcoin addresses.<br>The blockchain is searched for balances on these addresses.<br>All Bitcoin addresses with balances are listed for selection.</html>");
 		englishTextJSON.put("ToolTipText_btn_open_crypt","<html>Imports an encrypted .crypt file with Bitcoin addresses.<br>The password to decrypt the .crypt file must be entered.<br>The blockchain is searched for balances on these addresses.<br>All Bitcoin addresses with balances are listed for selection.</html>");
 		englishTextJSON.put("ToolTipText_btn_encrypt"	,"<html>Encrypt an unencrypted CSV file<br>that contains a list of Bitcoin addresses with a password.<br>This file is then saved as an encrypted variant (.crypt file).</html>");
 		englishTextJSON.put("ToolTipText_btn_input"		,"<html>All marked lines are imported as source addresses.<br>At least one line must be marked.</html>");
 		englishTextJSON.put("ToolTipText_btn_output"	,"<html>All marked lines are imported as target addresses.<br>At least one line must be marked.</html>");
 		englishTextJSON.put("ToolTipText_btn_scanQR"	,"<html>Scans the QR code of a signed transaction<br>and transfers it to this field.</html>");
 		englishTextJSON.put("ToolTipText_btn_openTx"	,"<html>Opens a signed transaction from a text file (.txn)<br>and transfers it to this field.</html>");
 		englishTextJSON.put("ToolTipText_btn_showTx"	,"<html>Before sending, view the transaction in the input field with the Tx printer<br> to check it!</html>");
 		englishTextJSON.put("ToolTipText_btn_send"		,"<html>This sends a fully signed transaction to the network.<br>When you press this button for the first time, the transaction is only checked to see whether it is accepted by the network.<br>It can then be finally sent.</html>");
 		englishTextJSON.put("ToolTipText_lineName"		,"Hide line / Show line");
 		englishTextJSON.put("ToolTipText_btn_2h"		,"View the last 2 hours.");
 		englishTextJSON.put("ToolTipText_btn_24h"		,"View the last 24 hours.");
 		englishTextJSON.put("ToolTipText_btn_week"		,"View the last week.");
 		englishTextJSON.put("ToolTipText_btn_4week"		,"View the last 4 weeks.");
 		englishTextJSON.put("ToolTipText_spinnerY"		,"Increase/decrease Y axis");
 	} 	

	



 	private void setGermanJSON() throws JSONException
 	{
 		germanTextJSON.put("Fee"							,"Fee"); // Übersetzung währe zu lang, nur 3 Zeichen!
 		germanTextJSON.put("Info"							,"Info");
 		germanTextJSON.put("Value"							,"Betrag (BTC)");
 		germanTextJSON.put("cancel"							,"Abbrechen");
 		germanTextJSON.put("1 week"							,"1 Woche");
 		germanTextJSON.put("4 week"							,"4 Wochen");
 		germanTextJSON.put("QR-Code"						,"QR-Code von Transaktion anzeigen");
 		germanTextJSON.put("open Tx"						,"Tx Laden");
 		germanTextJSON.put("show Tx"						,"Tx Anzeigen");
 		germanTextJSON.put("send Tx"						,"Tx Senden");
 		germanTextJSON.put("Settings"						,"Einstellungen");
 		germanTextJSON.put("TestNet3"						,"TestNet3");	
 		germanTextJSON.put("Language"						,"Sprache");
 		germanTextJSON.put("Fee rate"						,"Gebührensatz (sat/vB)");	
 		germanTextJSON.put("Password"						,"Passwort");
 		germanTextJSON.put("Functions"						,"Funktionen");
 		germanTextJSON.put("Fee (BTC)"						,"Tx-Gebühr (BTC)");
 		germanTextJSON.put("Rejection"						,"Ablehnung");
 		germanTextJSON.put("User abort"						,"Benutzer Abbruch");
 		germanTextJSON.put("View Tx Raw"					,"Transaktion in Hexa anzeigen");
 		germanTextJSON.put("Input count"					,"Anzahl Tx-Eingänge");
 		germanTextJSON.put("BTC-Address"					,"BTC-Adresse");
 		germanTextJSON.put("fees = Null"					,"Transaktions-Gebühren = Null");
 		germanTextJSON.put("Last output"					,"Letzter Ausgang");
 		germanTextJSON.put("Last blocks"					,"Letzte Blöcke");
 		germanTextJSON.put("feerate low"					,"Niedrigster Gebührensatz");
 		germanTextJSON.put("Output count"					,"Anzahl Tx-Ausgänge");
 		germanTextJSON.put("Virtual size"					,"Virtuelle Größe (vB)");
 		germanTextJSON.put("Fee settings"					,"Gebühren Einstellungen");
 		germanTextJSON.put("First output"					,"Erster Ausgang");
 		germanTextJSON.put("scan QR code"					,"Scan QR");
 		germanTextJSON.put("feerate high"					,"Höchster Gebührensatz");
 		germanTextJSON.put("show QR code"					,"QR-Code anzeigen");
 		germanTextJSON.put("No connect!\n"					,"Keine Verbindung!\n");
 		germanTextJSON.put("mempoolminfee"					,"Minimum erlaubt in Mempool");
 		germanTextJSON.put("open csv file"					,"CSV-Datei öffnen");
 		germanTextJSON.put("Source address"					,"Quell-Adressen   (Tx-Eingänge)");
 		germanTextJSON.put("Fee slider max"					,"Gebühren-Schieberegler Maximalwert");
 		germanTextJSON.put("Fee slider min"					,"Gebühren-Schieberegler Minimalwert");
 		germanTextJSON.put("open crypt file"				,".crypt Datei öffnen");
 		germanTextJSON.put("feerate average"				,"Durchschnittlicher Gebührensatz");
 		germanTextJSON.put("View Transaction"				,"Transaktion anzeigen");
 		germanTextJSON.put("Save Transaction"				,"Transaktion speichern");
 		germanTextJSON.put("No input address"				,"Keine Eingabeadresse");
 		germanTextJSON.put(" sat/vB\nIgnore?"				," sat/vB\nIgnorieren?");
 		germanTextJSON.put("Check connection"				,"Verbinden");
 		germanTextJSON.put("encrypt csv file"				,"CSV-Datei verschlüsseln");
 		germanTextJSON.put("programm-infoText"				,GUI_InfoText.infoTextDE);
 		germanTextJSON.put("Total value input"				,"Summe Eingänge (BTC)");
 		germanTextJSON.put("No output address"				,"Keine Ausgabeadresse");
 		germanTextJSON.put("As source address"				,"Als Quell-Adresse auswählen");
 		germanTextJSON.put("Total Value (BTC)"				,"Gesamtbetrag (BTC)");
 		germanTextJSON.put("Total value output"				,"Summe Ausgänge (BTC)");
 		germanTextJSON.put("Get Signature Hash"				,"Signature-Hash aus Tx berechnen");
 		germanTextJSON.put("Bitcoin Core Path:"				,"Bitcoin-Core Verzeichnis");
 		germanTextJSON.put("accept max feerate"				,"Maximal akzeptierte Gebührenrate");
 		germanTextJSON.put("accept min feerate"				,"Minimal akzeptierte Gebührenrate");
 		germanTextJSON.put("Destination address"			,"Ziel-Adressen   (Tx-Ausgänge)");		
 		germanTextJSON.put("Bitcoin Address List"			,"Meine Bitcoin Adressliste öffnen");
 		germanTextJSON.put("Transaction settings"			,"Transaktions Einstellungen");
 		germanTextJSON.put("Scan input address: "			,"Scan Eingangs Adresse: ");
 		germanTextJSON.put("Input Transaction ID"			,"Transaktion ID Eingeben");
 		germanTextJSON.put("Scan output address: "			,"Scan Ausgangs Adresse: ");
 		germanTextJSON.put("As destination address"			,"Als Zieladresse auswählen");
 		germanTextJSON.put("Send signed transaction"		,"Signierte Transaktion senden");
 		germanTextJSON.put("View historical fee rate"		,"Historischen Gebührensatz anzeigen");
 		germanTextJSON.put("Total Value Input = Null"		,"Kein Eingangs-Guthaben");		
 		germanTextJSON.put("Only .txn files allowed!"		,"Nur .txn Datei erlaubt!");

 		germanTextJSON.put("sliderMax_Language-Array",new JSONArray(new String[]{"Geschätzt in 1 Blöcken", "Geschätzt in 2 Blöcken", "Geschätzt in 3 Blöcken", "Geschätzt in 4 Blöcken", "Geschätzt in 5 Blöcken", "Geschätzt in 6 Blöcken", "Geschätzt in 7 Blöcken", "Geschätzt in 8 Blöcken", "Geschätzt in 9 Blöcken", "Geschätzt in 10 Blöcken"}));
 		germanTextJSON.put("sliderMin_Language-Array",new JSONArray(new String[]{"Minimum erlaubt in Mempool", "Geschätzt in 1000 Blöcken", "Geschätzt in 900 Blöcken", "Geschätzt in 800 Blöcken", "Geschätzt in 700 Blöcken", "Geschätzt in 600 Blöcken", "Geschätzt in 500 Blöcken", "Geschätzt in 400 Blöcken", "Geschätzt in 300 Blöcken", "Geschätzt in 200 Blöcken", "Geschätzt in 100 Blöcken", "0"}));
 		germanTextJSON.put("acceptMax_Language-Array",new JSONArray(new String[]{"Höchster Ø Gebührensatz", "(Höchster Ø Gebührensatz) * 2", "(Höchster Ø Gebührensatz) * 3", "(Höchster Ø Gebührensatz) * 4", "(Höchster Ø Gebührensatz) * 5", "(Höchster Ø Gebührensatz) * 6", "(Höchster Ø Gebührensatz) * 7", "(Höchster Ø Gebührensatz) * 8", "(Höchster Ø Gebührensatz) * 9", "(Höchster Ø Gebührensatz) * 10", "absolutes Maximum"}));
 		germanTextJSON.put("Get JSON from transaction"		,"Transaktion nach JSON konvertieren");
 		germanTextJSON.put("show address List as CSV?"		,"Soll die Bitcoin-Adressliste auch als CSV-Datei angezeigt werden?\n- Ja:   Alle Ihre Adressen werden im CSV-Format angezeigt. Auch die Adressen ohne Guthaben.\n- Nein:   Es werden nur die Adressen mit Guthaben importiert, angezeigt und alle Beträge aufsummiert.");
 		germanTextJSON.put("Passwort false!   Passwort:"	,"Passwort falsch!   Passwort:");
 		germanTextJSON.put("Fees must be greater than 0!"	,"Transaktions-Gebühren müssen größer als 0 sein!");
 		germanTextJSON.put("feerate min (mempool accept)"	,"Minimaler Gebührensatz (mempool accept)");
 		germanTextJSON.put("Authentication Method: Cookie"	,"Authentifizierungs-Methode: Cookie");
 		germanTextJSON.put("load Tx-inputs from Blockchain"	,"↺  Tx-Eingänge aus der Blockchain laden");
 		germanTextJSON.put("cancel loading from Blockchain"	,"✖  Ladevorgang abbrechen");
 		germanTextJSON.put("Create an unsigned transaction"	,"Erstelle eine unsignierte Transaktion");
 		germanTextJSON.put("Get transaction from blockchain","Transaktion aus der Blockchain laden");		
 		germanTextJSON.put("BitcoinCore connection settings","Bitcoin-Core Verbindungseinstellungen");
 		germanTextJSON.put("Creates a QR code from any text.","Erstellt einen QR-Code aus einem beliebigen Text.\nZ.B. Bitcoin-Adresse, Transaktion oder irgendetwas anderes, aus dem ein QR-Code erstellt werden soll.");
 		germanTextJSON.put("Password to be used for encryption:" ,"Passwort mit dem verschlüsselt werden soll:");
 		germanTextJSON.put("Tx Error: No correct transaction!\n","Tx Error: Ungültige Transaktion!\n");
 		germanTextJSON.put("Success! Transaction has been send!\n","Erfolgreich! Die Transaktion wurde gesendet!\n");
 		germanTextJSON.put("FallBack estimatesmartfee Data used","\nFallBack Daten werden ​​verwendet.\nDie estimatesmartfee-FallBack Daten können in der Config angepasst werden.");
 		germanTextJSON.put("Average Fee Rate could not be loaded!\n","Durchschnittlicher Gebührensatz konnte nicht geladen werden.\n");
 		germanTextJSON.put("Which output should the fee be deducted","Von welchem Ausgang soll die Gebühr abgezogen werden");
 		germanTextJSON.put("No address with an available amount found.","Keine Adresse mit verfügbarem Betrag gefunden.");
 		germanTextJSON.put("Insert signed transactions into this field","Transaktion per \"Drag and Drop\" hineinziehen, oder in Hexa eingeben");
 		germanTextJSON.put("The Fees is higher than 0,001 BTC \nIgnore?","Die Gebühren sind höher als 0,001 BTC \nIgnorieren?");		
 		germanTextJSON.put("This is a test network. Coins have no value!","Dies ist ein Testnetzwerk. Coins haben keinen Wert!");
 		germanTextJSON.put("Calculate Signature Hash\nInput RAW transaction:","Signatur-Hash berechnen\nRAW-Transaktion eingeben:");
 		germanTextJSON.put("Connected, but password or username are wrong!\n","Verbunden, aber Passwort oder Benutzername sind falsch!\n");
 		germanTextJSON.put("With OK, the transaction will be send irreversibly!","Durch OK wird die Transaktion unwiderruflich versendet!");
 		germanTextJSON.put("Convert transaction to JSON format\nInput RAW transaction:","Transaktion nach JSON-Format konvertieren\nRAW-Transaktion eingeben:");
 		germanTextJSON.put("The minimum and maximum values \nof the fee slider are set here.","Hier werden die Mindest- und Höchstwerte \ndes Gebührenreglers eingestellt.");
 		germanTextJSON.put("FeeRate exceeds the maximum allowable amount.\nMaximum FeeRate = ","Gebührensatz überschreitet den maximal zulässigen Betrag.\nMaximum Gebührensatz = ");
 		germanTextJSON.put("FeeRate falls below the minimum allowed amount.\nMinimum FeeRate = ","Der Gebührensatz ist unter den zulässigen Mindestbetrag.\nMindestgebührensatz = ");
 		germanTextJSON.put("Mark one or more lines and select them as the input or output address.","Markieren Sie eine oder mehrere Zeilen und wählen Sie Diese als Ein- oder Ausgabeadresse aus.");
 		germanTextJSON.put("Tx input has been changed or is empty.\n Load Tx-Inputs from Blockchain!","Tx-Eingänge wurden geändert oder sind leer.\n Du musst die Tx-Eingänge aus Blockchain neu laden!");
 		germanTextJSON.put("The FeeRate is less than the network minimum FeeRate. \nMinimum FeeRate = ","Der Gebührensatz liegt unter dem Mindestgebührensatz des Netzwerks. \nMindestgebührensatz = ");
 		germanTextJSON.put("The accepted fee rate that is allowed at all.\nThis setting will not be saved!","Der maximal erlaubte Gebührensatz.\nDiese Einstellung wird nicht gespeichert!");
 		germanTextJSON.put(" sat/vB \nTransaction will not be created!\nIn the FeeSettings you can set this."," sat/vB \nTransaktion wird nicht erstellt!\nIn den Gebühreneinstellungen können Sie dies einstellen.");
 		germanTextJSON.put("The number of transaction exits is redefined.\nThis action deletes all previous exits!","Die Anzahl der Transaktions-Ausgänge wird neu festgelegt.\nDiese Aktion löscht alle bisherigen Ausgänge!");
 		germanTextJSON.put("Specifies the time at which the transaction should be executed.\nAt: \"00000000\", immediately.","Eine festgelegte Sperrzeit gibt an, dass die Transaktion erst nach einer bestimmten Blockhöhe gültig ist.\nBei: \"00000000\" ist die Transaktion sofort gültig.\nDefault: \"00000000\"");
 		germanTextJSON.put("The number of transaction entries will be redefined.\nThis action will delete all previous entries!","Die Anzahl der Transaktions-Eingänge wird neu festgelegt.\nDiese Aktion löscht alle vorherigen Eingänge!");
 		germanTextJSON.put("The FeeRate is disproportionately high!\nHighest estimated FeeRate for confirmation in the next block is: ","Der Gebührensatz ist unverhältnismäßig hoch!\nDer höchste geschätzte Gebührensatz für die Bestätigung im nächsten Block beträgt: ");
 		germanTextJSON.put("If sequence number is < 0xFFFFFFFF: Makes the transaction input Replace-By-Fee. Default:\"4294967295\" hex: \"ffffffff\"","Wenn die Sequenznummer kleiner als 0xFFFFFFFF ist:\nkann die Transaktion mit \"Replace-By-Fee\" ersetzt werden. (BIP-125)\nDefault: \"4294967295\" hex: \"ffffffff\"");
 		germanTextJSON.put("Authentication method: Sign-in\nYou have to enter the RPC credentials from the bitcoin.conf file of the Bitcoin core here.","Authentifizierungs-Methode: Benutzerdaten\nHier müssen Sie die RPC-Anmeldeinformationen aus der bitcoin.conf-Datei des Bitcoin-Core eingeben.");
 		germanTextJSON.put("Change the default values only if you know exactly what you're doing!\nIncorrect values can lead to the loss of the coins!","Ändern Sie die Standardwerte nur, wenn Sie wissen, was Sie tun!\nFalsche Werte können zum Verlust der Münzen führen!");
 		germanTextJSON.put("No marked addresses.\nMark the desired addresses!\nUse the keyboard shortcuts: <Ctrl+a> or <Ctrl+select> or<Shift+select> etc.","Keine markierten Adressen.\nMarkieren Sie die gewünschten Adressen!\nVerwenden Sie die Tastenkombinationen: <Strg+a> oder <Strg+Auswahl> oder <Umschalt+Auswahl> usw.");
 		germanTextJSON.put("Signed transactions can be sent to the network here.\nThis action can never be reprimanded.\nCheck the transaction very carefully!","Hier können signierte Transaktionen an das Netzwerk gesendet werden.\nDiese Aktion ist unumkehrbar!\nÜberprüfen Sie die Transaktion sorgfältig!");
 		germanTextJSON.put("This program must be connected to a Bitcoin-Core.\nYou can find the connection data in the Bitcoin-Core directory, in the bitcoin.conf file.","Dieses Programm muss mit einem Bitcoin-Core verbunden werden.\nHiermit stellst du eine RPC-Verbindung zu deinem Bitcoin-Core her.\nDie Verbindungsdaten findest du im Bitcoin-Core-Verzeichnis, in der Datei bitcoin.conf.");
 		germanTextJSON.put("Bitcoin-Core is not running, or the file path to the Bitcoin directory is not correct!\nSpecify the file path where the configuration file (bitcoin.conf) is stored.","Die Datei \".cookie\" wurde nicht gefunden!\nDies kann folgende Gründe haben:\n- Bitcoin-Core läuft nicht. (Die .cookie Datei wird nur erstellt, wenn der Bitcoin-Core gestarte ist.)\n- Die .cookie Datei befindet sich nicht im angegebenem Verzeichnis\n- Der Bitcoin-Core ist falsch configuriert\nGeben Sie das richtige Verzeichnis an, in dem sich die .cookie Datei befindet.\nNormalerweise ist dies das Bitcoin Hauptverzeichnis."); 
 		germanTextJSON.put("This is where the csv file is imported, which contains your own Bitcoin addresses.\n Once imported, the total amount of all controlled Bitcoin addresses will be displayed.\n They can directly choose from which addresses they want to send or receive.","Hier wird die .csv oder .crypt-Datei importiert, die ihre eigenen Bitcoin-Adressen enthält.\nNach dem Import wird die Gesamtmenge aller kontrollierten Bitcoin-Adressen und deren Beträge angezeigt.\nSie können direkt auswählen, von welchen Adressen sie senden oder empfangen möchten.");
 	
 	
 	 	// -------------------------------------------------------------------------- ToolTip Texte ------------------------------------------------------------------------------------------------------------------------------

 		germanTextJSON.put("ToolTipText_btn_loadTx"		,"<html>Durchsucht die Blockchain nach allen Tx-Eingängen.<br>Muss bei jeder Änderung von Tx-Eingängen durchgeführt werden!<br>Dieser Vorgang dauert eine Weile.</html>");
 		germanTextJSON.put("ToolTipText_btn_QR"			,"QR-Code einer Bitcoin-Adresse Scannen");
 		germanTextJSON.put("ToolTipText_cBox_inCount"	,"Die Anzahl der Transaktions Eingänge festlegen");
 		germanTextJSON.put("ToolTipText_cBox_outCount"	,"Die Anzahl der Transaktions Ausgänge festlegen");
 		germanTextJSON.put("ToolTipText_pnl_fee"		,"<html>Legt die Transaktionsgebühr fest.<br>Die Transaktionsgebühr wird von der gelb marktierten Ausgabe abgezogen.</html>");
 		germanTextJSON.put("ToolTipText_txt_inAdr"		,"Absender von dem Coins versendet werden");
 		germanTextJSON.put("ToolTipText_txt_outAdr"		,"Ziel-Adresse wohin Coins versendet werden");
 		germanTextJSON.put("ToolTipText_txt_outValue"	,"<html>Betrag in BTC<br>Alle Zeichen müssen ausgefüllt sein.<br>z.B.: \"0000000,01230000\"</html>");
 		germanTextJSON.put("ToolTipText_btn_auth"		,"<html>Die Cookie-basierte Authentifizierung wird verwendet, wenn kein RPC-Passwort angegeben wird.<br>Der Bitcoin Core erstellt beim Start eine „.cookie“-Datei im Hauptverzeichnis.<br>Diese Datei muss vorhanden sein und der Dateipfad dazu muss angegeben werden.<br>Die IP-Adresse ist in diesem Fall immer 127.0.0.1</html>");
 		germanTextJSON.put("ToolTipText_lbl_path"		,"<html>Es wird der Bitcoin-Core Dateipfad festgelegt in dem sich die Datei: \"cookie\" befinden muss.<br>Normalerweise ist dies das Hauptverzeichnist vom Bitcoin-Core.</html>");
 		germanTextJSON.put("ToolTipText_txt_timeOut"	,"<html>Wenn nach dieser Zeit (in Sekunden)<br> keine Verbindung zustande kommt,<br>wird abgebrochen.</html>");
 		germanTextJSON.put("ToolTipText_txt_ip"			,"<html>Wenn sich der Bitcoin-Core auf demselben PC befindet, ist die IP-Adresse immer: 127.0.0.1<br>Anderenfalls muss hier die lokale IP-Adresse des PC´s eingetragen werden,<br>auf dem der Bitcoin-Core läuft.</html>");
 		germanTextJSON.put("ToolTipText_txt_port"		,"<html>Der Bitcoin RPC-Port ist normalerweise:<br>8332 für Main-Net, und<br>18332 für Testnet 3.<br>Der richtige Port steht in der bitcoin.conf Datei unter \"rpcport=\"</html>");
 		germanTextJSON.put("ToolTipText_txt_uName"		,"<html>Der richtige User-Name steht in der bitcoin.conf Datei unter \"rpcuser=\"</html>");
 		germanTextJSON.put("ToolTipText_txt_pw"			,"<html>Das richtige Passwort steht in der bitcoin.conf Datei unter \"rpcpassword=\"<br>Das Passwort kann dort geändert werden.<br>Es muss mit diesem Passwort übereinstimmen!</html>");
 		germanTextJSON.put("ToolTipText_txt_btn_cTest"	,"<html>Testet die RPC-Verbindung und verbindet sich mit dem Bitcoin-Core.<br>Der verbundene Bitcoin-Core legt das Netzwerk fest.<br>Wenn der verbundene Core sich im Testnet befindet,<br>wird auch dieses Programm im Testnet arbeiten.</html>");
 		germanTextJSON.put("ToolTipText_feeFrom"		,"<html>Die Transaktionsgebühr kann vom ersten<br>oder vom letzten Ausgang abgezogen werden.</html>");
 		germanTextJSON.put("ToolTipText_sliderMax"		,"<html>Hier wird der höchste, also oberste Wert,<br> des Transaktion Gebührenreglers festgelegt.<br>Beispiel: Wenn \"Geschätzt in 5 Blöcken\" ausgewählt wird, bedeutet dies,<br>das der oberste Wert des Schiebereglers eine Transaktions-Gebühr einstellt,<br>die die Transaktion in 5 Blöcken (geschätzt) bestätigen lässt.</html>");
 		germanTextJSON.put("ToolTipText_sliderMin"		,"<html>Hier wird der kleinste, also untere Wert,<br> des Transaktion Gebührenreglers festgelegt.<br>Beispiel: Wenn \"Geschätzt in 100 Blöcken\" ausgewählt wird, bedeutet dies,<br>das der kleinste Wert des Schiebereglers eine Transaktions-Gebühr einstellt,<br>die die Transaktion in 100 Blöcken (geschätzt) bestätigen lässt.<br>Falls \"0\" ausgewählt wird, beträgt der kleinste Wert des Schiebereglers 0 sat/vB</html>");
 		germanTextJSON.put("ToolTipText_acceptMax"		,"<html>Hier wird die prinzipiell höchste erlaubte Transaktionsgebührenrate festgelegt.<br>Beispiel: Wenn \"(Höchster Ø Gebührensatz) * 2\" ausgewählt wird, bedeutet dies:<br>dass der doppelte Wert, des höchsten durchschnittlichen Gebührensatzes,<br>der aktuell im Netzwerk ermittelt wurde,<br>als maximales Limit für eine Überweisung festgelegt wird.<br>Auch durch manuelles Ändern des Ausgabebetrages,<br>wird keine Transaktion akzeptiert, die eine höhere Transaktionsgebühr beinhalten würde.</html>");
 		germanTextJSON.put("ToolTipText_acceptMin"		,"<html>Hier wird die prinzipiell kleinste erlaubte Transaktionsgebührenrate festgelegt.<br>Beispiel: Wenn \"Minimum erlaubt in Mempool\" ausgewählt wird, bedeutet dies:<br>dass die im Netzwerk aktuell ermittelte minimale Transaktionsgebührenrate,<br>als minimales Limit für eine Überweisung festgelegt wird.<br>Auch durch manuelles Ändern des Ausgabebetrages,<br>wird keine Transaktion akzeptiert, die eine niedrigere Transaktionsgebührenrate beinhalten würde.<br>Wenn 0 ausgewählt wird, gibt es keine untere Grenze.</html>");
 		germanTextJSON.put("ToolTipText_btn_testNet"	,"<html>Diese Einstellung wird vom verbundenen Bitcoin-Core festgelegt.</html>");
 		germanTextJSON.put("ToolTipText_btn_open"		,"<html>Importiert eine unverschlüsselte CSV-Datei mit Bitcoin-Adressen.<br>Die Blockchain wird nach Guthaben auf diesen Adressen durchsucht.<br>Alle Bitcoin-Adressen mit Guthaben werden zur Auswahl aufgelistet.</html>");
 		germanTextJSON.put("ToolTipText_btn_open_crypt"	,"<html>Importiert eine verschlüsselte .crypt-Datei mit Bitcoin-Adressen.<br>Das Passwort zum Entschlüsseln der .crypt-Datei muss eingegeben werden.<br>Die Blockchain wird nach Guthaben auf diesen Adressen durchsucht.<br>Alle Bitcoin-Adressen mit Guthaben werden zur Auswahl aufgelistet.</html>");
 		germanTextJSON.put("ToolTipText_btn_encrypt"	,"<html>Eine unverschlüsselte CSV-Datei,<br>die eine Liste von Bitcoin-Adressen enthält, mit einem Passwort verschlüsseln.<br>Im Anschluss wird diese Datei als verschlüsselte Variante (.crypt-Datei) gespeichert.</html>");
 		germanTextJSON.put("ToolTipText_btn_input"		,"<html>Alle markierten Zeilen werden als Quell-Adressen importiert.<br>Es muss mindestens eine Zeile markiert werden.</html>");
 		germanTextJSON.put("ToolTipText_btn_output"		,"<html>Alle markierten Zeilen werden als Zieladressen importiert.<br>Es muss mindestens eine Zeile markiert werden.</html>");
 		germanTextJSON.put("ToolTipText_btn_scanQR"		,"<html>Scannt den QR-Code einer signierten Transaktion<br>und überträgt sie in dieses Feld.</html>");
 		germanTextJSON.put("ToolTipText_btn_openTx"		,"<html>Öffnet eine signierte Transaktion aus einer Textdatei (.txn)<br>und überträgt sie in dieses Feld.</html>");
 		germanTextJSON.put("ToolTipText_btn_showTx"		,"<html>Betrachte vor dem Senden die Transaktion im Eingabefeld mit dem Tx-Printer,<br> um sie zu prüfen!</html>");
 		germanTextJSON.put("ToolTipText_btn_send"		,"<html>Hiermit wird eine vollständig signierte Transaktion ins Netzwerk gesendet.<br>Beim ersten Betätigen dieses Buttons wird die Transaktion nur geprüft,<br> ob sie vom Netzwerk akzeptiert wird.<br>Danach kann sie endgültig abgesendet werden.</html>");
 		germanTextJSON.put("ToolTipText_lineName"		,"Linie aus/einblenden");
 		germanTextJSON.put("ToolTipText_btn_2h"			,"Die letzten 2 Stunden anzeigen.");
 		germanTextJSON.put("ToolTipText_btn_24h"		,"Die letzten 24 Stunden anzeigen.");
 		germanTextJSON.put("ToolTipText_btn_week"		,"Die letzte Woche anzeigen.");
 		germanTextJSON.put("ToolTipText_btn_4week"		,"Die letzten 4 Wochen anzeigen.");
 		germanTextJSON.put("ToolTipText_spinnerY"		,"Y-Achse vergrößern / verkleinern");
 	}	
 	
 	
 	
 	
 	
 	
 	
 	
 	
 	



 	// Stellt alle OptionPane,FileChooser etc. auf Deutsch.
    public void setGermanUI() 
    {
        UIManager.put("OptionPane.okButtonText"			, "OK");
        UIManager.put("OptionPane.cancelButtonText"		, "Abbrechen");
        UIManager.put("OptionPane.yesButtonText"		, "Ja");
        UIManager.put("OptionPane.noButtonText"			, "Nein");
        UIManager.put("OptionPane.messageDialogTitle"	, "Hinweis");
        UIManager.put("OptionPane.inputDialogTitle"		, "Eingabe");
        UIManager.put("FileChooser.openDialogTitleText"	, "Öffnen");
        UIManager.put("FileChooser.saveDialogTitleText"	, "Speichern");
        UIManager.put("FileChooser.openButtonText"		, "Öffnen");
        UIManager.put("FileChooser.saveButtonText"		, "Speichern");
        UIManager.put("FileChooser.cancelButtonText"	, "Abbrechen");
        UIManager.put("ProgressMonitor.progressText"	, "Fortschritt");    
    }
    
    
    
 	// Stellt alle OptionPane,FileChooser etc. auf English.
    public void setEnglishUI() 
    {
        UIManager.put("OptionPane.okButtonText"			, "OK");
        UIManager.put("OptionPane.cancelButtonText"		, "Cancel");
        UIManager.put("OptionPane.yesButtonText"		, "Yes");
        UIManager.put("OptionPane.noButtonText"			, "No");
        UIManager.put("OptionPane.messageDialogTitle"	, "Message");
        UIManager.put("OptionPane.inputDialogTitle"		, "Input"); 
        UIManager.put("FileChooser.openDialogTitleText"	, "Open");
        UIManager.put("FileChooser.saveButtonText"		, "Save");
        UIManager.put("FileChooser.openButtonText"		, "Open");
        UIManager.put("FileChooser.saveDialogTitleText"	, "Save");
        UIManager.put("FileChooser.cancelButtonText"	, "Cancel");
        UIManager.put("ProgressMonitor.progressText"	, "Progress");          
    }
}