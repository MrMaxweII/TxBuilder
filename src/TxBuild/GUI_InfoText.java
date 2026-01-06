package TxBuild;


/***************************************************************
 * Hier ist nur der Info-Text für dieses Programm enthalten.	*
 ***************************************************************/

public class GUI_InfoText
{

	// Info Text mit der Beschreibung des Programmes in English
	final static String infoTextEN = GUI.progName+"   Version: "+GUI.version+"\n\n"
			+ "- Latest compatible tested core version: v0.21.1\n"
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
	
	
	
	
	
	// Info Text mit der Beschreibung des Programmes in Deutsch
	final static String infoTextDE = GUI.progName+"   Version: "+GUI.version+"\n\n"
			+ "- Neuste kompatibel getestete Core Version: v0.21.1\n"
			+ "- Unsignierte Bitcoin-Transaktionen können erstellt werden\n"
			+ "- Um die Transaktionen zu signieren, verwenden Sie den CoinAddressGenerator\n"
			+ "- Signierte Bitcoin-Transaktionen können gesendet und veröffentlicht werden\n"
			+ "- Dieses Programm muss über RPC mit Ihrem eigenen BitcoinCore verbunden sein\n\n"
			+ "Kurzanleitung:\n\n"
			+ "1. Informieren Sie sich über die RPC-Verbindung des Bitcoin-Core, und konfigurieren Sie ggf. die bitcoin.conf Datei.\n"
			+ "2. Gehen Sie zum Menü: <Einstellungen / Bitcoin-Core Verbindungseinstellungen>\n"
			+ "3. Konfigurieren Sie die Einstellungen und verbinden Sie sich mit Ihrem Bitcoin-Core\n"
			+ "4. Stellen Sie die Anzahl der Transaktions-Eingänge und -Ausgänge ein: (Tx-Eingänge) und (Tx-Ausgänge)\n"
			+ "5. Geben Sie alle Bitcoin-Adressen für Absender und Empfänger ein\n"
			+ "6. Klicken Sie auf <Tx-Eingänge aus der Blockchain laden> Es dauert eine Weile, bis alle Eingaben geladen sind\n"
			+ "7. Tragen Sie alle Überweisungs-Beträge (in BTC) ein\n"
			+ "8. Achten Sie auf die Tx-Gebühr! Alle Bitcoins, die nicht an einen Empfänger gesendet werden, sind Gebühren, gehen also verloren!\n"
			+ "9. Überprüfen Sie die unsignierte Transaktion mit <Transaktion anzeigen>\n"
			+ "10. Speichern Sie die unsignierte Transaktion auf einem USB-Stick, oder übertragen Sie die Transaktion mit dem QR-Code, an einen offline Computer\n"
			+ "11. Signieren Sie die unsignierte Transaktion mit einem offline Computer, verwenden Sie dazu das Programm: CoinAddressGenerator\n"
			+ "12. Übertragen Sie die signierte Transaktion zurück auf diesen Computer. Ein QR-Code oder USB-Stick kann dazu verwendet werden.\n"
			+ "13. Senden Sie die signierte Transaktion an das Bitcoin-Netzwerk. Klicken Sie auf <Funktion>/<Signierte Transaktion senden>, öffnen Sie ihre Transaktion und senden Sie sie.";
					
}