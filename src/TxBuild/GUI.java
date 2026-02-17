package TxBuild;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import org.json.JSONObject;
import FeeEstimate.GUI_FeeChart;
import RPC.ConnectRPC;
import lib3001.btc.Transaktion;
import lib3001.btc.TxPrinter;
import lib3001.crypt.Convert;
import lib3001.java.Animated;
import lib3001.java.Hover;
import lib3001.java.MyFont;
import lib3001.qrCode.QRCodeZXING;
import lib3001.qrCode.QrCapture;


/***********************************************************************************************************************************************
*				   											 Autor: Mr. Maxwell   							vom 14.02.2026						*
*	Die GUI für den TxBuilder																													*
*************************************************************************************************************************************************/


public class GUI extends JFrame
{
	public static void main(String[] args) throws Exception 
	{	
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();			// Font intalliert
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MyFont.get_UbuntuMono()));		// Font Ubuntu Mono wird geladen
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MyFont.get_DejaVuSans()));		// Font DejaVu Sans wird geladen
					frame = new GUI();
					Config.load();
					setAllTextInGUI();
					frame.setVisible(true);
					frame.setBounds(posX, posY, 1100, 500);
					Animated.initAnimation();
					Animated.setColors(Color.DARK_GRAY, Color.LIGHT_GRAY);
					Animated.setSleepTime(150);
					GUI_CoreSettings.changesAuthenticationMethod();
					GUI_CoreSettings.checkCoreConnection();
					initNetwork();				
				} 
				catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	
	public final static String 	progName		= "TxBuilder";									// Program Name		
	public final static String	version 		= "V1.3.3";										// Version der Anwendung
	public final static String	autor 			= "Mr. Maxwell";								// Name Autor
	public final static byte[] MAINNET  = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	public final static byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};
	public static GUI 			frame;															// Der Frame dieser GUI
	public static JPanel 	 	pnl_in			= new JPanel();									// Haupt Input  Panel
	public static JPanel 	 	pnl_out 		= new JPanel();									// Haupt Output Panel
	public static JPanel		pnl_fee			= new JPanel();									// In dem Panel ist nur der Fee-Slider drin
	public static JPanel 		pnl_L 			= new JPanel();
	public static JPanel 		pnl_R 			= new JPanel();
	private static JPanel 		pnl_oben 		= new JPanel();
	private static JPanel 		pnl_unten 		= new JPanel();
	private static JPanel 		pnl_inOben 		= new JPanel();
	private static JPanel 		pnl_outOben 	= new JPanel();
	public static JTextPane 	txt_info 		= new JTextPane();								// InfoText
	public static JTextField 	txt_totalValIn	= new JTextField();								// Gesamter Eingangs-Betrag
	public static JTextField 	txt_totalValOut	= new JTextField();								// Gesamter Ausgangs-Betrag
	public static JTextField 	txt_fee 		= new JTextField();								// Netzwerk Gebühren
	public static JTextField 	txt_txVSize 	= new JTextField();								// Die Geschätzte/Berechnete größe der vorrausichtlichen signierten Transaktion (vByte)
	public static JTextField 	txt_feeRate 	= new JTextField();								// Gebühr pro VBytes (sat/vB)
	public static JTextArea  	txt_meld 		= new JTextArea();								// Alle Meldungen und Fehler
	public static JTextField[]	txt_inAdr;														// Input Adressen   Array
	public static JTextField[]	txt_inValue;													// Input BTC-Betrag Array
	public static JTextField[]	txt_outAdr;														// Output Adressen   Array
	public static JFormattedTextField[]	txt_outValue;											// Output BTC-Betrag Array
	public static JProgressBar 	progressBar 	= new JProgressBar();							// Wartebalklen unten, wenn der Core die Transaktionen sucht.
	public static JComboBox 	cBox_inCount	= new JComboBox();								// Auswahlmenü der Anzahl der Eingänge
	public static JComboBox 	cBox_outCount	= new JComboBox();								// Auswahlmenü der Anzahl der Ausgänge
	public static JComboBox 	cBox_language	= new JComboBox();								// Auswahlmenü der verwendeten Sprache
	public static JLabel		lbl_info		= new JLabel();									// Info-Text im Hauptfenster
	public static JLabel[]		btn_inQR;														// Wird als Butten verwendet. Buttons zum scannen des QR-Codes für die Input-Adressen	
	public static JLabel[]		btn_outQR;														// Wird als Butten verwendet. Buttons zum scannen des QR-Codes für die Output-Adressen	
	public static JLabel 		btn_loadTx 		= new JLabel();									// Der Button startet die Tx-In suche des Core´s
	public static JLabel 		btn_cancel 		= new JLabel();									// Stoppt die Tx-In suche des Core´s
	public static JLabel 		lbl_viewTx 		= new JLabel();									// Wird als Button benutzt und lässt die fertige Transaktion anzeigen. (TxPrinter)
	public static JLabel 		lbl_viewTxHex	= new JLabel();									// Wird als Button benutzt und lässt die fertige Transaktion anzeigen. (Hex)
	public static JLabel 		lbl_qrCode 		= new JLabel();									// Wird als Button benutzt und lässt die fertige Transaktion anzeigen. (als QR-Code)
	public static JLabel 		lbl_save 		= new JLabel();									// Save Button der die Tx speichert
	public static JLabel		lbl_progress	= new JLabel(MyIcons.load_gif);					// Animiertes progress gif. drehendes Bitcoin-Symbol
	public static JMenuBar 		menuBar 		= new JMenuBar();								// Hauptmenüleiste ganz oben
	public static JMenu 		menu_Setting	= new JMenu();									// Das Settings Menü
	public static JMenu			menu_functio	= new JMenu();									// Import-Menü
	public static JMenu 		menu_info 		= new JMenu();									// Information über dieses Programm
	public static JMenuItem 	mItem_feeRate	= new JMenuItem();								// Fee-Rate im Linien-Diagramm anzeigen
	public static JMenuItem 	mItem_import	= new JMenuItem();								// Zum Importieren einer großen Menge von Bitcoin-Adressen als CSV-Datei. Um Verfügbare Betrage zu finden.
	public static JMenuItem 	mItem_publis	= new JMenuItem();								// Menüpunkt Publish Tx
	public static JMenuItem 	mItem_txLoad	= new JMenuItem();								// zum Laden einer Transaktion aus der Blockchain
	public static JMenuItem 	mItem_txJSON	= new JMenuItem();								// Konvertiert die Transaktion in ein JSON-Format
	public static JMenuItem 	mItem_sigHash	= new JMenuItem();								// Berechnet den Signature-Hash einer Transaktion
	public static JMenuItem		mItem_QRShow	= new JMenuItem();								// Funktion zum algemeinen Anzeigen von QR-Codes
	public static JMenuItem 	mItem_corSet	= new JMenuItem();								// Menüpunkt CoreSettings
	public static JMenuItem 	mItem_txSet		= new JMenuItem();								// Menüpunkt Transaktions-Settings
	public static JMenuItem 	mItem_feeSet	= new JMenuItem();								// Menüpunkt Gebühren-Settings
	public static JCheckBox 	btn_testNet 	= new JCheckBox();								// Button im Settings-Menü. Wenn ausgewählt, dann TestNet, sonst Main-Net
	public static JSlider 		sliderFee 		= new JSlider();								// Der Slider für die Tx-Gebühren
	public static int			debugMode		= 0;											// DebugModus: 0=kein=Default, 1=MainNet, 2=TestNet3
	public static int 			posX = 400;
	public static int 			posY = 250;               	
	public static int 			countSearchBlocks = 1000;										// Anzahl der Blöcke die bei "Get Transaktion from Blockchain" rückwirkend durchsucht werden
	public static int	  		toolTipSetDismissDelay = 20000;									// Alle ToolTips werden nach dieser Zeit ausgeblendet. Kann in der Config angepasst werden.
	public static boolean 		toolTipEnabled 	= true;											// Alle ToolTips können hier global ausgeschaltet werden. Kann in der Config angepasst werden.
	public static String[] 		comboBoxList 	= new String[100];								// Die beiden Combo-Boxen für die In- und Outputs werden mit diesen Elementen initialisiert
	public static Color color1 	= new Color(255,244,230); 										// Farbe Hintergrund
	public static Color color3 	= new Color(120,120,120); 										// Farbe Text grau Feldbeschreibungen
	public static Color color4 	= new Color(247, 147, 26); 										// Farbe Linien (BTC-Farbe)
	public static Color color5	= new Color(255,255,200);										// Farbe für Fee-Regulator
	public static Color color6	= new Color(235,224,210);										// Farbe für MenuBar
	public static Color color7	= new Color(255,177,56);										// Farbe für ToolTip-Background	
	static final Font	font1	= new Font("DejaVu Sans", Font.PLAIN, 10);                      // Font für Rahmenbeschriftung klein    
	static final Font	font2	= new Font("DejaVu Sans", Font.PLAIN, 11);                      // Font für Rahmenbeschriftung normal	
	static final Font	font3	= new Font("DejaVu Sans", Font.PLAIN, 14);                      // Font für Rahmenbeschriftung groß     
	static final Font	font4	= new Font("Ubuntu Mono", Font.PLAIN, 14);                      // Font für Textfelder                  
	static final Font	font5	= new Font("DejaVu Sans", Font.PLAIN, 16);                      // Font für Beschreibung, Größe 16      
	static final Font	font6	= new Font("DejaVu Sans", Font.PLAIN, 20);                      // Font für Beschreibung, Größe 18      
	public static GUI_TxSettings dialogTxSettings;												// Das Transaktions-Settings Menü-Fenster (Muss hier inizialisiert werden!)
	public static GUI_ImportCSV  dialogImport;													// Der Import Dialog aus dem oberen Menü
	public static GUI_CoreSettings dialogCoreSettings;											// Der Core-Settings Dialog
	public static Translate t	= new Translate(); 												// Übersetzungstexte werden geladen
	
	
	
	// Alle Texte der GUI müssen hier plaztiert werden. Damit die Sprachumschalung funktioeniert.
	// Diese Methode wird aufgerufen nachdem die GUI und die Config geladen wurde, oder wenn die Sprache geändert wird.
	private static void setAllTextInGUI()
	{	
		ToolTipManager.sharedInstance().setDismissDelay(toolTipSetDismissDelay);  	// Tooltip bleibt 20s sichtbar
		ToolTipManager.sharedInstance().setEnabled(toolTipEnabled);					// Alle Tooltips können AUS oder Eingeschaltet werden
		if(GUI.cBox_language.getSelectedIndex()==1) t.setGermanUI();
	 	if(GUI.cBox_language.getSelectedIndex()==0) t.setEnglishUI();
		cBox_inCount.setSelectedIndex(cBox_inCount.getSelectedIndex());
		cBox_outCount.setSelectedIndex(cBox_outCount.getSelectedIndex());	

		txt_info		.setText(t.t("programm-infoText")); 				// Haupt Info Text mit der Beschreibung des Programmes
		lbl_info		.setText(t.t("Create an unsigned transaction")); 		
		btn_loadTx		.setText(t.t("load Tx-inputs from Blockchain")); 		
		btn_cancel		.setText(t.t("cancel loading from Blockchain")); 		
		lbl_viewTx		.setText(t.t("View Transaction")); 		
		lbl_viewTxHex	.setText(t.t("View Tx Raw"));	
		lbl_qrCode		.setText(t.t("QR-Code")); 		
		lbl_save		.setText(t.t("Save Transaction"));		
		menu_Setting	.setText(t.t("Settings"));
		menu_functio	.setText(t.t("Functions"));	
		mItem_feeRate	.setText(t.t("View historical fee rate"));	
		btn_testNet		.setText(t.t("TestNet3")); 	
		menu_info 		.setText(t.t("Info"));							
		mItem_import	.setText(t.t("Bitcoin Address List"));			
		mItem_publis	.setText(t.t("Send signed transaction"));			
		mItem_txLoad	.setText(t.t("Get transaction from blockchain"));	
		mItem_txJSON	.setText(t.t("Get JSON from transaction"));		
		mItem_sigHash	.setText(t.t("Get Signature Hash"));				
		mItem_QRShow	.setText(t.t("show QR code"));
		mItem_corSet	.setText(t.t("BitcoinCore connection settings"));	
		mItem_txSet		.setText(t.t("Transaction settings"));			
		mItem_feeSet	.setText(t.t("Fee settings"));					
		
		GUI_CoreSettings.btn_auth	.setText(t.t("Authentication Method: Cookie"));
		GUI_CoreSettings.btn_path	.setText(t.t("Bitcoin Core Path:"));
		GUI_FeeSettings.feeFrom		.setText(new String[]{t.t("First output"),t.t("Last output")});
		GUI_FeeSettings.sliderMax	.setText(t.getTextArray("sliderMax_Language-Array",10));
		GUI_FeeSettings.sliderMin	.setText(t.getTextArray("sliderMin_Language-Array",12));
		GUI_FeeSettings.acceptMax	.setText(t.getTextArray("acceptMax_Language-Array",11));
		GUI_FeeSettings.acceptMin	.setText(new String[] {"0",t.t("mempoolminfee")});

		pnl_fee			.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Fee"), 				TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		cBox_inCount	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),t.t("Input count"),  		TitledBorder.LEADING, 	TitledBorder.TOP, 		font3, color3));
		cBox_outCount	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),t.t("Output count"), 		TitledBorder.LEADING, 	TitledBorder.TOP, 		font3, color3));
		cBox_language	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),t.t("Language"), 			TitledBorder.LEADING, 	TitledBorder.TOP, 		font3, color3));
		pnl_L			.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Source address"), 		TitledBorder.LEFT, 		TitledBorder.ABOVE_TOP, font3, color3));
		pnl_R			.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Destination address"), TitledBorder.LEFT, 		TitledBorder.ABOVE_TOP, font3, color3));
		txt_totalValIn	.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Total value input"),	TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_totalValOut	.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Total value output"),	TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_fee			.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Fee (BTC)"), 			TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_txVSize		.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Virtual size"), 		TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_feeRate		.setBorder(new TitledBorder(new LineBorder(color4), 	t.t("Fee rate"), 			TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		
		btn_loadTx		.setToolTipText(t.t("ToolTipText_btn_loadTx"));
		cBox_inCount	.setToolTipText(t.t("ToolTipText_cBox_inCount"));
		cBox_outCount	.setToolTipText(t.t("ToolTipText_cBox_outCount"));
		sliderFee		.setToolTipText(t.t("ToolTipText_pnl_fee"));
		btn_testNet		.setToolTipText(t.t("ToolTipText_btn_testNet"));
	}
	
	
	
	
	public GUI()
	{			
		JLayeredPane pnl_lp 	= new JLayeredPane();									// LayerPane wird verwendet um die Progress-Animation auf dem übergeordneten Panel einzublenden.
		JPanel 		contentPane = new JPanel();
		JPanel 		pnl_meld 	= new JPanel();
		JSplitPane 	splitPane 	= new JSplitPane();
		JScrollPane scroll_in 	= new JScrollPane();
		JScrollPane scroll_out 	= new JScrollPane();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(progName+"     Version: "+version);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(GUI.posX > screenSize.width-855 || GUI.posX < 0)  GUI.posX=0;
		if(GUI.posY > screenSize.height-340|| GUI.posY < 0) GUI.posY=0;			
		setBounds(posX, posY, 1250, 500);
		setMinimumSize(new Dimension(950,300));
		setJMenuBar(menuBar);
		setContentPane(contentPane);
		UIManager.put("OptionPane.background", 	color1);
		UIManager.put("Panel.background", 		color1);
		UIManager.put("TextField.background", 	color1);
        UIManager.put("ToolTip.background", 	color7);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(color4));

// --------------------------------------- Hover ---------------------------------------------------------------------
		Hover.setBorderColor(color4);
		Hover.addBackground(menu_Setting);
		Hover.addBackground(menu_functio);
		Hover.addBackground(menu_info);
		Hover.addBackground(cBox_inCount);
		Hover.addBackground(cBox_outCount);
		Hover.addBackground(cBox_language);
		Hover.addBackground(btn_loadTx);
		Hover.addBackground(btn_cancel);
		Hover.addBackground(sliderFee);
		Hover.addBorder(lbl_viewTx );	
		Hover.addBorder(lbl_viewTxHex);	
		Hover.addBorder(lbl_qrCode);
		Hover.addBorder(lbl_save);		
				
// --------------------------------------- Hier wird alles Transparennt gestellt -------------------------------------			
//		pnl_in		.setOpaque(false);
//		pnl_out 	.setOpaque(false);
//		pnl_fee		.setOpaque(false);
//		pnl_L 		.setOpaque(false);	
//		pnl_R 		.setOpaque(false);
//		pnl_oben 	.setOpaque(false);
//		pnl_unten 	.setOpaque(false);
//		pnl_inOben 	.setOpaque(false);
//		pnl_outOben	.setOpaque(false);
//		txt_meld	.setOpaque(false);
//		txt_txVSize	.setOpaque(false);
//		txt_feeRate	.setOpaque(false);	
//		splitPane	.setOpaque(false);
//		scroll_in	.setOpaque(false);
//		pnl_lp		.setOpaque(false);
		
		
	
		for(int i=0;i<100;i++) {comboBoxList[i]=String.valueOf(i+1);}
		cBox_inCount	.setModel(new DefaultComboBoxModel(comboBoxList));	
		cBox_outCount	.setModel(new DefaultComboBoxModel(comboBoxList));
		cBox_language	.setModel(new DefaultComboBoxModel(Translate.languages));
		
		menu_functio	.setFont(font3); 
		menu_Setting	.setFont(font3);
		menu_info		.setFont(font3);
		lbl_info		.setFont(font6);
		lbl_viewTx		.setFont(font3);
		lbl_viewTxHex	.setFont(font3);
		lbl_qrCode		.setFont(font3);
		lbl_save		.setFont(font3);
		txt_totalValIn	.setFont(font4);
		txt_totalValOut	.setFont(font4);
		txt_fee			.setFont(font4);
		txt_txVSize		.setFont(font4);
		txt_feeRate		.setFont(font4);
		btn_loadTx		.setFont(font3);
		btn_cancel		.setFont(font3);
		
		cBox_inCount	.setForeground(color3);
		cBox_outCount	.setForeground(color3);
		cBox_language	.setForeground(color3);
		menu_functio	.setForeground(color3);
		menu_Setting	.setForeground(color3);
		menu_info		.setForeground(color3);
		btn_loadTx		.setForeground(color3);		
		btn_cancel		.setForeground(color3);
		txt_meld		.setForeground(Color.red);
		progressBar		.setForeground(color4);
		lbl_info		.setForeground(color4);
		lbl_viewTx		.setForeground(color3);
		lbl_viewTxHex	.setForeground(color3);
		lbl_qrCode		.setForeground(color3);
		lbl_save		.setForeground(color3);
		txt_txVSize		.setForeground(Color.gray);
		txt_feeRate		.setForeground(Color.gray);
		txt_fee			.setForeground(Color.red);
		menuBar			.setBackground(color1);
		cBox_inCount	.setBackground(color1);
		cBox_outCount	.setBackground(color1);
		cBox_language	.setBackground(color1);
		mItem_import	.setBackground(color1);
		mItem_publis	.setBackground(color1);
		mItem_feeRate	.setBackground(color1);
		mItem_corSet	.setBackground(color1);
		mItem_txSet		.setBackground(color1);
		mItem_feeSet	.setBackground(color1);
		mItem_txLoad	.setBackground(color1);
		mItem_txJSON	.setBackground(color1);
		mItem_QRShow	.setBackground(color1);
		mItem_sigHash	.setBackground(color1);
		progressBar		.setBackground(color1);
		btn_testNet		.setBackground(color1);
		pnl_fee			.setBackground(color5);
		sliderFee		.setBackground(color5);
	
		menuBar			.setBorder(new LineBorder(color4));
		pnl_oben		.setBorder(new EmptyBorder(15, 15, 15, 4));
		pnl_unten		.setBorder(new EmptyBorder(15,0,10,0));
		txt_meld		.setBorder(new EmptyBorder(8, 8, 8, 8));
		btn_loadTx		.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, new Color(255, 214, 93), new Color(227, 127, 6), null));
		btn_cancel		.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, new Color(255, 214, 93), new Color(227, 127, 6), null));
		splitPane		.setBorder(null);
		scroll_in		.setBorder(null);
		scroll_out		.setBorder(null);
		lbl_viewTx		.setBorder(new EmptyBorder(1,1,1,1));	  
		lbl_viewTxHex	.setBorder(new EmptyBorder(1,1,1,1));		  
		lbl_qrCode		.setBorder(new EmptyBorder(1,1,1,1));	      
		lbl_save		.setBorder(new EmptyBorder(1,1,1,1));			  
		
		menu_functio	.setIcon(MyIcons.chain);
		menu_Setting	.setIcon(MyIcons.settings);
		mItem_publis	.setIcon(MyIcons.send);
		mItem_import	.setIcon(MyIcons.open);
		mItem_feeRate	.setIcon(MyIcons.chart);
		mItem_txLoad	.setIcon(MyIcons.load2);
		mItem_txJSON	.setIcon(MyIcons.json);
		mItem_QRShow	.setIcon(MyIcons.qrCode);
		mItem_corSet	.setIcon(MyIcons.connect);
		mItem_txSet		.setIcon(MyIcons.txSettings);
		mItem_feeSet	.setIcon(MyIcons.fee);
		lbl_qrCode		.setIcon(MyIcons.qrCode);
		lbl_viewTx		.setIcon(MyIcons.viewTx);
		lbl_viewTxHex	.setIcon(MyIcons.hex);
		lbl_save		.setIcon(MyIcons.save);
		
				
		cBox_inCount	.setMaximumSize(new Dimension(15000, 32767));
		cBox_outCount	.setMaximumSize(new Dimension(15000, 32767));
		cBox_language	.setMaximumSize(new Dimension(9000, 32767));
		btn_testNet		.setMaximumSize(new Dimension(2000, 23));
		txt_txVSize		.setMaximumSize(new Dimension(100, 32767));
		txt_feeRate		.setMaximumSize(new Dimension(100, 32767));
		txt_totalValIn	.setMaximumSize(new Dimension(4000, 39));	
	
		txt_totalValIn	.setColumns(20);
		txt_totalValOut	.setColumns(21);
		txt_fee			.setColumns(19);
		txt_txVSize		.setColumns(19);
		txt_feeRate		.setColumns(19);
		
		pnl_lp			.setLayout(new OverlayLayout(pnl_lp));
		contentPane		.setLayout(new BorderLayout(0, 0));
		pnl_fee			.setLayout(new BorderLayout(0, 0));
		pnl_oben		.setLayout(new BoxLayout(pnl_oben, BoxLayout.X_AXIS));

		pnl_unten		.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnl_inOben		.setLayout(new BoxLayout(pnl_inOben,BoxLayout.X_AXIS));
		pnl_outOben		.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnl_L			.setLayout(new BorderLayout(0, 0));
		pnl_R			.setLayout(new BorderLayout(0, 0));
		pnl_out			.setLayout(new BoxLayout(pnl_out, BoxLayout.Y_AXIS));
		pnl_meld		.setLayout(new BoxLayout(pnl_meld, BoxLayout.Y_AXIS));
					
		txt_totalValIn	.setHorizontalAlignment(SwingConstants.RIGHT);
		txt_totalValOut	.setHorizontalAlignment(SwingConstants.RIGHT);
		txt_fee			.setHorizontalAlignment(SwingConstants.RIGHT);
		txt_txVSize		.setHorizontalAlignment(SwingConstants.RIGHT);
		txt_feeRate		.setHorizontalAlignment(SwingConstants.RIGHT);
		menu_functio	.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		menu_Setting	.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		menu_info		.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		sliderFee		.setOrientation(SwingConstants.VERTICAL);
		progressBar		.setStringPainted(true);
		pnl_fee			.setPreferredSize(new Dimension(40,1));
		pnl_inOben		.setPreferredSize(new Dimension(10, 50));
		pnl_outOben		.setPreferredSize(new Dimension(10, 50));
		progressBar		.setPreferredSize(new Dimension(146, 13));
		
		progressBar		.setVisible(false);
		lbl_progress	.setVisible(false);
		btn_cancel		.setVisible(false);
		lbl_viewTx		.setEnabled(false);
		lbl_qrCode		.setEnabled(false);
		lbl_viewTxHex	.setEnabled(false);
		lbl_save		.setEnabled(false);
		btn_testNet		.setEnabled(false);
		GUI.sliderFee	.setVisible(false);
		txt_fee			.setEditable(false);
		txt_txVSize		.setEditable(false);
		txt_feeRate		.setEditable(false);
		txt_totalValIn	.setEditable(false);
		txt_totalValOut	.setEditable(false);
		txt_meld		.setEditable(false);
		txt_info		.setEditable(false);
		
		cBox_inCount	.setFocusable(false);
		cBox_outCount	.setFocusable(false);
		cBox_language	.setFocusable(false);
		btn_cancel		.setFocusable(false);

		splitPane		.setDividerSize(15);
		splitPane		.setLeftComponent(pnl_lp);
		splitPane		.setRightComponent(pnl_R);
		splitPane		.setDividerLocation(500);
		scroll_in		.setViewportView(pnl_in);
		scroll_out		.setViewportView(pnl_out);
			
		pnl_lp		.add(lbl_progress);
		pnl_lp		.add(pnl_L);
		menuBar		.add(cBox_inCount);
		menuBar		.add(cBox_outCount);	
		menuBar		.add(Box.createHorizontalStrut(20));
		menuBar		.add(menu_functio);
		menu_functio.add(mItem_import);
		menu_functio.add(mItem_publis);
		menu_functio.add(mItem_feeRate);
		menu_functio.add(mItem_txLoad);
		menu_functio.add(mItem_txJSON);
		menu_functio.add(mItem_QRShow);
		menu_functio.add(mItem_sigHash);
		menuBar		.add(Box.createHorizontalStrut(20));
		menuBar		.add(menu_Setting);
		menuBar		.add(Box.createHorizontalGlue());
		menuBar		.add(cBox_language);
		menuBar		.add(Box.createHorizontalStrut(20));	
		menuBar		.add(menu_info);
		menuBar		.add(Box.createHorizontalStrut(20));
		menu_info	.add(txt_info);
		menu_Setting.add(mItem_corSet);
		menu_Setting.add(mItem_txSet);	
		menu_Setting.add(mItem_feeSet);		
		menu_Setting.add(btn_testNet);
		pnl_oben	.add(Box.createHorizontalGlue());
		pnl_oben	.add(lbl_info);
		pnl_oben	.add(Box.createHorizontalGlue());
		pnl_oben	.add(txt_txVSize);
		pnl_oben	.add(Box.createHorizontalStrut(5));
		pnl_oben	.add(txt_feeRate);
		pnl_L		.add(scroll_in, BorderLayout.CENTER);
		pnl_L		.add(pnl_inOben, BorderLayout.NORTH);
		pnl_inOben	.add(Box.createHorizontalStrut(10));
		pnl_inOben	.add(btn_loadTx);
		pnl_inOben	.add(btn_cancel);
		pnl_inOben	.add(Box.createHorizontalGlue());
		pnl_inOben	.add(txt_totalValIn);
		pnl_R		.add(scroll_out, BorderLayout.CENTER);
		pnl_R		.add(pnl_outOben, BorderLayout.NORTH);
		pnl_outOben	.add(txt_fee);
		pnl_outOben	.add(txt_totalValOut);
		pnl_unten	.add(Box.createHorizontalStrut(10));
		pnl_unten	.add(lbl_viewTx);
		pnl_unten	.add(Box.createHorizontalStrut(20));
		pnl_unten	.add(lbl_viewTxHex);
		pnl_unten	.add(Box.createHorizontalStrut(20));
		pnl_unten	.add(lbl_qrCode);
		pnl_unten	.add(Box.createHorizontalStrut(20));
		pnl_unten	.add(lbl_save);
		pnl_meld	.add(progressBar);
		pnl_meld	.add(pnl_unten);
		pnl_meld	.add(txt_meld);
		pnl_fee		.add(sliderFee);
		contentPane	.add(pnl_oben, BorderLayout.NORTH);
		contentPane	.add(splitPane, BorderLayout.CENTER);
		contentPane	.add(pnl_fee, BorderLayout.EAST);
		contentPane	.add(pnl_meld, BorderLayout.SOUTH);

		TxBuildAction.actions();  				// Die Actions der Klasse TxBuildActions werden gestartet.

	
		
		

		
// ------------------------------------------------------------------------------------- TX In ------------------------------------------------------------------------------------------------------
		
		
		
	// Ändert die Anzahl der Eingänge
	cBox_inCount.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(isVisible() && txt_inAdr.length == cBox_inCount.getSelectedIndex()+1) return;   // Wenn keine Größenänderung eingestellt wurde, wird sofort hier abgebrochen.
			int m = 0;
			if(isVisible()) {m = JOptionPane.showConfirmDialog(contentPane, t.t("The number of transaction entries will be redefined.\nThis action will delete all previous entries!"));}			
			if(m==0)
			{
				Animated.stop(GUI.btn_loadTx);
				int txInCount 		= cBox_inCount.getSelectedIndex()+1;
				pnl_in 				= new JPanel();	
				JPanel[] pnlA_in 	= new JPanel[txInCount];
				btn_inQR			= new JLabel[txInCount];
				txt_inAdr 			= new JTextField[txInCount];
				txt_inValue			= new JTextField[txInCount];
				txt_totalValIn.setText("");
				pnl_in.setLayout(new BoxLayout(pnl_in, BoxLayout.Y_AXIS));
				pnl_in.setBackground(color1);
				scroll_in.setViewportView(pnl_in);
				for(int i=0;i<txInCount;i++)
				{
					pnlA_in[i] = new JPanel();
					pnlA_in[i].setLayout(new BoxLayout(pnlA_in[i], BoxLayout.X_AXIS));	
					pnlA_in[i].setMaximumSize(new Dimension(20000,39));
					pnlA_in[i].setOpaque(false);
					pnl_in.add(pnlA_in[i]);
			
					btn_inQR[i] = new JLabel();
					btn_inQR[i].setIcon(MyIcons.qrScan);
					btn_inQR[i].setText(String.valueOf(i+1));  
					btn_inQR[i].setHorizontalTextPosition(2);
					btn_inQR[i].setToolTipText(t.t("ToolTipText_btn_QR"));
					Hover.addBorder(btn_inQR[i]);
					pnlA_in[i].add(btn_inQR[i]);
					
					txt_inAdr[i] = new JTextField();
					txt_inAdr[i].setFont(font4);
					txt_inAdr[i].setMaximumSize(new Dimension(400,40));
					txt_inAdr[i].setBorder(new TitledBorder(new LineBorder(color4), t.t("BTC-Address"), 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_inAdr[i].setBackground(Color.white);
					txt_inAdr[i].setToolTipText(t.t("ToolTipText_txt_inAdr"));
					Hover.addBorder(txt_inAdr[i]);
					pnlA_in[i].add(txt_inAdr[i]);				
					txt_inValue[i] = new JTextField();
					txt_inValue[i].setFont(font4);
					txt_inValue[i].setBackground(color1);
					txt_inValue[i].setMaximumSize(new Dimension(165,40));
					txt_inValue[i].setHorizontalAlignment(SwingConstants.RIGHT);
					txt_inValue[i].setBorder(new TitledBorder(new LineBorder(color4), t.t("Value"), 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_inValue[i].setEditable(false);
					txt_inValue[i].setOpaque(false);
					pnlA_in[i].add(txt_inValue[i]);
					
					// Scannt den QR-Code für die Input-Adressen
					btn_inQR[i].addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent e) 
						{							
							int index = Integer.valueOf(((JLabel) e.getComponent()).getText())-1;	// Achtung: Hier wird Reflection verwendet um den den Schleifen-Index[i] zu erhalten.	
							qrScan(btn_inQR[index], txt_inAdr[index]);	
						}
					});
					
					
					// Löscht bei jeder Eingabe sofort das JSONObject "coreTxOutSet" in der Klasse TxBuildAction. Verhindert das veraltete Inputs verarbeitet werden können.
					// Löscht auch bei jeder Eingabe sofort für ALLE die Input-Value Betrag! Signalisiert Änderungen durch Farbänderungen in den Feldern.
					// Es gibt in Java keinen Listener für ein JTextField der alle Änderungen am Text erkennt! Daher muss hier ein DocumentListener verwendet werden! 
					// Dieser Listener ist wirklich wichtig! Sollte er Änderungen nicht erkännen, könnte eine Tx mit falschen Inputs ausgegeben werden! 
					// Änderungen an dieser Methode daher mit höschster Sorgfalt und ausreichend prüfen!
					// Es müssen auch Änderungen erkannt werden, die durch die Eingabe über z.B. die Kamera erfolgen, oder Eingabe-Funktion der CSV-Datei.
					txt_inAdr[i].getDocument().addDocumentListener(new DocumentListener() 
					{
						@Override
						public void insertUpdate(DocumentEvent e) 
						{																										
							TxBuildAction.coreTxOutSet = null;
							for(int i=0;i<txInCount;i++) 
							{
								txt_inAdr[i].setBackground(Color.white);
								txt_inValue[i].setText("");																
							}
							txt_totalValIn.setText("");								
							if(allInputFieldsEdited()) Animated.start(btn_loadTx, true);
							else					   Animated.stop(btn_loadTx);			
						}
						@Override
						public void removeUpdate(DocumentEvent e) 
						{		
							TxBuildAction.coreTxOutSet = null;						
							if(allInputFieldsEdited()) Animated.start(btn_loadTx, true);
							else					   Animated.stop(btn_loadTx);		
						}
						@Override
						public void changedUpdate(DocumentEvent e) {TxBuildAction.coreTxOutSet = null;}
					});
				}					
				splitPane.validate();
				valueOutVerify();
				TxBuildAction.verifyOutputsAndCalcFeeRate();
			}
			else {cBox_inCount.setSelectedIndex(txt_inAdr.length-1);}
		}
	});		
	cBox_inCount.setSelectedIndex(0);

		
		
		
// ------------------------------------------------------------------------------------- TX Out ------------------------------------------------------------------------------------------------------

		
		
	// Ändert die Anzahl der Ausgänge
	cBox_outCount.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(isVisible() && txt_outAdr.length == cBox_outCount.getSelectedIndex()+1) return;   // Wenn keine Größenänderung eingestellt wurde, wird sofort hier abgebrochen.
			int m =0;
			if(isVisible()) {m = JOptionPane.showConfirmDialog(contentPane, t.t("The number of transaction exits is redefined.\nThis action deletes all previous exits!"));}			
			if(m==0)
			{
				int txOutCount 		= cBox_outCount.getSelectedIndex()+1;
				pnl_out 			= new JPanel();	
				JPanel[] pnlA_out 	= new JPanel[txOutCount];
				btn_outQR			= new JLabel[txOutCount];
				txt_outAdr 			= new JTextField[txOutCount];
				txt_outValue		= new JFormattedTextField[txOutCount];
				pnl_out.setLayout(new BoxLayout(pnl_out, BoxLayout.Y_AXIS));
				pnl_out.setBackground(color1);
				txt_totalValOut.setText("");
				txt_fee.setText("");
				scroll_out.setViewportView(pnl_out);
				for(int i=0;i<txOutCount;i++)
				{
					pnlA_out[i] = new JPanel();
					pnlA_out[i].setLayout(new BoxLayout(pnlA_out[i], BoxLayout.X_AXIS));
					pnlA_out[i].setMaximumSize(new Dimension(20000,39));
					pnlA_out[i].setOpaque(false);
					pnl_out.add(pnlA_out[i]);
					
					btn_outQR[i] = new JLabel();
					btn_outQR[i].setIcon(MyIcons.qrScan);
					btn_outQR[i].setText(String.valueOf(i+1));  // Index wird hier als Text verwendet um mit Reflection den Index später im Thread zu erkennen. Und als Zeilen-Aufzählung
					btn_outQR[i].setHorizontalTextPosition(2);
					btn_outQR[i].setToolTipText(t.t("ToolTipText_btn_QR"));
					Hover.addBorder(btn_outQR[i]);
					pnlA_out[i].add(btn_outQR[i]);
									
					txt_outAdr[i] = new JTextField();
					txt_outAdr[i].setFont(font4);
					txt_outAdr[i].setMaximumSize(new Dimension(400,40));
					txt_outAdr[i].setBorder(new TitledBorder(new LineBorder(color4), GUI.t.t("BTC-Address"), 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_outAdr[i].setBackground(Color.white);
					txt_outAdr[i].setToolTipText(t.t("ToolTipText_txt_outAdr"));
					Hover.addBorder(txt_outAdr[i]);
					pnlA_out[i].add(txt_outAdr[i]);
					
					try 
					{
						MaskFormatter fm = new MaskFormatter("******#,########");
						fm.setPlaceholderCharacter( '*' );
						fm.setValidCharacters("0123456789");
						txt_outValue[i] = new JFormattedTextField(fm);	
					} 
					catch (ParseException e1) {e1.printStackTrace();}
					
					txt_outValue[i].setHorizontalAlignment(JTextField.RIGHT);
					txt_outValue[i].setFont(font4);
					txt_outValue[i].setMaximumSize(new Dimension(165,40));
					txt_outValue[i].setBorder(new TitledBorder(new LineBorder(color4), GUI.t.t("Value"), 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_outValue[i].setBackground(Color.white);
					txt_outValue[i].setToolTipText(t.t("ToolTipText_txt_outValue"));
					Hover.addBorder(txt_outValue[i]);
					pnlA_out[i].add(txt_outValue[i]);
							
					// Scannt den QR-Code für die Output-Adressen
					btn_outQR[i].addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent e) 
						{
							int index = Integer.valueOf(((JLabel) e.getComponent()).getText())-1;	// Achtung: Hier wird Reflection verwendet um den den Schleifen-Index[i] zu erhalten.
							qrScan(btn_outQR[index], txt_outAdr[index]);			
						}
					});			
								
					// Dieser Listener erkennt zuverlässig Änderungen in allen TxOut-Address Feldern.
					// Wenn alle benötigten Eingabefelder ausgefüllt sind, werden weitere Berechnungen für die Fee-Rate ausgelöst
					txt_outAdr[i].getDocument().addDocumentListener(new DocumentListener() 
					{
						public void insertUpdate(DocumentEvent e) {TxBuildAction.verifyOutputsAndCalcFeeRate();}
						public void removeUpdate(DocumentEvent e) {TxBuildAction.verifyOutputsAndCalcFeeRate();}
						public void changedUpdate(DocumentEvent e){TxBuildAction.verifyOutputsAndCalcFeeRate();}
					});
					
					
					// Dieser Listener erkennt zuverlässig Änderungen in allen TxOut-Value Feldern.
					// Wenn alle benötigten Eingabefelder ausgefüllt sind, werden weitere Berechnungen für die Fee-Rate ausgelöst
					// Jede Änderung löste mehrere Berechnungen und Core-RPC calls aus.  
					// Total value output, Fee, Virtual-size, Fee-Rate wird berechnet. Virtual-Size löst zusätzlich einen RPC-Call zum Bitcon Core aus.
					txt_outValue[i].getDocument().addDocumentListener(new DocumentListener() 
					{
						@Override
						public void insertUpdate(DocumentEvent e) {valueOutVerify(); TxBuildAction.verifyOutputsAndCalcFeeRate();}
						public void removeUpdate(DocumentEvent e) {valueOutVerify(); TxBuildAction.verifyOutputsAndCalcFeeRate();}
						public void changedUpdate(DocumentEvent e){valueOutVerify(); TxBuildAction.verifyOutputsAndCalcFeeRate();}
					});				
				}					
				splitPane.validate();
				TxBuildAction.verifyOutputsAndCalcFeeRate();
			}
			else {cBox_outCount.setSelectedIndex(txt_outAdr.length-1);}
		}
	});		
	cBox_outCount.setSelectedIndex(0);
	
		

		

		
		
	
		
		
		
		
// ------------------------------------------------------------------------- Metohoden aus dem Menü: "Functions" ---------------------------------------------------------------------------		
		
		
	
	// Öffnet den Import-csv Datei Dialog
	mItem_import.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(dialogImport ==null) {dialogImport = new GUI_ImportCSV(getX()+245,getY()+55); dialogImport.setVisible(true);}
			else					{dialogImport.setLocation(getX()+245,getY()+55);         dialogImport.setVisible(true);}
		}
	});	
	
	
		
	
	// Öffnet den Tx-Publish Dialog
	mItem_publis.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI_PublishTx pTx = new GUI_PublishTx(getX()+10,getY()+40);
			pTx.setVisible(true);	
		}
	});	
	
	
	// Öffnet den JFrame mit dem Linien-Diagramm der Fee-Rate
	mItem_feeRate.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI_FeeChart feeChart = new GUI_FeeChart(getX()+245,getY()+55);
			feeChart.setVisible(true);	
			GUI.mItem_feeRate.setEnabled(false);
			GUI.menu_Setting.setEnabled(false);
		}
	});	
	
	
	// Lädt eine Tx aus der Blockchain
	// Tx ID muss eingegeben werden.
	mItem_txLoad.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_meld.setText("");
				String str = JOptionPane.showInputDialog(frame,t.t("Input Transaction ID"),"Transaction ID",-1);	
				if(str==null || str.length()<=0) return;
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
				peer.setTimeOut(Integer.parseInt(GUI_CoreSettings.txt_timeOut.getText()));
				JSONObject jo = peer.getrawtransaction(str);	
				if( (jo.optJSONObject("error"))!=null ) 
				{
					if((jo.getJSONObject("error").getInt("code") == -5))
					{	
						// Wenn Bitcoin Error "code":-5, auftritt, und der Hash stimmt, dann fehlt dem Core die Datenbank "-txindex" Dadurch kann er keine Transaktionen anhand dem Tx-Hash finden!
						// Aber er kann sie finden, wenn der Blockhash mit angegeben wird. 
						// Also werden in dem Fall einfach die letzen 100 oder 1000 Blöcke durchsucht.					
						lbl_progress.setVisible(true);
						frame.setEnabled(false);
						txt_meld.setForeground(Color.red);
						txt_meld.setText(countSearchBlocks+" blocks are searched! Please wait!");
						// @Thread Alle GUI-Anpassungen sind in der EDT (mit Außnahme der Exception)
						Thread t = new Thread(new Runnable() 
						{
							public void run() 
							{
								try
								{							
									JSONObject jo1 = new JSONObject(peer.get("getblockcount", null));											// getblockcount = Gibt die letze Blocknummer an.
									int last_blockNr = jo1.getInt("result");
									for(int i=0;i<countSearchBlocks;i++)																		// Anzahl der suchschleifen in denen Blöcke zurück gesucht werden
									{
										String last_blockHash =   peer.getblockhash(last_blockNr).getString("result");							// getblockhash "Block-Nummer" Zeigt den Blockhash mit dieser nummer an							
										JSONObject jo2 = peer.getrawtransaction(str, false, last_blockHash);
										if( (jo2.optJSONObject("error"))==null )
										{
											Transaktion sigTx = new Transaktion(Convert.hexStringToByteArray(jo2.getString("result")),0); 
											byte[] magic;
											if(btn_testNet.isSelected()) magic = TESTNET3;
											else						 magic = MAINNET;											
											SwingUtilities.invokeLater(new Runnable() 
											{
												public void run()
												{
													TxPrinter tx = new TxPrinter(magic, sigTx, getX()+5, getY()+30);
													tx.setModal(false);
													tx.setVisible(true);
													txt_meld.setText("");
												}
											});
											break;
										}
										last_blockNr--;
										if(i==countSearchBlocks-1)
										{
											String str = jo2.getJSONObject("error").toString(1);	
											SwingUtilities.invokeLater(new Runnable() 
											{
												public void run()
												{
													txt_meld.setText(str);	
												}
											});
										}
									}
								}
								catch(Exception e){txt_meld.setForeground(Color.red); txt_meld.setText(e.getMessage());}			
								SwingUtilities.invokeLater(new Runnable() 
								{
									public void run()
									{
										frame.setEnabled(true);
										lbl_progress.setVisible(false);	
									}
								});
							}
						});
						t.start();
					}
					else
					{
						throw new Exception(jo.getJSONObject("error").toString(1));													// Bei irgendeinem anderem Bitcoin Fehler
					}	
				}
				else
				{
					Transaktion sigTx = new Transaktion(Convert.hexStringToByteArray(jo.getString("result")),0); 
					byte[] magic;
					if(btn_testNet.isSelected()) magic = TESTNET3;
					else						 magic = MAINNET;
					TxPrinter tx = new TxPrinter(magic, sigTx, getX()+5, getY()+30);
					tx.setModal(false);
					tx.setVisible(true);
					System.out.println(peer.decoderawtransaction(jo.getString("result")).toString(1));
				}	
			} 
			catch (Exception e1) {txt_meld.setForeground(Color.red); txt_meld.setText(e1.getMessage());}		
		}
	});	
	
	
	
	// Konvertiert die TX in ein JSON Format und gibt sie aus
	mItem_txJSON.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_meld.setText("");
				String str = JOptionPane.showInputDialog(frame,t.t("Convert transaction to JSON format\nInput RAW transaction:"),"Transaction to JSON",-1);	
				if(str==null || str.length()<=0) return;
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
				peer.setTimeOut(Integer.parseInt(GUI_CoreSettings.txt_timeOut.getText()));
				JSONObject jo = peer.decoderawtransaction(str);	
				if( (jo.optJSONObject("error"))!=null )	throw new Exception(jo.getJSONObject("error").toString(1));		// Wenn Error BitcoinCore
				else
				{
					String erg 		= jo.getJSONObject("result").toString(5);
					JDialog di 		= new JDialog();
					JScrollPane	sp	= new JScrollPane();
					JTextArea txt 	= new JTextArea();
					di.setTitle("TxID:  "+jo.getJSONObject("result").getString("txid"));
					di.setBounds(frame.getX()+5, frame.getY()+5, 1100, 800);
					txt.setEditable(false);
					txt.setText(erg);
					sp.setViewportView(txt);
					di.getContentPane().add(sp);
					di.setVisible(true);	
				}	
			} 
			catch (Exception e1) {txt_meld.setForeground(Color.red); txt_meld.setText(e1.getMessage()); e1.printStackTrace();}		
		}
	});			
	
	
	
	// Erstellt einen QR-Code aus beliebigen Daten.
	mItem_QRShow.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				txt_meld.setText("");
				String str = (String) JOptionPane.showInputDialog(frame,t.t("Creates a QR code from any text."),t.t("show QR code"),-1, MyIcons.qrCode42x42, null, null);	
				if(str==null || str.length()<=0) return;
				QRCodeZXING.printQR_on_JDialog(str, str, GUI.color1, Color.black, GUI.frame.getX()+250, GUI.frame.getY()+5);
			} 
			catch (Exception e1)  {txt_meld.setForeground(Color.red); txt_meld.setText(e1.getMessage()); e1.printStackTrace();}	
		}
	});
	
	
	
	// Berechnet den SigHash für jeden Input aus einer Transaktion
	mItem_sigHash.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_meld.setText("");
				String txRaw = JOptionPane.showInputDialog(frame,t.t("Calculate Signature Hash\nInput RAW transaction:"),"Calc SigHash",-1);	
				if(txRaw==null || txRaw.length()<=0) return;
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC core = new ConnectRPC(ip, port, name, pw);
				core.setTimeOut(Integer.parseInt(GUI_CoreSettings.txt_timeOut.getText()));
				byte[] b 		= Convert.hexStringToByteArray(txRaw);
				Transaktion tx 	= new Transaktion(b,0);
				byte[][] prevHash = tx.getTxPrevHash();
				int[] prevIndex	= tx.getTxPrevIndex();
				String erg 		= "";
				for(int i=0;i<tx.getTxInCount();i++)
				{
					JSONObject jo = core.getrawtransaction(Convert.byteArrayToHexString(prevHash[i]));
					byte[] prev_b = Convert.hexStringToByteArray(jo.getString("result"));
					Transaktion txPrev = new Transaktion(prev_b, 0);
					byte[] pk 	= txPrev.getPkScript()[prevIndex[i]];
					byte[] value= txPrev.getValueRaw()[prevIndex[i]];
					byte[] sigh = tx.getSigHash(pk, value, i);
					erg = erg + "SigHash " + i + "  =  " + Convert.byteArrayToHexString(sigh) + "\n";
				}
				JDialog di 		= new JDialog();
				JScrollPane	sp	= new JScrollPane();
				JTextArea txt 	= new JTextArea();
				di.setTitle("SigHash");
				di.setBounds(frame.getX()+200, frame.getY()+5, 600, 500);
				txt.setEditable(false);
				txt.setText(erg);
				sp.setViewportView(txt);
				di.getContentPane().add(sp);
				di.setVisible(true);		
			} 
			catch (Exception e1) {txt_meld.setForeground(Color.red); txt_meld.setText(e1.getMessage()); e1.printStackTrace();}		
		}
	});			
		
		
		
		
		
		
	
		
		
		
// ------------------------------------------------------------------------------------ Methoden aus dem "Settings" Menü --------------------------------------------------------------------------------------	

	
		
		
		
	// Öffnet den Core-Settings Dialog
	mItem_corSet.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			dialogCoreSettings = new GUI_CoreSettings(getX()+340,getY()+55);
			dialogCoreSettings.setVisible(true);	
		}
	});	

	
	
	// Öffnet den Transaktions-Settings Dialog
	mItem_txSet.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(dialogTxSettings ==null) {dialogTxSettings = new GUI_TxSettings(getX()+340,getY()+55); dialogTxSettings.setVisible(true);}
			else						{dialogTxSettings.setLocation(getX()+340,getY()+55);          dialogTxSettings.setVisible(true);}
		}
	});	

	
	
	// Öffnet den Fee-Settings Dialog
	mItem_feeSet.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI_FeeSettings fs = new GUI_FeeSettings(getX()+340,getY()+55);
			fs.setVisible(true);	
		}
	});	
	
	
	
	
	
	
// -------------------------------------------------------------------------------- sonnstige Listeners -----------------------------------------------------------------------
	
	
	
	
	
	// Veraltete! Wird verwendet wenn TestNet manuell eingestellt werden soll.
	// Wenn TestNet eingestellt, wird die Hintergrundfarbe grün.
//	btn_testNet.addActionListener(new ActionListener() 
//	{
//		public void actionPerformed(ActionEvent e) 
//		{	
//			if(btn_testNet.isSelected())	
//			{
//				color1 = new Color(245,255,233);	frame.setIconImage(MyIcons.bitcoinLogoTest.getImage());
//				lbl_info		.setText(t.t("This is a test network. Coins have no value!")); 
//				lbl_info.setForeground(Color.red);
//			}		
//			else 							
//			{
//				color1 = new Color(255,244,230);  	frame.setIconImage(MyIcons.bitcoinLogoMain.getImage());
//				lbl_info		.setText(t.t("Create an unsigned transaction"));
//				lbl_info.setForeground(color4);
//			}
//			UIManager.put("Panel.background", 		color1);
//			UIManager.put("OptionPane.background", 	color1);
//			UIManager.put("TextField.background", 	color1);
//			menuBar			.setBackground(color6);
//			cBox_inCount	.setBackground(color6);
//			cBox_outCount	.setBackground(color6);
//			cBox_language	.setBackground(color6);
//			mItem_import	.setBackground(color6);
//			mItem_publis	.setBackground(color6);
//			mItem_feeRate	.setBackground(color6);
//			mItem_corSet	.setBackground(color6);
//			mItem_txSet		.setBackground(color6);
//			mItem_feeSet	.setBackground(color6);
//			mItem_txLoad	.setBackground(color6);
//			mItem_txJSON	.setBackground(color6);
//			mItem_sigHash	.setBackground(color6);
//			pnl_oben		.setBackground(color1);
//			pnl_unten		.setBackground(color1);
//			txt_meld		.setBackground(color1);
//			pnl_L			.setBackground(color1);
//			pnl_R			.setBackground(color1);
//			pnl_inOben		.setBackground(color1);
//			pnl_outOben		.setBackground(color1);
//			pnl_in			.setBackground(color1);
//			pnl_out			.setBackground(color1);
//			txt_fee			.setBackground(color1);
//			txt_feeRate		.setBackground(color1);
//			txt_txVSize		.setBackground(color1);
//			txt_totalValIn	.setBackground(color1);
//			txt_totalValOut	.setBackground(color1);
//			progressBar		.setBackground(color1);
//			txt_info		.setBackground(color1);
//			btn_testNet		.setBackground(color6);
//		}
//	});		
	
	
	// Wenn die Sprache geändert wird, werden alle Texte neu geschrieben.
	cBox_language.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			setAllTextInGUI();
		}
	});
	
	
	
	// Der Gebühren Slider (FeeSlider)		
	sliderFee.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent event) 
		{					
			double valueIn = Double.parseDouble(txt_totalValIn.getText().replace(",", "."));
			double feeRate = (double)sliderFee.getValue() / 100.0;
			double vSize   = Double.parseDouble(txt_txVSize.getText().replace(",", "."));
			double vOutOhneGelb = 0.0;
			int txOutCount 	= cBox_outCount.getSelectedIndex()+1;
			int outFeeIndex;
			if(GUI_FeeSettings.feeFrom.getSelectedIndex()==1) 	{outFeeIndex = txt_outValue.length-1;}
			else												{outFeeIndex = 0; }

			for(int i=0;i<txOutCount;i++)
			{
				if(i != outFeeIndex) vOutOhneGelb = vOutOhneGelb +  Double.parseDouble(txt_outValue[i].getText().replace(",", "."));	
			}
			double calcVOut = (valueIn - ((feeRate * vSize) / 100000000.0))  - vOutOhneGelb; 
			DecimalFormat df = new DecimalFormat("0000000.00000000");

			// Dieser Aufruf (Das schreiben in das Textfeld) muss zwingend threadsicher, verzögert aufgerufen werden!
			// Da das TextFeld Document-Listender enthält kommt es sonnst zum Abstzurz!
			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
		        public void run() 
		        {
		        	GUI.txt_outValue[outFeeIndex].setText(df.format(calcVOut));
		        }
			});
		}
	});	
	
	
	
	
	
	// Close Button wird abgefangen und hier selbst verarbeitet.
	addWindowListener(new java.awt.event.WindowAdapter() 
	{
	    @Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
	    {
	    	Config.save();
	    	Animated.close();
			try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}	
	    	System.exit(0);		
	    }
	});
}
	

	
	
	
	
	
// ----------------------------------------------------------------------------------------------- Ende GUI      --------------------------------------------------------------------------------------------------------	
// ----------------------------------------------------------------------------------------------- Hilfsmethoden --------------------------------------------------------------------------------------------------------

	
	
/**	Wird aufgerufen wenn das Netzwerk geändert oder initialisiert wird.
	Also wenn Main-Net oder Nets-Net ausgewählt wird, werden alle Anpassungen hier vorgenommen.	**/
	public static void initNetwork() 
	{	
		if(btn_testNet.isSelected())	
		{
			color1 = new Color(245,255,233);	frame.setIconImage(MyIcons.bitcoinLogoTest.getImage());
			lbl_info		.setText(t.t("This is a test network. Coins have no value!")); 
			lbl_info.setForeground(Color.red);
		}		
		else 							
		{
			color1 = new Color(255,244,230);  	frame.setIconImage(MyIcons.bitcoinLogoMain.getImage());
			lbl_info		.setText(t.t("Create an unsigned transaction"));
			lbl_info.setForeground(color4);
		}
		UIManager.put("Panel.background", 		color1);
		UIManager.put("OptionPane.background", 	color1);
		UIManager.put("TextField.background", 	color1);
		menuBar			.setBackground(color6);
		cBox_inCount	.setBackground(color6);
		cBox_outCount	.setBackground(color6);
		cBox_language	.setBackground(color6);
		mItem_import	.setBackground(color6);
		mItem_publis	.setBackground(color6);
		mItem_feeRate	.setBackground(color6);
		mItem_corSet	.setBackground(color6);
		mItem_txSet		.setBackground(color6);
		mItem_feeSet	.setBackground(color6);
		mItem_txLoad	.setBackground(color6);
		mItem_txJSON	.setBackground(color6);
		mItem_QRShow	.setBackground(color6);
		mItem_sigHash	.setBackground(color6);
		pnl_oben		.setBackground(color1);
		pnl_unten		.setBackground(color1);
		txt_meld		.setBackground(color1);
		pnl_L			.setBackground(color1);
		pnl_R			.setBackground(color1);
		pnl_inOben		.setBackground(color1);
		pnl_outOben		.setBackground(color1);
		pnl_in			.setBackground(color1);
		pnl_out			.setBackground(color1);
		txt_fee			.setBackground(color1);
		txt_feeRate		.setBackground(color1);
		txt_txVSize		.setBackground(color1);
		txt_totalValIn	.setBackground(color1);
		txt_totalValOut	.setBackground(color1);
		progressBar		.setBackground(color1);
		txt_info		.setBackground(color1);
		btn_testNet		.setBackground(color6);
	}
	
	
	
	
	
	
	
	
	
	// Führt den QR-Scan der Bitcoin Adressen von allen Input und Autput Feldern durch und nimmt entsprechende Anpassungen an der GUI vor.
	// Beim QR-Code-Scan wird ein blockierender eigener Thread gestartet.
	// btn = der Button als JLabel, der das Scan-Ereignis auslöst.
	// txt = das Text-Feld in das die BTC-Adresse nach dem Scan eingetragen wird.
	private static void qrScan(JLabel btn, JTextField txt)
	{
		btn.setOpaque(true);
		btn.setBackground(Color.gray);
		// @Thread alle GUI Anpassungen werden Threadsicher in der EDT ausgeführt!
		Thread t = new Thread(new Runnable() 
		{
			public void run() 
			{														
				try
				{
					Thread.sleep(100);				
					SwingUtilities.invokeLater(new Runnable() 
					{
						public void run()
						{
							btn.setOpaque(false);
							btn.setBackground(GUI.color1);
							txt_meld.setText("");
							frame.setEnabled(false);						
						}
					});
					QrCapture qr = new QrCapture(null,"Scan address", btn.getLocationOnScreen().x, btn.getLocationOnScreen().y);	
					String p2 = qr.getResult();
					qr.close();		
					if(p2.equals("")) throw new IOException(GUI.t.t("User abort"));								
					SwingUtilities.invokeLater(new Runnable() 
					{
						public void run()
						{
							txt.setText(p2);
							frame.setEnabled(true);
						}
					});
				}
				catch(Exception ex) 
				{ 
					SwingUtilities.invokeLater(new Runnable() 
					{
						public void run()
						{
							txt_meld.setForeground(Color.red); 
							txt_meld.setText(ex.getMessage());
							frame.setEnabled(true);
						}
					});
				};	
			}
		});
		if(btn.isEnabled()) t.start();	
	}
	
	
	

	

	
	
	// Diese Methode wird vom GUI-Thread aufgerufen, wenn ein Button betätigt wird, der die finale Transaktion aufrufen möchte.
	// Als erstes werden die FeeSettings geladen!
	// Prüft ob alle eingaben plausibel sind und gibt ggf. eine Error-Message am Bildschirm aus.
	// Prüft ob die Gebühren-Rate im erlaubtem Bereich ist und lehnt die Tx-Erstellung ggf. ab.
	// Nur wenn die Prüfung fehlerfrei durchlaufen wird, wird true zurück gegeben. Und die Transaktion kann ausgegeben werden.
	public static boolean txVerify()
	{
		try
		{
			GUI_FeeSettings.loadData();
			if(txt_inAdr[0].getText().length()==0)									{ JOptionPane.showMessageDialog(frame, t.t("No input address"), "Error", 0);  return false; };
			if(txt_outAdr[0].getText().length()==0)									{ JOptionPane.showMessageDialog(frame, t.t("No output address"), "Error", 0);  return false; };
			if(txt_totalValIn.getText().equals("")) 								{ JOptionPane.showMessageDialog(frame, t.t("Total Value Input = Null"), "Error", 0);  return false;   }
			if(txt_fee.getText().equals("")) 										{ JOptionPane.showMessageDialog(frame, t.t("fees = Null"), "Error", 0);  return false; }
			try { Double.parseDouble(txt_fee.getText().replace(",", "."));}
			catch(Exception e) 														{ JOptionPane.showMessageDialog(frame, t.t("fees = Null"), "Error", 0);  return false; };
			double fees = Double.parseDouble(txt_fee.getText().replace(",", "."));
			double feeRate = Double.parseDouble(txt_feeRate.getText().replace(",", "."));
			if(fees <= 0) 	{ JOptionPane.showMessageDialog(frame, t.t("Fees must be greater than 0!"), "Error", 0);  return false; };														
			double acceptMaxFeeRate = Double.parseDouble(GUI_FeeSettings.lbl_accMax.getText().replace(",", "."));
			double acceptMinFeeRate = Double.parseDouble(GUI_FeeSettings.lbl_accMin.getText().replace(",", "."));

			// Wenn die Feerate den Maximal zulässigen Bereich überschreitet, wird abgelehnt.
			if(feeRate > acceptMaxFeeRate)
			{
				JOptionPane.showMessageDialog(frame, t.t("FeeRate exceeds the maximum allowable amount.\nMaximum FeeRate = ")+acceptMaxFeeRate+t.t(" sat/vB \nTransaction will not be created!\nIn the FeeSettings you can set this."), t.t("Rejection"), 0);
				return false;
			}
			
			// Wenn die Feerate den Minimal zulässigen Bereich unterschreitet, wird abgelehnt.
			if(feeRate < acceptMinFeeRate)
			{
				JOptionPane.showMessageDialog(frame, t.t("FeeRate falls below the minimum allowed amount.\nMinimum FeeRate = ")+acceptMinFeeRate+t.t(" sat/vB \nTransaction will not be created!\nIn the FeeSettings you can set this."), t.t("Rejection"), 0);
				return false;
			}
			
			if(feeRate < GUI_FeeSettings.memMinFeeRate)														
			{ 
				int m = JOptionPane.showConfirmDialog(frame, t.t("The FeeRate is less than the network minimum FeeRate. \nMinimum FeeRate = ")+GUI_FeeSettings.memMinFeeRate+t.t(" sat/vB\nIgnore?"));
				if(m!=0) return false;
			}
			
			if(feeRate > GUI_FeeSettings.estimaFeeRateHigh)
			{
				int m = JOptionPane.showConfirmDialog(frame, t.t("The FeeRate is disproportionately high!\nHighest estimated FeeRate for confirmation in the next block is: ")+GUI_FeeSettings.estimaFeeRateHigh+t.t(" sat/vB\nIgnore?"));
				if(m!=0) return false;
			}
			
			if(fees >= 0.001)
			{
				int m = JOptionPane.showConfirmDialog(frame, t.t("The Fees is higher than 0,001 BTC \nIgnore?"));
				if(m!=0) return false;
			}
			if(TxBuildAction.coreTxOutSet==null)  { JOptionPane.showMessageDialog(frame, t.t("Tx input has been changed or is empty.\n Load Tx-Inputs from Blockchain!"), "Error", 0);  return false; }
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			txt_meld.setForeground(Color.red);
			txt_meld.setText(e.getMessage());
			return false;
		}
	}
	
	
	
	
	
	// Diese Methode wird von allen Value-Out Feldern bei jeder Änderung im Feld ausgeführt (also auch während des Tippens). Und nach dem Scan (von einem anderem Thread).
	// Es wird dann der Gesamtbetrag und die Fees berechnet und in die Felder eingetragen.
	// Es wird eine plausibilität überprüft und die Schriftfarbe geändert. 
	// Nur wenn keine Fehler auftreten, wird überhaupt eine Ausgabe erzeugt.
	// Exceptions werden ignoriert und führen immer nur zur Ausgabe von roten Strichen "---   ---   ---".
	// Grund ist, das vor und während der Eingabe die Felder undefiniert sind.
	// Dieses ungewöhnliche Exception-Handling soll 1.falsche Ausgaben in diesen Feldern unter allen Umständen verhindern, und 2.auch nicht zu einem Abstzurz des Threads führen.
	public static void valueOutVerify()
	{
		//System.out.println("valueOutVerify() ausgelöst. Ist in EDT: "+SwingUtilities.isEventDispatchThread());	
		try
		{
			double valueOut = 0.0;
			int txOutCount 	= cBox_outCount.getSelectedIndex()+1;
			for(int i=0;i<txOutCount;i++)
			{
				valueOut = valueOut +  Double.parseDouble(txt_outValue[i].getText().replace(",", "."));	
			}
			valueOut = Math.round(valueOut*100000000.0)/100000000.0;
			double valueIn = 0.0;
			if(txt_totalValIn.getText().equals("")==false) valueIn = Double.parseDouble(txt_totalValIn.getText().replace(",", "."));
			txt_totalValOut	.setText(String.format("%.8f", valueOut));
			double fees = valueIn - valueOut;
			txt_fee			.setText(String.format("%.8f", fees));
			if(valueIn > valueOut) 	txt_totalValOut.setForeground(Color.black);
			else					txt_totalValOut.setForeground(Color.red);
		}
		catch(Exception e) 
		{
			txt_totalValOut.setForeground(Color.red);
			txt_totalValOut.setText("---   ---   ---");
			txt_fee.setText("---   ---   ---");			
		};
	}	
	
	
	
	// Prüft die Bedingungen für den Fee-Slider und Aktiviert Ihn
	public static void feeSliderActivated()
	{
		// System.out.println("feeSliderActivated()          ausgelöst.");
		try
		{
			if(Convert.isNumeric(txt_totalValIn.getText()) == false){sliderFee.setVisible(false); return;}
			if(Convert.isNumeric(txt_txVSize.getText()) == false) 	{sliderFee.setVisible(false); return;}
			GUI_FeeSettings.loadData();
			double dmax = Double.parseDouble(GUI_FeeSettings.lbl_sliMax.getText().replaceAll(",", "."))*100;
			double dmin = Double.parseDouble(GUI_FeeSettings.lbl_sliMin.getText().replaceAll(",", "."))*100;
			sliderFee.setMaximum((int) dmax);
			sliderFee.setMinimum((int) dmin);						
			int outFeeIndex = 0;
			sliderFee.setVisible(true);
			if(GUI_FeeSettings.feeFrom.getSelectedIndex()==1) 	{outFeeIndex = txt_outValue.length-1; txt_outValue[0].setBackground(Color.white);}
			else												{outFeeIndex = 0; txt_outValue[txt_outValue.length-1].setBackground(Color.white);}
			GUI.txt_outValue[outFeeIndex].setBackground(color5);	
		}
		catch(Exception e1){txt_meld.setForeground(Color.red); txt_meld.setText(t.t("Average Fee Rate could not be loaded!\n") + e1.getMessage()); e1.printStackTrace();}
	}
	
	
	
	// Gibt true zurück, wenn alle Input-Adressen mindestens 20 Zeichen enthalten.
	// Wird für die Animation verwendet. Dann startet die Animation des Buttens btn_LoadTx
	private static boolean allInputFieldsEdited()
	{
		for(int i=0; i<txt_inAdr.length; i++)
		{
			if(txt_inAdr[i].getText().length()<20) return false;
		}
		return true;
	}

}