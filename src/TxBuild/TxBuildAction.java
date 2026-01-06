package TxBuild;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import RPC.ConnectRPC;
import lib3001.btc.PkScript;
import lib3001.btc.Transaktion;
import lib3001.btc.TxPrinter;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;
import lib3001.java.Animated;
import lib3001.java.ByteArrayList;
import lib3001.qrCode.QRCodeZXING;



/********************************************************************************************************************
*				   					 Autor: Mr. Maxwell   							vom 02.12.2025					*
*	ActionListener für den TxBuild																					*
*	Hier wird die Funktion des TxBuild implemenetier und die GUI gesteuert											*
*********************************************************************************************************************/




public class TxBuildAction 
{

	public static JSONObject coreTxOutSet;			// Das Ergebnis vom Core des Befehls "get_scantxoutset" wird hier für die Weiterverarbeitund gespeichert.
	
	public static String 	ip		= GUI_CoreSettings.txt_ip.getText();                     
	public static int 		port	= Integer.valueOf(GUI_CoreSettings.txt_port.getText());  
	public static String 	name	= GUI_CoreSettings.txt_uName.getText();                  
	public static String 	pw		= GUI_CoreSettings.txt_pw.getText();  	
	
	public final static byte[] MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	public final static byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};

	
	

// - Prüft ob alle Felder ausgefüllt sind.
// - Aktiviert dann die Tx-Buttens
// - Dadurch wird die Tx vom Core per RPC geladen.
// - Die Geladene Tx schreibt dann die Virtual-Size in das Feld
// - Dadurch wird dann die Feerate berechnet
public static void verifyOutputsAndCalcFeeRate() 
{			    	
//	System.out.println("verifyOutputsAndCalcFeeRate() ausgelöst.");
	GUI.txt_meld.setText("");
	GUI.txt_feeRate.setText(""); 
	GUI.txt_txVSize.setText(""); 
	if	   (GUI.txt_totalValIn.getText().equals("")) 		{ enalbleTxButtons(false); GUI.txt_feeRate.setText(""); GUI.txt_txVSize.setText(""); }
	else if(ifOneFieldEmty(GUI.txt_inAdr))					{ enalbleTxButtons(false); GUI.txt_feeRate.setText(""); GUI.txt_txVSize.setText(""); }							
	else if(ifOneFieldEmty(GUI.txt_outAdr))					{ enalbleTxButtons(false); GUI.txt_feeRate.setText(""); GUI.txt_txVSize.setText(""); }
	else if(Convert.isNumeric(GUI.txt_fee.getText())==false){ enalbleTxButtons(false); GUI.txt_feeRate.setText(""); GUI.txt_txVSize.setText(""); }
	else
	{			
		try 
		{ 
			Double.parseDouble(GUI.txt_fee.getText().replace(",", "."));
			enalbleTxButtons(true); 
			if(coreTxOutSet != null) createTx();
			calcFeeRate();
		} 
		catch(Exception e) 	
		{
			enalbleTxButtons(false); 
			GUI.txt_feeRate.setText("");
			GUI.txt_meld.setForeground(Color.red);
			GUI.txt_meld.setText(e.getMessage());
		}
	}
	GUI.feeSliderActivated();
}
	


// Gibt true zurück, wenn nur ein einziges Feld in diesem TextField-Array leer ist.
// Wird benötigt um Ereignisse auszulösen wenn alle Felder ausgefüllt sind.
private static boolean ifOneFieldEmty(JTextField[] textField)
{
	for(int i=0; i<textField.length; i++)
	{
		if(textField[i].getText().length()==0) return true;
	}
	return false;
}



// Berechnet/Schätzt die Fee-Rate (sat/vB) der späteren Signierten Tx, auf Grundlage der unsighierten Tx.
// Es wird die geschätzte "Virtual Size" als Bezug verwendet.
// Dies ist nur eine Schätzung, da die tatsächliche Größe der späteren signierten Tx leicht variieren kann. (Die Signaturen variieren in der Regel um ein Byte/Tx-Input)
// Achtung! Tx-Inputs aus Uncompressed-PubKey´s können nicht erkannt werden! Alle Berechnungen werden für Compressed angenommen! Folge: Alle Uncompressed-Inputs werden um 32Byte zu kurz berechnet/geschätzt!
// Diese Methode wird vom GUI-Handler zyklisch (aller 0,5s) aufgerufen, um auch wirklich immer jede Änderung zu erfassen.  
// Daher dürfen hier nur einfache (schnelle) Berechnungn enthalten sein und keine Resourchenfressenden oder blockierenden Methoden. Und keine RPC-Aufrufe etc.!
// Diese Methode holt sich die Paramter selbst, prüft sie und schreibt das Ergebnis selbstständig in die GUI.
// Kein Exception-Handling hier! Da die Methode zyklisch aufgerufen wird, werden alle Fehler ignoriert und im Fehlerfall einfach keine Berechnung durchgeführt. Das Feld "Fee-Rate" bleibt dann im Fehlerfall einfach leer.
private static void calcFeeRate()
{	
//	System.out.println("calcFeeRate()                 ausgelöst.");
	try 
	{		
		Double fee 		= Double.parseDouble(GUI.txt_fee.getText().replace(",", "."));
		Double feeRate	= (fee * 100000000.0) / Double.parseDouble(GUI.txt_txVSize.getText().replace(",","."));
		feeRate = Math.round(feeRate*1000)/1000.0;
		GUI.txt_feeRate.setText(String.valueOf(String.format("%.3f", feeRate)));
	}
	catch(Exception e) {GUI.txt_feeRate.setText("");}; 	
}



// Aktiviert/Deaktivert die View-Transaktions-Buttons
// Achtung: durch das Aktivieren Labels (speziell dem lbl_viewTx) wird ein Listender ausgelöst, der in Folge die Tx vom Core lädt.
public static void enalbleTxButtons(boolean enable)
{
	//System.out.println("enalbleTxButtons("+enable+")       ausgelöst.");
	GUI.lbl_viewTx		.setEnabled(enable);
	GUI.lbl_viewTxHex	.setEnabled(enable);
	GUI.lbl_qrCode		.setEnabled(enable);
	GUI.lbl_save		.setEnabled(enable);
	if(enable) 
	{
		Animated.start(GUI.lbl_viewTx,true);
		Animated.start(GUI.lbl_viewTxHex,true);
		Animated.start(GUI.lbl_qrCode,true);
		Animated.start(GUI.lbl_save,true);
	}
	else
	{
		Animated.stop(GUI.lbl_viewTx);
		Animated.stop(GUI.lbl_viewTxHex);
		Animated.stop(GUI.lbl_qrCode);
		Animated.stop(GUI.lbl_save);
	}
	
}



	
public static void actions()
{
	// Lädt die Transaktionen mit dem Bitcoin-Core von der Blockchain
	GUI.btn_loadTx.addMouseListener(new MouseAdapter() 
	{
		public void mouseReleased(MouseEvent e) 
		{				
			if (e.getButton() == MouseEvent.BUTTON1)
			 {			
					ip 	 = GUI_CoreSettings.txt_ip.getText();
					port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
					name = GUI_CoreSettings.txt_uName.getText();
					pw 	 = GUI_CoreSettings.txt_pw.getText();					
					GUI.txt_totalValIn.setText("");
				 	GUI.btn_loadTx.setVisible(false);
					Animated.stop(GUI.btn_loadTx);
					GUI.btn_cancel.setVisible(true);
					GUI.cBox_inCount.setEnabled(false);
					GUI.menu_Setting.setEnabled(false);
					GUI.menu_functio.setEnabled(false);
					GUI.txt_meld.setText("");
					for(int i=0; i<GUI.txt_inValue.length;i++) 
					{
						GUI.txt_inValue[i].setText("");
						GUI.txt_inAdr[i].setEnabled(false);
						GUI.btn_inQR[i].setEnabled(false);
					}
					
					Thread thread1 = new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{								
							ConnectRPC core = new ConnectRPC(ip,port,name,pw);
							core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
							try 
							{
								String[] str 		= new String[GUI.txt_inAdr.length];
								String[] adrFormat 	= new String[GUI.txt_inAdr.length];
								for(int i=0; i<GUI.txt_inAdr.length;i++)
								{
									adrFormat[i] = "addr";
									str[i] = GUI.txt_inAdr[i].getText();
								}					
								coreTxOutSet = core.get_scantxoutset("start", adrFormat, str);
								scantxoutsetResult(coreTxOutSet);
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
								GUI.txt_meld.setForeground(Color.red);
								GUI.txt_meld.setText(ex.getMessage());
							}
							
							for(int i=0; i<GUI.txt_inValue.length;i++) 
							{ 
								GUI.txt_inAdr[i].setEnabled(true);
								GUI.btn_inQR[i].setEnabled(true);	
							}
							GUI.btn_loadTx.setVisible(true);
							GUI.btn_cancel.setVisible(false);
							GUI.cBox_inCount.setEnabled(true);
							GUI.menu_Setting.setEnabled(true);
							GUI.menu_functio.setEnabled(true);
						}
					});
					
					
					// Steuert die Process-Bar
					Thread thread2 = new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							ConnectRPC core = new ConnectRPC(ip,port,name,pw);
							core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
							GUI.progressBar.setVisible(true);
							GUI.lbl_progress.setVisible(true);
							for(int i=0; i<600; i++ )
							{
								try 
								{
									Thread.sleep(1000);
									JSONObject jo = core.get_scantxoutset("status", new String[] {""}, new String[] {""});
									GUI.progressBar.setValue(jo.getJSONObject("result").getInt("progress"));
								}
								catch(Exception ex)
								{
									GUI.progressBar.setVisible(false);
									GUI.lbl_progress.setVisible(false);
									break;
								}
							}
						}
					});
					thread1.start();
					thread2.start();	
			 }
		}
	});
	
	
	
	
	
	// Bricht den "scantxoutset-Befehl" des Cores (Laden der TX) ab.
	GUI.btn_cancel.addMouseListener(new MouseAdapter()  
	{
		public void mouseReleased(MouseEvent e)
		{		
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				Animated.start(GUI.btn_loadTx,true);
				// Steuert die Process-Bar
				Thread thread3 = new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						
						ConnectRPC core = new ConnectRPC(ip,port,name,pw);
						core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
						try 
						{
							core.get_scantxoutset("abort", new String[] {""}, new String[] {""});
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
							GUI.txt_meld.setForeground(Color.red);
							GUI.txt_meld.setText(ex.getMessage());
						}
					}
				});
				thread3.start();
			}
		}
	});
	
	

	
	// Erstellt die Tx (TxPrinter)
	GUI.lbl_viewTx.addMouseListener(new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent e) 
		{
			 if (e.getButton() == MouseEvent.BUTTON1 && GUI.lbl_viewTx.isEnabled())
			 {
				if(GUI.txVerify()==true)
					try 
					{
						Transaktion tx = new Transaktion(createTx(),0);
						byte[] magic;
						if(GUI.btn_testNet.isSelected()) magic = TESTNET3;
						else							 magic = MAINNET;
						TxPrinter txP = new TxPrinter(magic, tx, GUI.frame.getX()+15, GUI.frame.getY()+30);
						txP.setModal(true);
						txP.setVisible(true);
					} 
					catch (Exception e1) 
					{
						GUI.txt_meld.setForeground(Color.red);
						GUI.txt_meld.setText(e1.getMessage());
						e1.printStackTrace();
					}
             }
		}
	});
	
	
	
	// Erstellt die Tx (Hex Raw)
	GUI.lbl_viewTxHex.addMouseListener(new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent e) 
		{
			 if (e.getButton() == MouseEvent.BUTTON1 && GUI.lbl_viewTxHex.isEnabled())
			 {
				if(GUI.txVerify()==true)
					try 
					{	
						byte[] tx = createTx();
						JDialog d = new JDialog();
						d.setTitle("RAW Transaktion");
						JTextArea txt = new JTextArea(Convert.byteArrayToHexString(tx));
						txt.setLineWrap(true);
						txt.setEditable(false);
						d.setBounds(GUI.frame.getX()+5, GUI.frame.getY()+50, 1050, (tx.length/5)+80);
						d.getContentPane().add(txt);
						d.setModal(true);
						d.setVisible(true);
					} 
					catch (Exception e1) 
					{
						GUI.txt_meld.setForeground(Color.red);
						GUI.txt_meld.setText(e1.getMessage());
						e1.printStackTrace();
					}
             }
		}
	});
	

	
	// Erstellt die Tx (QR-Code)
	GUI.lbl_qrCode.addMouseListener(new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent e) 
		{
			 if (e.getButton() == MouseEvent.BUTTON1 && GUI.lbl_qrCode.isEnabled())
			 {
				if(GUI.txVerify()==true)
				try 
				{	
					String tx = Convert.byteArrayToHexString(createTx());
					QRCodeZXING.printQR_on_JDialog(tx, "Unsig Transaction", GUI.color1, Color.black, GUI.frame.getX()+250, GUI.frame.getY()+5);
				} 
				catch (Exception e1) 
				{
					GUI.txt_meld.setForeground(Color.red);
					GUI.txt_meld.setText(e1.getMessage());
					e1.printStackTrace();
				}
             }
		}
	});
	
	
	
	// Erstellt die Tx und öffnet den FileCooster zum speichern der Tx
	GUI.lbl_save.addMouseListener(new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent e) 
		{
			 if (e.getButton() == MouseEvent.BUTTON1 && GUI.lbl_qrCode.isEnabled())
			 {
				if(GUI.txVerify()==true)
					try 
					{	
						String tx = Convert.byteArrayToHexString(createTx());
						String userDir = System.getProperty("user.home");
						JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
						FileFilter filter = new FileNameExtensionFilter(".txn", "txn");
						chooser.setFileFilter(filter);	
						chooser.setAcceptAllFileFilterUsed(false);
						chooser.setSelectedFile(new File("unsigned.txn"));
						int button = chooser.showSaveDialog(GUI.lbl_save);
						if(button==0)																					
						{
							String file = chooser.getSelectedFile().getAbsolutePath();	
							BufferedWriter br = new BufferedWriter(new FileWriter(file));
							br.write(tx);
							br.close();				
						}	
					} 
					catch (Exception e1) 
					{
						GUI.txt_meld.setForeground(Color.red);
						GUI.txt_meld.setText(e1.getMessage());
						e1.printStackTrace();
					}
             }
		}
	});

}	






// ------------------------------------------------------------------ Hilfs Methoden -----------------------------------------------------------------------




	//	Wertet das Ergebnis vom Core (get_scantxoutset) aus und und schreibt die Daten in die GUI
	private static void scantxoutsetResult(JSONObject jo) throws JSONException
	{	
		try{GUI.txt_meld.setForeground(Color.red); GUI.txt_meld.setText("BitcoinCore Error:\n"+ jo.getJSONObject("error").getString("message"));}   // Gibt Fehler-Meldungen vom Core aus
		catch(Exception e) {};
		try
		{
			JSONObject jo_result = jo.getJSONObject("result");
			System.out.println(jo_result.toString(1));
			
			if(jo_result.getBoolean("success") == true) 													// Wenn Ergebnis fehlerfrei abgeschlossen ist.
			{
				double[] valueTemp = new double[GUI.txt_inAdr.length];										// Temp-Variable wird benutzt um die Beträge zu addiern
				for(int i=0; i<valueTemp.length; i++) valueTemp[i] = 0.0;									// Temp-Variable wird auf 0 gesetzt.
				
				GUI.txt_totalValIn.setText(String.format("%.8f", jo_result.getDouble("total_amount")));		
				JSONArray unspents = jo_result.getJSONArray("unspents");
				for(int i=0;i<unspents.length();i++)
				{
					JSONObject jo_el =  unspents.getJSONObject(i);
					String addr = jo_el.getString("desc");
					addr = addr.substring(addr.indexOf("(")+1, addr.indexOf(")"));
					
					for(int j=0; j<GUI.txt_inAdr.length;j++)
					{
						if(addr.equals(GUI.txt_inAdr[j].getText()))
						{
							valueTemp[j] = valueTemp[j] + jo_el.getDouble("amount");  
						}
						GUI.txt_inAdr[j].setBackground(GUI.color1);
					}
				}
				for(int i=0; i<valueTemp.length; i++)  GUI.txt_inValue[i].setText(String.format("%.8f",(Math.round(valueTemp[i]*100000000.0)/100000000.0)));  
				GUI.valueOutVerify();
				verifyOutputsAndCalcFeeRate();
			}
		}   
		catch(Exception e) {e.printStackTrace();};	
	}


	
	// Die Transaktion wird vom BitcoinCore per RPC angefordert und hier erstellt, die benötigten Werte holt sich diese Methode selbst.
	// Die Gebühren Berechnung wird von hier aus ausgelöst.
	private static byte[] createTx() throws Exception
	{
		GUI.txt_meld.setText("");
		JSONObject 	jo_result 	= coreTxOutSet.getJSONObject("result");
		JSONArray 	unspents 	= jo_result.getJSONArray("unspents");
		String[]	pkScript	= new String[unspents.length()];						// Das PkScript der vorherigen Tx
		String[] 	txid 		= new String[unspents.length()];						// Die Tx-ID  (Tx-Hash- 32Byte) der vorherigen Tx
		double[]	preValue	= new double[unspents.length()];						// Der Input-Betrag, ist der Output-Betrag der vorherigen Tx
		int[] 		txPrevIndex = new int[unspents.length()];							// Der Index der vorherigen Tx auf die sich bein Signieren bezogen wird
		long[] 		sequence	= new long[unspents.length()];							// Sequence 8Byte (oft ffffffff)
		boolean 	toWitness	= false;												// Wenn true, wird die Tx zu einer Witness-Tx konveriert.
		for(int i=0;i<unspents.length();i++) 	// Tx-Eingänge
		{
			JSONObject joEl =  unspents.getJSONObject(i);
			pkScript[i]		= joEl.getString("scriptPubKey");
			txid[i] 		= joEl.getString("txid");
			txPrevIndex[i]	= joEl.getInt("vout");
			preValue[i]		= joEl.getDouble("amount");
			sequence[i]		= Long.valueOf(GUI_TxSettings.txt_sequence.getText());
			PkScript pk 	= new PkScript(Convert.hexStringToByteArray(pkScript[i]));
			if(pk.getNr()==3 || pk.getNr()==4) toWitness = true;						// Wenn true, wird die Tx zu einer Witness-Tx konveriert.
		}
		String[]	addressOut	= new String[GUI.txt_outAdr.length];
		double[]	valueOut	= new double[GUI.txt_outAdr.length];
		for(int i=0; i<GUI.txt_outAdr.length;i++)
		{
			addressOut[i] = GUI.txt_outAdr[i].getText();	
			valueOut[i]   =	Double.parseDouble(GUI.txt_outValue[i].getText().replace(",", "."));	
		}	
		long locktime	= Long.valueOf(GUI_TxSettings.txt_locktime.getText());;
		ConnectRPC core = new ConnectRPC(ip,port,name,pw);
		core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
		JSONObject jo = core.get_createrawtransaction(txid, txPrevIndex, sequence, addressOut,valueOut, locktime);
		//System.out.println(jo.toString());
		if( (jo.optJSONObject("error"))!=null )	throw new Exception(jo.getJSONObject("error").toString(1));   	// Gibt Fehler-Meldungen vom Core aus
		String str = insertPkScript(jo.getString("result"),pkScript);											// PkScript der vorherigen Tx wird im Sig-Feld eingefügt		
		byte[] out = Convert.hexStringToByteArray(str);
		if(toWitness==true) out = legancyToWitness(out, preValue);												// Witness-Struktur wird eingefügt, wenn die Tx als Witness-Tx signiert werden muss
		GUI.txt_txVSize.setText(String.valueOf(String.format("%.2f",  calcVirtualSize(out,GUI_FeeSettings.estimateProfile))));	// Berechnet/Schätzt die Virtual-Sitze (vByte)
		return out;
	}	
	
	
	
	// Konvertiert eine Legancy Transakion in eine Witness Transaktion in dem das Witness-Flag und Witness Daten eingefügt werden.
	// Die Witness-Felder werden hier genutzt (nur für die UnsigTx) um die Beträge der Inputs zu übermittels. Ist nur bei Witness-Tx notwendig!
	// Es werden einfach Alle Input-Beträge in jedes Witness-Feld eingetragen, egal ob sie zum signieren wirklich benötigt werden oder nicht.
	// Beim Signieren werden die Witness-Felder sowieso neu erstellt.
	private static byte[] legancyToWitness(byte[] txb, double[] preValue)
	{
		Transaktion tx = new Transaktion(txb,0);
		byte[] tx2 = txb.clone();
		ByteArrayList b = new ByteArrayList(tx2);
		for(int i=tx.getTxInCount()-1; i>=0; i--)
		{			
			String str = String.format("%.8f", preValue[i]);	
			byte[] value_b = new byte[10];
			value_b[0] = 0x01;
			value_b[1] = 0x08;
			System.arraycopy(Convert.valueToHex(str), 0, value_b, 2, 8);
			b.insert(value_b, tx.lockTime_pos); 										// Für jeden Tx-Input wird der Input-Betrag eingetragen
		}
		b.insert(new byte[]{00,01}, 4);
		return b.getArrayAll();
	}
	
	
	
	// Fügt die PK-Scripte (der vorherigen Tx) an der Stelle der Signaturen ein.
	// (Bei Unsignierten Transaktionen müssen die Pk-Scripte an der Stelle der späteren Signaturen temporär eingefügt werden)
	// Der Core macht dies leider nicht selbstständig und lässt die Signaturen leer.
	private static String insertPkScript(String txStr, String[] pkScript)
	{
		Transaktion tx = new Transaktion(Convert.hexStringToByteArray(txStr),0);
		int[] sigScript_len = tx.getSigScript_len();
		int[] sigScript_pos = tx.getSigScript_pos();
		StringBuilder sb = new StringBuilder(txStr);
		for(int i=pkScript.length-1; i>=0; i--)
		{
			sb.delete(sigScript_pos[i]*2 -2, sigScript_pos[i]*2 -2 + sigScript_len[i]*2 +2);
			String pk = Convert.byteArrayToHexString(Calc.encodeCompactSize(Convert.hexStringToByteArray(pkScript[i])));
			sb.insert(sigScript_pos[i]*2 -2 , pk);
		}
		return sb.toString();
	}
	
	
	
	// Berechnet/Schätzt die Virtual Size (vByte) der späteren Signierten Tx, auf Grundlage der unsignierten Tx.
	// Dies ist nur eine Schätzung, da die tatsächliche Größe der späteren signierten Tx leicht variieren kann. (Die Signaturen variieren in der Regel um ein Byte/Tx-Input)
	// Achtung! Tx-Inputs aus Uncompressed-PubKey´s können nicht erkannt werden! Alle Berechnungen werden für Compressed angenommen! Folge: Alle Uncompressed-Inputs werden um 32Byte zu kurz berechnet/geschätzt!
	// Es wird die fertige unsignierte Tx so übergeben, wie sie auch im Programm ausgegeben wird. Byte-Array
	// Der Parameter "estimateProfil" bewirkt 3 verschiedene Schätzprofile, die in der Config eingestellt werden können:
	// estimateProfile=1: Die geschätzte VirtualSize ist der niedrigste Mögliche Wert. Alle Signaturen werden in kürzester Form angenommen, ohne das zusätzliche Byte.
	// estimateProfile=2: Die geschätzte VirtualSize ist ein mittlerer Durchschnitt.   Für alle Signaturen wird ein zusätzliches halbes Byte angenommen.
	// estimateProfile=3: Die geschätzte VirtualSize ist der höchste Mögliche Wert.    Alle Signaturen werden in längster Form angenommen, mit dem zusätzlichen Byte.
	private static double calcVirtualSize(byte[] uTxRaw, int estimateProfile)
	{		
		double zSigByte = 0.0;													// Das zuätzliche Signatur-Byte, was beim Signieren für jeden Input mit einer wahrscheinlichkeit von 50% zusätzlich anfällt.
		if(estimateProfile ==1) zSigByte = 0.0;									// Minimaler 		  Schätzwert
		if(estimateProfile ==2) zSigByte = 0.5;									// Durchschnittlicher Schätzwert
		if(estimateProfile ==3) zSigByte = 1.0;									// Maximaler 		  Schätzwert
		
		
		double addSize = 0;
		Transaktion tx = new Transaktion(uTxRaw,0);	
		if(tx.isWitness)  														// bei Witness-Tx
		{
			
			byte[][] pk = tx.getSigScript(); 									// Das prevPK Spript wird aus dem Datenfeld des SigScript geparst	
			for(int i=0; i<tx.getTxInCount();i++)
			{
				int pkLen = pk[i].length;
				if(pkLen==25)	addSize = addSize + 71.25 + zSigByte;			// bei eingebetteter Legancy P2PKH
				if(pkLen==23)	addSize = addSize + 97.0 - 80.25 + zSigByte;	// bei P2SH
				if(pkLen==22)	addSize = addSize + 75.0 - 80.25 + zSigByte;	// bei P2WPKH			
			}
			return uTxRaw.length + addSize - 1.5;
		}
		else																	// bei Legancy Tx: Für alle Inputs +81 Bytes! (Uncompressed Tx können nicht erkannt werden !!!)
		{
			addSize = tx.getTxInCount() * (81.0+zSigByte);
			return uTxRaw.length + addSize;
		}
	}
}