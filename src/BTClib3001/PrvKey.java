package BTClib3001;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;



/***********************************************************************************************************************
*		Version 2.5    						Autor: Mr. Maxwell   						vom 23.12.2023					*
*																														*
*		Letzte Änderung: getHash160_RedeemScript_P2SH() hinzugefügt														*
*		BTClib3001 Klasse																								*
*		Nicht statische Klasse die ein Private-Key Object erstellt.	Für verschiedene Cois ausgelegt.					*
*		Es sind mehrere Konstrukor´s implementiert die den Private Key aus diversen Formaten erkennen.					*
*		Dem Konstruktor müssen zusätzlich die CoinParameter des jeweiligen Coin´s übergeben werden.						*
*		Siehe CoinParameter Class																						*
*		Die Bitcoin Adresse wird nur als hash160 erzeugt und sollte dann von der BitcoinAddr-Kasse verarbeitet werden.	*
*		Info: https://en.bitcoin.it/wiki/Address																		*
*************************************************************************************************************************/



public class PrvKey
{

	private byte[] 	prvKey;					// Der Priv.Key als 32Byte-Array
	private byte[] 	pref_PrivKey;			// Das Prefix Private-Key aus den Coin-Parametern zur Identifizierung des jeweiligen Coin Private-Key´s. Siehe CoinParameter Klasse
	private int 	compressed = 0;			// gibt an, ob der Private-Key in "compressed" Format übergeben wurde:   0 = ohne Format Angabe,  1 = compressed,   2 = uncompressed







// ---------------------------------------------------- Konstruktoren -------------------------------------------------------------

/**	@param privKey Dem Konstruktor wird der Priv.Key als 32Byte Array im rohen Hexa-Format übergeben.
	@param pref_PrivKey Die CoinParameter müssen übergeben werden, zur Identifizierung des jeweiligen Coin. Siehe CoinParameter Class
	Bitcoin-MainNet=0x80, Bitcoin-TestNet=0xEF   **/
public PrvKey(byte[] privKey, byte[] pref_PrivKey) throws IllegalArgumentException
{
	this.pref_PrivKey = pref_PrivKey;
	checkHexPrivKey(privKey);
	this.prvKey = privKey;
}



/**	@param privKey Dem Konstruktor wird ein beliebiger Text-String übergeben, der Priv.Key entsteht dann durch den Hash von 1xSHA256
	@param coinParameter Die CoinParameter müssen übergeben werden, zur Identifizierung des jeweiligen Coin. Siehe CoinParameter Class. Bitcoin-MainNet=0x80, Bitcoin-TestNet=0xEF,
	@param TRUE Das dritte Argument wird nicht benutzt, und kann true oder false sein.	 **/
public PrvKey(String privKey, byte[] pref_PrivKey, boolean TRUE) throws UnsupportedEncodingException
{
	this.pref_PrivKey = pref_PrivKey;
	this.prvKey = 	Calc.getHashSHA256((privKey).getBytes("UTF-8"));
	checkHexPrivKey(prvKey);
}



/**	@param privKey Dem Konstruktor wird der Priv.Key als String übergeben, das richtige Format wird selbst erkannt.
	Es sind folgende Formate möglich: Hex, Base58, Base58-Compressed, Base64, Base6.
	@param coinParameter Die CoinParameter müssen übergeben werden, zur Identifizierung des jeweiligen Coin. Siehe CoinParameter Class. Bitcoin-MainNet=0x80, Bitcoin-TestNet=0xEF   **/
public PrvKey(String privKey, byte[] pref_PrivKey) throws IllegalArgumentException
{
	this.pref_PrivKey = pref_PrivKey;
	byte[]  prvKey = txtToHexPrivKey(privKey);
	checkHexPrivKey(prvKey);
	this.prvKey = prvKey;
}





// ------------------------------------------- public Methoden ------------------------------------------

/**	Gibt an, ob der Private-Key in "compressed" Format übergeben wurde:
	@return   0 = ohne Format Angabe,
	1 = compressed,
	2 = uncompressed **/
public int getCompressed()
{
	return compressed;
}


/**	@return Gibt den Priv.Key 32 Byte-Array **/
public byte[] getHexPrivKey()
{
	return prvKey;
}


/**	@return Gibt den Priv.Key als Hex-String zurück 64Zeichen.	*/
@Override
public String toString()
{
	return Convert.byteArrayToHexString(prvKey);
}


/** @return Gibt den Priv.Key als Base64 String zurück.  */
public String getBase64PrivKey()
{
	return Base64.getEncoder().encodeToString(prvKey);
}



/**	@param compressed wenn true, wird im compressed Format codiert, sonnst im uncompressed Format.
	@return Gibt den Priv.Key als Base58 String zurück. **/
public String getBase58PrivKey(boolean compressed)
{
	String prefix = Convert.byteArrayToHexString(pref_PrivKey);
	String com = "";
	if(compressed) com = "01";
	String str = Convert.byteArrayToHexString(prvKey);
	String privKey = prefix + str + com;
	String hash = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(privKey));
 	hash = hash.substring(0,8);
	return Convert.hexStringToBase58(privKey + hash);
}



/**	@param compressed wenn "true" dann wird der Pub-Key komprimiert (nur X-Koordinate), wenn "false" dann unkomprimiert mit x und y Koordinaten.
	@return Gibt den Public Key als Byte-Array zurück.
	Das Erste Byte ist ein Status-Byte mit den folgenden Informationen.
	02 : komprimierter Pub-Key, enthält nur die X-Koordinate, die Y-Koordinate ist positiv (33Byte)
	03 : komprimierter Pub-Key, enthält nur die X-Koordinate, die Y-Koordinate ist negativ (33Byte)
	04 : unkomprimierter Pub-Key mit X und Y Koordinaten (65Byte) **/
public byte[] getPubKey(boolean compressed)
{
	return Calc.getPublicKey(prvKey, compressed);
}



/**	Es wird die Bitcoin-Adresse im Hash160 Format berechnet.
	@param compressed wenn "true" dann wird mit komprimierten Public-Key berechnet.
	@return	Gibt die BitcoinAdresse als Hash160 in Byte-Array zurück, so wie sie in der BitcoinAddr-Klasse weiterverarbeitet werden kann.
	Weitere Formate der Bitcoin-Adresse werden in der BitcoinAddr-Klasse vorgenommen.	**/
public byte[] getHash160BitcoinAddress(boolean compressed)
{
	byte[] pub = getPubKey(compressed);
	return	Calc.getHashRIPEMD160(Calc.getHashSHA256(pub));
}


/**	Hash160 (RedeemScripot) für P2SH-Adressen.
Achtung! Dieser Hash160 unterscheidet sich vom legancy-Hash160 und ist nur bei P2SH gültig. 
Es handelt sich hierbei um das RedeemScript und eigentlich nicht um einen Hash160. 
Wird die Bitcoin-Adresse aus diesem Has160 gebildet, handelt es sich um eine Adresse mit "3" am Anfang **/
public byte[] getHash160_RedeemScript_P2SH()
{
	return getRedeemScript(getHash160BitcoinAddress(true));
}










//------------------------------ Private Konstruktor Hilfsmethoden ---------------------------------------------



// Hier wird geprüft ob der Hexa-Private-Key das richtige Formate hat und innerhalb des erlaubten Zahlenbereiches für ECDSA ist.
// Diese Methode löst nur Exceptions aus, damit der jeweilige Fehler genauer gekennzeichnet wird.
private void checkHexPrivKey(byte[] prv) throws IllegalArgumentException
{
	if((prv == null) || prv.equals(null)) 				throw new IllegalArgumentException("Error in \"PrvKey\": Private Key is NULL!");
	if(prv.length!=32) 					throw new IllegalArgumentException("Error in \"PrvKey\": False size!");
	BigInteger min = new BigInteger("0",16);
	BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140",16);
	BigInteger key = new BigInteger(1,prv);
	if(key.compareTo(min) <= 0) 		throw new IllegalArgumentException("Error in \"PrvKey\": Private Key is <= 0!");
	if(key.compareTo(max) >  0) 		throw new IllegalArgumentException("Error in \"PrvKey\": Private Key is > Max!");
}



// erkennt den String als private-Key in den 3 möglichen Formaten, und gibt ihn als Hexa-PrivateKey zurück
// Hexa, Base58, Base6
private byte[] txtToHexPrivKey(String str) throws IllegalArgumentException
{
	str = str.trim();
	int format = getFormat(str);
	switch(format)
	{
		case-1: 	throw new IllegalArgumentException("Error in \"PrvKey\": false format");						//-1 = Fehler kein richtiges Format erkannt
		case 0:     throw new IllegalArgumentException("Error in \"PrvKey\": Null-String");							// 0 = Null String
		case 16:  	return Convert.hexStringToByteArray(str);  														// 16 = Hexa
		case 58:  	return base58_PrivateKey_to_HexPrivateKey(str);													// 58 = Base58
		case 6:     return Convert.hexStringToByteArray(base6_PrivateKey_to_HexPrivateKey(str));  					// 6 = Base6
		default:	break;
	}
	return null;
}



// gibt das Format des Strings zurück
// -1 = Fehler kein richtiges Format erkannt
// 0 = Null String
// 16 = Hexa
// 58 = Base58
// 6 = Base6
private int getFormat(String str)
{
	if(str.equals(""))   																												return 0;	// prüfen ob leer String
	if(str.length()==64 && 	str.matches("[0-9a-fA-F]+")) 																				return 16;	// prüfen auf Hexa
	if(						str.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+")) 								return 58;	// prüfen auf Base58
	if(str.length()==109 && str.matches("[123456-]+")) 																					return 6;	// prüfen auf Base6
	return -1;
}



// Bekommt den PrivateKey in Base 58 übergeben
// - Wird in Hexa konvertiert mit allen Status-Byes und dem Hash
// - Anhand der CoinParameter wird der Coin-Typ identifiziert
// - Nun wird der Key in die entsprechenden Teile zerlegt: [Status-Bytes | Raw-PrivateKey | compressed-byte | 4byte Hash]
// - Die Status-Bytes und der Hash, werden geprüft.
// - Wenn alles richtig ist, wird der raw-PrivateKey ohne die status-Bytes und ohne den Hash  zurück gegeben.
// dabei werden alle status-bytes und Hashes entfernt
private byte[] base58_PrivateKey_to_HexPrivateKey(String str) throws IllegalArgumentException
{
	byte[] prv = Convert.removeLeadingZeros(Convert.hexStringToByteArray_oddLength(Convert.Base58ToHexString(str,str.length()*2)));
	if(!Arrays.equals(pref_PrivKey, Arrays.copyOfRange(prv,0,pref_PrivKey.length)))	throw new IllegalArgumentException("Error in \"PrvKey\": Private key has an incorrect coin-parameter!");
	byte[] h32 = Calc.getHashSHA256(Calc.getHashSHA256(Arrays.copyOfRange(prv, 0, prv.length-4)));		// 32Byte langer Hash
	byte[] h4  = Arrays.copyOfRange(h32,0,4);															// 4Byte langer Hash
	byte[] hash = Arrays.copyOfRange(prv, prv.length-4, prv.length);									// Angehängter original Hash
	if(!Arrays.equals(hash, h4))   										throw new IllegalArgumentException("Error in \"PrvKey\": Private key hash incorrect!");
	prv = Arrays.copyOfRange(prv, pref_PrivKey.length, prv.length-4);
	if(prv.length>33 || prv.length<32)											throw new IllegalArgumentException("Error in \"PrvKey\": Private key size incorrect!");
	if(prv.length==32)	{ compressed = 2; return prv;}													// Unkompressed Priv Key
	if(prv.length==33 && prv[32]==0x01)	{compressed = 1; return Arrays.copyOfRange(prv, 0, 32);}		// Compressed Priv Key
	throw new IllegalArgumentException("Error in \"PrvKey\": Private key incorrect Format!");
}



// Hier werden die Würfelzeichen in HEX konvertiert
// 1=1, 2=2, 3=3 4=4, 5=5, 6=0
// Mamimaler Würfelwerd (Mod) = 1621416542-2615626236-3462631235-4363525141-6636261141-4266313436-1546433233-1342224233-3313325535-4344331641
private String base6_PrivateKey_to_HexPrivateKey(String str)
{
	BigInteger mod = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16);		// Modulo des Privatkey / überlauf
	str = str.replaceAll("-","");								 												// Platzhalter Zeichen "-" wird aus dem String entfernt
	str = str.replaceAll("6","0");																				// Die Ziffer 6 wird mit 0 ersetzt
	BigInteger dec = new BigInteger(str,6);																		// nach BigInteger zur Bases  6
	dec = dec.mod(mod);																							// Private Key Modulo FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141
	String erg = dec.toString(16);  																			// nach String     zur Bases 16
	while(erg.length() < 64) erg="0"+erg;																		// String wird vorne mit nullen aufgefüllt
	return erg;
}


//Erstellt das RedeemScript für eine SegWit P2SH Adresse die mit 3 beginnt. 
//Es wird der Hash160 übergeben so wie er üblicherweise berechnet wird. Allerdings muss er von einem komprimierten PubKey stammen!
//Zurück gegeben wird das "RedeemScript" welches dem neuem Hash160 entspricht, der dann so in eine Base58 Adresse codert werden kann.
private byte[] getRedeemScript(byte[] hash160)
{
	final byte[] scriptPrefix = {0x00,0x14};  // Das Script Prefix 	
	byte[] script = new byte[22];
	script[0] = scriptPrefix[0];
	script[1] = scriptPrefix[1];	
	System.arraycopy(hash160, 0, script, 2, 20);
	return Calc.getHashRIPEMD160(Calc.getHashSHA256(script));
}
}