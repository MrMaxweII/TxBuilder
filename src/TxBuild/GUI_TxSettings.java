package TxBuild;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigInteger;

import lib3001.crypt.Convert;
import lib3001.java.DocumentFilter;
import lib3001.java.Hover;



/********************************************************************************************************************
*	V1.4							 Autor: Mr. Maxwell   							vom 27.12.2025					*
*	Die GUI (JDialog) der Transaktions-Settings für den TxBuilder													*
********************************************************************************************************************/



public class GUI_TxSettings extends JDialog 
{

	public static JTextField 	txt_sequence = new JTextField("4294967295");
	public static JTextField 	txt_sequeHex = new JTextField("ffffffff");
	public static JTextField 	txt_locktime = new JTextField("0");
	public static JTextField 	txt_locktHex = new JTextField("00000000");

	
	
	public GUI_TxSettings(int x, int y)
	{
		
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(GUI.t.t("Transaction settings"));
		setModal(true);
		getContentPane().setLayout(null);
		setMinimumSize(new Dimension(600,250));
		if(GUI.btn_testNet.isSelected()) setIconImage(MyIcons.bitcoinLogoTest.getImage());			
		else 							 setIconImage(MyIcons.bitcoinLogoMain.getImage());	
		
		JTextArea 	lbl_info 		= new JTextArea(GUI.t.t("Change the default values only if you know exactly what you're doing!\nIncorrect values can lead to the loss of the coins!"));
		JTextArea 	lbl_sequence 	= new JTextArea(GUI.t.t("If sequence number is < 0xFFFFFFFF: Makes the transaction input Replace-By-Fee. Default:\"4294967295\" hex: \"ffffffff\""));
		JTextArea 	lbl_locktime 	= new JTextArea(GUI.t.t("Specifies the time at which the transaction should be executed.\nAt: \"00000000\", immediately."));

					 setBounds(x,    y,950, 330);
		lbl_info	.setBounds(10,  11,800, 38);
		txt_sequence.setBounds(10, 100, 88, 40);
		txt_sequeHex.setBounds(108,100, 74, 40);
		txt_locktime.setBounds(10, 184, 88, 40);
		txt_locktHex.setBounds(108,184, 74, 40);
		lbl_sequence.setBounds(201, 97,800, 70);
		lbl_locktime.setBounds(201,179,800, 100);

		txt_sequence.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Sequence", TitledBorder.LEADING, 	TitledBorder.TOP, GUI.font2, GUI.color3));
		txt_sequeHex.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Hex", 		TitledBorder.LEADING, 	TitledBorder.TOP, GUI.font2, GUI.color3));
		txt_locktime.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Locktime", TitledBorder.LEADING, 	TitledBorder.TOP, GUI.font2, GUI.color3));
		txt_locktHex.setBorder(new TitledBorder(new LineBorder(GUI.color4), 	"Hex", 		TitledBorder.LEADING, 	TitledBorder.TOP, GUI.font2, GUI.color3));

		lbl_info	.setEditable(false);
		lbl_sequence.setEditable(false);
		lbl_locktime.setEditable(false);
		txt_sequeHex.setEditable(false);
		txt_locktHex.setEditable(false);

		getContentPane().setBackground(GUI.color1);
		lbl_info		.setForeground(GUI.color4);
		lbl_sequence	.setForeground(GUI.color4);
		lbl_locktime	.setForeground(GUI.color4);
		lbl_info		.setBackground(GUI.color1);
		lbl_sequence	.setBackground(GUI.color1);
		lbl_locktime	.setBackground(GUI.color1);
		txt_sequeHex	.setBackground(GUI.color1);
		txt_locktHex	.setBackground(GUI.color1);
		
		txt_sequence	.setBackground(Color.white);
		txt_locktime	.setBackground(Color.white);
		
		lbl_info		.setFont(GUI.font3);
		lbl_sequence	.setFont(GUI.font3);
		lbl_locktime	.setFont(GUI.font3);
		txt_sequence	.setFont(GUI.font4);
		txt_sequeHex	.setFont(GUI.font4);
		txt_locktime	.setFont(GUI.font4);
		txt_locktHex	.setFont(GUI.font4);
	
		Hover.addBorder(txt_sequence);
		Hover.addBorder(txt_locktime);
	
		DocumentFilter df1 = new DocumentFilter("longFilterPositiv");  // DocumentenFilter (Eingabe-Prüfung) 		
		df1.setLongMaxMinValue(0,4294967295L);
		txt_sequence.setDocument(df1);
		txt_sequence.setText(GUI_TxSettings.hexToSequence(txt_sequeHex.getText())); // Inhalt muss neu gesetzt werden, da der DocumentFilder das Feld löscht!

		DocumentFilter df2 = new DocumentFilter("longFilterPositiv");  // DocumentenFilter (Eingabe-Prüfung) 		
		df2.setLongMaxMinValue(0,4294967295L);
		txt_locktime.setDocument(df2);
		txt_locktime.setText(GUI_TxSettings.hexToSequence(txt_locktHex.getText()));  // Inhalt muss neu gesetzt werden, da der DocumentFilder das Feld löscht!
		
		getContentPane().add(txt_sequence);
		getContentPane().add(txt_sequeHex);
		getContentPane().add(txt_locktime);
		getContentPane().add(txt_locktHex);
		getContentPane().add(lbl_info);
		getContentPane().add(lbl_sequence);
		getContentPane().add(lbl_locktime);
		
		
		
// -------------------------------------------------------------- Actions ---------------------------------------------------------------------		
		
		
		txt_sequence.getDocument().addDocumentListener(new DocumentListener()
        {

            public void changedUpdate(DocumentEvent arg0) 
            {
            }
            public void insertUpdate(DocumentEvent arg0) 
            {
            	txt_sequeHex.setText(sequenceToHex(txt_sequence.getText()));
            }
            public void removeUpdate(DocumentEvent arg0) 
            {
            	txt_sequeHex.setText(sequenceToHex(txt_sequence.getText()));
            }
        });
		
		
		txt_locktime.getDocument().addDocumentListener(new DocumentListener()
        {

            public void changedUpdate(DocumentEvent arg0) 
            {
            }
            public void insertUpdate(DocumentEvent arg0) 
            {
            	txt_locktHex.setText(sequenceToHex(txt_locktime.getText()));
            }
            public void removeUpdate(DocumentEvent arg0) 
            {
            	txt_locktHex.setText(sequenceToHex(txt_locktime.getText()));
            }
        });
	}
	
	
	
// ----------------------------------------------------------------------- Hilfsmethoden --------------------------------------------------------------	
	
	
	// Konvertiert den inhalt vom txt_sequence (Zahl zur Basis 10) nach Hexa.
	// Die Konvertierung im Feld txt_sequeHex erfolgt nach BTC-Protokoll. Das Feld txt_sequeHex entspricht dann dem dem Feld "sequence" einer Standard-Transaktion.
	private String sequenceToHex(String in)
	{
		if(in.equals("")) return "00000000";
		BigInteger bi = new BigInteger(in);
		byte[] b = Convert.hexStringToByteArray_oddLength(bi.toString(16));
		b = Convert.to_fixLength(b, 4);		
		Convert.swapBytes(b);
		b = Convert.to_fixLength(b, 4);
		return Convert.byteArrayToHexString(b);	
	}
	
	
	// Konvertiert einen 4-Byte Hex-String (aus dem BTC-Feld Sequence) in eine Zahl zur Basis-10 die als String zurück gegeben wird.
	public static String hexToSequence(String in)
	{
		byte[] b = Convert.hexStringToByteArray(in);
		Convert.swapBytes(b);
		BigInteger bi = new BigInteger(1,b);
		return bi.toString();
	}
}