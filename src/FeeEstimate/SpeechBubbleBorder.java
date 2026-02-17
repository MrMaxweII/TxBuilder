package FeeEstimate;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Path2D;


/***********************************************************************************************
*	Version 1.0  			  	Autor: Mr. Maxwell   					vom 09.02.2026			*
*	Kann als Bibliothkes-Klasse verwendet werden. Keine Abhängikeiten!							*
*	Es handelt sich um einen Border, wie ein gewöhnlicher Swing-Border.							*
*	Kann allen Swing-Komponenten als Border übergeben werden.									*
*	Dieser Border hat ein Design wie eine Sprechblase mit einer Ecke am rechten unterem Rand	*
*	Wird im Line-Diagram verwendet als Inlay-Sprechblase wenn in das Diagramm geklickt wird.	*
**************************************************************************************************/




public class SpeechBubbleBorder implements Border 
{

	
	private final int 		oben;
	private final int 		rechts;
	private final int 		unten;
	private final int 		links;
	private final int 		pointerSize = 10;
	private final Color 	color;
	
	
	
	/** Konstruktor
		@param oben		Abstand in Pixeln nach oben
		@param rechts	Abstand in Pixeln nach rechts
		@param unten	Abstand in Pixeln nach unten
		@param links	Abstand in Pixeln nach links
		@param color	Farbe des Borders				**/
	public SpeechBubbleBorder(int oben, int rechts, int unten, int links, Color color) 
	{
	    this.oben 	= oben;
	    this.rechts = rechts;
	    this.unten 	= unten;
	    this.links 	= links;
	    this.color = color;
	}
	
	
	public Insets getBorderInsets(Component c) 
	{
	    return new Insets(oben, rechts, unten + pointerSize , links);
	}
	
	
	public boolean isBorderOpaque() 
	{
	    return false;
	}
	
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
	{
		Graphics2D g2 = (Graphics2D) g.create();
		int bubbleHeight = height - pointerSize;
		Path2D path = new Path2D.Double();
		int right = x + width - 1;
		int bottom = y + bubbleHeight;
		path.moveTo(x, y);
		path.lineTo(right, y);
		path.lineTo(right, bottom);
		path.lineTo(right - pointerSize, bottom);
		path.lineTo(right, y + height);
		path.lineTo(right - pointerSize * 2, bottom);
		path.lineTo(x, bottom);
		path.closePath();
		g2.setColor(color);
		g2.draw(path);
		g2.dispose();
	}
}      