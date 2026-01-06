package TxBuild;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import org.json.JSONObject;
import RPC.ConnectRPC;
import lib3001.java.Animated;
import lib3001.java.Hover;



/********************************************************************************************************************
*	V1.3							 Autor: Mr. Maxwell   							vom 26.12.2025					*
*	Die GUI (JDialog) der BitcoinCore RPC Connections-Settings für den TxBuilder									*
********************************************************************************************************************/


/*******************************************************************************************************************
* Beschreibung zur Authentication Methode: Coocki																	*
* Dadurch entfällt die Eingabe von IP-Adresse, Passwort und Benutzername. 											*
* IP ist dann immer 127.0.0.1   Es geht also nur auf dem selbem PC.													*
* Der Pfad zum Bitcoin Installationsverzeichniss, wo sich diese .cookie Datei befindet, muss angegeben werden.		*
* Der Core muss gestartet sein!																						*
* Der Core erstellt bei jedem Start eine neue .cookie Datei. Der Inhalt wird zufallsgeneriert und immer neu erstellt.
* Es Wird die datei ".cookie" geladen. In der Datei befindet sich dann der Benutzername und das Passwort			*
* Benutzername ist immer: 	__cookie__																				*
* Passwort sieht so aus, Beispiel:	5b4f84f7e7858b67c9d7533fb19e2a6427c771d8da0ac3796607eb52c90d4e7e				*
**********************************************************************************************************************/


public class GUI_CoreSettings extends JDialog 
{

	public static JTextField txt_ip 	= new JTextField("127.0.0.1");
	public static JTextField txt_port 	= new JTextField("8332");
	public static JTextField txt_uName	= new JTextField();
	public static JTextField txt_pw 	= new JTextField();
	public static JTextField txt_timeOut= new JTextField("3");
	public static JCheckBox  btn_auth	= new JCheckBox("Authentication Method: Cookie",true); // Default-Werte, werden von Config nach dem Laden überschrieben.
	public static JButton 	 btn_path	= new JButton();
	public static JLabel	 lbl_path	= new JLabel("user.dir");
	public static JTextArea  txt_meld 	= new JTextArea();
	public static boolean	 connected	= false; 			// Bei Erfolgreicher Verbindung wird hier true gesetzt.

	
	public GUI_CoreSettings(int x, int y) 
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(GUI.t.t("BitcoinCore connection settings"));
		setBounds(x, y, 720, 450);
		setMinimumSize(new Dimension(700, 400));;	
		if(GUI.btn_testNet.isSelected()) setIconImage(MyIcons.bitcoinLogoTest.getImage());			
		else 							 setIconImage(MyIcons.bitcoinLogoMain.getImage());	
		setModal(true);
		JPanel  contentPane	= new JPanel();
		JPanel 	pnl_haupt 	= new JPanel(null);
		JScrollPane sp		= new JScrollPane();
		JButton btn_cTest 	= new JButton(GUI.t.t("Check connection"));
		JTextArea lbl_info  = new JTextArea(GUI.t.t("This program must be connected to a Bitcoin-Core.\nYou can find the connection data in the Bitcoin-Core directory, in the bitcoin.conf file."));
		
		btn_auth	.setToolTipText(GUI.t.t("ToolTipText_btn_auth"));
		btn_path	.setToolTipText(GUI.t.t("ToolTipText_lbl_path"));	
		txt_timeOut	.setToolTipText(GUI.t.t("ToolTipText_txt_timeOut"));
		txt_ip		.setToolTipText(GUI.t.t("ToolTipText_txt_ip"));
		txt_port	.setToolTipText(GUI.t.t("ToolTipText_txt_port"));
		txt_uName	.setToolTipText(GUI.t.t("ToolTipText_txt_uName"));
		txt_pw		.setToolTipText(GUI.t.t("ToolTipText_txt_pw"));
		btn_cTest	.setToolTipText(GUI.t.t("ToolTipText_txt_btn_cTest"));

		lbl_info	.setBounds(10, 11, 699, 65);
		txt_ip		.setBounds(10, 125, 300, 40);
		txt_port	.setBounds(320,125, 100, 40);
		txt_uName	.setBounds(10, 175,410, 40);
		txt_pw		.setBounds(10, 225,410, 40);
		txt_timeOut	.setBounds(550, 125, 150, 40);
		btn_cTest	.setBounds(550, 220, 144, 23);
		btn_path	.setBounds(240, 85, 180, 20);
		lbl_path	.setBounds(425, 85, 345, 20);
		btn_auth	.setBounds(12, 85, 225, 24);
		txt_meld	.setBounds(10, 10, 686, 209);	
		
		txt_ip		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"IP-Address",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_port	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Port",			TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_uName	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"User name",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_pw		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Password",		TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_timeOut .setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"TimeOut (sec)",TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_meld	.setBorder(new EmptyBorder(8, 8, 8, 8));

		lbl_info	.setFont(GUI.font3);
		txt_ip		.setFont(GUI.font4);
		txt_port	.setFont(GUI.font4);
		txt_uName	.setFont(GUI.font4);
		txt_pw		.setFont(GUI.font4);
		txt_timeOut	.setFont(GUI.font4);

		btn_auth	.setFont(new Font("Century Gothic", Font.PLAIN, 11));
		btn_path	.setFont(new Font("Century Gothic", Font.PLAIN, 11));
		btn_cTest	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		lbl_path	.setFont(new Font("Century Gothic", Font.PLAIN, 9));

		lbl_info	.setBackground(GUI.color1);
		txt_ip 		.setBackground(Color.white);
		txt_port 	.setBackground(Color.white);	
		txt_uName	.setBackground(Color.white);	
		txt_pw 		.setBackground(Color.white);
		txt_timeOut	.setBackground(Color.white);
		txt_meld	.setBackground(GUI.color1);
		btn_auth	.setBackground(GUI.color1);	
		
		lbl_info	.setForeground(GUI.color4);
		txt_meld	.setForeground(Color.black);
		lbl_info	.setEditable(false);
		txt_meld	.setEditable(false);
		btn_cTest	.setMargin(new Insets(0, 0, 0, 0));
		btn_path	.setMargin(new Insets(0, 0, 0, 0));
		btn_auth	.setMargin(new Insets(0, 0, 0, 0));
		sp			.setBorder(null);
		txt_meld	.setRows(10);
		pnl_haupt	.setPreferredSize(new Dimension(0, 280));
		pnl_haupt	.setMaximumSize(new Dimension(32767, 280));
		pnl_haupt	.setMinimumSize(new Dimension(0, 280));
		
		Hover.addBorder(txt_ip);
		Hover.addBorder(txt_port);
		Hover.addBorder(txt_uName);
		Hover.addBorder(txt_pw);
		Hover.addBorder(txt_timeOut);
		
		sp.setViewportView(txt_meld); 
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		pnl_haupt.add(txt_ip);		
		pnl_haupt.add(txt_port);
		pnl_haupt.add(txt_uName);		
		pnl_haupt.add(txt_pw);
		pnl_haupt.add(lbl_info);
		pnl_haupt.add(txt_timeOut);
		pnl_haupt.add(btn_cTest);
		pnl_haupt.add(btn_path);
		pnl_haupt.add(lbl_path);
		pnl_haupt.add(btn_auth);
		contentPane.add(pnl_haupt);
		contentPane.add(sp);
		add(contentPane);

			
		
		// Der Button "Authentication Method: Cookie" änert die Anmelde-Methode beim Bitcoin Core
		// Wenn er inaktiv ist, wird sich mit Benutzername und Passwort angemeldet
		// Wenn er aktiv ist, dann wird die Anmeldemethode: Cookie verwendet.
		btn_auth.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				changesAuthenticationMethod();	
				if(btn_auth.isSelected()==false)
				{
					txt_uName.setText("");
					txt_pw.setText("");
				}
			}
		});	
		
		
		
		
		
		// Legt mit dem JFileChooser das Haupt-Verzeichnis vom Bitcoin Core fest, auf dem die .cookie Datei liegt.
		btn_path.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{							
				JFileChooser chooser = new JFileChooser();
		        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setCurrentDirectory(new File(lbl_path.getText()));							
				int button = chooser.showOpenDialog(sp);	
				if(button==0)																					
				{
					lbl_path.setText(chooser.getSelectedFile().getAbsolutePath());	
					changesAuthenticationMethod();
				}			
			}
		});
		
		
		
		
		

		// Prüft die Verbindung zum Bitcoin-Core
		btn_cTest.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				checkCoreConnection();
			}
		});	

	}	

	
// ------------------------------------------------------------------------- Ende GUI ------------------------------------------------------------------------------------
	
	
	
/**	Wird bei jedem Programmstart ausgeführt.
	Prüft die BitcoinCore RPC-Verbindung und stellt je nach Einstellung des BitcoinCores das Programm auf TestNet oder MainNet ein. **/
	public static void checkCoreConnection()
	{
		try 																				// Verbindungsteil
		{		
			txt_meld.setText("");
			txt_meld.setForeground(Color.red);
			String 	ip 	 = txt_ip.getText();
			int 	port = Integer.valueOf(txt_port.getText());
			String 	name = txt_uName.getText();
			String 	pw 	 = txt_pw.getText();
			ConnectRPC peer = new ConnectRPC(ip, port, name, pw);
			peer.setTimeOut(Integer.parseInt(txt_timeOut.getText()));
			String str = peer.get("getblockchaininfo", null);
			try 
			{
				JSONObject jo = new JSONObject(str);
				txt_meld.setForeground(new Color(0,180,0));
				txt_meld.setText("Connected!\n"+jo.toString(1));
				connected=true;	
				String chain = jo.getJSONObject("result").getString("chain");
				if(chain.equals("main")) GUI.btn_testNet.setSelected(false);
				if(chain.equals("test")) GUI.btn_testNet.setSelected(true);
				GUI.initNetwork();
				System.out.println("Connected BitcoinCore with: "+chain);
				GUI.txt_meld.setForeground(new Color(0,180,0));
				GUI.txt_meld.setText("Connected BitcoinCore with: "+chain);
			} 
			catch (Exception e1) 
			{
				connected=false;	
				txt_meld.setForeground(Color.red);
				txt_meld.setText(GUI.t.t("Connected, but password or username are wrong!\n")+str);
				GUI.txt_meld.setForeground(Color.red);
				GUI.txt_meld.setText(GUI.t.t("Connected, but password or username are wrong!\n")+str);
			}	
			txt_meld.grabFocus();
			txt_meld.setCaretPosition(0);
		} 
		catch (Exception e1) 
		{
			connected=false; 
			txt_meld.setForeground(Color.red);  
			txt_meld.setText(GUI.t.t("No connect!\n")+e1.getMessage());
			GUI.txt_meld.setForeground(Color.red);  
			GUI.txt_meld.setText(GUI.t.t("No connect!\n")+e1.getMessage());
		} 	
			
		if(GUI.dialogCoreSettings==null) 						// Nur Wenn der Dialog noch nicht geöffnet ist, also beim Neustart.
		{
			if(GUI.debugMode == 1)								// DebugMode im Main-Net, Wenn DebugMode aktiv, wird hier sofort abgebrochen.
			{
				GUI.btn_testNet.setSelected(false);
				GUI.initNetwork();
				GUI.txt_meld.setForeground(new Color(240,0,0));
				GUI.txt_meld.setText("Debug mode with MainNet");
				return;
			}
			if(GUI.debugMode == 2)								// DebugMode im Test-Net, Wenn DebugMode aktiv, wird hier sofort abgebrochen.
			{
				GUI.btn_testNet.setSelected(true);
				GUI.initNetwork();
				GUI.txt_meld.setForeground(new Color(240,0,0));
				GUI.txt_meld.setText("Debug mode with TestNet3");
				return;
			}
			
			GUI.dialogCoreSettings = new GUI_CoreSettings(GUI.frame.getX()+340,GUI.frame.getY()+55);
			GUI.dialogCoreSettings.addWindowListener(new java.awt.event.WindowAdapter() 						// Close Button wird abgefangen und hier selbst verarbeitet.
			{
			    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
			    {
			    	if(connected) {}
			    	else 
			    	{
			    		Config.save();
				    	Animated.close();
						try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}	
				    	System.exit(0);	
			    	}
			    }
			});	
			if(connected) 	GUI.dialogCoreSettings.setVisible(false);											// Nur wenn die Verbindung schon vorher richtig configuriert war, wird der Dialog sofort geschlossen.														
			else 			GUI.dialogCoreSettings.setVisible(true);				
			GUI.dialogCoreSettings.pack();		
		}		
		if(connected) 	GUI.dialogCoreSettings.setVisible(false);
	}
	
	
	
	
	
	
	
	
	
	
	
	
/**	Wird ausgeführt wenn die Authentications-Methode geändert wird.
	Also bei Betätigung des Buttens "Authentication Method: Cookie" (btn_auth), oder bei jedem Start des Programmes.
	Achtung, dadurch wird die Datei: ".cookie" im Bitcoin-Core Verzeichness geöffnet und ausgelesen. Diese Datei ist nur vorhanden, wenn der Core läuft!
	Wenn die Datei vorhanden ist, Werden Benutzername und Passwort aus dieser Datei verwendet.
	Benutzername ist:"__cookie__" und Passwort, das hinter dem ":"
	Wenn der Butten deaktiviert wird, werden wieder Passwort und Username zur Eingabe verwendet.
	In der GUI "BitcoinCore connection settings" wird entsprechend der Einstellung dieses Buttens verändert. **/
	public static void changesAuthenticationMethod()
	{
		if(btn_auth.isSelected())
		{
			txt_ip.setEnabled(false);
			txt_uName.setEnabled(false);
			txt_pw.setEnabled(false);
			btn_path.setVisible(true);
			lbl_path.setVisible(true);
			txt_ip.setText("127.0.0.1");
			
			File f = new File(lbl_path.getText()+"/.cookie");
			if(f.exists())
			{
				try
				{		
					BufferedReader br = new BufferedReader(new FileReader(f));
					String str = br.readLine();
					br.close();
					txt_uName.setText("__cookie__");
					txt_pw.setText(str.substring(11));	
					
					txt_meld.setForeground(Color.black);
					txt_meld.setText    (GUI.t.t("Authentication Method: Cookie"));
					
					
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					GUI.txt_meld.setForeground(Color.red);
					GUI.txt_meld.setText(e.getMessage());
				}
			}
			else
			{			
				txt_meld.setForeground(Color.red);
				txt_meld.setText    (GUI.t.t("Bitcoin-Core is not running, or the file path to the Bitcoin directory is not correct!\nSpecify the file path where the configuration file (bitcoin.conf) is stored."));
				System.out.println("File: "+f.toString()+" not exist!");
			}			
		}
		else
		{
			txt_ip.setEnabled(true);
			txt_uName.setEnabled(true);
			txt_pw.setEnabled(true);
			btn_path.setVisible(false);
			lbl_path.setVisible(false);		
			txt_meld.setForeground(Color.black);
			txt_meld.setText    (GUI.t.t("Authentication method: Sign-in\nYou have to enter the RPC credentials from the bitcoin.conf file of the Bitcoin core here."));
		}
	}
}