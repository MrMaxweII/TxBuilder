package FeeEstimate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import org.json.JSONObject;
import RPC.ConnectRPC;
import TxBuild.GUI;
import TxBuild.GUI_CoreSettings;



/****************************************************************************************************************************************************
*	Version 1.0  									 				  	Autor: Mr. Maxwell   										vom 24.02.2024	*
*	Hier wird das Line-Diagramm für die Tx-Gebühren implementiert.																					*
*	Die Klasse ist fester Teil vom Tx-Builder.																										*
*	Die Klasse erbt von JFrame und muss zum starten nur geöfnet werden.																				*
*	Folgende Funktionen sind implementiert:																											*
*	- Bisherige Gebührensätze werden als JSON-Datei vom Bitcoin-Core per RPC-Befehl geladen (RPC: getblockstats();)									*
*	- Die Geladenen Datensätze werden 1. in eine JSON-Datei gespeichert. 2. als Chart in einem Line-Diagramm angezeigt.								*
*	- Es werden maximal 4000 Datensätze (4Wochen) gespeichert und angezeigt. (Ringspeicher)															*
*	- Die JSON-Datei verwendet als Schlüssel die Tx-Nummer und enthält mehrere Daten zur Fee-Rate													*
*****************************************************************************************************************************************************/



public class GUI_FeeChart extends JFrame
{
	private JPanel 			pnl_chart	= new JPanel();					// Das Panel in das nur das Chart-Label kommt
	private LineDiagram 	ld 			= new LineDiagram(pnl_chart);	// Das Line-Diagramm 
	public static JTextArea txt_meld 	= new JTextArea();				// Alle Meldungen und Fehler
	private boolean 		threadRun	= false;						// Beendet den Thread LoadData
	private int 			aktualBlockNr = 0;							// Die Aktuelle Blocknummer wird geladen und hier gespeichert
	
	
	public GUI_FeeChart(int x, int y)
	{
		JPanel 		pnl_main = new JPanel(new BorderLayout());	// Haupt-Panlel	

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("View past fee rate");
		setBounds(x, y, 1070, 521);
		setMinimumSize(new Dimension(600,440));
		pnl_chart.setBackground(GUI.color1);
		
		txt_meld		.setForeground(Color.red);
		txt_meld		.setBorder(new EmptyBorder(8, 8, 8, 8));
		txt_meld		.setEditable(false);
		txt_meld		.setBackground(GUI.color1);
		txt_meld		.setText("Load Data ...");

//		ld.setBackground(Color.white);
//		ld.setColorAxesText(Color.cyan);
		ld.setColorBorderEdgeGrid(Color.gray);
		ld.setColorGrid(new Color(230,230,230), Color.white);
		ld.setFontAxesLabel(new Font("Lucida Sans Unicode", Font.PLAIN, 16));
		ld.setTextX("Last blocks");
		ld.setTextY("      sat/vB");
		ld.setTextLine("feerate high"	,1, new Color(247, 147, 26));
		ld.setTextLine("feerate average",2, Color.magenta);
		ld.setTextLine("feerate low"	,3, Color.green);
		ld.setTextLine("feerate min (mempool accept)",4, Color.blue);

		ld.setVisible(true);
	
		pnl_main.add(pnl_chart,BorderLayout.CENTER);
		pnl_main.add(txt_meld,BorderLayout.SOUTH);
		add(pnl_main);
			
		// Close Button wird abgefangen und hier selbst verarbeitet. 
		// Hier müssen die Daten gespeichert werden!
		addWindowListener(new java.awt.event.WindowAdapter() 
		{
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
		    {	    	
		    	threadRun = false;
		    	deliteOldData();
		    	FeeDatabase.save();
		    	GUI.mItem_feeRate.setEnabled(true);
		    	GUI.menu_Setting.setEnabled(true);
		    	dispose();
		    }
		});	
		loadData();
	}
	
	
	
	
// -------------------------------------------------------------- Private Methoden -----------------------------------------	
	
	
// Lädt die Daten per RPC, oder aus der Datei
// Zeichnet anschließend das Liagramm
// Muss als eigener Thread ausgeführt werden!
private void loadData()
{
	Thread t = new Thread(new Runnable() 
	{
		public void run() 
		{ 
			threadRun = true;		
			try
			{
				FeeDatabase.load();
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
				peer.setTimeOut(5);
				aktualBlockNr =  new JSONObject(peer.get("getblockcount", null)).getInt("result"); // Lädt die neuste BlockNr. runter

				ArrayList<Double> reg1 = new ArrayList<Double>();  								// feerate high
				ArrayList<Double> reg2 = new ArrayList<Double>();  								// avgFeerate
				ArrayList<Double> reg3 = new ArrayList<Double>();  								// feerate Low
				ArrayList<Double> reg4 = new ArrayList<Double>();  								// feerate minimum

				for(int i=aktualBlockNr;i>aktualBlockNr-4000;i--)
				{
					if(threadRun == false) break;	
					JSONObject data = FeeDatabase.data.optJSONObject(String.valueOf(i));			// Enthält den einzelnen Datensatz
					if(data == null)
					{
						data = peer.getblockstats_my(i).getJSONObject("result");
						FeeDatabase.data.put(String.valueOf(i), data);						
					}
					reg1.add(data.getJSONArray("feerate_percentiles").getDouble(4));
					reg2.add(data.getDouble	  ("avgfeerate"));
					reg3.add(data.getJSONArray("feerate_percentiles").getDouble(0));
					reg4.add(data.getDouble	  ("minfeerate"));
					whriteToDiagram(reg1, 1, new Color(247, 147, 26));
					whriteToDiagram(reg2, 2, Color.magenta);
					whriteToDiagram(reg3, 3, Color.green);
					whriteToDiagram(reg4, 4, Color.blue);
				}	
				txt_meld.setText("");
			}
			catch(Exception e)
			{	
				txt_meld.setText(e.getMessage());  e.printStackTrace();	
			}	
		}
	});
	if(threadRun==false)t.start();
}
	
	

// glättet Kurve und zeichnet die Datenpunkte in das LinienDiagram
// Dazu wird ein kleines Register angelegt, welches die letzten Datenpukte zur Glättung speichert. 
private void whriteToDiagram(ArrayList<Double> reg, int nr, Color color)
{
	if(reg.size()==1)  
	{
		ld.addPoint(reg.get(0),nr, color);
	}	
	if(reg.size()==2)  
	{
		double schnitt = (reg.get(0)+reg.get(1)) / 2.0;
		ld.addPoint(schnitt,nr, color);
	}
	if(reg.size()==3)  
	{
		double schnitt = (reg.get(0)+reg.get(1)+reg.get(2)) / 3.0;
		ld.addPoint(schnitt,nr, color);
	}
	if(reg.size()==4)  
	{
		double schnitt = (reg.get(0)+reg.get(1)+reg.get(2)+reg.get(3)) / 4.0;
		ld.addPoint(schnitt,nr, color);
	}
	if(reg.size()==5)  
	{
		double schnitt = (reg.get(0)+reg.get(1)+reg.get(2)+reg.get(3)+reg.get(4)) / 5.0;
		ld.addPoint(schnitt,nr, color);
	}
	if(reg.size()==6)  
	{
		double schnitt = (reg.get(0)+reg.get(1)+reg.get(2)+reg.get(3)+reg.get(4)+reg.get(5)) / 6.0;
		ld.addPoint(schnitt,nr, color);
	}
	if(reg.size()==7)  
	{
		double schnitt = (reg.get(0)+reg.get(1)+reg.get(2)+reg.get(3)+reg.get(4)+reg.get(5)+reg.get(6)) / 7.0;
		ld.addPoint(schnitt,nr, color);
	}
	if(reg.size()>=8)  
	{
		double schnitt = (reg.get(0)+reg.get(1)+reg.get(2)+reg.get(3)+reg.get(4)+reg.get(5)+reg.get(6)+reg.get(7)) / 8.0;
		ld.addPoint(schnitt,nr, color);
		reg.remove(0);
	}
}



	
// Löscht Datenpunkte die älter sind als 4000 Blöcke aus der JSON-Datei
private void deliteOldData() 
{
	for(int i=aktualBlockNr-4000;i>0;i--)
	{	
		if(GUI.btn_testNet.isSelected()) 	FeeDatabase.data.remove(String.valueOf(i));
		else 								FeeDatabase.data.remove(String.valueOf(i));
	}
}
}