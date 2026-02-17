package TxBuild;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import RPC.ConnectRPC;
import lib3001.java.Hover;
import lib3001.java.MyComboBox;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/************************************************************************************************************************************************
*	V1.4												 Autor: Mr. Maxwell   											vom 05.01.2026			*
*	Die GUI (JDialog) der Gebührenrate Fee-Settings für den TxBuilder																			*
*	Letzte Änderung: fallBackLoadData(); Hinzugefügt.																							*
*	Achtung: Die Methode "loadData()" sollte beim Programmstart noch nicht ausgeführt werden! 													*
*	Weil RPC-Befehle vom Core geladen werden. 																									*
*	Falls die Core-Settings falsch sind, oder beim ersten Start des Programmes, würde das Programm für die "TimeOut" Zeit blockeiren.			*
*************************************************************************************************************************************************/



public class GUI_FeeSettings extends JDialog 
{
	
	public static MyComboBox feeFrom 	= new MyComboBox();		// Auswahl von welchem Output die Gebühr abgezogen werden soll
	public static MyComboBox sliderMax 	= new MyComboBox();		// Slider oberer  maximaler Betrag
	public static MyComboBox sliderMin 	= new MyComboBox();		// Slider unterer minimaler Betrag
	public static MyComboBox acceptMax 	= new MyComboBox();		// Größte	akkzeptierte Fee-Rate die das Programm zum erstellen der Tx zulässt.  (Diese Einstellung darf nicht gspeichert werden!)
	public static MyComboBox acceptMin	= new MyComboBox();		// Kleinste akkzeptierte Fee-Rate die das Programm zum erstellen der Tx zulässt.  (Diese Einstellung darf nicht gspeichert werden!)
	public static JLabel lbl_sliMax 	= new JLabel("no data");
	public static JLabel lbl_sliMin 	= new JLabel("no data");
	public static JLabel lbl_accMax 	= new JLabel("no data");
	public static JLabel lbl_accMin 	= new JLabel("no data");
	private static JLabel[]	lbl_satVb 	= new JLabel[4];		// Die Beschriftung "sat/vB" hinter den Zahlen
	public static JTextArea txt_meld  	= new JTextArea();
	public static double memMinFeeRate	= 0.0;					// Mempoo-MinFee-Rate. Minimalse Gebührenrate (sat/vB) der vom Mempool akzeptiert wird.  	Wird vom Core geladen, sobalt eine Tx erstellt wird, oder wenn die Fee-Settings geladen werden.
	public static double estimaFeeRateHigh = 0.0;				// Höchste geschätzte Feerate die nach einem Block bestätigt wird. 							Wird vom Core geladen, sobalt eine Tx erstellt wird, oder wenn die Fee-Settings geladen werden.
	public static int  maxAccaptFeeRate = 100;					// Maximale Feerate die prizipiell erlaubt wird. Dieser Werd wird gespeichert und kann nur manuel in der TxBuilder.json verändert werden.
	public static int estimateProfile   = 2;					// Der Parameter "estimateProfil" bewirkt 3 verschiedene Schätzprofile, für die Methode: "calcVirtualSize()" die in der Config eingestellt werden können. Siehe calcVirtualSize();
	public static double fallBackEstimaFeeRate = 5.0;			// Diese Gebührenrate wird im Fehlerfall benutzt, wenn der Core keine estimaFeeRate liefern kann. Diese fallBackEstimaFeeRate wird dann aus der Config geladen.
	
	JPanel 		pnl_haupt 				= new JPanel(null);
	JTextPane 	lbl_info  				= new JTextPane();
	JTextPane 	lbl_info1 				= new JTextPane();


	
	
	// Initialisierungs-Block. Muss static sein, damit er bei Programmstart ausgeführt wird! 
	// Die Comboboxen müssen vor der Config-load initialisiert werden! Darf auch nicht im Konsruktor rein!
	// Default-Werte werden hier gesetzt. Für den Fall, das die Config noch leer ist.
	// Die Sprachumschaltung ist hier noch nicht geladen, und wird die Werte daher später überschreiben.
	static
	{
		feeFrom		.setModel(new DefaultComboBoxModel(new String[] {"First output","Last output"}));
		sliderMax	.setModel(new DefaultComboBoxModel(new String[] {"Estimated in 1 blocks", "Estimated in 2 blocks", "Estimated in 3 blocks", "Estimated in 4 blocks", "Estimated in 5 blocks", "Estimated in 6 blocks", "Estimated in 7 blocks", "Estimated in 8 blocks", "Estimated in 9 blocks", "Estimated in 10 blocks"}));
		sliderMin	.setModel(new DefaultComboBoxModel(new String[] {"mempoolminfee", "Estimated in 1000 blocks", "Estimated in   900 blocks", "Estimated in   800 blocks", "Estimated in   700 blocks", "Estimated in   600 blocks", "Estimated in   500 blocks", "Estimated in   400 blocks", "Estimated in   300 blocks", "Estimated in   200 blocks", "Estimated in   100 blocks","0"}));
		acceptMax	.setModel(new DefaultComboBoxModel(new String[] {"Highest Estimated Fee Rate", "(Highest Fee Rate) * 2", "(Highest Fee Rate) * 3", "(Highest Fee Rate) * 4", "(Highest Fee Rate) * 5", "(Highest Fee Rate) * 6", "(Highest Fee Rate) * 7", "(Highest Fee Rate) * 8", "(Highest Fee Rate) * 9", "(Highest Fee Rate) * 10", "max"}));
		acceptMin	.setModel(new DefaultComboBoxModel(new String[] {"0", "mempoolminfee"}));
		sliderMax	.setSelectedIndex(0);
		sliderMin	.setSelectedIndex(1);
		acceptMax	.setSelectedIndex(1);
		acceptMin	.setSelectedIndex(1);
	}
	
	
	
	public GUI_FeeSettings(int x, int y) 
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(GUI.t.t("Fee settings"));
		setBounds(x, y, 740, 350);
		setMinimumSize(new Dimension(670,300));
		setModal(true);		
		setIconImage(MyIcons.fee.getImage());			
	
		lbl_satVb[0] 			= new JLabel("sat/vB");	
		lbl_satVb[1] 			= new JLabel("sat/vB");	
		lbl_satVb[2] 			= new JLabel("sat/vB");	
		lbl_satVb[3] 			= new JLabel("sat/vB");	
		
		lbl_info	.setText(GUI.t.t("The minimum and maximum values \nof the fee slider are set here."));
		lbl_info1	.setText(GUI.t.t("The accepted fee rate that is allowed at all.\nThis setting will not be saved!"));
		
		lbl_info	.setBounds(10, 82, 330, 45);
		lbl_info1	.setBounds(379, 82, 330, 60);	
		lbl_satVb[0].setBounds(290, 153, 50, 30);
		lbl_satVb[1].setBounds(290, 209, 50, 30);
		lbl_satVb[2].setBounds(660, 153, 50, 30);
		lbl_satVb[3].setBounds(660, 209, 50, 30);	
		feeFrom		.setBounds(10, 26, 350, 45);
		sliderMax	.setBounds(10, 138,230, 45);
		sliderMin	.setBounds(10, 194,230, 45);
		acceptMax	.setBounds(381,138,230, 45);
		acceptMin	.setBounds(381,194,230, 45);
		lbl_sliMax	.setBounds(250, 153, 141, 30);
		lbl_sliMin	.setBounds(250, 209, 141, 30);
		lbl_accMax	.setBounds(621, 153, 103, 30);
		lbl_accMin	.setBounds(621, 209, 103, 30);
		txt_meld	.setBounds(8, 243, 686, 60);
		
		feeFrom		.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),GUI.t.t("Which output should the fee be deducted"), TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));	
		sliderMax	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),GUI.t.t("Fee slider max"),  						TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		sliderMin	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),GUI.t.t("Fee slider min"),  						TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		acceptMax	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),GUI.t.t("accept max feerate"),  					TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		acceptMin	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),GUI.t.t("accept min feerate"),  					TitledBorder.LEADING, 	TitledBorder.TOP, 	GUI.font2, GUI.color3));
		txt_meld	.setBorder(new EmptyBorder(8, 8, 8, 8));

		feeFrom 	.setToolTipText(GUI.t.t("ToolTipText_feeFrom"));
		sliderMax	.setToolTipText(GUI.t.t("ToolTipText_sliderMax"));
		sliderMin	.setToolTipText(GUI.t.t("ToolTipText_sliderMin"));
		acceptMax	.setToolTipText(GUI.t.t("ToolTipText_acceptMax"));
		acceptMin	.setToolTipText(GUI.t.t("ToolTipText_acceptMin"));
			
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
		lbl_satVb[0].setForeground(Color.gray);
		lbl_satVb[1].setForeground(Color.gray);
		lbl_satVb[2].setForeground(Color.gray);
		lbl_satVb[3].setForeground(Color.gray);
		txt_meld	.setForeground(Color.red);
		
		lbl_info	.setFont(GUI.font3);
		lbl_info1	.setFont(GUI.font3);
		
		feeFrom		.setFont(GUI.font2);
		sliderMax	.setFont(GUI.font2);
		sliderMin	.setFont(GUI.font2);
		acceptMax	.setFont(GUI.font2);
		acceptMin	.setFont(GUI.font2);

		Hover.addBackground(feeFrom);
		Hover.addBackground(sliderMax);
		Hover.addBackground(sliderMin);
		Hover.addBackground(acceptMax);
		Hover.addBackground(acceptMin);		
		
		pnl_haupt.add(feeFrom);		
		pnl_haupt.add(sliderMax);		
		pnl_haupt.add(sliderMin);
		pnl_haupt.add(acceptMax);		
		pnl_haupt.add(acceptMin);
		pnl_haupt.add(lbl_info);
		pnl_haupt.add(lbl_info1);	
		pnl_haupt.add(lbl_satVb[0]);
		pnl_haupt.add(lbl_satVb[1]);
		pnl_haupt.add(lbl_satVb[2]);
		pnl_haupt.add(lbl_satVb[3]);
		pnl_haupt.add(lbl_sliMax);
		pnl_haupt.add(lbl_sliMin);
		pnl_haupt.add(lbl_accMax);
		pnl_haupt.add(lbl_accMin);
		pnl_haupt.add(txt_meld);
		getContentPane().add(pnl_haupt);
		loadData();
		
		// Registriert Veränderungn an den Comboboxen
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
			
			String coreResult =peer.get("getmempoolinfo", null);										
			try
			{ 
				JSONObject jo = new JSONObject(coreResult).getJSONObject("result");
				memMinFeeRate = jo.getDouble("mempoolminfee")*100000;				
			}
			catch(JSONException e)
			{
				GUI.txt_meld.setForeground(Color.red);
				GUI.txt_meld.setText("Core Error: "+coreResult);
				txt_meld.setText("Core Error: "+coreResult);  
				e.printStackTrace(); return;
			}
			
			// FeeRate für SliderMax wird berechnet
			String str1 = peer.get("estimatesmartfee", new JSONArray().put(sliderMax.getSelectedIndex()+1));
			JSONObject jo1 = new JSONObject(str1).getJSONObject("result");
			
			double feerate1;
			try{feerate1 = jo1.getDouble("feerate")*100000;}   // hier tritt der Core-Fehler auf, wenn die estimatesmartfee vom Core nicht berechnete werden kann.
			catch(JSONException e) 
			{
				txt_meld.setText("Core Error: "+jo1.toString() + GUI.t.t("FallBack estimatesmartfee Data used")); 
				GUI.txt_meld.setForeground(Color.red); 
				GUI.txt_meld.setText("Core Error: "+jo1.toString() + GUI.t.t("FallBack estimatesmartfee Data used"));  
				//e.printStackTrace(); return;			
				fallBackLoadData(); return;		
			};											
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
			if(acceptMax.getSelectedIndex()==10){dMax = maxAccaptFeeRate;};						
			lbl_accMax.setText(String.format("%.2f",dMax));
			
			// accept min Feerate wird berechnet
			double dmin = jo3.getDouble("feerate")*100000;			
			if(acceptMin.getSelectedIndex()==0) {dmin = 0;};
			if(acceptMin.getSelectedIndex()==1) {dmin = memMinFeeRate;};
			lbl_accMin.setText(String.format("%.2f",dmin));
			
		}
		catch(Exception e)
		{
			GUI.txt_meld.setForeground(Color.red);
			GUI.txt_meld.setText(e.getMessage()); 
			txt_meld.setText(e.getMessage()); 
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	//	Alternative loadData() Methode, wenn der Core estimatesmartfee nicht liefern kann.
	//	Im TestNet kommte es häufiger vor, das der Core "estimatesmartfee" nicht liefern kann und dann einen Fehler zurückgibt.
	//	Damit dieses Programm trotdem funktionsfähig bleibt, wird in diesem Fall diese fallBackLoadData() Methode aufgerufen.
	// 	Da die echten estimatesmartfee-Daten dann nicht zur verfügung stehen, werden die FallBack-Daten  aus der Config geladen.
	//	In der Config, werden die FallBack-estimatesmartfee-Daten einmal festgelegt und können dann später angepasst werden.         
	private static void fallBackLoadData()
	{
		System.out.println("FallBack estimatesmartfee Data used!");
		estimaFeeRateHigh = Math.max( memMinFeeRate * 2 , fallBackEstimaFeeRate); 
		
		// FeeRate für SliderMax wird berechnet
		lbl_sliMax.setText(String.format("%.2f",estimaFeeRateHigh));
		
		// FeeRate für SliderMin wird berechnet	
		if(sliderMin.getSelectedIndex()==11) 
		{		
			lbl_sliMin.setText("0,00");
		}
		else 
		{		
			lbl_sliMin.setText(String.format("%.2f",memMinFeeRate));
		}
			
		// accept max Feerate wird berechnet		
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
		if(acceptMax.getSelectedIndex()==10){dMax = maxAccaptFeeRate;};						
		lbl_accMax.setText(String.format("%.2f",dMax));
			
		// accept min Feerate wird berechnet
		double dmin = 0;;			
		if(acceptMin.getSelectedIndex()==0) {dmin = 0;};
		if(acceptMin.getSelectedIndex()==1) {dmin = memMinFeeRate;};
		lbl_accMin.setText(String.format("%.2f",dmin));	
	}
}