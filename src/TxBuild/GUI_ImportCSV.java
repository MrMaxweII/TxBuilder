package TxBuild;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import RPC.ConnectRPC;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;



/********************************************************************************************************************
*	V1.1									 Autor: Mr. Maxwell   								vom 13.02.2026		*
*	Die GUI (JDialog). Importiert die csv Datei mit den eigenen kontrollieten Bitcoin-Adressen.						*
*	Zeigt alle Adressen an, die Beträge enthalten und zeigt die gesamt-Summe an										*
********************************************************************************************************************/


public class GUI_ImportCSV extends JDialog 
{
	
	private GUI_ImportCSV		dialog;
	private JTable 				table;															// Die Tabelle mit den Bitcon-Adressen				
	private JScrollPane  		scrollPane 		= new JScrollPane();
	private JTextPane 			txt_meld 		= new JTextPane();								// Ausgabe-Fenster mit allen Meldungen
	private JProgressBar 		progressBar 	= new JProgressBar();							// Wartebalklen unten, wenn der Core die Transaktionen sucht.
	private JTextField 			txt_totalValue	= new JTextField();								// Gesamter Ausgangs-Betrag
	private JButton 			btn_open 		= new JButton();								// öffnet den FileCooser zum laden der csv-Datei
	private JButton 			btn_open_crypt 	= new JButton();								// öffnet den FileCooser zum entschlüsseln und laden der csv-Datei
	private JButton 			btn_encrypt 	= new JButton();								// öffnet den FileCooser und verschlüsselt die Bitcoin-Address-List.csv Datei
	private JButton 			btn_cancel 		= new JButton();								// Abbruch Load
	private JButton 			btn_input		= new JButton("As source address");				// Button: Als Eingabe setzten
	private JButton 			btn_output		= new JButton("As destination address");		// Button: Als Ausgabe setzten
	private JLabel				lbl_progress	= new JLabel(MyIcons.load_gif);					// Animiertes progress gif. drehendes Bitcoin-Symbol
	public static JTextField 	lbl_file 		= new JTextField("user.dir");					// Speicherort der csv.Datei wird in Config gespeichert.
	private static JTextPane  	lbl_info 		= new JTextPane();								// Beschreibungs-Text


	public GUI_ImportCSV(int x, int y) 
	{
		dialog = this;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(GUI.t.t("Bitcoin Address List"));
		setBounds(x, y, 820, 350);
		setMinimumSize(new Dimension(810,350));
		setModal(true);
		setIconImage(MyIcons.open.getImage());					
			
		JMenuBar 	menuBar 	= new JMenuBar();
		JPanel 		pnl_menu 	= new JPanel();
		JPanel 		pnl_2 		= new JPanel();
		JPanel 		pnl_3 		= new JPanel();
		JLayeredPane pnl_lp		= new JLayeredPane();
		
		btn_open		.setText(GUI.t.t("open csv file"));
		btn_open_crypt	.setText(GUI.t.t("open crypt file"));
		btn_encrypt		.setText(GUI.t.t("encrypt csv file"));
		btn_cancel		.setText(GUI.t.t("cancel"));
		btn_input		.setText(GUI.t.t("As source address"));
		btn_output		.setText(GUI.t.t("As destination address"));
		lbl_info		.setText(GUI.t.t("This is where the csv file is imported, which contains your own Bitcoin addresses.\n Once imported, the total amount of all controlled Bitcoin addresses will be displayed.\n They can directly choose from which addresses they want to send or receive."));

		btn_open		.setToolTipText(GUI.t.t("ToolTipText_btn_open"));
		btn_open_crypt	.setToolTipText(GUI.t.t("ToolTipText_btn_open_crypt"));
		btn_encrypt		.setToolTipText(GUI.t.t("ToolTipText_btn_encrypt"));
		btn_input		.setToolTipText(GUI.t.t("ToolTipText_btn_input"));
		btn_output		.setToolTipText(GUI.t.t("ToolTipText_btn_output"));		
		
		btn_open		.setIcon(MyIcons.csv);
		btn_open_crypt	.setIcon(MyIcons.openCrypt);
		btn_encrypt		.setIcon(MyIcons.lock);

		
		menuBar			.setBackground(GUI.color1);
		lbl_info		.setForeground(GUI.color4);
		txt_meld		.setForeground(Color.RED);
		lbl_file		.setBackground(GUI.color1);
		progressBar		.setForeground(GUI.color4);
		lbl_info		.setBackground(GUI.color1);
		txt_meld		.setBackground(GUI.color1);
		progressBar		.setBackground(GUI.color1);
		txt_totalValue	.setBackground(GUI.color1);
		
		lbl_info		.setFont(GUI.font3);
		txt_totalValue	.setFont(GUI.font4);	
		btn_input		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_output		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_open		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_open_crypt	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_encrypt		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_cancel		.setFont(new Font("Century Gothic", Font.PLAIN, 12));		
		lbl_file		.setFont(new Font("Century Gothic", Font.PLAIN, 10));
		
		txt_totalValue	.setBorder(new TitledBorder(new LineBorder(GUI.color4), GUI.t.t("Total Value (BTC)"), TitledBorder.LEADING, TitledBorder.TOP, GUI.font2, GUI.color3));
		scrollPane		.setBorder(new LineBorder(GUI.color1, 7));
		progressBar		.setBorder(null);
		lbl_file		.setBorder(null);
		lbl_info		.setBorder(new EmptyBorder(5,5,5,5));
			
		menuBar			.setPreferredSize(new Dimension(500, 65));
		btn_open		.setPreferredSize(new Dimension(250, 30));	
		btn_open_crypt	.setPreferredSize(new Dimension(250, 30));	
		btn_encrypt		.setPreferredSize(new Dimension(250, 30));			
		btn_cancel		.setPreferredSize(new Dimension(120, 22));	
		btn_input		.setPreferredSize(new Dimension(240, 22));
		btn_output		.setPreferredSize(new Dimension(240, 22));
		progressBar		.setPreferredSize(new Dimension(146, 13));

		btn_input		.setMargin(new Insets(0, 0, 0, 0));
		btn_output		.setMargin(new Insets(0, 0, 0, 0));	
		btn_open		.setMargin(new Insets(0, 0, 0, 0));
		btn_open_crypt	.setMargin(new Insets(0, 0, 0, 0));
		btn_encrypt		.setMargin(new Insets(0, 0, 0, 0));	
		btn_cancel		.setMargin(new Insets(0, 0, 0, 0));
		
		lbl_info		.setEditable(false);
		txt_totalValue	.setEditable(false);
		txt_meld		.setEditable(false);
		btn_input		.setEnabled(false);
		btn_output		.setEnabled(false);
		progressBar		.setVisible(false);
		btn_cancel		.setVisible(false);
		lbl_progress	.setVisible(false);

		progressBar		.setStringPainted(true);
		txt_totalValue	.setColumns(19);
			
		scrollPane		.setOpaque(false);
		scrollPane		.setFocusable(false);
		scrollPane		.setFocusTraversalKeysEnabled(false);
		scrollPane		.getViewport().setOpaque(false);

		pnl_lp			.setLayout(new OverlayLayout(pnl_lp));
		pnl_2			.setLayout(new BorderLayout(0, 0));
		pnl_menu		.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
		pnl_3			.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		txt_totalValue	.setHorizontalAlignment(SwingConstants.RIGHT);
			
		setJMenuBar(menuBar);
		menuBar			.add(pnl_menu);
		pnl_menu		.add(btn_open);
		pnl_menu		.add(Box.createHorizontalStrut(7));
		pnl_menu		.add(btn_open_crypt);
		pnl_menu		.add(Box.createHorizontalStrut(7));
		pnl_menu		.add(btn_encrypt);	
		pnl_menu		.add(btn_cancel);
		pnl_menu		.add(Box.createHorizontalStrut(7));
		pnl_menu		.add(lbl_file);
		pnl_lp			.add(lbl_progress);
		pnl_lp			.add(scrollPane);
		pnl_2			.add(pnl_3, BorderLayout.NORTH);
		pnl_2			.add(txt_meld, BorderLayout.CENTER);
		pnl_2			.add(progressBar, BorderLayout.SOUTH);
		pnl_3			.add(btn_input);
		pnl_3			.add(Box.createHorizontalStrut(40));
		pnl_3			.add(btn_output);
		pnl_3			.add(Box.createHorizontalStrut(100));
		pnl_3			.add(txt_totalValue);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(lbl_info, BorderLayout.NORTH);	
		getContentPane().add(pnl_lp, BorderLayout.CENTER);
		getContentPane().add(pnl_2, BorderLayout.SOUTH);
		
			
		
// ----------------------------------------------------------- Actions --------------------------------------------------------------------------

	
		
		// Öffnet mit dem JFileChooser die BTCAddressList.crypt.
		btn_open_crypt.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{			
				txt_meld.setText("");
				String userDir = System.getProperty("user.home");			
				JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
				chooser.setFileFilter(new FileNameExtensionFilter("BTCAddressList.crypt", "crypt"));			
				chooser.setCurrentDirectory(new File(lbl_file.getText()));							
				int button = chooser.showOpenDialog(scrollPane);		
				if(button==0)																					
				{
					lbl_file.setText(chooser.getSelectedFile().getAbsolutePath());
					try 
					{
						FileInputStream br = new FileInputStream(lbl_file.getText());						
						byte[] chiffre = br.readAllBytes();
						br.close();	
		
						String pw = null;
						JPasswordField pf = new JPasswordField();
						int ok = JOptionPane.showConfirmDialog(getContentPane(), pf, GUI.t.t("Password"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (ok == JOptionPane.OK_OPTION) pw = new String(pf.getPassword());	
						else return;
						
						byte[] key = Convert.hexStringToByteArray(Calc.getHashSHA256(pw));
						byte[] decrypt_b =  Crypt.decrypt(chiffre, key, "AES");
						byte[][] erg = Crypt.removeAndCheckSHA256Checksum(decrypt_b);
						byte[] data_b  = erg[1];
						boolean richtig = Convert.byteToBool(erg[2][0]);
						
						while(!richtig)
						{
							java.awt.Toolkit.getDefaultToolkit().beep();
							ok = JOptionPane.showConfirmDialog(getContentPane(), pf, GUI.t.t("Passwort false!   Passwort:"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
							if (ok == JOptionPane.OK_OPTION) pw = new String(pf.getPassword());	
							else return;

							key = Convert.hexStringToByteArray(Calc.getHashSHA256(pw));
							decrypt_b =  Crypt.decrypt(chiffre, key, "AES");
							erg = Crypt.removeAndCheckSHA256Checksum(decrypt_b);
							data_b  = erg[1];
							richtig = Convert.byteToBool(erg[2][0]);					
						}				
						
						// Öffnet ein neues JFrame zum anzeigen der CSV-Adress-Liste
						int eingabe = JOptionPane.showConfirmDialog(getContentPane(), GUI.t.t("show address List as CSV?"), "Show CSV file?", 0);
						if(eingabe <  0) return;
						if(eingabe == 0)
						{
							JFrame fm 		= new JFrame();
							JScrollPane	sp	= new JScrollPane();
							JTextArea txt 	= new JTextArea();
							fm.setTitle("CSV address list");
							fm.setBounds(getContentPane().getX()+5, getContentPane().getY()+5, 1100, 800);
							txt.setEditable(false);
							txt.setFont(GUI.font4);
							txt.setText(new String(data_b, "UTF-8"));
							sp.setViewportView(txt);
							fm.getContentPane().add(sp);
							fm.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
							fm.setVisible(true);
						}
											
						
						// Der Datensatz ist durch umkodierungen (nicht meine) mit Sonderzeichen verschmutzt!
						// Hier Wird ein "Sonderzeichen" = 0a welches einen Zeilenumbruch bewirkt, "hard" aus dem Datensatz enterfen.
						// Dieses Zeichen kommt vermutlich durch umcodierung des ursprünglichen "\n" zustande. Und wird dann zusätzlich in den datensatzt gedrückt.
						// Dieses kann nicht auf üblichem Weg im Text-String entfernt werden!!! Vermute Java-Bug! (Keine Change mit String.replaceAll(...)!!! Dieses Zeichen gibt es dort nicht!
						String data_hexString = Convert.byteArrayToHexString(data_b);
						data_hexString = data_hexString.replaceAll("0a","");
						data_hexString = data_hexString.replaceAll("00","");				// "00" Wird auch entfern! Dies wurde als AES-Padding hinten angefügt.	
						data_b = Convert.hexStringToByteArray(data_hexString);
						// -------------------------------------------------------- Ende entfernung ------------------------------------------------------------------------------------------
						
						String str = new String(data_b, "UTF-8");							// entschlüsselter und bereinigter Datensatz
						str.replaceAll("\n","");											// Zeilenumbrüche werden entfernt				
						str.replaceAll(" ","");												// Leerzeichen werden entfernt				
						String[] addr = str.split(",");										// String wird in String-Array an der Stelle "," Aufgteilt.										
				        loadTxfromCore(addr);
					} 
					catch (Exception e1) 
					{
						txt_meld.setText(e1.getMessage());
						e1.printStackTrace();
					}			
				}			
			}
		});	
		
		
		
	// Öffnet mit dem JFileChooser die BTCAddressList.csv.
	btn_open.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			txt_meld.setText("");
			String userDir = System.getProperty("user.home");			
			JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
			chooser.setFileFilter(new FileNameExtensionFilter("BTCAddressList.csv", "csv"));			
			chooser.setCurrentDirectory(new File(lbl_file.getText()));							
			int button = chooser.showOpenDialog(scrollPane);		
			if(button==0)																					
			{
				lbl_file.setText(chooser.getSelectedFile().getAbsolutePath());
				try 
				{
					ArrayList<String> list = new ArrayList<String>();
					Scanner scanner = new Scanner(new File(lbl_file.getText()));
			        scanner.useDelimiter(",");
			        while(scanner.hasNext())
			        {
			            String str = ((scanner.next().replaceAll("\n", "")));
			            if(str.length()>8) list.add(str);
			        }
			        scanner.close();
			        String[] addr  = list.toArray(new String[0]);
			        loadTxfromCore(addr);
				} 
				catch (Exception e1) 
				{
					txt_meld.setText(e1.getMessage());
					e1.printStackTrace();
				}			
			}			
		}
	});
	
	
	
	// Verschlüsselt BTCAddressList.csv.    
	btn_encrypt.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			txt_meld.setText("");
			String userDir = System.getProperty("user.home");			
			JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
			chooser.setFileFilter(new FileNameExtensionFilter("BTCAddressList.csv", "csv"));			
			chooser.setCurrentDirectory(new File(lbl_file.getText()));							
			int button = chooser.showOpenDialog(scrollPane);		
			if(button==0)																					
			{
				lbl_file.setText(chooser.getSelectedFile().getAbsolutePath());
				try 
				{
					String str = "";
					BufferedReader br = new BufferedReader(new FileReader(new File(lbl_file.getText())));
					while(br.ready())
					{
						str = str + br.readLine() + "\n";
					}
					br.close();
					
					String pw = JOptionPane.showInputDialog(getContentPane(), GUI.t.t("Password to be used for encryption:"), GUI.t.t("Password"), 3);					
					byte[] key = Convert.hexStringToByteArray(Calc.getHashSHA256(pw));
					byte[] schiffre = encryptAES(str.getBytes("UTF-8") , key, "AES");
					
					JFileChooser chooser2 = new JFileChooser(userDir +"/Desktop");
					FileFilter filter = new FileNameExtensionFilter(".crypt", "crypt");
					chooser2.setFileFilter(filter);	
					chooser2.setAcceptAllFileFilterUsed(false);
					chooser2.setSelectedFile(new File("BTCAddressList.crypt"));
					int button2 = chooser2.showSaveDialog(getContentPane());
					if(button2==0)																					
					{
						String file = chooser2.getSelectedFile().getAbsolutePath();	
						FileOutputStream fo = new FileOutputStream(file);
						fo.write(schiffre);
						fo.close();				
					}
				} 
				catch (Exception e1) 
				{
					txt_meld.setText(e1.getMessage());
					e1.printStackTrace();
				}			
			}			
		}
	});
	
	

	// Bricht den "scantxoutset-Befehl" des Cores (Laden der TX) ab.
	btn_cancel.addMouseListener(new MouseAdapter()  
	{
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				// @Thread  Keine relevante GUI-Anpassung in diesem Thread. (Der catch-Zweig wird ignoriert, weil er normalerweise nicht auftritt)
				Thread thread3 = new Thread(new Runnable() 
				{
					public void run() 
					{
						ConnectRPC core = new ConnectRPC(TxBuildAction.ip,TxBuildAction.port,TxBuildAction.name,TxBuildAction.pw);
						core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
						try 
						{
							core.get_scantxoutset("abort", new String[] {""}, new String[] {""});
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
							txt_meld.setText(ex.getMessage());
						}
					}
				});
				thread3.start();
			}
		}
	});
	
	
	
	// Übernimmt markierte Zeilen und schreibt sie in den Input der Haupt-GUI
	btn_input.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_meld.setText("");
				int[] auswahl = table.getSelectedRows();
				if(auswahl.length<=0)  	JOptionPane.showMessageDialog(scrollPane, GUI.t.t("No marked addresses.\nMark the desired addresses!\nUse the keyboard shortcuts: <Ctrl+a> or <Ctrl+select> or<Shift+select> etc."), "Info", 1);   
				else
				{
					GUI.cBox_inCount.setSelectedIndex(auswahl.length-1);
					if(auswahl.length == GUI.txt_inAdr.length) // Prüft ob die Anzahl Inputs richtig aktuallisiert wurde. Ist Notwendig, der Benutzer es ablehen kann.
					{
						for(int i=0; i<auswahl.length;i++)
						{
							String addr = (String) table.getModel().getValueAt(auswahl[i], 0);
							GUI.txt_inAdr[i].setText(addr);
						}
						GUI.dialogImport.dispose();
					}
				}	
			} 
			catch (Exception e1) 
			{
				txt_meld.setText(e1.getMessage());
				e1.printStackTrace();
			}	
		}
	});	
	
	
	
	// Übernimmt markierte Zeilen und schreibt sie in den Output der Haupt-GUI
	btn_output.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_meld.setText("");
				int[] auswahl = table.getSelectedRows();
				if(auswahl.length<=0)  	JOptionPane.showMessageDialog(scrollPane, GUI.t.t("No marked addresses.\nMark the desired addresses!\nUse the keyboard shortcuts: <Ctrl+a> or <Ctrl+select> or<Shift+select> etc."), "Info", 1);   
				else
				{
					GUI.cBox_outCount.setSelectedIndex(auswahl.length-1);
					if(auswahl.length == GUI.txt_outAdr.length) // Prüft ob die Anzahl Outputs richtig aktuallisiert wurde. Ist Notwendig, der Benutzer es ablehen kann.
					{
						for(int i=0; i<auswahl.length;i++)
						{
							String addr = (String) table.getModel().getValueAt(auswahl[i], 0);
							GUI.txt_outAdr[i].setText(addr);
						}
						GUI.dialogImport.dispose();
					}
				}	
			} 
			catch (Exception e1) 
			{
				txt_meld.setText(e1.getMessage());
				e1.printStackTrace();
			}			
		}
	});	
}
	
	
	
	
	
// --------------------------------------------------------------- private Methoden ----------------------------------------------------------
	
	
	// Verschlüsselt die Bitcoin-Adressliste mit AES
	private static byte[] encryptAES(byte[] text, byte[] key, String name) throws Exception
	{
		byte[] b = Crypt.padding16(text);
		b = Crypt.addSHA256Checksum(b);
		return Crypt.encrypt(b, key , "AES");
	}
	
	
	
	// Hier wird die Tabelle erstellt und in die GUI gesetzt
	private void setTable(String[][] str_tab) throws Exception
	{
		String[] ueberschrift= new String[] { GUI.t.t("BTC-Address"), GUI.t.t("Value")};
		table 	= new JTable();
		table.setEnabled(true);
		table.setFont(GUI.font4);
		table.setForeground(GUI.color3);
		table.setBackground(GUI.color1);
		//table.setSelectionForeground(new Color(255, 147, 26));
		table.setSelectionBackground(new Color(225,204,190));  // Geht nur wenn die Tabelle nicht Transparent ist (setOpaque(true))
		table.getTableHeader().setBackground(GUI.color3);
		table.getTableHeader().setForeground(GUI.color1);
		table.setSurrendersFocusOnKeystroke(true);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setDefaultEditor(Object.class, null);
		table.setModel(new DefaultTableModel(str_tab, ueberschrift));
		table.getColumnModel().getColumn(0).setPreferredWidth(400);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.setGridColor(GUI.color4);
		table.setRowHeight(20);
		table.setOpaque(true);
		//table.setCellSelectionEnabled(false); // Damit wird nur eine Zelle Markiert.
		((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);  // Macht die gesamte Tabele Transparent/oder nicht
		scrollPane	.setViewportView(table);	
		
		SwingUtilities.invokeLater(new Runnable() 		// Passt die Größe des Dialoges nachträglich an die Anzahl der Zeilen in der Tabelle an.
		{
			public void run() 
			{			
				int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height; 				
				int maxHeight = screenHeight - dialog.getY() - 50;
				dialog.setSize(dialog.getWidth(), Math.min(table.getHeight() + 250, maxHeight));		
			}
		});
	}		
	
	
	
	// Lädt die Transaktionen vom Bitcoin-Core
	private void loadTxfromCore(String[] addr) 
	{						
		txt_totalValue.setText("");
	 	btn_open.setVisible(false);
	 	btn_open_crypt.setVisible(false);
	 	btn_encrypt.setVisible(false);
		btn_cancel.setVisible(true);
		btn_input.setEnabled(false);
		btn_output.setEnabled(false);	
		txt_meld.setText(addr.length + " addresses found");	
		TxBuildAction.ip   = GUI_CoreSettings.txt_ip.getText();
		TxBuildAction.port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
		TxBuildAction.name = GUI_CoreSettings.txt_uName.getText();
		TxBuildAction.pw   = GUI_CoreSettings.txt_pw.getText();
		// @Thread Alle GUI-Anpassungen sind in der EDT!
		Thread thread1 = new Thread(new Runnable() 
		{
			public void run() 
			{				
				ConnectRPC core = new ConnectRPC(TxBuildAction.ip,TxBuildAction.port,TxBuildAction.name,TxBuildAction.pw);
				core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
				try 
				{
					String[] adrFormat 	= new String[addr.length];
					for(int i=0; i<addr.length;i++){adrFormat[i] = "addr";}					
					JSONObject coreTxOutSet = core.get_scantxoutset("start", adrFormat, addr);
					scantxoutsetResult(coreTxOutSet);
				}
				catch(Exception ex)
				{
					SwingUtilities.invokeLater(new Runnable() 
					{
						public void run()
						{
							ex.printStackTrace();
							txt_meld.setText(ex.getMessage());
						}
					});
				}
				SwingUtilities.invokeLater(new Runnable() 
				{
					public void run()
					{
						int count = 0;
						if(table!=null) count = table.getModel().getRowCount();
						if(count > 0) // Wenn mindestens eine Zeile in der Tabelle steht, werden die unteren Buttons aktiviert, sonnst nicht.
						{
							btn_input		.setEnabled(true);
							btn_output		.setEnabled(true);
						}
						else
						{
							btn_input		.setEnabled(false);
							btn_output		.setEnabled(false);
						}
						btn_open.setVisible(true);
					 	btn_open_crypt.setVisible(true);
					 	btn_encrypt.setVisible(true);
						btn_cancel.setVisible(false);
					}
				});	
			}
		});
				
		
		
		
		
		lbl_progress.setVisible(true);
		progressBar.setVisible(true);
		// Steuert die Process-Bar
		// @Thread Alle GUI-Anpassungen sind in der EDT!
		Thread thread2 = new Thread(new Runnable() 
		{
			public void run() 
			{
				ConnectRPC core = new ConnectRPC(TxBuildAction.ip,TxBuildAction.port,TxBuildAction.name,TxBuildAction.pw);
				core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
				for(int i=0; i<600; i++ )
				{
					try 
					{
						Thread.sleep(1000);
						JSONObject jo = core.get_scantxoutset("status", new String[] {""}, new String[] {""});
						int value = jo.getJSONObject("result").getInt("progress");
						SwingUtilities.invokeLater(new Runnable() 
						{
							public void run()
							{
								progressBar.setValue(value);
							}
						});
					}
					catch(Exception ex)
					{
						SwingUtilities.invokeLater(new Runnable() 
						{
							public void run()
							{
								lbl_progress.setVisible(false);
								progressBar.setVisible(false);
							}
						});
						break;
					}
				}
			}
		});
		thread1.start();
		thread2.start();		 
	}
		
	

	//	Wertet das Ergebnis vom Core aus und und schreibt die Daten in die GUI
	//	Muss in die EDT, da die Methode von einem eigenem Thread ausgeführt wird.
	private void scantxoutsetResult(JSONObject jo) throws JSONException
	{	
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try{txt_meld.setText("BitcoinCore Error:\n"+ jo.getJSONObject("error").getString("message"));}   		// Gibt Fehler-Meldungen vom Core aus
				catch(Exception e) {};
				try
				{
					JSONObject jo_result = jo.getJSONObject("result");													// Das Ergebnis von Core, als JSON-Object
					if(jo_result.getBoolean("success") == true) 														// Wenn Ergebnis fehlerfrei abgeschlossen ist.
					{
						LinkedHashMap<String,Double>hm = new LinkedHashMap<String,Double>();							// In die HashMap werden die Adressen und zugehörigen Beträge gespeichert. "LinkedHashMap" bedeutet, dass die Reihenfolge der Elemente bei behalten wird.
						txt_totalValue.setText(String.format("%.8f", jo_result.getDouble("total_amount")));				// Gesamtbetrag aller Adressen
						JSONArray unspents = jo_result.getJSONArray("unspents");										// Nicht ausgegebene Inputs als JSONArray
						for(int i=0;i<unspents.length();i++)
						{
							JSONObject jo_el =  unspents.getJSONObject(i);
							String addr = jo_el.getString("desc");														// Die Bitcoin Adresse, mit angehängten Daten
							addr = addr.substring(addr.indexOf("(")+1, addr.indexOf(")"));								// Die reine Bitcoin Adresse. (Angehängte Daten werden entfernt)
							hm.put(addr, hm.getOrDefault(addr, 0.0) + jo_el.getDouble("amount"));						// Speichert Addresse mit Betrag in die HashMap und addiert dabei die Beträge bei mehrfachen gleichen Adressen.
						}																								// In der LinkedHashMap (hm) befinden sich nun alle Adressen mit den zugehörigen Beträgen. Mehrfache Adressen wurden zusammen-verrechnet.
						String[][] tab = new String[hm.size()][2];														// 2Dim String-Array wird hier angelegt und später der Tabelle übergeben. Entspricht im Prinzip der Tabelle.
						Object[] keys = hm.keySet().toArray();															// Mit "keys" sind die BTC-Adressen gemeint, die als Schlüssel in der HashMap fungieren. Die Schlüssel müssen aufgelistet werden um sie anschließend in der Schleife durchlaufen zu können.
						for(int i=0; i<keys.length;i++)																	// Damit wird die HashMap in die Tabelle übertragen.
						{
							tab[i][0] = (String) keys[i];																// Schreibt die BTC-Adressen (keys) in die Tabelle				
							tab[i][1] = String.format("%.8f",(Math.round(hm.get(tab[i][0])*100000000.0)/100000000.0));	// Schreibt die Beträge in die Tabelle, die Beträge werden noch richtig formatiert.
						}
						setTable(tab);
						if(keys.length>0) 	lbl_info.setText(GUI.t.t("Mark one or more lines and select them as the input or output address."));
						else 				txt_meld.setText(GUI.t.t("No address with an available amount found."));		
					}
					else txt_meld.setText(GUI.t.t("User abort"));	
				}   
				catch(Exception e) {e.printStackTrace();};	
			}
		});				
	}
}