package TxBuild;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.awt.event.ActionEvent;



/********************************************************************************************************************
*	V1.2							 Autor: Mr. Maxwell   							vom 09.11.2025					*
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
* Passwort sieht das so aus, Beispiel:	5b4f84f7e7858b67c9d7533fb19e2a6427c771d8da0ac3796607eb52c90d4e7e		*
**********************************************************************************************************************/


public class GUI_CoreSettings extends JDialog 
{

	public static JTextField txt_ip 	= new JTextField();
	public static JTextField txt_port 	= new JTextField("18332");
	public static JTextField txt_uName	= new JTextField();
	public static JTextField txt_pw 	= new JTextField();
	public static JTextField txt_timeOut= new JTextField("3");
	public static JCheckBox  btn_auth	= new JCheckBox("Authentication Method: Cookie "); 
	public static JButton 	 btn_path	= new JButton("Bitcoin Core Path:");
	public static JLabel	 lbl_path	= new JLabel("user.dir");
	public static JTextArea  txt_meld 	= new JTextArea();

	
	public GUI_CoreSettings(int x, int y) 
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("BitcoinCore connection settings");
		setBounds(x, y, 720, 400);
		setModal(true);
		JPanel  contentPane	= new JPanel(new BorderLayout());
		JPanel 	pnl_haupt 	= new JPanel(null);
		JScrollPane sp		= new JScrollPane();
		JButton btn_cTest 	= new JButton("Check connection");
		JTextArea lbl_info  = new JTextArea("This program must be connected to a Bitcoin-Core.\nEnter the RPC connection data for the Bitcoin-Core here.");
		
		lbl_info	.setBounds(10, 11, 699, 38);
		txt_ip		.setBounds(10, 105, 300, 40);
		txt_port	.setBounds(320,105, 100, 40);
		txt_uName	.setBounds(10, 155,410, 40);
		txt_pw		.setBounds(10, 205,410, 40);
		txt_timeOut	.setBounds(550, 105, 150, 40);
		btn_cTest	.setBounds(550, 200, 144, 23);
		btn_path	.setBounds(240, 65, 120, 20);
		lbl_path	.setBounds(365, 65, 345, 20);
		btn_auth	.setBounds(12, 65, 225, 24);
		txt_meld	.setBounds(10, 227, 686, 209);
		
		txt_ip		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"IP-Address",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_port	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Port",			TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_uName	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"User name",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_pw		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Password",		TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_timeOut .setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"TimeOut (sec)",TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_meld	.setBorder(new EmptyBorder(8, 8, 8, 8));

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
		lbl_info	.setFont(GUI.font3);	
		lbl_info	.setEditable(false);
		txt_meld	.setEditable(false);
		btn_cTest	.setMargin(new Insets(0, 0, 0, 0));
		btn_path	.setMargin(new Insets(0, 0, 0, 0));
		btn_auth	.setMargin(new Insets(0, 0, 0, 0));
		sp			.setBorder(null);
		txt_meld	.setRows(5);
		
		sp.setViewportView(txt_meld); 
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
		contentPane.add(pnl_haupt,BorderLayout.CENTER);
		contentPane.add(sp,BorderLayout.SOUTH);
		getContentPane().add(contentPane);

			
		
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
					txt_ip.setText("");
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
				try 
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
					} 
					catch (Exception e1) 
					{
						txt_meld.setForeground(Color.red);
						txt_meld.setText("Connected, but password or username are wrong!\n"+str);
					}	
					txt_meld.grabFocus();
					txt_meld.setCaretPosition(0);
				} 
				catch (Exception e1) {txt_meld.setForeground(Color.red);  txt_meld.setText("No connect!\n"+e1.getMessage());} 
			}
		});	

	}	

	
	
	
	
	// Wird ausgeführt wenn die Authentications-Methode geändert wird.
	// Also bei Betätigung des Buttens "Authentication Method: Cookie" (btn_auth), oder bei jedem Start des Programmes.
	// Achtung, dadurch wird die Datei: ".cookie" im Bitcoin-Core Verzeichness geöffnet und ausgelesen. Diese Datei ist nur vorhanden, wenn der Core läuft!
	// Wenn die Datei vorhanden ist, Werden Benutzername und Passwort aus dieser Datei verwendet.
	// Benutzername ist:"__cookie__" und Passwort, das hinter dem ":"
	// Wenn der Butten deaktiviert wird, werden wieder Passwort und Username zur Eingabe verwendet.
	// In der GUI "BitcoinCore connection settings" wird entsprechend der Einstellung dieses Buttens verändert.
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
					txt_meld.setText    ("Authentication method: cookie");
					
					
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					GUI.txt_meld.setText(e.getMessage());
				}
			}
			else
			{			
				txt_meld.setForeground(Color.red);
				txt_meld.setText    ("Bitcoin-Core is not running, or the file path to the Bitcoin directory is not correct!\nSpecify the file path where the configuration file (bitcoin.conf) is stored.");
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
			txt_meld.setText    ("Authentication method: Sign-in\nYou have to enter the RPC credentials from the bitcoin.conf file of the Bitcoin core here.");
		}
	}
}