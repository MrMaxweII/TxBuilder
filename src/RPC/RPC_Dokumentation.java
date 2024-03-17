package RPC;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;



/************************************************************************************************************
*	Version 1.0					   			 Autor: Mr. Maxwell   					vom 15.10.2021			*
*	Beispiel Klasse die eine RPC-Verbindung zum BitcoinCore über den Port 18332 herstellt.					*
*	Diese Klasse dient nur der Dokumentation und darf nicht gelöscht oder verändert werden!					*
*	Diese Klasse ist in keinem Project eingebunden.															*
*	Links: https://developer.bitcoin.org/reference/rpc/index.html											*
*																											*
*	Damit die Verbindung funktioniert muss die "bitcoin.conf" im Bitcoin-Verzeichnis angepasst werden!		*
*	Eine "bitcoin.conf" als Beispiel fürs Testnet liegt in diesem Projekt.									*
*	Die bitcoin.conf fürs Testnet muss min. folgende Einträge haben.										*
*																											*
*	server=1																								*
*	rpcport=18332																							*
*	# IP-Adresse des eigenen PC´s auf dem Bitcoin Core läuft												*
*	rpcbind=192.168.178.98																					*
*	# IP-Adresse des PC´s der über RPC auf den BitcoinCore zugreifen will 									*
*	rpcallowip=192.168.178.36																				*
*	rpcuser= ***																							*
*	rpcpassword= ***																						*
*	chain=test																								*
************************************************************************************************************/



public class RPC_Dokumentation 
{


	public static void main(String[] args) throws Exception
	{
		connectExample("192.168.178.31",8332,"Maxwell","***");
	}	
	
	

	
/**	Stellt die RPC-Verbindung zum BitcoinCore her und gibt die Antwort auf dem Bildschirm aus.
	Dies ist eine Beispielmethode um die Syntax zu dokumentieren, Methode nicht ändern oder löschen!
	@param ip IP-Adresse des Cores als String, Beispiel: "192.168.178.10"
	@param port Der Port als Integer, Beispiel: "18332"
	@param userName	Der Username für die Verbindungs-Authentifizierung, so wie in der bitcoin.conf eingetragen
	@param Password Das Password für die Verbindungs-Authentifizierung, so wie in der bitcoin.conf eingetragen  
 * @throws Exception **/
	public static void connectExample(String ip, int port, String userName, String Password) throws Exception
	{
		String userPasswort = userName+":"+Password;
		String pw = Base64.getEncoder().encodeToString(userPasswort.getBytes()); // Codiert UserName:Passwort nach Base64
				
		String str = 	"POST / HTTP/1.1\r\n" + 
						"Host: 127.0.0.1\r\n" + 
						"Connection: close\r\n" + 
						"Content-Type: application/json\r\n" + 
						"Authorization: Basic "+pw+"\r\n" + 
						"Content-Length: 49\r\n" + 
						"\r\n" + 
						"{\"method\":\"getbestblockhash\",\"params\":[],\"id\":1}\n";
		
		byte[] b1 = str.getBytes("utf-8");	
		Socket soc = new Socket();
		soc.connect(new InetSocketAddress(ip, port));																		
		OutputStream out = soc.getOutputStream();																						
		out.write(b1);																				
		DataInputStream in = new DataInputStream(soc.getInputStream());																						
		byte[] b = in.readAllBytes();
		System.out.println(new String(b));
		soc.close();
	}
}







// ------------------------------------------ Backup Verbindung-String -----------------------------------------------------

//String str = "POST / HTTP/1.1\r\n" + 
//"Host: 127.0.0.1\r\n" + 
//"Connection: close\r\n" + 
//"Content-Type: application/json\r\n" + 
//"Authorization: Basic eHh4Onh4eA==\r\n" + 
//"Content-Length: 49\r\n" + 
//"\r\n" + 
//"{\"method\":\"getbestblockhash\",\"params\":[],\"id\":1}\n";
//byte[] b1 = str.getBytes("utf-8");

