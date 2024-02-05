package TxBuild;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import org.json.JSONObject;

import BTClib3001.Convert;
import BTClib3001.Transaktion;
import BTClib3001.TxPrinter;
import RPC.ConnectRPC;



/***********************************************************************************************************************************************
*				   											 Autor: Mr. Maxwell   							vom 30.01.2024						*
*	Die GUI für den TxBuilder																													*
*	Achtung eclipse Windwos-Builer kann die Zeile (String infoText) nicht anzeigen. Zum verwendes des Windows Builder hier temporär ausblenden!	*
*************************************************************************************************************************************************/



public class GUI extends JFrame 
{


	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					frame = new GUI();
					Config.load();
					frame.setVisible(true);
					frame.setBounds(posX, posY, 1060, 500);
					btn_testNet.doClick();
					btn_testNet.doClick();
					TxBuildAction.startGuiHandler();
				} 
				catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	
	
	final static String			progName		= "TxBuilder";									// Program Name		
	final static String			version 		= "V1.0.7";										// Version der Anwendung
	final static String			autor 			= "Mr. Maxwell";								// Name Autor
	public static GUI 			frame;															// Der Frame dieser GUI
	public static JPanel 	 	pnl_in			= new JPanel();									// Haupt Input  Panel
	public static JPanel 	 	pnl_out 		= new JPanel();									// Haupt Output Panel
	public static JTextField 	txt_totalValIn	= new JTextField();								// Gesamter Eingangs-Betrag
	public static JTextField 	txt_totalValOut	= new JTextField();								// Gesamter Ausgangs-Betrag
	public static JTextField 	txt_fee 		= new JTextField();								// Netzwerk Gebühren
	public static JTextArea  	txt_meld 		= new JTextArea();								// Alle Meldungen und Fehler
	public static JTextField[]	txt_inAdr;														// Input Adressen   Array
	public static JTextField[]	txt_inValue;													// Input BTC-Betrag Array
	public static JTextField[]	txt_outAdr;														// Output Adressen   Array
	public static JFormattedTextField[]	txt_outValue;											// Output BTC-Betrag Array
	public static JProgressBar 	progressBar 	= new JProgressBar();							// Wartebalklen unten, wenn der Core die Transaktionen sucht.
	public static JComboBox 	cBox_inCount	= new JComboBox();								// Auswahlmenü der Anzahl der Eingänge
	public static JComboBox 	cBox_outCount	= new JComboBox();								// Auswahlmenü der Anzahl der Ausgänge
	public static JLabel[]		btn_inQR;														// Wird als Butten verwendet. Buttons zum scannen des QR-Codes für die Input-Adressen	
	public static JLabel[]		btn_outQR;														// Wird als Butten verwendet. Buttons zum scannen des QR-Codes für die Output-Adressen	
	public static JLabel 		btn_loadTx 		= new JLabel("load Tx-inputs from Blockchain");	// Der Button startet die Tx-In suche des Core´s
	public static JLabel 		btn_cancel 		= new JLabel("cancel loading from Blockchain");	// Stoppt die Tx-In suche des Core´s
	public static JLabel 		lbl_viewTx 		= new JLabel("View Transaction");				// Wird als Button benutzt und lässt die fertige Transaktion anzeigen. (TxPrinter)
	public static JLabel 		lbl_viewTxHex	= new JLabel("View Tx Raw");					// Wird als Button benutzt und lässt die fertige Transaktion anzeigen. (Hex)
	public static JLabel 		lbl_qrCode 		= new JLabel("QR-Code");						// Wird als Button benutzt und lässt die fertige Transaktion anzeigen. (als QR-Code)
	public static JLabel 		lbl_save 		= new JLabel("Save Transaction");				// Save Button der die Tx speichert
	public static JCheckBox 	btn_testNet 	= new JCheckBox("TestNet3");					// Button im Settings-Menü. Wenn ausgewählt, dann TestNet, sonst Main-Net
	public static int 			posX = 0;
	public static int 			posY = 0;               	
	public static String[] 		comboBoxList 	= new String[100];								// Die beiden Combo-Boxen für die In- und Outputs werden mit diesen Elementen initialisiert

	public static Color color1 	= new Color(255,244,230); 								// Farbe Hintergrund
	public static Color color3 	= new Color(120,120,120); 								// Farbe Text grau Feldbeschreibungen
	public static Color color4 	= new Color(247, 147, 26); 								// Farbe Linien (BTC-Farbe)
	public static Font	font1	= new Font("SansSerif",   Font.PLAIN, 9); 				// Font für Rahmenbeschriftung klein
	public static Font	font2	= new Font("SansSerif",   Font.PLAIN, 11); 				// Font für Rahmenbeschriftung normal
	public static Font	font3	= new Font("SansSerif",   Font.PLAIN, 14); 				// Font für Rahmenbeschriftung groß
	public static Font	font4	= new Font("DialogInput", Font.PLAIN, 12); 				// Font für Textfelder
	public static Font	font5	= new Font("SansSerif",   Font.PLAIN, 16); 				// Font für Beschreibung, sehr groß

	public static GUI_TxSettings dialogTxSettings;										// Das Transaktions-Settings Menü-Fenster (Muss hier inizialisiert werden!)
	public static GUI_ImportCSV  dialogImport;											// Der Import Dialog aus dem oberen Menü

	public final static byte[] MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	public final static byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};	
	
	
	public GUI() 
	{		
		JMenuBar 	menuBar 	= new JMenuBar();										// Hauptmenüleiste ganz oben
		JMenu 		menu_info 	= new JMenu("Info");									// Information über dieses Programm
		JMenu 		menu_Setting= new JMenu("Settings");								// Das Settings Menü
		JMenu		menu_functio= new JMenu("Functions");									// Import-Menü
		JMenuItem 	mItem_publis= new JMenuItem("Send signed transaction");				// Menüpunkt Publish Tx
		JMenuItem 	mItem_import= new JMenuItem("Import BTCAddressList.csv");			// Zum Importieren einer großen Menge von Bitcoin-Adressen als CSV-Datei. Um Verfügbare Betrage zu finden.
		JMenuItem 	mItem_corSet= new JMenuItem("BitcoinCore connection settings");		// Menüpunkt CoreSettings
		JMenuItem 	mItem_txSet	= new JMenuItem("Transaction settings");				// Menüpunkt Transaktions-Settings
		JMenuItem 	mItem_txLoad= new JMenuItem("Get transaction from blockchain");		// zum Laden einer Transaktion aus der Blockchain
		JMenuItem 	mItem_txJSON= new JMenuItem("Get JSON from transacion");					// Konvertiert die Transaktion in ein JSON-Format
		JMenuItem 	mItem_sigHash= new JMenuItem("Get Signature Hash");					// Berechnet den Signature-Hash einer Transaktion

		
		JPanel 		contentPane = new JPanel();
		JPanel 		pnl_oben 	= new JPanel();
		JPanel 		pnl_inOben 	= new JPanel();
		JPanel 		pnl_outOben = new JPanel();
		JPanel 		pnl_L 		= new JPanel();
		JPanel 		pnl_R 		= new JPanel();
		JPanel 		pnl_meld 	= new JPanel();
		JSplitPane 	splitPane 	= new JSplitPane();
		JScrollPane scroll_in 	= new JScrollPane();
		JScrollPane scroll_out 	= new JScrollPane();
		JTextPane	lbl_beschr 	= new JTextPane();
		JTextPane 	txt_info 	= new JTextPane();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(progName+"     Version: "+version);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(GUI.posX > screenSize.width-855 || GUI.posX < 0)  GUI.posX=0;
		if(GUI.posY > screenSize.height-340|| GUI.posY < 0) GUI.posY=0;			
		setBounds(posX, posY, 1250, 500);
		setJMenuBar(menuBar);
		setContentPane(contentPane);	
		UIManager.put("OptionPane.background", 	color1);
		UIManager.put("Panel.background", 		color1);
		UIManager.put("TextField.background", 	color1);

		for(int i=0;i<100;i++) {comboBoxList[i]=String.valueOf(i+1);}
		cBox_inCount	.setModel(new DefaultComboBoxModel(comboBoxList));	
		cBox_outCount	.setModel(new DefaultComboBoxModel(comboBoxList));
		
		menu_functio	.setFont(font3); 
		menu_Setting	.setFont(font3);
		menu_info		.setFont(font3);
		lbl_viewTx		.setFont(font2);
		lbl_viewTxHex	.setFont(font2);
		lbl_qrCode		.setFont(font2);
		lbl_save		.setFont(font2);
		lbl_beschr		.setFont(font5);
		txt_totalValIn	.setFont(font4);
		txt_totalValOut	.setFont(font4);
		txt_fee			.setFont(font4);

		cBox_inCount	.setForeground(color3);
		cBox_outCount	.setForeground(color3);
		menu_functio	.setForeground(color3);
		menu_Setting	.setForeground(color3);
		menu_info		.setForeground(color3);
		btn_loadTx		.setForeground(color3);
		btn_cancel		.setForeground(color3);
		txt_meld		.setForeground(Color.red);
		progressBar		.setForeground(color4);
		lbl_viewTx		.setForeground(color3);
		lbl_beschr		.setForeground(color4);
		lbl_viewTxHex	.setForeground(color3);
		lbl_qrCode		.setForeground(color3);
		lbl_save		.setForeground(color3);
		menuBar			.setBackground(color1);
		cBox_inCount	.setBackground(color1);
		cBox_outCount	.setBackground(color1);
		mItem_import	.setBackground(color1);
		mItem_publis	.setBackground(color1);
		mItem_corSet	.setBackground(color1);
		mItem_txSet		.setBackground(color1);
		mItem_txLoad	.setBackground(color1);
		mItem_txJSON	.setBackground(color1);
		mItem_sigHash	.setBackground(color1);

		progressBar		.setBackground(color1);
		lbl_beschr		.setBackground(color1);	
		btn_testNet		.setBackground(color1);
		
		menuBar			.setBorder(new LineBorder(color4));
		cBox_inCount	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Input count",  					TitledBorder.LEADING, 	TitledBorder.TOP, 		font3, color3));
		cBox_outCount	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Output count", 					TitledBorder.LEADING, 	TitledBorder.TOP, 		font3, color3));
		pnl_L			.setBorder(new TitledBorder(new LineBorder(color4), 	"Source address   (Tx-Input)", 		TitledBorder.LEFT, 		TitledBorder.ABOVE_TOP, font3, color3));
		pnl_R			.setBorder(new TitledBorder(new LineBorder(color4), 	"Destination address   (Tx-Output)",TitledBorder.LEFT, 		TitledBorder.ABOVE_TOP, font3, color3));
		txt_totalValIn	.setBorder(new TitledBorder(new LineBorder(color4), 	"Total Value Input",				TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_totalValOut	.setBorder(new TitledBorder(new LineBorder(color4), 	"Total Value Output",				TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_fee			.setBorder(new TitledBorder(new LineBorder(color4), 	"fees", 							TitledBorder.LEADING, 	TitledBorder.TOP, 		font2, color3));
		txt_meld		.setBorder(new EmptyBorder(8, 8, 8, 8));
		splitPane		.setBorder(null);
		scroll_in		.setBorder(null);
		scroll_out		.setBorder(null);	
		
		lbl_viewTx		.setIcon(new ImageIcon("icons/viewTx.png"));
		lbl_qrCode		.setIcon(new ImageIcon("icons/qrCode.png"));
		lbl_viewTxHex	.setIcon(new ImageIcon("icons/hex.png"));
		lbl_save		.setIcon(new ImageIcon("icons/save.png"));
		btn_loadTx		.setIcon(new ImageIcon("icons/load.png"));
		btn_cancel		.setIcon(new ImageIcon("icons/cancel.png"));
		
		txt_info		.setText(GUI_InfoText.infoText);									// <<<<<<<<<<< Achtung eclipse Windwos-Builer kann diese Zeile nicht anzeigen. Zum verwendes des Windows Builder hier temporär ausblenden!
		lbl_beschr		.setText("Here you can create an unsigned transaction");

		cBox_inCount	.setMaximumSize(new Dimension(110, 32767));
		cBox_outCount	.setMaximumSize(new Dimension(110, 32767));
		btn_testNet		.setMaximumSize(new Dimension(2000, 23));

		txt_totalValIn	.setColumns(19);
		txt_totalValOut	.setColumns(19);
		txt_fee			.setColumns(19);

		contentPane		.setLayout(new BorderLayout(0, 0));
		pnl_oben		.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		pnl_inOben		.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnl_outOben		.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnl_L			.setLayout(new BorderLayout(0, 0));
		pnl_R			.setLayout(new BorderLayout(0, 0));
		pnl_out			.setLayout(new BoxLayout(pnl_out, BoxLayout.Y_AXIS));
		pnl_meld		.setLayout(new BoxLayout(pnl_meld, BoxLayout.Y_AXIS));
	
		txt_totalValIn	.setHorizontalAlignment(SwingConstants.RIGHT);
		txt_totalValOut	.setHorizontalAlignment(SwingConstants.RIGHT);
		txt_fee			.setHorizontalAlignment(SwingConstants.RIGHT);
		menu_functio	.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		menu_Setting	.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		menu_info		.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		progressBar		.setStringPainted(true);
		
		pnl_oben		.setPreferredSize(new Dimension(10, 80));
		pnl_inOben		.setPreferredSize(new Dimension(10, 50));
		pnl_outOben		.setPreferredSize(new Dimension(10, 50));
		progressBar		.setPreferredSize(new Dimension(146, 13));

		progressBar		.setVisible(false);
		btn_cancel		.setVisible(false);
	
		txt_fee			.setEditable(false);
		txt_totalValIn	.setEditable(false);
		txt_totalValOut	.setEditable(false);
		txt_meld		.setEditable(false);
		lbl_beschr		.setEditable(false);
		
		cBox_inCount	.setFocusable(false);
		cBox_outCount	.setFocusable(false);
		btn_cancel		.setFocusable(false);
		
		splitPane		.setResizeWeight(0.5);
		splitPane		.setDividerSize(15);
		splitPane		.setLeftComponent(pnl_L);
		splitPane		.setRightComponent(pnl_R);
		splitPane		.setDividerLocation(610);
		scroll_in		.setViewportView(pnl_in);
		scroll_out		.setViewportView(pnl_out);

		menuBar		.add(cBox_inCount);
		menuBar		.add(cBox_outCount);	
		menuBar		.add(Box.createHorizontalStrut(20));
		menuBar		.add(menu_functio);
		menu_functio.add(mItem_import);
		menu_functio.add(mItem_publis);
		menu_functio.add(mItem_txLoad);
		menu_functio.add(mItem_txJSON);
		menu_functio.add(mItem_sigHash);
		menuBar		.add(Box.createHorizontalStrut(20));
		menuBar		.add(menu_Setting);
		menuBar		.add(Box.createHorizontalStrut(20));
		menuBar		.add(menu_info);
		menu_info	.add(txt_info);
		menu_Setting.add(mItem_corSet);
		menu_Setting.add(mItem_txSet);		
		menu_Setting.add(btn_testNet);
		contentPane	.add(pnl_oben, BorderLayout.NORTH);
		contentPane	.add(splitPane);
		pnl_oben	.add(lbl_beschr);
		pnl_oben	.add(Box.createHorizontalStrut(4000));
		pnl_oben	.add(Box.createHorizontalStrut(20));
		pnl_oben	.add(lbl_viewTx);
		pnl_oben	.add(Box.createHorizontalStrut(20));
		pnl_oben	.add(lbl_viewTxHex);
		pnl_oben	.add(Box.createHorizontalStrut(20));
		pnl_oben	.add(lbl_qrCode);
		pnl_oben	.add(Box.createHorizontalStrut(20));
		pnl_oben	.add(lbl_save);
		pnl_L		.add(scroll_in, BorderLayout.CENTER);
		pnl_L		.add(pnl_inOben, BorderLayout.NORTH);
		pnl_inOben	.add(btn_loadTx);
		pnl_inOben	.add(btn_cancel);
		pnl_inOben	.add(Box.createHorizontalStrut(80));
		pnl_inOben	.add(txt_totalValIn);
		pnl_R		.add(scroll_out, BorderLayout.CENTER);
		pnl_R		.add(pnl_outOben, BorderLayout.NORTH);
		pnl_outOben	.add(txt_fee);
		pnl_outOben	.add(txt_totalValOut);
		contentPane	.add(pnl_meld, BorderLayout.SOUTH);
		pnl_meld	.add(progressBar);
		pnl_meld	.add(txt_meld);

	
		
		TxBuildAction.actions();  				// Die Actions der Klasse TxBuildActions werden gestartet.

	
		
// ------------------------------------------------------------------------------------- TX In ------------------------------------------------------------------------------------------------------
		
		
		
	// Ändert die Anzahl der Eingänge
	cBox_inCount.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(isVisible() && txt_inAdr.length == cBox_inCount.getSelectedIndex()+1) return;   // Wenn keine Größenänderung eingestellt wurde, wird sofort hier abgebrochen.
			int m = 0;
			if(isVisible()) {m = JOptionPane.showConfirmDialog(contentPane, "Die Anzahl der Transaktions-Eingänge wird neu festgelegt.\n Diese Aktion löscht alle bisherigen Eingänge!");}			
			if(m==0)
			{
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
					btn_inQR[i].setIcon(new ImageIcon("icons/qrCode.png"));
					btn_inQR[i].setText(String.valueOf(i+1));  // Index wird hier als Text verwendet um mit Reflection den Index später im Thread zu erkennen. Und als Zeilen-Aufzählung
					btn_inQR[i].setHorizontalTextPosition(2);
					pnlA_in[i].add(btn_inQR[i]);
					
					txt_inAdr[i] = new JTextField();
					txt_inAdr[i].setFont(font4);
					txt_inAdr[i].setMaximumSize(new Dimension(400,40));
					txt_inAdr[i].setBorder(new TitledBorder(new LineBorder(color4), "BTC-Address", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_inAdr[i].setBackground(Color.white);
					pnlA_in[i].add(txt_inAdr[i]);
					
					txt_inValue[i] = new JTextField();
					txt_inValue[i].setFont(font4);
					txt_inValue[i].setBackground(color1);
					txt_inValue[i].setMaximumSize(new Dimension(165,40));
					txt_inValue[i].setHorizontalAlignment(SwingConstants.RIGHT);
					txt_inValue[i].setBorder(new TitledBorder(new LineBorder(color4), "Value", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_inValue[i].setEditable(false);
					txt_inValue[i].setOpaque(false);
					pnlA_in[i].add(txt_inValue[i]);
					
					// Scannt den QR-Code für die Input-Adressen
					btn_inQR[i].addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent e) 
						{
							int index = Integer.valueOf(((JLabel) e.getComponent()).getText())-1;	// Achtung: Hier wird Reflection verwendet um den den Schleifen-Index[i] zu erhalten.
							Thread t = new Thread(new Runnable() 
							{
								public void run() 
								{														
									try
									{
										txt_meld.setText("");
										frame.setEnabled(false);
										QrCapture qr = new QrCapture(null,"Scan input address: "+String.valueOf(index+1), frame.getX()+25, frame.getY()+190);	
										String p2 = qr.getResult();
										qr.close();								
										if(p2.equals("")) throw new IOException("User abort");								
										txt_inAdr[index].setText(p2);
										TxBuildAction.coreTxOutSet = null;
									}
									catch(Exception ex) {txt_meld.setText(ex.getMessage());};	
									frame.setEnabled(true);
								}
							});
							if(btn_inQR[index].isEnabled()) t.start();	
						}
					});
						
					// Löscht bei jeder Eingabe sofort das JSONObject "coreTxOutSet" in der Klasse TxBuildAction. Verhindert das veraltete Inputs verarbeitet werden können.
					txt_inAdr[i].addKeyListener(new KeyAdapter() 
					{
						public void keyReleased(KeyEvent e) 
						{
							TxBuildAction.coreTxOutSet = null;
						}
					});
				}					
				splitPane.validate();
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
			if(isVisible()) {m = JOptionPane.showConfirmDialog(contentPane, "Die Anzahl der Transaktions-Ausgänge wird neu festgelegt.\n Diese Aktion löscht alle bisherigen Ausgänge!");}			
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
					btn_outQR[i].setIcon(new ImageIcon("icons/qrCode.png"));
					btn_outQR[i].setText(String.valueOf(i+1));  // Index wird hier als Text verwendet um mit Reflection den Index später im Thread zu erkennen. Und als Zeilen-Aufzählung
					btn_outQR[i].setHorizontalTextPosition(2);
					pnlA_out[i].add(btn_outQR[i]);
									
					txt_outAdr[i] = new JTextField();
					txt_outAdr[i].setFont(font4);
					txt_outAdr[i].setMaximumSize(new Dimension(400,40));
					txt_outAdr[i].setBorder(new TitledBorder(new LineBorder(color4), "BTC-Address", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_outAdr[i].setBackground(Color.white);
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
					txt_outValue[i].setBorder(new TitledBorder(new LineBorder(color4), "Value", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
					txt_outValue[i].setBackground(Color.white);
					pnlA_out[i].add(txt_outValue[i]);
							
					// Scannt den QR-Code für die Output-Adressen
					btn_outQR[i].addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent e) 
						{
							int index = Integer.valueOf(((JLabel) e.getComponent()).getText())-1;	// Achtung: Hier wird Reflection verwendet um den den Schleifen-Index[i] zu erhalten.
							Thread t = new Thread(new Runnable() 
							{
								public void run() 
								{														
									try
									{
										txt_meld.setText("");
										frame.setEnabled(false);
										QrCapture qr = new QrCapture(null,"Scan output address: "+String.valueOf(index+1), frame.getX()+560, frame.getY()+190);	
										String p2 = qr.getResult();
										qr.close();								
										if(p2.equals("")) throw new IOException("User abort");								
										txt_outAdr[index].setText(p2);
									}
									catch(Exception ex) {txt_meld.setText(ex.getMessage());};	
									frame.setEnabled(true);
								}
							});
							t.start();	
						}
					});
						
					txt_outValue[i].addKeyListener(new KeyAdapter() 
					{
						public void keyReleased(KeyEvent e) 
						{
							valueOutVerify();
						}
					});	
				}					
				splitPane.validate();
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
			if(dialogImport ==null) {dialogImport = new GUI_ImportCSV(getX()+10,getY()+40); dialogImport.setVisible(true);}
			else					{dialogImport.setLocation(getX()+10,getY()+40);         dialogImport.setVisible(true);}
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
	
	
	
	// Lädt eine Tx aus der Blockchain
	// Tx ID muss eingegeben werden.
	mItem_txLoad.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_meld.setText("");
				String str = JOptionPane.showInputDialog(frame,"                                                           Input Transaction ID                                                           ","Transaction ID",-1);	
				if(str==null || str.length()<=0) return;
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
				peer.setTimeOut(5);
				JSONObject jo = peer.getrawtransaction(str);	
				if( (jo.optJSONObject("error"))!=null )	throw new Exception(jo.getJSONObject("error").toString(1));		// Wenn Error BitcoinCore
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
			catch (Exception e1) {txt_meld.setText(e1.getMessage());}		
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
				String str = JOptionPane.showInputDialog(frame,"Convert transaction to JSON format                                                                                                         \nInput RAW transaction:","Transaction to JSON",-1);	
				if(str==null || str.length()<=0) return;
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
				peer.setTimeOut(5);
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
					txt.setText(erg);
					sp.setViewportView(txt);
					di.add(sp);
					di.setVisible(true);	
				}	
			} 
			catch (Exception e1) {txt_meld.setText(e1.getMessage()); e1.printStackTrace();}		
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
				String txRaw = JOptionPane.showInputDialog(frame,"Calculate Signature Hash                                                                                                         \nInput RAW transaction:","Calc SigHash",-1);	
				if(txRaw==null || txRaw.length()<=0) return;
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC core = new ConnectRPC(ip, port, name, pw);
				core.setTimeOut(5);
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
				txt.setText(erg);
				sp.setViewportView(txt);
				di.add(sp);
				di.setVisible(true);		
			} 
			catch (Exception e1) {txt_meld.setText(e1.getMessage()); e1.printStackTrace();}		
		}
	});			
		
		
		
		
		
		
	
		
		
		
// ------------------------------------------------------------------------------------ Methoden aus dem "Settings" Menü --------------------------------------------------------------------------------------	

	
		
		
		
	// Öffnet den Core-Settings Dialog
	mItem_corSet.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI_CoreSettings cs = new GUI_CoreSettings(getX()+245,getY()+50);
			cs.setVisible(true);	
		}
	});	

	
	
	// Öffnet den Transaktions-Settings Dialog
	mItem_txSet.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(dialogTxSettings ==null) {dialogTxSettings = new GUI_TxSettings(getX()+10,getY()+40); dialogTxSettings.setVisible(true);}
			else						{dialogTxSettings.setLocation(getX()+10,getY()+40);              dialogTxSettings.setVisible(true);}
		}
	});	

	
	
	// Wenn TestNet eingestellt, wird die Hintergrundfarbe grün.
	btn_testNet.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{	
			if(btn_testNet.isSelected())	color1 = new Color(245,255,233);				
			else 							color1 = new Color(255,244,230);  
			UIManager.put("Panel.background", 		color1);
			UIManager.put("OptionPane.background", 	color1);
			UIManager.put("TextField.background", 	color1);
			menuBar			.setBackground(color1);
			cBox_inCount	.setBackground(color1);
			cBox_outCount	.setBackground(color1);
			mItem_import	.setBackground(color1);
			mItem_publis	.setBackground(color1);
			mItem_corSet	.setBackground(color1);
			mItem_txSet		.setBackground(color1);
			mItem_txLoad	.setBackground(color1);
			mItem_txJSON	.setBackground(color1);
			mItem_sigHash	.setBackground(color1);
			pnl_oben		.setBackground(color1);
			txt_meld		.setBackground(color1);
			pnl_L			.setBackground(color1);
			pnl_R			.setBackground(color1);
			pnl_inOben		.setBackground(color1);
			pnl_outOben		.setBackground(color1);
			pnl_in			.setBackground(color1);
			pnl_out			.setBackground(color1);
			txt_fee			.setBackground(color1);
			txt_totalValIn	.setBackground(color1);
			txt_totalValOut	.setBackground(color1);
			progressBar		.setBackground(color1);
			txt_info		.setBackground(color1);
			lbl_beschr		.setBackground(color1);	
			btn_testNet		.setBackground(color1);
		}
	});		
	
	
	
	// Close Button wird abgefangen und hier selbst verarbeitet.
	addWindowListener(new java.awt.event.WindowAdapter() 
	{
	    @Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
	    {
	    	TxBuildAction.runGuiHandler=false;
	    	Config.save();
			try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}	
	    	System.exit(0);
	    }
	});
			
}
	

	
	
	
	
	
	
// ----------------------------------------------------------------------------------------------- Hilfsmethoden --------------------------------------------------------------------------------------------------------

	

	
	
	// Diese Methode wird vom GUI-Thread aufgerufen, wenn ein Button betätigt wird, der die finale Transaktion aufrufen möchte.
	// Prüft ob alle eingaben plausibel sind und gibt ggf. eine Error-Message am Bildschirm aus.
	// nur wenn die Prüfung fehlerfrei durchlaufen wird, wird true zurück gegeben. Und die Transaktion kann ausgegeben werden.
	public static boolean txVerify()
	{
		try
		{
			if(txt_inAdr[0].getText().length()==0)									{ JOptionPane.showMessageDialog(frame, "No input address", "Error", 0);  return false; };
			if(txt_outAdr[0].getText().length()==0)									{ JOptionPane.showMessageDialog(frame, "No output address", "Error", 0);  return false; };
			if(txt_totalValIn.getText().equals("")) 								{ JOptionPane.showMessageDialog(frame, "Total Value Input = Null", "Error", 0);  return false;   }
			if(txt_fee.getText().equals("")) 										{ JOptionPane.showMessageDialog(frame, "fees = Null", "Error", 0);  return false; }
			try { Double.parseDouble(txt_fee.getText().replace(",", "."));}
			catch(Exception e) 														{ JOptionPane.showMessageDialog(frame, "fees = Null", "Error", 0);  return false; };
			double fees = Double.parseDouble(txt_fee.getText().replace(",", "."));
			if(fees <= 0) 	{ JOptionPane.showMessageDialog(frame, "Fees must be greater than 0!", "Error", 0);  return false; };														
			if(fees < 0.00001)														
			{ 
				int m = JOptionPane.showConfirmDialog(frame, "The fees is less than the network minimum fee. \nIgnore?");
				if(m!=0) return false;
			}
			double valueOut = Double.parseDouble(txt_totalValOut.getText().replace(",", "."));
			if(fees >= valueOut/2)
			{
				int m = JOptionPane.showConfirmDialog(frame, "The fees is disproportionately high. \nIgnore?");
				if(m!=0) return false;
			}
			if(fees >= 0.001)
			{
				int m = JOptionPane.showConfirmDialog(frame, "The fees is higher than 0,001 BTC \nIgnore?");
				if(m!=0) return false;
			}
			if(TxBuildAction.coreTxOutSet==null)  { JOptionPane.showMessageDialog(frame, "Tx input has been changed or is empty.\n Load Tx-Inputs from Blockchain!", "Error", 0);  return false; }
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
			if(fees > 0.00001 && fees < valueOut/2) txt_fee.setForeground(Color.black);
			else									txt_fee.setForeground(Color.red);
			if(valueIn > valueOut) 	txt_totalValOut.setForeground(Color.black);
			else					txt_totalValOut.setForeground(Color.red);
		}
		catch(Exception e) 
		{
			txt_totalValOut.setForeground(Color.red);
			txt_totalValOut.setText("---   ---   ---");
			txt_fee.setForeground(Color.red);
			txt_fee.setText("---   ---   ---");			
		};
	}	
}