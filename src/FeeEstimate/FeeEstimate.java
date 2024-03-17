package FeeEstimate;



/********************************************************************************************************************************************
 *	Klasse im Bau!   FeeSetting fehlen noch!																								*
 *	Hier findet die Berechnung (Schätzung) der vorgeschlagenen Gebürhren/Rate statt. Einstellbereich des Fee-Slinders in: sat/vb			*
 *	Die Werte des Sliders zum einstellen der Gebühren werden hier festgeledt. Max und min Werte des Sliders.								*
 *	Die Werte werden mit verschiedenen RPC-Befehlen vom BitcoinCore ermittelt. (getmempoolinfo, estimatesmartfee, etc)						*
 *	Die verwendeten min und max Werte sind variabel und können in den FeeSettings verändert werden.											*
 *	Die Klasse ist static, hat keinen Konsruktor und ist fester Bestandteil des TxBuilders. (Es wird direkt mit dem TxBuilder Komuniziert)	*
 ********************************************************************************************************************************************/




public class FeeEstimate 
{

	/**	Gibt den unteren Wert für den Fee-Slider zurück.
	 	Der Wert wird per RPC vom Core angefordert.
	 	In Abhängikeit der Einsellung in den Fee-Settings	**/
	public static double getMin()
	{
		return 0.0;
	}
	
	
}
