package TxBuild;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JTextArea;



/********************************************************************************************************************
*	V1.0							 Autor: Mr. Maxwell   							vom 29.10.2023					*
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
		JTextArea  lbl_info = new JTextArea("This program must be connected to a Bitcoin-Core.\nEnter the RPC connection data for the Bitcoin-Core here.");
		setBounds(x, y, 735, 330);
		setModal(true);
		getContentPane().setLayout(null);
			
		lbl_info	.setBounds(10, 11, 699, 38);
		txt_ip		.setBounds(10, 100, 300, 40);
		txt_port	.setBounds(320,100, 100, 40);
		txt_uName	.setBounds(10, 150,410, 40);
		txt_pw		.setBounds(10, 200,410, 40);
		txt_timeOut.setBounds(609, 100, 100, 40);
		
		txt_ip		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"IP-Address",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_port	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Port",			TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_uName	.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"User name",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_pw		.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Password",		TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		txt_timeOut .setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"TimeOut (sec)",TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));

		lbl_info	.setEditable(false);
		lbl_info	.setBackground(GUI.color1);
		txt_ip 		.setBackground(Color.white);
		txt_port 	.setBackground(Color.white);	
		txt_uName	.setBackground(Color.white);	
		txt_pw 		.setBackground(Color.white);
		txt_timeOut	.setBackground(Color.white);

		lbl_info	.setForeground(GUI.color4);
		lbl_info	.setFont(GUI.font3);
		
		getContentPane().add(txt_ip);		
		getContentPane().add(txt_port);
		getContentPane().add(txt_uName);		
		getContentPane().add(txt_pw);
		getContentPane().add(lbl_info);
		getContentPane().add(txt_timeOut);
	}
}
