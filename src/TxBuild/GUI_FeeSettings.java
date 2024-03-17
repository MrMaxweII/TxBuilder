package TxBuild;
import java.awt.Color;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import RPC.ConnectRPC;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/************************************************************************************************************************************************
*	V1.0											 Autor: Mr. Maxwell   										vom 28.02.2024					*
*	Die GUI (JDialog) der Gebührenrate Fee-Settings für den TxBuilder																			*
*	Achtung: Die Methode "loadData()" sollte beim Programmstart noch nicht ausgeführt werden! 													*
*			 Weil RPC-Befehle vom Core geladen werden. 																							*
*			 Falls die Core-Settings falsch sind, oder beim ersten Start des Programmes, würde das Programm für die "TimeOut" Zeit blockeiren.	*
*************************************************************************************************************************************************/



public class GUI_FeeSettings extends JDialog 
{
	
	public static JComboBox feeFrom 	= new JComboBox();	// Auswahl von welchem Output die Gebühr abgezogen werden soll
	public static JComboBox sliderMax 	= new JComboBox();	// Slider oberer  maximaler Betrag
	public static JComboBox sliderMin 	= new JComboBox();	// Slider unterer minimaler Betrag
	public static JComboBox acceptMax 	= new JComboBox();	// Größte	akkzeptierte Fee-Rate die das Programm zum erstellen der Tx zulässt.  (Diese Einstellung darf nicht gspeichert werden!)
	public static JComboBox acceptMin	= new JComboBox();	// Kleinste akkzeptierte Fee-Rate die das Programm zum erstellen der Tx zulässt.  (Diese Einstellung darf nicht gspeichert werden!)
	public static JLabel lbl_sliMax 	= new JLabel("no data");
	public static JLabel lbl_sliMin 	= new JLabel("no data");
	public static JLabel lbl_accMax 	= new JLabel("no data");
	public static JLabel lbl_accMin 	= new JLabel("no data");
	public static JTextArea txt_meld  	= new JTextArea();
	public static double memMinFeeRate	= 0.0;				// Mempoo-MinFee-Rate. Minimalse Gebührenrate (sat/vB) der vom Mempool akzeptiert wird.  	Wird vom Core geladen, sobalt eine Tx erstellt wird, oder wenn die Fee-Settings geladen werden.
	public static double estimaFeeRateHigh = 0.0;			// Höchste geschätzte Feerate die nach einem Block bestätigt wird. 							Wird vom Core geladen, sobalt eine Tx erstellt wird, oder wenn die Fee-Settings geladen werden.
	
	// Initialisierungs-Block. Muss static sein, damit er bei Programmstart ausgeführt wird! 
	// Die Comboboxen müssen vor der Config-load initialisiert werden! Darf auch nicht im Konsruktor rein!
	// Default-Werte werden hier gesetzt. Für den Fall, das die Config noch leer ist.
	static
	{
		feeFrom		.setModel(new DefaultComboBoxModel(new String[] {"First output","Last output"}));
		sliderMax	.setModel(new DefaultComboBoxModel(new String[] {"Estimated in 1 blocks", "Estimated in 2 blocks", "Estimated in 3 blocks", "Estimated in 4 blocks", "Estimated in 5 blocks", "Estimated in 6 blocks", "Estimated in 7 blocks", "Estimated in 8 blocks", "Estimated in 9 blocks", "Estimated in 10 blocks"}));
		sliderMin	.setModel(new DefaultComboBoxModel(new String[] {"mempoolminfee", "Estimated in 1000 blocks", "Estimated in   900 blocks", "Estimated in   800 blocks", "Estimated in   700 blocks", "Estimated in   600 blocks", "Estimated in   500 blocks", "Estimated in   400 blocks", "Estimated in   300 blocks", "Estimated in   200 blocks", "Estimated in   100 blocks","0"}));
		acceptMax	.setModel(new DefaultComboBoxModel(new String[] {"Highest Estimated Fee Rate", "(Highest Fee Rate) * 2", "(Highest Fee Rate) * 3", "(Highest Fee Rate) * 4", "(Highest Fee Rate) * 5", "(Highest Fee Rate) * 6", "(Highest Fee Rate) * 7", "(Highest Fee Rate) * 8", "(Highest Fee Rate) * 9", "(Highest Fee Rate) * 10", "max"}));
		acceptMin	.setModel(new DefaultComboBoxModel(new String[] {"all", "mempoolminfee"}));
		sliderMax	.setSelectedIndex(0);
		sliderMin	.setSelectedIndex(1);
		acceptMax	.setSelectedIndex(1);
		acceptMin	.setSelectedIndex(1);
	}
	
	
	
	public GUI_FeeSettings(int x, int y) 
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Fee settings");
		setBounds(x, y, 720, 330);
		setModal(true);
			
		JPanel 		pnl_haupt = new JPanel(null);
		JTextPane 	lbl_info  = new JTextPane();
		JTextPane 	lbl_info1 = new JTextPane();

		lbl_info	.setText("The minimum and maximum values \r\nof the fee slider are set here. (sat/vB)");
		lbl_info1	.setText("The accepted fee rate that is allowed at all. (sat/vB)\r\nThis setting will not be saved!");
		
		lbl_info	.setBounds(10, 82, 313, 45);
		lbl_info1	.setBounds(379, 64, 325, 63);

		feeFrom		.setBounds(10, 26,253, 45);
		sliderMax	.setBounds(10, 138,210, 45);
		sliderMin	.setBounds(10, 194,210, 45);
		acceptMax	.setBounds(381,138,210, 45);
		acceptMin	.setBounds(381,194,210, 45);
		lbl_sliMax	.setBounds(230, 153, 141, 30);
		lbl_sliMin	.setBounds(230, 209, 141, 30);
		lbl_accMax	.setBounds(601, 153, 103, 30);
		lbl_accMin	.setBounds(601, 209, 103, 30);
		txt_meld	.setBounds(8, 243, 686, 48);

		feeFrom		.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Which output should the fee be deducted", TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		sliderMax	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Fee slider max",  		TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		sliderMin	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Fee slider min",  		TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		acceptMax	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"accept max feerate",  	TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		acceptMin	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"accept min feerate",  	TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		txt_meld	.setBorder(new EmptyBorder(8, 8, 8, 8));

		lbl_info	.setEditable(false);
		lbl_info1	.setEditable(false);
		txt_meld	.setEditable(false);

		lbl_info	.setBackground(GUI.color1);
		lbl_info1	.setBackground(GUI.color1);
		feeFrom 	.setBackground(GUI.color1);
		sliderMax 	.setBackground(GUI.color1);
		sliderMin 	.setBackground(GUI.color1);	
		acceptMax	.setBackground(GUI.color1);	
		acceptMin 	.setBackground(GUI.color1);
		txt_meld	.setBackground(GUI.color1);
		
		lbl_info	.setForeground(GUI.color4);
		lbl_info1	.setForeground(GUI.color4);
		txt_meld	.setForeground(Color.red);
		lbl_info	.setFont(GUI.font3);
		lbl_info1	.setFont(GUI.font3);
		
		pnl_haupt.add(feeFrom);		
		pnl_haupt.add(sliderMax);		
		pnl_haupt.add(sliderMin);
		pnl_haupt.add(acceptMax);		
		pnl_haupt.add(acceptMin);
		pnl_haupt.add(lbl_info);
		pnl_haupt.add(lbl_info1);
		pnl_haupt.add(lbl_sliMax);
		pnl_haupt.add(lbl_sliMin);
		pnl_haupt.add(lbl_accMax);
		pnl_haupt.add(lbl_accMin);
		pnl_haupt.add(txt_meld);
		getContentPane().add(pnl_haupt);
		loadData();
		
		// Registriert Veränderungn an den CheckBocken
		sliderMax.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {loadData();}
		});
		sliderMin.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {loadData();}
		});
		acceptMax.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {loadData();}
		});
		acceptMin.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {loadData();}
		});
	}
	
	
	
	/**	Lädt die Daten vom Core
		Muss aufgerufen werden, befor der Fee-Slider geladen wird.
		Achtung: Die Methode "loadData()" sollte beim Programmstart noch nicht ausgeführt werden!
				 Weil RPC-Befehle vom Core geladen werden.
				 Falls die Core-Settings falsch sind, oder beim ersten Start des Programmes, würde das Programm für die "TimeOut" Zeit blockeiren.  **/
	public static void loadData()
	{
		try
		{
			txt_meld.setText("");
			String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
			int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
			String 	name = GUI_CoreSettings.txt_uName.getText();
			String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
			ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
			peer.setTimeOut(Integer.parseInt(GUI_CoreSettings.txt_timeOut.getText()));
			
			JSONObject jo = new JSONObject(peer.get("getmempoolinfo", null)).getJSONObject("result");		// MempoolMinFee wird angefordert.
			memMinFeeRate = jo.getDouble("mempoolminfee")*100000;				
			
			// FeeRate für SliderMax wird berechnet
			String str1 = peer.get("estimatesmartfee", new JSONArray().put(sliderMax.getSelectedIndex()+1));
			JSONObject jo1 = new JSONObject(str1).getJSONObject("result");
			double feerate1 = jo1.getDouble("feerate")*100000;							
			lbl_sliMax.setText(String.format("%.2f",feerate1));

			// FeeRate für SliderMin wird berechnet
			
			
			
			if(sliderMin.getSelectedIndex()==11) 
			{		
				lbl_sliMin.setText("0,00");
			}
			else if(sliderMin.getSelectedIndex()==0) 
			{		
				lbl_sliMin.setText(String.format("%.2f",memMinFeeRate));
			}
			else
			{
				String str2 = "";
				if(sliderMin.getSelectedIndex()==1) {str2 = peer.get("estimatesmartfee", new JSONArray().put(1000));}
				if(sliderMin.getSelectedIndex()==2) {str2 = peer.get("estimatesmartfee", new JSONArray().put(900));}
				if(sliderMin.getSelectedIndex()==3) {str2 = peer.get("estimatesmartfee", new JSONArray().put(800));}
				if(sliderMin.getSelectedIndex()==4) {str2 = peer.get("estimatesmartfee", new JSONArray().put(700));}
				if(sliderMin.getSelectedIndex()==5) {str2 = peer.get("estimatesmartfee", new JSONArray().put(600));}
				if(sliderMin.getSelectedIndex()==6) {str2 = peer.get("estimatesmartfee", new JSONArray().put(500));}
				if(sliderMin.getSelectedIndex()==7) {str2 = peer.get("estimatesmartfee", new JSONArray().put(400));}
				if(sliderMin.getSelectedIndex()==8) {str2 = peer.get("estimatesmartfee", new JSONArray().put(300));}
				if(sliderMin.getSelectedIndex()==9) {str2 = peer.get("estimatesmartfee", new JSONArray().put(200));}
				if(sliderMin.getSelectedIndex()==10){str2 = peer.get("estimatesmartfee", new JSONArray().put(100));}
				JSONObject jo2 = new JSONObject(str2).getJSONObject("result");
				double feerate2 = jo2.getDouble("feerate")*100000;							
				lbl_sliMin.setText(String.format("%.2f",feerate2));
			}
		
			
			
			// accept max Feerate wird berechnet		
			String str3 = peer.get("estimatesmartfee", new JSONArray().put(1));
			JSONObject jo3 = new JSONObject(str3).getJSONObject("result");
			
			estimaFeeRateHigh = jo3.getDouble("feerate")*100000;
			double dMax = estimaFeeRateHigh;			
			if(acceptMax.getSelectedIndex()==0) {dMax = (dMax * 1);};
			if(acceptMax.getSelectedIndex()==1) {dMax = (dMax * 2);};
			if(acceptMax.getSelectedIndex()==2) {dMax = (dMax * 3);};
			if(acceptMax.getSelectedIndex()==3) {dMax = (dMax * 4);};
			if(acceptMax.getSelectedIndex()==4) {dMax = (dMax * 5);};
			if(acceptMax.getSelectedIndex()==5) {dMax = (dMax * 6);};
			if(acceptMax.getSelectedIndex()==6) {dMax = (dMax * 7);};
			if(acceptMax.getSelectedIndex()==7) {dMax = (dMax * 8);};
			if(acceptMax.getSelectedIndex()==8) {dMax = (dMax * 9);};
			if(acceptMax.getSelectedIndex()==9) {dMax = (dMax * 10);};	
			if(acceptMax.getSelectedIndex()==10){dMax = 100000;};						
			lbl_accMax.setText(String.format("%.2f",dMax));
			
			// accept min Feerate wird berechnet
			double dmin = jo3.getDouble("feerate")*100000;			
			if(acceptMin.getSelectedIndex()==0) {dmin = 0;};
			if(acceptMin.getSelectedIndex()==1) {dmin = memMinFeeRate;};
			lbl_accMin.setText(String.format("%.2f",dmin));
		}
		catch(Exception e){txt_meld.setText(e.getMessage());}
	}
}