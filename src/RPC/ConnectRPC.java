package RPC;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




/********************************************************************************************************************
*	Version 1.4					   					 Autor: Mr. Maxwell   							vom 01.02.2024	*
*	Hier wird die RPC-Verbindung zum Bitcoin-Core über Port 8332 hergestellt.										*
*	- Links: https://developer.bitcoin.org/reference/rpc/index.html													*
*	- https://en.bitcoin.it/wiki/Original_Bitcoin_client/API_calls_list												*
*	Damit die Verbindung funktioniert muss die "bitcoin.conf" im Bitcoin-Verzeichnis angepasst werden!				*
*	Eine "bitcoin.conf" als Beispiel fürs TestNet liegt in diesem Projekt.											*
*	In diesem Projekt liegt auch ein Dokument "Anleiung.txt" mit weiteren Informationen über den Verbindungsaufbau.	*
*********************************************************************************************************************/



public class ConnectRPC 
{
		
	private String 	pw;					// Benutzername mit Passwort in Base64 codiert
	private String 	ip;					// IP-Adresse 
	private int		port;				// Port der IP-Adresse
	private int 	timeOut = 10;		// TimeOut bei Verbindugsversuch in sec.

	
/**	Konstruktor stellt ein Verbindung-Objekt her.
	@param ip IP-Adresse des Bitcoin-Cores als String, Beispiel: "192.168.178.10"
	@param port Der Port als Integer, Beispiel: 18332
	@param userName	Der Username für die Verbindung-Authentifizierung, so wie in der bitcoin.conf eingetragen
	@param Password Das Password für die Verbindung-Authentifizierung, so wie in der bitcoin.conf eingetragen  **/
	public ConnectRPC(String ip, int port, String userName, String Password)
	{
		String userPasswort = userName+":"+Password;
		pw = Base64.getEncoder().encodeToString(userPasswort.getBytes()); // Codiert UserName:Passwort nach Base64	
		this.ip  = ip;
		this.port= port;
	}
	

	
/**	Hauptmethode
	Sendet eine Message an den Core und empfängt die Antwort.
	Die richtigen Parameter sind der API-Dokumentation zu entnehmen: https://developer.bitcoin.org/reference/rpc/index.html
	@param rpc_OP der RPC-Operator der gesendet werden soll. z.B. "getbalance" 
	@param param Die Parameter müssen als JSONArray übergeben werden und variieren je nach RPC-Operator.
	Werden keine Parameter benötigt, wird "null" übergeben.
	1. Beispiel für getbalance: "[\"*\",6,true]".
	2. Beispiel für getbalance: ja.put("*");
								ja.put(6);
								ja.put(true);
	@return Gibt die Antwort vom Core als JSON-String zurück oder eine Fehlermeldung.
	Im Falle eines Fehlers wird ein normaler String der Fehlermeldung vom Core zurückgegeben.
	Oder falls der Core nicht antwortet eine Exception, oder gar nichts. **/
	public String get(String rpc_OP, JSONArray param) throws Exception
	{
		JSONObject jo = new JSONObject();
		jo.put("method", rpc_OP);
		jo.put("params", param);
		jo.put("id", 1);		
		String joStr = jo.toString();
		int len = joStr.length()+1;
	
		String str = 	"POST / HTTP/1.1\r\n" + 
						"Host: 127.0.0.1\r\n" + 
						"Connection: close\r\n" + 
						"Content-Type: application/json\r\n" + 
						"Authorization: Basic "+pw+"\r\n" + 
						"Content-Length: "+len+"\r\n" + 
						"\r\n" + 
						joStr + "\n";
														
		Socket soc = new Socket();
		soc.connect(new InetSocketAddress(ip, port), timeOut*1000);
		OutputStream out = soc.getOutputStream();																						
		DataInputStream in = new DataInputStream(soc.getInputStream());																						
		byte[] b1 = str.getBytes("utf-8");																		
		out.write(b1);																				
		byte[] b = in.readAllBytes();
		soc.close();		
		String strOut = new String(b);
		try {return strOut.substring(strOut.indexOf("{"));}
		catch(Exception e) {return strOut;}
	}
	
	
	/** Sezt die Verbindungs TimeOut Zeit in Sekunden. **/
	public void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}
	
	
	
	
	
// ----------------------- Optional: Spezifische get-Methoden mit diversen RPC-Befehlen, Vereinfacht die Parametereingabe etwas. -----------------------------	
// ----------------------- Diverse RPC-Befehle sind recht komplex aufgebaut. Die Optionalen get-Methoden dienen dazu die Parameter richtig zu übergeben.
// ----------------------- Die Methoden werden nicht genau Dokumentiert, das es sich um Funktionen des Bitcoin-Cores handelt, und dort Dokumentiert sind!

	
	
	

	
/** Scannt die UTXO Datenbank nach z.B. einer Adresse und gibt Informationen zu allen nicht ausgegebenen Transaktionen zurück. Z.B. alle TX/In-ID´s der betreffenden Adresse.
	Kann verwendet werden um den Betrag einer Adresse zu erfahren, oder um neue Transaktonen zu erstellen. Achtung, kann einige Minuten dauern!
	@param arg1 Kann: "start", "abort" oder "status" sein.   Mit "start" wird der Befehl ausgefürhrt.
	@param arg2 Kann: "addr" (Bitcoin-Adresse), "raw" (hex-Skript), "Compbo" (PubKey), "pkh" (PubKey), "sh" (Multi) sein. (Weitere sind möglich)
	@param arg3 Die BTC-Adresse, der Pubkey, oder das Script etc.
			Die Argumente sind als String-Array Definiert, da die Suche mehrere Objecte beinhalten kann. arg2 und arg3 muss die selbe Länge des String-Arrays haben!
	@return	JSONObject mit diversen Informationen. **/
	public JSONObject get_scantxoutset(String arg1, String[] arg2, String[] arg3) throws Exception 
	{
		JSONArray ja = new JSONArray();
		ja.put(arg1);	
		JSONArray ja1 = new JSONArray();	
		for(int i=0; i<arg2.length;i++)
		{
			ja1.put(arg2[i] + "(" + arg3[i] + ")");
		}
		ja.put(ja1);
		String str = get("scantxoutset",ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}
	}
	
	
	
	/**	Generiert eine neue unsignierte Transaktion. 
	 	Es können mehrere Ein und Ausgeänge verwendet werden.
	 	Die Array-Langen von txid, txPrevIndex, sequence, müssen gleich lang sein!
	 	Die Array-Langen von AddressOut und valueOut,     müssen gleich lang sein!
	 @param txid			String-Array mit allen Tx-ID´s der einzulösenden Transakton
	 @param txPrevIndex		Integer-Array mit allen Tx-Indexen. Das sind die Postionen der Outputs in der vorherigen Transaktion
	 @param sequence		Long-Array mit den Sequencen.	ffffffff = 4294967295l  
	 @param AddressOut		String-Array mit den Ziel-BTC-Adressen    
	 @param valueOut		double-Array mit den Beträgen für die Zieladressen
	 @param locktime		Locktime wird als einzelner Long als letztes übergeben.
	 @return JSONArray mit Daten vom Bitcoin-Core zurück kommen. 
	 Hinweis: An der Position des SigScript wird normalerweise zum Signieren das vorherige PK-Script eingefügt. 
	 Der Core liefert an der Stelle aber immer nur "00" zurück. Also ein Leeres Script mit der Länge Null.
	 Das PK-Script muss zum signieren also selbst hier eingefügt werden. **/
	public JSONObject get_createrawtransaction(String[] txid, int[] txPrevIndex, long[] sequence, String[] AddressOut, double[] valueOut, long locktime) throws Exception
	{
		int countIn  = txid.length;
		int countOut = AddressOut.length;
		JSONArray  ja_main = new JSONArray();
		JSONArray  ja_in   = new JSONArray();
		JSONArray  ja_out  = new JSONArray();
		for(int i=0; i<countIn; i++)
		{
			JSONObject joIn    = new JSONObject();
			joIn.put("txid", txid[i]);
			joIn.put("vout", txPrevIndex[i]);
			joIn.put("sequence", sequence[i]); 
			ja_in.put(i, joIn);
		}
		for(int i=0; i<countOut; i++)
		{
			JSONObject joOut   = new JSONObject();
			joOut.put(AddressOut[i], valueOut[i]);
			ja_out.put(joOut);
		}
		ja_main.put(0, ja_in);
		ja_main.put(1, ja_out);
		ja_main.put(2,locktime);
		String str = get("createrawtransaction", ja_main);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}	
	}
	
	
	
	/** Decodiert eine Raw-Tx und gibt sie als JSON-Object zurück
	@param rawTx Die Tx wird als Hex-String übergeben.
	@return JSONObject oder Fehler-Meldung  **/
	public JSONObject decoderawtransaction(String rawTx) throws Exception
	{
		JSONArray ja = new JSONArray();
		ja.put(rawTx);
		String str = get("decoderawtransaction", ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}	
	}	
	
	
	
	/** Testet eine feritig Signierte Transaktion ob sie vom Mempool akzeptiert werden würde.
	Die Tx wird dabei NICHT gesendet!
	@param rawTx Die Tx wird als Hex-String übergeben.
	@return JSONObject mit Informationen  **/
	public JSONObject testmempoolaccept(String rawTx) throws Exception
	{
		JSONArray ja = new JSONArray();
		JSONArray ja1 = new JSONArray();
		ja1.put(rawTx);
		ja.put(ja1);
		String str = get("testmempoolaccept", ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}	
	}
	
	
	
	/** Sendet eine feritig Signierte Transaktion ins Netzwerk
		@param rawTx Die Tx wird als Hex-String übergeben.
		@return TransakionsID oder Fehler-Meldung  **/
	public JSONObject sendrawtransaction(String rawTx) throws Exception
	{
		JSONArray ja = new JSONArray();
		ja.put(rawTx);
		String str = get("sendrawtransaction", ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}	
	}	
	
	
	
	/** Gibte eine RawTx zurück
	@param TxID wird als Hex-String übergeben.
	@return RawTx oder Fehler-Meldung  **/
	public JSONObject getrawtransaction(String txID) throws Exception
	{
		JSONArray ja = new JSONArray();
		ja.put(txID);
		String str = get("getrawtransaction", ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}	
	}	
	
	
	
	
	/** Gibt verschiedene statistische Informationen über einen Block zurück. Z.B. Infos über Tx-Gebühren etc.
	@param nr = Block-Nummer. Wird -1 übergeben, dann wird der letzte Block verwendet!
	@return JSON-Object mit Informationen oder Fehler-Meldung  **/
	public JSONObject getblockstats(int nr) throws Exception
	{
		JSONArray ja = new JSONArray();
		ja.put(nr);
		String str = get("getblockstats", ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}
	}	
	
	
	
	/** Gibt verschiedene statistische Informationen über einen Block zurück. Z.B. Infos über Tx-Gebühren etc.
	Diese Version der Methode gibt nur ausgewählte Ergebnise zurück, die in dieser Methode fest codiert sind!
	@param nr = Block-Nummer. 
	@return JSON-Object mit Informationen oder Fehler-Meldung  **/
	public JSONObject getblockstats_my(int nr) throws Exception
	{	
		JSONArray params = new JSONArray();	
		// ----------------------- Legt fest welche Ergebnisse später enthalten sein werden, kann beliebig erweitert werden
		params.put("minfeerate");
		params.put("avgfeerate");
		params.put("feerate_percentiles");
		//------------------------ Hier erweitern -------------------------------------------------//
		JSONArray ja = new JSONArray();
		ja.put(nr);
		ja.put(params);
		String str = get("getblockstats", ja);
		try 
		{
			return new JSONObject(str);
		} 
		catch (JSONException e) 
		{
			if(str.length()!=0) throw new JSONException(str);
			else 				throw new JSONException(e);
		}
	}	
	
}