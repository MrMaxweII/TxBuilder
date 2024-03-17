package TxBuild;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JDialog;
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
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/********************************************************************************************************************
*	V1.1							 Autor: Mr. Maxwell   							vom 29.02.2024					*
*	Die GUI (JDialog) der BitcoinCore RPC Connections-Settings für den TxBuilder									*
********************************************************************************************************************/


public class GUI_CoreSettings extends JDialog 
{

	public static JTextField txt_ip 	= new JTextField();
	public static JTextField txt_port 	= new JTextField("0");
	public static JTextField txt_uName	= new JTextField();
	public static JTextField txt_pw 	= new JTextField();
	public static JTextField txt_timeOut= new JTextField("0");
	
	
	public GUI_CoreSettings(int x, int y) 
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("BitcoinCore connection settings");
		setBounds(x, y, 720, 371);
		setModal(true);
		JPanel  contentPane	= new JPanel(new BorderLayout());
		JPanel 	pnl_haupt 	= new JPanel(null);
		JScrollPane sp		= new JScrollPane();
		JButton btn_cTest 	= new JButton("Check connection");
		JTextArea  lbl_info = new JTextArea("This program must be connected to a Bitcoin-Core.\nEnter the RPC connection data for the Bitcoin-Core here.");
		JTextArea txt_meld 	= new JTextArea();
		
		lbl_info	.setBounds(10, 11, 699, 38);
		txt_ip		.setBounds(10, 85, 300, 40);
		txt_port	.setBounds(320,85, 100, 40);
		txt_uName	.setBounds(10, 135,410, 40);
		txt_pw		.setBounds(10, 185,410, 40);
		txt_timeOut	.setBounds(550, 85, 150, 40);
		btn_cTest	.setBounds(550, 200, 144, 23);
		txt_meld	.setBounds(8, 227, 686, 109);
		
		txt_ip		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"IP-Address",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_port	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Port",			TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_uName	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"User name",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_pw		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Password",		TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_timeOut .setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"TimeOut (sec)",TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_meld	.setBorder(new EmptyBorder(8, 8, 8, 8));

		lbl_info	.setEditable(false);
		lbl_info	.setBackground(GUI.color1);
		txt_ip 		.setBackground(Color.white);
		txt_port 	.setBackground(Color.white);	
		txt_uName	.setBackground(Color.white);	
		txt_pw 		.setBackground(Color.white);
		txt_timeOut	.setBackground(Color.white);
		txt_meld	.setBackground(GUI.color1);

		lbl_info	.setForeground(GUI.color4);
		txt_meld	.setForeground(Color.red);
		lbl_info	.setFont(GUI.font3);	
		txt_meld	.setEditable(false);
		btn_cTest	.setMargin(new Insets(0, 0, 0, 0));
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
		contentPane.add(pnl_haupt,BorderLayout.CENTER);
		contentPane.add(sp,BorderLayout.SOUTH);
		getContentPane().add(contentPane);

		
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
}