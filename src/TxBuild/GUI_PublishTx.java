package TxBuild;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;
import BTClib3001.Convert;
import BTClib3001.Transaktion;
import BTClib3001.TxPrinter;
import RPC.ConnectRPC;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JOptionPane;



/********************************************************************************************************************
*	V1.1							 Autor: Mr. Maxwell   							vom 29.10.2023					*
*	Die GUI (JDialog). Sendet signierte Transaktionen in das Netzwerk per RPC										*
********************************************************************************************************************/


public class GUI_PublishTx extends JDialog 
{
	
	public final static byte[] MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	public final static byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};
	
	JDialog frame;							// Wird für den QR-Code benötigt.

	public GUI_PublishTx(int x, int y) 
	{
		frame = this;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Publish Transaction");
		setBounds(x, y, 1040, 450);
		setModal(true);

		JTextPane  	lbl_info 	= new JTextPane();
		JTextPane 	txt_meld 	= new JTextPane();
		JLabel 		lbl_file_sigTx = new JLabel("user.dir"); // Das Label des Speicherorts der SigTx
		JTextArea 	txt_tx 		= new JTextArea();
		JButton 	btn_scanQR 	= new JButton("scan QR code");
		JButton 	btn_openTx 	= new JButton("open Tx");
		JButton 	btn_showTx 	= new JButton("show Tx");
		JButton 	btn_send 	= new JButton("send Tx");
		JPanel 		pnl_1 		= new JPanel();
		JPanel 		pnl_2 		= new JPanel();
		JPanel 		pnl_3 		= new JPanel();
		JScrollPane scrollPane 	= new JScrollPane();

		lbl_info		.setForeground(GUI.color4);
		btn_send		.setForeground(Color.RED);
		txt_meld		.setForeground(Color.RED);

		txt_tx			.setBackground(Color.WHITE);
		lbl_info		.setBackground(GUI.color1);
		txt_meld		.setBackground(GUI.color1);
		lbl_file_sigTx	.setBackground(GUI.color1);	
		
		lbl_info		.setFont(GUI.font3);
		btn_scanQR		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_openTx		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_showTx		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		btn_send		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		lbl_file_sigTx	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		txt_tx			.setFont(new Font("Consolas", Font.PLAIN, 11));

		lbl_file_sigTx	.setBounds(95, 6, 1000, 18);

		lbl_info		.setText("Signed transactions can be sent to the network here.\r\nThis action can never be reprimanded.\r\nCheck the transaction very carefully!");
		lbl_file_sigTx	.setText("user.dir");	

		scrollPane		.setBorder(null);
		txt_tx			.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Insert signed transactions into this field",	TitledBorder.LEADING, 	TitledBorder.TOP, 		GUI.font2, GUI.color3));
		
		btn_scanQR		.setPreferredSize(new Dimension(110, 19));
		btn_openTx		.setPreferredSize(new Dimension(85, 19));
		btn_showTx		.setPreferredSize(new Dimension(85, 19));
		btn_send		.setPreferredSize(new Dimension(85, 19));

		btn_scanQR		.setMargin(new Insets(0, 0, 0, 0));
		btn_openTx		.setMargin(new Insets(0, 0, 0, 0));
		btn_showTx		.setMargin(new Insets(0, 0, 0, 0));
		btn_send		.setMargin(new Insets(0, 0, 0, 0));

		lbl_info		.setEditable(false);
		txt_tx			.setLineWrap(true);
		
		scrollPane		.setViewportView(txt_tx);
		scrollPane		.setColumnHeaderView(pnl_1);
		
		pnl_1			.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnl_2			.setLayout(new BorderLayout(0, 0));
		pnl_3			.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnl_1			.add(btn_scanQR);
		pnl_1			.add(btn_openTx);
		pnl_1			.add(lbl_file_sigTx);
		pnl_2			.add(pnl_3, BorderLayout.NORTH);
		pnl_2			.add(txt_meld, BorderLayout.CENTER);
		pnl_3			.add(btn_showTx);
		pnl_3			.add(btn_send);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(lbl_info, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(pnl_2, BorderLayout.SOUTH);	
		
		
		
		
// -------------------------------------------------------------------------- Actions -----------------------------------------------------------------------------		
		
		
		
		
		
	// Scanct die Tx als QR-Code mit einer Kamera
	btn_scanQR.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			Thread t = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{														
					try
					{
						frame.setEnabled(false);
						QrCapture qr = new QrCapture(frame,"Scan Sign Transaction", getX()+50, getY()+80);	
						String p2 = qr.getResult();
						qr.close();								
						if(p2.equals("")) throw new IOException("User abort");								
						txt_tx.setText(p2);
					}
					catch(Exception ex) 
					{
						txt_meld.setText(ex.getMessage());
					};	
					frame.setEnabled(true);
				}
			});
			t.start();	
		}
	});			
	
	
	
	

	// Öffnet mit dem JFileChooser die sig.Tx.
	btn_openTx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			String userDir = System.getProperty("user.home");			
			JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
			chooser.setFileFilter(new FileNameExtensionFilter("signed.txn", "txn"));			
			chooser.setCurrentDirectory(new File(lbl_file_sigTx.getText()));							
			int button = chooser.showOpenDialog(txt_tx);		
			if(button==0)																					
			{
				lbl_file_sigTx.setText(chooser.getSelectedFile().getAbsolutePath());	
				try 
				{
					BufferedReader br = new BufferedReader(new FileReader(lbl_file_sigTx.getText()));
					String str = "";
					while(br.ready()) str = str +br.readLine();	
					br.close();				
					txt_tx.setText(str);
					txt_meld.setText("");
				} 
				catch (Exception e1) 
				{
					txt_meld.setText(e1.getMessage());
					e1.printStackTrace();
				}			
			}			
		}
	});		
	
	
	
	// Signierte Transaktion wird mit dem TxPrinter angezeigt.
	btn_showTx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				Transaktion sigTx = new Transaktion(Convert.hexStringToByteArray(txt_tx.getText()),0); 
				byte[] magic;
				if(GUI.btn_testNet.isSelected()) magic = TESTNET3;
				else							 magic = MAINNET;
				TxPrinter tx = new TxPrinter(magic, sigTx, getX()+5, getY()+30);
				tx.setModal(true);
				tx.setVisible(true);
			}
			catch(Exception ex) 
			{	
				txt_meld.setText("Tx Error: No correct transaction!\n"+ex.getMessage());
				ex.printStackTrace();
			}
		}
	});
	
	
	
	// Sendet die Transaktion ins Netzwerk (RPC)
	btn_send.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try 
			{
				txt_meld.setText("");
				String 	ip 	 = GUI_CoreSettings.txt_ip.getText();
				int 	port = Integer.valueOf(GUI_CoreSettings.txt_port.getText());
				String 	name = GUI_CoreSettings.txt_uName.getText();
				String 	pw 	 = GUI_CoreSettings.txt_pw.getText();
				ConnectRPC core = new ConnectRPC(ip,port,name,pw);
				core.setTimeOut(Integer.valueOf(GUI_CoreSettings.txt_timeOut.getText()));
				JSONObject jo = core.testmempoolaccept(txt_tx.getText());
				System.out.println(jo.toString(1));
				if( (jo.optJSONObject("error"))!=null )	throw new Exception(jo.getJSONObject("error").toString(1));								// Wenn der Core die Tx nicht parsen kann
				else																															// Wenn der Core die Tx richtig geparst hat
				{
					JSONArray ja = jo.getJSONArray("result");
					if(ja.getJSONObject(0).getBoolean("allowed")==false) throw new Exception(ja.getJSONObject(0).toString(1));					// Wenn der Core die Tx nicht akzeptiert. (Verstoß gegetn Konsensregln etc.)
					if(JOptionPane.showConfirmDialog(txt_tx, "With OK, the transaction will be send irreversibly!","Send Transaction", 2,2)==0)
					{
						jo = core.sendrawtransaction(txt_tx.getText());
						if( (jo.optJSONObject("error"))!=null )	throw new Exception(jo.getJSONObject("error").toString(1));						// Wenn der Core beim Senden einen Fehler zurückgibt.
						else JOptionPane.showMessageDialog(txt_tx, "Success! Transaction has been send!\n"+jo.toString(1),"Success",1); 		// Wenn Tx erfolgreich gesendet wurde.
					}	
				}
			} 
			catch (Exception e1) 
			{
				txt_meld.setText("Tx Error: No correct transaction!\n"+e1.getMessage());
			}
		}
	});
}
}