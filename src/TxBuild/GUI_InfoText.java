package TxBuild;


/***************************************************************
 * Hier ist nur der Info-Text für dieses Programm enthalten.	*
 ***************************************************************/

public class GUI_InfoText
{

	// <<<<<<<<<<< Achtung eclipse Windwos-Builer kann diese Zeile nicht anzeigen. Zum verwendes des Windows Builder muss sie ausgeblendet werden!
	final static String infoText = GUI.progName+"   Version: "+GUI.version+"\n\n"
			+ "- Unsigned Bitcoin transactions can be created\n"
			+ "- To sign the transactions, use the CoinAddressGenerator\n"
			+ "- Signed Bitcoin transactions can be sent and published\n"
			+ "- This program needs to be connected to your own BitcoinCore via RPC\n\n"
			+ "Quick Instructions:\n\n"
			+ "1.  Find out how to connect to the BitcoinCore via RPC\n"
			+ "2.  Go to the menu: <Settings / BitcoinCore connection settings>\n"
			+ "3.  Configure the settings and connect to your BitcoinCore\n"
			+ "4.  Set the number of transaction inputs and outputs: <Input count> and <Output count>\n"
			+ "5.  Enter all Bitcoin addresses for sender and recipient\n"
			+ "6.  Click on <load Tx-inputs from Blockchain>  It takes a while for all inputs to load\n"
			+ "7.  Set all transfer amounts\n"
			+ "8.  Pay attention to the fee! Any money that is not spent will be lost!\n"
			+ "9.  Check the unsig transaction with <View Transaction>\n"
			+ "10. Save the unsigned transaction to a USB stick or transfer the transaction via QR code\n"
			+ "11. Sign the unsigned transaction on an offline computer, use the program: CoinAddressGenerator\n"
			+ "12. Transfer the Signed Transaction back to this computer. A QR code can be used again.\n"
			+ "13. Send the Signed Transaction to the Bitcoin Network. Click on <Publish / Send signed transaction>";
}