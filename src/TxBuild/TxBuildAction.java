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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import BTClib3001.ByteArrayList;
import BTClib3001.Calc;
import BTClib3001.Convert;
import BTClib3001.PkScript;
import BTClib3001.Transaktion;
import BTClib3001.TxPrinter;
import RPC.ConnectRPC;



/********************************************************************************************************************
*				   					 Autor: Mr. Maxwell   							vom 25.10.2023					*
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
	public static boolean	runGuiHandler = false;
	
	public final static byte[] MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	public final static byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};

	
	
	
	
// Der Gui-Handler ist ein eigener Thrad der in einer Endlosschleife läuft.
// Regelmäßig (aller 0,5 sec.) prüft er die Eingabe-Felder und aktiviert/deaktiviert wenn alles richtig die View-Transaktions-Buttons.
// Darf nur einmal gleichzeitig laufen und muss am Ende des Programms geschlossen werden.
public static void startGuiHandler()
{
	Thread t = new Thread(new Runnable() 
	{
		@Override
		public void run() 
		{
			runGuiHandler = true;
			while(runGuiHandler)
			{	
				if		(GUI.txt_totalValIn.getText().equals("")) 	enalbleTxButtons(false);
				else if (GUI.txt_inAdr[0].getText().length()==0)	enalbleTxButtons(false);							
				else if (GUI.txt_outAdr[0].getText().length()==0)	enalbleTxButtons(false);
				else
				{			
					try { Double.parseDouble(GUI.txt_fee.getText().replace(",", "."));		 enalbleTxButtons(true);}
					catch(Exception e) 														{enalbleTxButtons(false);}
				}
				try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
			}	
		}
	});
	if(runGuiHandler == false) t.start();
}
	



// Aktiviert/Deaktivert die View-Transaktions-Buttons
private static void enalbleTxButtons(boolean enable)
{
	GUI.lbl_viewTx		.setEnabled(enable);
	GUI.lbl_viewTxHex	.setEnabled(enable);
	GUI.lbl_qrCode		.setEnabled(enable);
	GUI.lbl_save		.setEnabled(enable);
}



	
public static void actions()
{
	// Lädt die Transaktionen vom Bitcoin-Core
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
					GUI.btn_cancel.setVisible(true);	
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
								GUI.txt_meld.setText(ex.getMessage());
							}
							
							for(int i=0; i<GUI.txt_inValue.length;i++) 
							{ 
								GUI.txt_inAdr[i].setEnabled(true);
								GUI.btn_inQR[i].setEnabled(true);	
							}
							GUI.btn_loadTx.setVisible(true);
							GUI.btn_cancel.setVisible(false);
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
						TxPrinter txP = new TxPrinter(magic, tx, GUI.frame.getX(), GUI.frame.getY());
						txP.setModal(true);
						txP.setVisible(true);
					} 
					catch (Exception e1) 
					{
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
						QRCodeZXING.printQR(tx, "Unsig Transaction", GUI.color1, Color.black, GUI.frame.getX()+200, GUI.frame.getY()+5);
					} 
					catch (Exception e1) 
					{
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
		try{GUI.txt_meld.setText("BitcoinCore Error:\n"+ jo.getJSONObject("error").getString("message"));}   // Gibt Fehler-Meldungen vom Core aus
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
					}
				}
				for(int i=0; i<valueTemp.length; i++)  GUI.txt_inValue[i].setText(String.format("%.8f",(Math.round(valueTemp[i]*100000000.0)/100000000.0)));  
				GUI.valueOutVerify();
			}
		}   
		catch(Exception e) {e.printStackTrace();};	
	}


	
	// Die Transaktion wird vom BitcoinCore per RPC angefordert und hier erstellt, die benötigten Werte holt sich diese Methode selbst.
	private static byte[] createTx() throws Exception
	{
		GUI.txt_meld.setText("");
		JSONObject 	jo_result 	= coreTxOutSet.getJSONObject("result");
		JSONArray 	unspents 	= jo_result.getJSONArray("unspents");
		String[]	pkScript	= new String[unspents.length()];					// Das PkScript der vorherigen Tx
		String[] 	txid 		= new String[unspents.length()];					// Die Tx-ID  (Tx-Hash- 32Byte) der vorherigen Tx
		double[]	preValue	= new double[unspents.length()];					// Der Input-Betrag, ist der Output-Betrag der vorherigen Tx
		int[] 		txPrevIndex = new int[unspents.length()];						// Der Index der vorherigen Tx auf die sich bein Signieren bezogen wird
		long[] 		sequence	= new long[unspents.length()];						// Sequence 8Byte (oft ffffffff)
		boolean 	toWitness	= false;											// Wenn true, wird die Tx zu einer Witness-Tx konveriert.
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
		System.out.println(jo.toString());
		if( (jo.optJSONObject("error"))!=null )	throw new Exception(jo.getJSONObject("error").toString(1));   // Gibt Fehler-Meldungen vom Core aus
		String str = insertPkScript(jo.getString("result"),pkScript);						// PkScript der vorherigen Tx wird im Sig-Feld eingefügt		
		byte[] out = Convert.hexStringToByteArray(str);
		if(toWitness==true) out = legancyToWitness(out, preValue);							// Witness-Struktur wird eingefügt, wenn die Tx als Witness-Tx signiert werden muss
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
}