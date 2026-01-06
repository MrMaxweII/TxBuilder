package FeeEstimate;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import TxBuild.GUI;
import TxBuild.MyIcons;
import lib3001.java.Hover;



/****************************************************************************************************************************************************
*	Version 1.2  									 				  	Autor: Mr. Maxwell   										vom 31.12.2025	*
*																																					*
*	Letzte Änderung: 																																*
*	Hover für Buttens hinzugefügt		Kann für Bibliothek wieder entfnert werden.																	*
*	Anpassung: GUI.t.t(...) 			Kann für Bibliothek wieder entfnert werden.																	*
*																																					*
*	Ertellt ein Liniendiagramm in ein JPanel																										*
*	Klasse arbeitet unabhängit und kann in Bibliotheken verwendet werden. 																			*
*	Abhängigkeit nur: Draw Klasse für dieses Linien-Diagramm, keine externen Bibliotheken erforderlich												*
*	Das LinienDiagramm ist angepasst für den TxBuilder, kann bei bedarf aber erweitert werden.														*
*	Es sind 8 Daten-Reihen implementiert. Kann erweitert werden.																					*
*	Diese Klasse ist NICHT vererbt. (Methodenüberflutung ist hier unerwünscht)																		*
*	Anwendung:																																		*
*	Da die Klasse nicht von JPanel erbt, muss das JPanel im Konstruktor als Argument übergeben werden!												*
*	Das JPanel des LinienDiagramms muss Absolut-Layout sein! (Layout = null).	Dynamische GrößenScallierung ist hier selbst Implementiert!			*
*	Dieses JPanel ist dem LinienDiagramm vorbehalten, Keine Elemente in diese Panel legen!															*
*	Mit dem Konstruktor muss zu erst ein Liniendiagramm-Objekt erstellt werden. Siehe Konstruktor-Beschreibung.										*
*	Es gibt Anpassungs set()-Methoden die verschiedene Parameter z.B. Farben etc. anpassen können.													*
*	Die Linien selbst werden nicht statisch vordifiniert dem Liniendiagramm übergeben, sondern dynamisch gezeichnet.								*
*	Damit kann das Diagramm während einer Ladezeit, die Linien bei angezeigtem Diagramm live weiterzeichnen.										*
*	Daher gibt es add()-Methoden, mit denen dynamisch Punkte/Linien hinzugefügt werden.																*
*****************************************************************************************************************************************************/




public class LineDiagram 
{
	private JPanel		  pnl_main;									// Das übergebene Haupt Panel
	private JLabel 		  lbl_chart	= new JLabel();					// Das Label mit dem LinienDiagramm, ohne die Beschriftungsleisten					
	private JLabel 		  lbl_X 	= new JLabel("X-Axis");			// Achsen Beschreibung X
	private JLabel		  lbl_Y 	= new JLabel("Y-Axis");			// Achsen Beschreibung Y
	public  JLabel  	  lbl_load 	= new JLabel(MyIcons.load_gif);	// Animiertes progress gif. drehendes Bitcoin-Symbol
	private JToggleButton btn_2h 	= new JToggleButton("2h");		// Butten skalliert Zeitachse auf 2h = 12 Blöcke
	private JToggleButton btn_24h	= new JToggleButton("24h");		// Butten skalliert Zeitachse auf 24h = 150 Blöcke
	private JToggleButton btn_week  = new JToggleButton(GUI.t.t("1 week"));// Butten skalliert Zeitachse auf 1 Woche = 1000 Blöcke, Für Translate modifiziert.
	private JToggleButton btn_4week = new JToggleButton(GUI.t.t("4 week"));// Butten skalliert Zeitachse auf 4 Wochen= 4000 Blöcke, Für Translate modifiziert.

	private JSpinner 	  spinnerY	= new JSpinner();				// Skalierungs Spinner der Y-Achse
	private Draw   		  drawChart;								// Linien-Diagramm ohne Beschriftungsleisten
	private	Draw   		  draw_xU;									// BeschriftungsLabel X-Achse
	private	Draw   		  draw_yL; 									// BeschriftungsLabel Y-Achse links
	private	Draw   		  draw_yR; 									// BeschriftungsLabel Y-Achse rechts
	private Color		  color0 = Color.white;						// Hintergrundfarbe innerhalb des Diagramms	
	private Color		  color1 = Color.black;						// Schriftfarbe der Achsenbeschriftung	
	private Color		  color2 = Color.black;						// LinienFarbe das Randgitters (kurze Teilungs-Striche) 
	private Color		  color3 = Color.lightGray;					// LinienFarbe das inneren horizontalen Hauptgitters
	private Color		  color4 = Color.lightGray;					// LinienFarbe das inneren vertikalen   Hauptgitters
	private Font		  font1  = null;							// Schriftart der Achsen-Beschriftung (Zahlen/Zeichen)
	private double  	  sizeX = 889;								// Breite des LinienDiagramms (double zu korrekten Berechnen) Wird dynamisch angepasst
	private double  	  sizeY = 300;								// Höhe   des LinienDiagramms (double zu korrekten Berechnen) Wird dynamisch angepasst
	private double		  scalX = 1;								// Skallierungs-Faktor X-Achse, entspricht dem akteullem maximalen X-Wert
	private double		  scalY = 1;								// Skallierungs-Faktor Y-Achse, entspricht dem aktuellem maximalen Y-Wert
	
	private ArrayList<Double> data1 = new ArrayList<Double>();		//  Daten Reihe1 
	private ArrayList<Double> data2 = new ArrayList<Double>();		//  Daten Reihe2 
	private ArrayList<Double> data3 = new ArrayList<Double>();		//  Daten Reihe3 
	private ArrayList<Double> data4 = new ArrayList<Double>();		//  Daten Reihe4 
	private ArrayList<Double> data5 = new ArrayList<Double>();		//  Daten Reihe5 
	private ArrayList<Double> data6 = new ArrayList<Double>();		//  Daten Reihe6 
	private ArrayList<Double> data7 = new ArrayList<Double>();		//  Daten Reihe7 
	private ArrayList<Double> data8 = new ArrayList<Double>();		//  Daten Reihe8 

	private	Color colorData1;										//  Linienfarbe Reihe 1
	private	Color colorData2;										//  Linienfarbe Reihe 2
	private	Color colorData3;										//  Linienfarbe Reihe 3
	private	Color colorData4;										//  Linienfarbe Reihe 4
	private	Color colorData5;										//  Linienfarbe Reihe 5
	private	Color colorData6;										//  Linienfarbe Reihe 6
	private	Color colorData7;										//  Linienfarbe Reihe 7
	private	Color colorData8;										//  Linienfarbe Reihe 8
	private Color oldColorDate1;
	private Color oldColorDate2;
	private Color oldColorDate3;
	private Color oldColorDate4;

	private JLabel lineName1 = new JLabel();						// Linen-Bezeichnung der Linie 1
	private JLabel lineName2 = new JLabel();						// Linen-Bezeichnung der Linie 2
	private JLabel lineName3 = new JLabel();						// Linen-Bezeichnung der Linie 3
	private JLabel lineName4 = new JLabel();						// Linen-Bezeichnung der Linie 4
	private JLabel lineName5 = new JLabel();						// Linen-Bezeichnung der Linie 5
	private JLabel lineName6 = new JLabel();						// Linen-Bezeichnung der Linie 6
	private JLabel lineName7 = new JLabel();						// Linen-Bezeichnung der Linie 7
	private JLabel lineName8 = new JLabel();						// Linen-Bezeichnung der Linie 8


	
	
	
//------------------------------------------- Konstruktor --------------------------------------------
	
public LineDiagram(JPanel pnl)
{
	this.pnl_main = pnl;
	pnl_main.setLayout(null);					// Das Panel muss Absolut-Layout sein. 
	pnl_main.setVisible(false);
	JLabel 	lbl_xU			= new JLabel();									// BeschriftungsLabel X-Achse unten
	JLabel 	lbl_yL			= new JLabel();									// BeschriftungsLabel Y-Achse links
	JLabel 	lbl_yR			= new JLabel();									// BeschriftungsLabel Y-Achse rechts
	ButtonGroup   btnGroup 	= new ButtonGroup();
	drawChart				= new Draw(lbl_chart,(int)sizeX,(int)sizeY);	// Linien-Diagramm ohne Beschriftungsleisten
	draw_xU 				= new Draw(lbl_xU,(int)sizeX,20);				// BeschriftungsLabel X-Achse
	draw_yL 				= new Draw(lbl_yL,30,(int)sizeY);				// BeschriftungsLabel Y-Achse
	draw_yR 				= new Draw(lbl_yR,30,(int)sizeY);				// BeschriftungsLabel Y-Achse

	lbl_load	.setBounds((int) (sizeX/2)+50,(int) (sizeY/2)+35,100,100);		
	lbl_chart	.setBounds(100, 100, (int)sizeX, (int)sizeY);
	lbl_xU		.setBounds(100, (int)sizeY+100, (int)sizeX,20);
	lbl_yL		.setBounds(70, 100, 30, (int)sizeY);
	lbl_yR		.setBounds((int) (sizeX+100), 100, 30, (int)sizeY);
	lbl_X		.setBounds(100, (int)sizeY+125, (int)sizeX, 20);
	lbl_Y		.setBounds(10, 68, 300, 30);
	btn_2h		.setBounds((int)sizeX-300, 45, 95, 23);
	btn_24h		.setBounds((int)sizeX-200, 45, 95, 23);
	btn_week	.setBounds((int)sizeX-100, 45, 95, 23);
	btn_4week	.setBounds((int)sizeX,     45, 95, 23);
	spinnerY	.setBounds((int) (sizeX+135), 100, 16, (int)sizeY);	
	lineName1	.setBounds(180, 10, 300, 15);
	lineName2	.setBounds(180, 25, 300, 15);
	lineName3	.setBounds(180, 40, 300, 15);
	lineName4	.setBounds(180, 55, 300, 15);
	lineName5	.setBounds(180, 70, 300, 15);
	lineName6	.setBounds(180, 85, 300, 15);
	lineName7	.setBounds(180, 100, 300,15);
	lineName8	.setBounds(180, 115, 300,15);
	
	lbl_load	.setText("Loading...");
	lbl_load	.setHorizontalTextPosition(JLabel.CENTER);
	lbl_load	.setVerticalTextPosition(JLabel.NORTH);

	btn_2h		.setBorder(new LineBorder(GUI.color4,1));
	btn_24h		.setBorder(new LineBorder(GUI.color4,1));
	btn_week	.setBorder(new LineBorder(GUI.color4,1));
	btn_4week	.setBorder(new LineBorder(GUI.color4,1));

	lineName1	.setToolTipText(GUI.t.t("ToolTipText_lineName"));
	lineName2	.setToolTipText(GUI.t.t("ToolTipText_lineName"));
	lineName3	.setToolTipText(GUI.t.t("ToolTipText_lineName"));
	lineName4	.setToolTipText(GUI.t.t("ToolTipText_lineName"));
	btn_2h		.setToolTipText(GUI.t.t("ToolTipText_btn_2h"));
	btn_24h		.setToolTipText(GUI.t.t("ToolTipText_btn_24h"));
	btn_week	.setToolTipText(GUI.t.t("ToolTipText_btn_week"));
	btn_4week	.setToolTipText(GUI.t.t("ToolTipText_btn_4week"));
	spinnerY	.setToolTipText(GUI.t.t("ToolTipText_spinnerY"));
	
	btn_2h		.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
	btn_24h		.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
	btn_week	.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
	btn_4week	.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
	lineName1	.setFont(new Font("DejaVu Sans", Font.PLAIN, 11));
	lineName2	.setFont(new Font("DejaVu Sans", Font.PLAIN, 11));
	lineName3	.setFont(new Font("DejaVu Sans", Font.PLAIN, 11));
	lineName4	.setFont(new Font("DejaVu Sans", Font.PLAIN, 11));

	Hover.addBorder(btn_2h);
	Hover.addBorder(btn_24h);
	Hover.addBorder(btn_week);
	Hover.addBorder(btn_4week);
	Hover.addBorder(lineName1);
	Hover.addBorder(lineName2);
	Hover.addBorder(lineName3);
	Hover.addBorder(lineName4);

	spinnerY	.setModel(new SpinnerListModel(new Integer[] {20, 50, 100, 200, 500, 1000}));
	spinnerY	.setValue(100);
	lbl_X		.setHorizontalAlignment(SwingConstants.CENTER);
	lbl_chart	.setHorizontalAlignment(SwingConstants.CENTER);

	btnGroup.add(btn_2h);
	btnGroup.add(btn_24h);
	btnGroup.add(btn_week);
	btnGroup.add(btn_4week);
	pnl_main.add(lbl_load);
	pnl_main.add(btn_2h);
	pnl_main.add(btn_24h);
	pnl_main.add(btn_week);
	pnl_main.add(btn_4week);
	pnl_main.add(lineName1);
	pnl_main.add(lineName2);
	pnl_main.add(lineName3);
	pnl_main.add(lineName4);
	pnl_main.add(lineName5);
	pnl_main.add(lineName6);
	pnl_main.add(lineName7);
	pnl_main.add(lineName8);
	pnl_main.add(lbl_chart);
	pnl_main.add(lbl_xU);
	pnl_main.add(lbl_yL);
	pnl_main.add(lbl_yR);
	pnl_main.add(spinnerY);
	pnl_main.add(lbl_Y);
	pnl_main.add(lbl_X);

	
	
	
	lineName1.addMouseListener(new MouseAdapter() 
	{
		public void mouseReleased(MouseEvent e) 
		{
			if(lineName1.getForeground().equals(Color.lightGray)) 
			{
				lineName1.setForeground(Color.black);
				colorData1 = oldColorDate1;
				writeLine();
			}
			else 
			{
				lineName1.setForeground(Color.lightGray);
				oldColorDate1 = colorData1;
				colorData1 = Color.white;
				scalingDiagramY();
			}
		}
	});
	
	
	lineName2.addMouseListener(new MouseAdapter() 
	{
		public void mouseReleased(MouseEvent e) 
		{
			if(lineName2.getForeground().equals(Color.lightGray)) 
			{
				lineName2.setForeground(Color.black);
				colorData2 = oldColorDate2;
				writeLine();
			}
			else 
			{
				lineName2.setForeground(Color.lightGray);
				oldColorDate2 = colorData2;
				colorData2 = Color.white;
				scalingDiagramY();
			}
		}
	});
	
	
	lineName3.addMouseListener(new MouseAdapter() 
	{
		public void mouseReleased(MouseEvent e) 
		{
			if(lineName3.getForeground().equals(Color.lightGray)) 
			{
				lineName3.setForeground(Color.black);
				colorData3 = oldColorDate3;
				writeLine();
			}
			else 
			{
				lineName3.setForeground(Color.lightGray);
				oldColorDate3 = colorData3;
				colorData3 = Color.white;
				scalingDiagramY();
			}
		}
	});

	
	lineName4.addMouseListener(new MouseAdapter() 
	{
		public void mouseReleased(MouseEvent e) 
		{
			if(lineName4.getForeground().equals(Color.lightGray)) 
			{
				lineName4.setForeground(Color.black);
				colorData4 = oldColorDate4;
				writeLine();
			}
			else 
			{
				lineName4.setForeground(Color.lightGray);
				oldColorDate4 = colorData4;
				colorData4 = Color.white;
				scalingDiagramY();
			}
		}
	});

	



	
	
	// Ändert die X-Skallierung auf "2 Stunden = 12 Blöcke"
	btn_2h.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			String[] strX = {"11","10"," 9"," 8"," 7"," 6"," 5"," 4"," 3"," 2"," 1"};
			scalingDiagramX(strX,12);
		}
	});
	
	
	// Ändert die X-Skallierung auf "24 Strunden ~ 150 Blöcke"
	btn_24h.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			String[] strX = {"140","130","120","110","100","90","80","70","60","50","40","30","20","10"};
			scalingDiagramX(strX,150);
		}
	});
	
	
	// Ändert die X-Skallierung auf "eine Woche = 1000 Blöcke"
	btn_week.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			String[] strX = {"900","800","700","600","500","400","300","200","100"};
			scalingDiagramX(strX,1000);
		}
	});
	
	
	// Ändert die X-Skallierung auf "4 Wochen = 4000 Blöcke"
	btn_4week.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			String[] strX = {"3500","3000","2500","2000","1500","1000","500"};
			scalingDiagramX(strX,4000);
		}
	});
	
	
	// Ändert die Skallierung der Y-Achse
	spinnerY.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent e) 
		{
			scalingDiagramY();
		}
	});
	

	
	// Erfasst Größenänderung des Haupt-Panels und skalliert das Diagramm entsprechend an.
	pnl_main.addComponentListener(new ComponentResizeEndListener() 
	{
		public void resizeTimedOut() 
		{
			sizeX = pnl_main.getSize().width-165;	
			sizeY = pnl_main.getSize().height-150;	
			drawChart	= new Draw(lbl_chart,(int)sizeX,(int)sizeY);	
			draw_xU 	= new Draw(lbl_xU,(int)sizeX,20);				
			draw_yL 	= new Draw(lbl_yL,30,(int)sizeY);	
			draw_yR 	= new Draw(lbl_yR,30,(int)sizeY);				
			lbl_load	.setBounds((int) (sizeX/2)+50,(int) (sizeY/2)+35,100,100);		
			lbl_chart	.setBounds(100, 100, (int)sizeX, (int)sizeY);
			lbl_xU		.setBounds(100, (int)sizeY+100, (int)sizeX,20);
			lbl_yL		.setBounds(70, 100, 30, (int)sizeY);
			lbl_yR		.setBounds((int) (sizeX+100), 100, 30, (int)sizeY);
			lbl_X		.setBounds(100, (int)sizeY+125, (int)sizeX, 20);
			btn_2h		.setBounds((int)sizeX-300, 45, 95, 23);
			btn_24h		.setBounds((int)sizeX-200, 45, 95, 23);
			btn_week	.setBounds((int)sizeX-100, 45, 95, 23);
			btn_4week	.setBounds((int)sizeX,     45, 95, 23);
			spinnerY	.setBounds((int) (sizeX+135), 100, 16, (int)sizeY);	
			scalingDiagramY();
			drawChart.clr(color0);
			if(btn_2h.isSelected())   {btn_2h.doClick();}
			if(btn_24h.isSelected())  {btn_24h.doClick();}
			if(btn_week.isSelected()) {btn_week.doClick();}
			if(btn_4week.isSelected()){btn_4week.doClick();}
			writeLine();
		}
	});      
}





	
// ------------------------------------------------------------------------ Public Methoden -----------------------------------------------------------	
	

/** Muss zum erstmaligen Zeichnen des Liniendiagrammes aufgerufen werden, nach dem möglche Parameter eingestellt wurden sind **/	
public void setVisible(boolean v)
{
	scalY = (int) spinnerY.getValue();
	lbl_chart	.setBorder(new LineBorder(color2));
	drawChart.clr(color0);
	drawChart.setGrid(20, 10, color3, color4);
	drawChart.setEdgeGrid(100, 15, 6, color2, color2);
	draw_xU.clr(pnl_main.getBackground());
	draw_yL.clr(pnl_main.getBackground());
	draw_yR.clr(pnl_main.getBackground());
	String[] strX = {"140","130","120","110","100","90","80","70","60","50","40","30","20","10"};
	String[] strY = {"5","10","15","20","25","30","35","40","45","50","55","60","65","70","75","80","85","90","95"};
	draw_xU	.labelingXAchse(strX, font1, color1);
	draw_yL	.labelingYAchse(strY, font1, color1);
	draw_yR	.labelingYAchse(strY, font1, color1);
	btn_2h.doClick();
	pnl_main.setVisible(v);
}
	
/** Hintergrundfarbe innerhalb des Linien-Diagramm **/
public void setBackground(Color color0)
{
	this.color0 = color0;
}
	
/** Die Schriftfarbe der Achsenbeschriftung **/
public void setColorAxesText(Color color1)
{
	this.color1 = color1;
}

/** LinienFarbe das Ramens und des Randgitters (kurze Teilungs-Striche)  **/
public void setColorBorderEdgeGrid(Color color2)
{
	this.color2 = color2;
}

/** LinienFarbe des inneren Hauptgitters. Horizontal, Vertikal  **/
public void setColorGrid(Color h, Color v)
{
	color3 = h; color4 = v;
}

/** Schriftart der Achsen-Beschriftung (Zahlen/Zeichen)	**/
public void setFontAxesPitch(Font font1)
{
	this.font1 = font1;
}

/** Schriftart der Achsen Beschreibung (Text/Bezeichnung der gesamtenAchsen)**/
public void setFontAxesLabel(Font font)
{
	lbl_X.setFont(font); lbl_Y.setFont(font);
}

/** Achsenbeschriftung der X-Achse **/
public void setTextX(String txt)
{
	lbl_X.setText(txt);
}

/** Achsenbeschriftung der Y-Achse **/
public void setTextY(String txt)
{
	lbl_Y.setText(txt);
}

/** Linien-Beschreibung für Linie: "nr" , LinienFarbe muss hinzugefügt werden**/
public void setTextLine(String txt, int nr, Color color)
{
	if(nr==1) {Draw drawl1 = new Draw(lineName1,30,10);  drawl1 .setRechteck(new Point(0,0), 30, 10, color);  lineName1.setText(txt);}
	if(nr==2) {Draw drawl2 = new Draw(lineName2,30,10);  drawl2 .setRechteck(new Point(0,0), 30, 10, color);  lineName2.setText(txt);}
	if(nr==3) {Draw drawl3 = new Draw(lineName3,30,10);  drawl3 .setRechteck(new Point(0,0), 30, 10, color);  lineName3.setText(txt);};
	if(nr==4) {Draw drawl4 = new Draw(lineName4,30,10);  drawl4 .setRechteck(new Point(0,0), 30, 10, color);  lineName4.setText(txt);};
	if(nr==5) {Draw drawl5 = new Draw(lineName5,30,10);  drawl5 .setRechteck(new Point(0,0), 30, 10, color);  lineName5.setText(txt);};
	if(nr==6) {Draw drawl6 = new Draw(lineName6,30,10);  drawl6 .setRechteck(new Point(0,0), 30, 10, color);  lineName6.setText(txt);};
	if(nr==7) {Draw drawl7 = new Draw(lineName7,30,10);  drawl7 .setRechteck(new Point(0,0), 30, 10, color);  lineName7.setText(txt);};
	if(nr==8) {Draw drawl8 = new Draw(lineName8,30,10);  drawl8 .setRechteck(new Point(0,0), 30, 10, color);  lineName8.setText(txt);};
}



/** Fügt einen Punkt im LinienDiagramm hinzu. 
 	value 	der Wert als Double
 	nr. 	die Nummer der Daten Reihe.		
 	color	die LinenFarbe dieser Linie   **/
public void addPoint(double value, int nr, Color color)
{
	if(nr==1) {addPoint(value, data1, color);  colorData1 = color;};
	if(nr==2) {addPoint(value, data2, color);  colorData2 = color;};
	if(nr==3) {addPoint(value, data3, color);  colorData3 = color;};
	if(nr==4) {addPoint(value, data4, color);  colorData4 = color;};
	if(nr==5) {addPoint(value, data5, color);  colorData5 = color;};
	if(nr==6) {addPoint(value, data6, color);  colorData6 = color;};
	if(nr==7) {addPoint(value, data7, color);  colorData7 = color;};
	if(nr==8) {addPoint(value, data8, color);  colorData8 = color;};
}


/** Gibt Datensatzt mit der Nummer nr zurück.**/
public ArrayList<Double> getData(int nr)
{
	if(nr==1) return data1;
	if(nr==2) return data2;
	if(nr==3) return data3;
	if(nr==4) return data4;
	if(nr==5) return data5;
	if(nr==6) return data6;
	if(nr==7) return data7;
	if(nr==8) return data8;
	return null;
}


// ------------------------------------------------------------------- private Methoden------------------------------------------------------


//  Fügt einen Punkt im LinienDiagramm einer Datenreihe hinzu und zeichnet ihn 
//	value 	der Wert als Double
//	data. 	die Datenreihe selbst muss hier übergeben werden.		**/
public void addPoint(double value, ArrayList<Double> data, Color color)
{
	data.add(value);
	double px = sizeX - (data.size()-1) * (sizeX / scalX) -1;	 // -2 , Mitte des Kreises wird justiert
	double py = sizeY - value 		    * (sizeY / scalY) - 1; // -2 , Mitte des Kreises wird justiert
	if(data.size()>=2) 
	{ 
		double valueLast = data.get(data.size()-2);
		double pxl = sizeX - (data.size()-2) * (sizeX / scalX);	 	
		double pyl = sizeY - valueLast 	  * (sizeY / scalY) ;  
		drawChart.setLine(new Point((int)pxl,(int)pyl), new Point((int)px+1,(int)py+1), color);
	}
	drawChart.setCircle(new Point((int)px,(int)py), 3, color);
	lbl_load.setVisible(false);
}



// Zeichnet alle Linien neu
private void writeLine()
{
	writeLine(data1, colorData1);
	writeLine(data2, colorData2);
	writeLine(data3, colorData3);
	writeLine(data4, colorData4);
	writeLine(data5, colorData5);
	writeLine(data6, colorData6);
	writeLine(data7, colorData7);
	writeLine(data8, colorData8);
}



// Zeichnet eine Linien neu
private void writeLine(ArrayList<Double> data, Color color)
{
	for(int i=0;i<data.size();i++)
	{
		double px = sizeX - i * (sizeX / scalX) -1;	 	
		double py = sizeY - data.get(i) * (sizeY / scalY) - 1;  	
		if(i>=1) 
		{ 
			double valueLast = data.get(i-1);
			double pxl = sizeX - (i-1) * (sizeX / scalX);	 	
			double pyl = sizeY - valueLast 	  * (sizeY / scalY) ;  
			drawChart.setLine(new Point((int)pxl,(int)pyl), new Point((int)px+1,(int)py+1), color);
		}
		drawChart.setCircle(new Point((int)px,(int)py), 3, color);
	}
}



// Skalliert das Diagramm auf die Zeitskale (X-Achse)
// labelingXAxis:		Übergeben wird ein String-Array welches die Beschriftung der X-Achse und die Unterteilung der Gitterlinien festlegt.
//						Das Erste Element im Int-Array ist die größte Zahl ganz links. Beispiel. {"900","800","700","600","500","400","300","200","100"}. 
// scalX:				Skallierungs-Faktor X-Achse, entspricht dem akteullem maximalen X-Wert. Also die Anzal der Punkte die auf die X-Achse gezeichnet werden. 
//						der Skallierungs-Faktor "scalX" sollte natürlich durch die Anzahl String-Arrays der X-Achsenmarkierung teilbar sein!
//						Alle Elemente werden dadurch skalliert und neu gezeichnet. Diagramm-Linien, Daten-Linien, X-Achse (Ausnahme Y-Achse, diese ändert sich nicht)
private void scalingDiagramX(String[] labelingXAxis, int scalX)
{
	draw_xU.clr(pnl_main.getBackground());
	draw_xU	.labelingXAchse(labelingXAxis, font1, color1);
	drawChart.clr(color0);
	drawChart.setGrid(20, labelingXAxis.length+1, color3, color4);
	drawChart.setEdgeGrid(100, labelingXAxis.length+1, 6, color2, color2);
	this.scalX = scalX;
	writeLine();
}



//Skalliert das Diagramm auf die Y-Achse (Eigentlich wird nur die Y-Skala angepasst)
private void scalingDiagramY() 
{
	scalY = (int) spinnerY.getValue();
	String[] str = new String[19];
	for(int i=0; i<19; i++)
	{
		if(scalY==20) 	{str[i] = String.valueOf(i+1);					}
		if(scalY==50)	{str[i] = String.valueOf((double)(i+1)*2.5);	}	
		if(scalY==100)	{str[i] = String.valueOf((i+1)*5);				}
		if(scalY==200)	{str[i] = String.valueOf((i+1)*10);				}
		if(scalY==500)	{str[i] = String.valueOf((i+1)*25);				}
		if(scalY==1000)	{str[i] = String.valueOf((i+1)*50);				}
	}
	drawChart	.clr(color0);
	draw_yL		.clr(pnl_main.getBackground());
	draw_yL		.labelingYAchse(str, font1, color1);
	draw_yR		.clr(pnl_main.getBackground());
	draw_yR		.labelingYAchse(str, font1, color1);
	if(btn_2h.isSelected())   {btn_2h.doClick();}
	if(btn_24h.isSelected())  {btn_24h.doClick();}
	if(btn_week.isSelected()) {btn_week.doClick();}
	if(btn_4week.isSelected()){btn_4week.doClick();}
	writeLine();
}
}
// --------------------------------------------------------------- Ende Klasse LineDiagram --------------------------------------------------------------------------------





// ---------------------------------------------------------- Abstracte Unterklasse für das LinienDiagramm-------------------------------------------------------------------



// Diese Klasse sorgt dafür das der Listener der die Größe des übergeordneten Hauptfensters erkennt, den Trigger erst nach beendeter Größenänderung auslöst. 
abstract class ComponentResizeEndListener extends ComponentAdapter
implements ActionListener 
{
    private final Timer timer;
    public ComponentResizeEndListener() {this(200);}
    public ComponentResizeEndListener(int delayMS) 
    {
        timer = new Timer(delayMS, this);
        timer.setRepeats(false);
        timer.setCoalesce(false);
    }
    public void componentResized(ComponentEvent e)  {timer.restart();}
    public void actionPerformed(ActionEvent e) {resizeTimedOut();}
    public abstract void resizeTimedOut();
}