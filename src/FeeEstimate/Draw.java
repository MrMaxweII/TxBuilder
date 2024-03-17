package FeeEstimate;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;



/********************************************************************************
 * Diese Draw Klasse ist für Linien-Diagramme optimiert
 * Zeichnet auf ein JLabel
 * Keine Vererbung, Methodenüberflutung ist unerwünscht!
 * Dem Konstruktor wird das JLabel und weitere Parameter übergeben.
 * Dann kann mit den Methoden gezeichnet werden.
 * Viele Koordinaten werden als "Point" übergeben!
 * Diese Klasse ist allgemein gültig und nicht Projectbezogen. Keine Abhängigkeiten
 * Kann in Bibliotheken verwendet werden. (Achtung, es gibt mehrere Draw-Klassen für verschiedene Anwendungen, die aber prizipiel gleich funktionieren)
 ********************************************************************************/


public class Draw 
{
	
	private JLabel lbl;
	private BufferedImage img;  // Das Bild in das gezeichnet wird.
	public int sizeX;			// Pixelgröße des Bildes		
	public int sizeY;			// Pixelgröße des Bildes
	
		
//----------------------------------------- Konstruktor ----------------------------------------------	
	
	
	/** Dem Konstruktor muss das JLabel übergeben werden, auf das gezeichnet wird.
	 	sizeX und sizeY ist die größe des Bildes, sollte mit der Größe des JLabels übereinstimmen, 
	 	Wenn JLabel kleiner ist, wird ein Teil des Bildes abgeschnitten.					**/
	public Draw(JLabel lbl, int sizeX, int sizeY)
	{
		this.lbl=lbl;
		img=new BufferedImage(sizeX, sizeY,BufferedImage.TYPE_INT_RGB);
		this.sizeX=sizeX;
		this.sizeY=sizeY;
	}
	
	
	
	
	
	
//----------------------------------------- Public methoden ----------------------------------------------	
	

	/** Löscht den Inhalt, setzt alle Pixel auf die übergebene Farbe.  **/
	public void clr(Color color)
	{
		for(int i=0;i<sizeX;i++)
		{
			for(int j=0;j<sizeY;j++)
			{
				img.setRGB(i, j, color.getRGB());
			}
		}
		lbl.setIcon(new ImageIcon(img));
		lbl.repaint();
	}
	
	
	/** Setzt ein Pixel mit der Farbe color  **/
	public void setPixel(Point p, Color color)
	{	
		img.setRGB(p.x,p.y, color.getRGB());	
		lbl.setIcon(new ImageIcon(img));
		lbl.repaint();
	}
	
	
	/** Gibt die PixelFarbe der Korordinaten x,y zurück  **/
	public Color getPixelColor(int x, int y)
	{	
		return new Color(img.getRGB(x, y)); 
	}
	
	
	/** Setzt ein Rechteck. (PixelMatrix) b,h = breite, höhe   **/
	public void setRechteck(Point p, int b, int h, Color color)
	{		
		for(int i=p.x;i<b+p.x;i++)
		{
			for(int j=p.y;j<h+p.y;j++)
			{
				img.setRGB(i, j, color.getRGB());
			}
		}
		lbl.setIcon(new ImageIcon(img));
		lbl.repaint();
	}
	
	

	/** Zeichnet einen Kreis. size = die Größe des Kreises. **/
	public void setCircle(Point p, int size, Color color)
	{	
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(color);
		g2d.fillOval(p.x, p.y, size, size);
		g2d.dispose();
		lbl.setIcon(new ImageIcon(img));
		lbl.repaint();
	}
	
	
	/** Zeichnet eine Linie,  **/
	public void setLine(Point p1, Point p2, Color color)
	{	
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(color);
		g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		g2d.dispose();
		lbl.setIcon(new ImageIcon(img));
		lbl.repaint();
	}

	
	
	/** Zeichnet ein durchgehendes Gitter. 
	countH = Anzahl der horizontalen Gitter-Linien,  
	countV = Anzahl der vertikalen   Gitter-Linien 
	colorH = LinienFarbe Horizontale Linie  
	colorV = LinienFarbe Vertikale Linie**/
	public void setGrid(int countH, int countV, Color colorH, Color colorV)
	{	
		int b = sizeX / countV;
		int h = sizeY / countH;
		for(int i=0; i<countV;i++)			// Zeichnet Vertikale Linien
		{
			Point p1 = new Point(i*b+b, 0);
			Point p2 = new Point(i*b+b, sizeY);
			setLine(p1,p2,colorV);
		}
		for(int i=0; i<countH;i++)			// Zeichnet Horizontale Linien
		{
			Point p1 = new Point(0, i*h+h);
			Point p2 = new Point(sizeX, i*h+h);
			setLine(p1,p2,colorH);
		}
	}
	
	
	
	/** Zeichnet ein Rand Gitter. 
	countH = Anzahl der horizontalen Gitter-Linien,  
	countV = Anzahl der vertikalen   Gitter-Linien 
	colorH = LinienFarbe Horizontale Linie  
	colorV = LinienFarbe Vertikale Linie
	t = Tiefe, wie weit das Randgitter in die Bild-Mitte hineinragt  **/
	public void setEdgeGrid(int countH, int countV, int t, Color colorH, Color colorV)
	{
		int b = sizeX / countV;
//		int h = sizeY / countH;
		for(int i=0; i<countV;i++)			// Zeichnet Vertikale Linien  
		{
			Point p1 = new Point(i*b+b, 0);
			Point p2 = new Point(i*b+b, t);
			setLine(p1,p2,colorV);
			p1 = new Point(i*b+b, sizeY-t);
			p2 = new Point(i*b+b, sizeY);
			setLine(p1,p2,colorV);
		}
//		for(int i=0; i<countH;i++)			// Zeichnet Horizontale Linien (Buggy, deaktiviert!)
//		{
//			Point p1 = new Point(sizeX-t, i*h+h);
//			Point p2 = new Point(sizeX, i*h+h);
//			setLine(p1,p2,colorH);
//			p1 = new Point(0, i*h+h);
//			p2 = new Point(t, i*h+h);
//			setLine(p1,p2,colorH);
//		}
	}
	
	
	
	/** Zeichnet Text
	@param txt	  Der Text selbst, als String
	@param font	  Schriftart, kann "null" sein, dann default
	@param p	  Koordinaten als Point
	@param color  Schriftfarbe			**/
	public void setText(String txt,Font font, Point p, Color color)
	{	
		Graphics2D g2d = img.createGraphics();
		g2d.setPaint(color);
		g2d.setFont(font);
		g2d.drawString(txt, p.x, p.y);
		g2d.dispose();
		lbl.setIcon(new ImageIcon(img));
		lbl.repaint();
	}
	
	
	
	/**	Eine X-Achse wird beschriftet. Oben oder unten.
	 Die Beschriftungs-Achsen werden auf seperate JLabels gezeichnet.
	 @param txt		Es muss ein String-Array mit den Zeichen, oder Zahlen übergeben werden, die die Beschriftung darstellen. Index[0] ist der Erste links, also bei 1.
	 				Wert 0 und Wert=Max werden nicht angezeigt und dürfen nicht im String-Array enthalten sein!
	 				Beispiel: Wenn also die X-Skala zwischen 0 und 10 ist, dann wäre das richtige String Array: 1,2,3,4,5,6,7,8,9
					Die Länge des String-Array´s und "sizeX" wird verwendet um den Abstand auf der X-Achse zu skalieren. 
	 @param font	Schriftart, kann "null" sein.
	 @param color 	Schriftfarbe		**/
	public void labelingXAchse(String[] txt,Font font, Color color)
	{
		int a = sizeX / (txt.length+1);   // Abstand 
		
		for(int i=0; i<txt.length;i++)
		{
			Point p = new Point((i*a+5)+a-12, 15);
			setText(txt[i], font, p, color);
		}
	}
	
	
	
	/**	Eine Y-Achse wird beschriftet. Links oder rechst.
	 Die Beschriftungs-Achsen werden auf seperate JLabels gezeichnet.
	 @param txt		Es muss ein String-Array mit den Zeichen, oder Zahlen übergeben werden, die die Beschriftung darstellen. Index[0] ist der Erste unten, also bei 1.
	 				Wert 0 und Wert=Max werden nicht angezeigt und dürfen nicht im String-Array enthalten sein!
	 				Beispiel: Wenn also die Y-Skala zwischen 0 und 10 ist, dann wäre das richtige String Array: 1,2,3,4,5,6,7,8,9
					Die Länge des String-Array´s und "sizeY" wird verwendet um den Abstand auf der Y-Achse zu skalieren. 
	 @param font	Schriftart, kann "null" sein.
	 @param color 	Schriftfarbe		**/
	public void labelingYAchse(String[] txt,Font font, Color color)
	{
		int a = sizeY / (txt.length+1);   // Abstand 
		
		int j = txt.length-1;
		for(int i=0; i<txt.length;i++)
		{
			Point p = new Point(2, (i*a+5)+a);
			setText(txt[j], font, p, color);
			j--;
		}
	}	
}