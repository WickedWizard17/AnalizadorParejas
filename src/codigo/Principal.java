package codigo;

import java.util.LinkedList;

import lib20.Datos;

public class Principal
{
	private DatosIDS dat;
	private Datos obd = new Datos();
	private AnalisisAsiganacion anaAsig = new AnalisisAsiganacion();
	private NotacionPosfija ntPos = new NotacionPosfija();
	private CodigoIntermedio codInter = new CodigoIntermedio();
	
	private int pos=0,contador=0;//posicion de error
	private boolean ban=false;
	
	private String[] 
	terminales = new String[] {"id","int","float","char",",",";","+","-","*","/","(",")","=","num"},
	Tipos = new String[] {"int","float","char"},
	operadores = new String[] {"+","-","*","/","=","(",")"},

	
	columnas = new String[] {"id","int","float","char",",",";","+","-","*","/","(",")","num","=","$","P","Tipo","V","A","EXP","E","TERM","T","F"},
	estados = new String[] {"I00","I01","I02","I03","I04","I05","I06","I07","I08","I09","I10","I11","I12","I13","I14","I15","I16","I17","I18","I19"
			,"I20","I21","I22","I23","I24","I25","I26","I27","I28","I29","I30","I31","I32","I33","I34","I35","I36","I37","I38"},
	producciones = new String[] {"Q","P","P","Tipo","Tipo","Tipo","V","V","A","EXP","E","E","E","TERM","T","T","T","F","F","F"},
	noterminales = new String[] {"P","Tipo","V","A","EXP","E","TERM","T","F"}
	;
	
	private String 
	expId=("[a-z]([a-z]|[A-Z])*[0-9]*"),
	expNum=("-?[0-9][0-9]*(.[0-9]*[1-9])?"),
	elchar=("'([a-z]|[A-Z])'"),
	temp="";//guarda elementos sin importacia repetidos por la tablasintactica
	;
	private int[] 
			prodTam = new int [] {1,3,1,1,1,1,3,2,4,2,3,3,0,2,3,3,0,1,3,1};
			
	private LinkedList<DatosIDS> datos = new LinkedList<DatosIDS>(); //pila donde se guardan las variables,tipode datos,valor
	private LinkedList<String> entraUser = new LinkedList<String>();//pila de entrada del usuario no modificado
	private LinkedList<String> entrada = new LinkedList<String>();//pila modifica para usar tabla sintactica
	private LinkedList<String> pilaT = new LinkedList<String>();//pila de estados de la tabla sintactica
        
        LinkedList<String> tablaSimbolos=new LinkedList<String>();
	
	private String[][]
			tablaSint =  new String[][] 
			{//		id	    int		 float	char	 	,			 ;		+		-		*		 /		(	     )		num	  	=   	$		P		Tipo		V		   A	EXP   	E		TERM  	T		F
			/*I00*/	{"I07",	"I04",	"I05",	"I06",		"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"I01",	"I02",		"",		"I03",	"",		 "",	 "",    "",    "" },
			/*I01*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"P0",	"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I02*/	{"I08",	"", 	"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I03*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"P2",	"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I04*/	{"P3",	"", 	"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I05*/	{"P4",	"",	 	"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I06*/	{"P5",	"",	  	"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I07*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"I09",	"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I08*/	{"",	"",		"",		"",			"I11",	 "I12",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"I10",	"",		"",		"",		"",		"",		""},
			/*I09*/	{"I16",	 "",	"",		"",			"",			"",		"",		"",		"",		"",		"I17",	"",	  "I18",	"",		"",		"",		"",			"",		"",		"I13",	"",  "I14", 	"", "I15"},
			/*I10*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"P1",	"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I11*/	{"I19",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I12*/	{"I07",	"I04",	"I05",	"I06",		"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"I20",	"I02",		"",		"I03",	"",		"",		"",		"",		""},
			/*I13*/	{"",	"",		"",		"",			"",			"I21",	"",	   	"",	  	"",  	 "",	"",		"",		"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I14*/	{"",	"",		"",		"",			"",			"P12",	"I23",	"I24",	"", 	"",	 	"",		"P12",	"",		"",		"",		"",		"",			"",		"",		"",		"I22",	"",		"",		""},
			/*I15*/	{"",	"",		"",		"",			"",			"P16",	"P16",	"P16",	"I26",	"I27",	"",		"P16",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"I25",	""},
			/*I16*/	{"",	"",		"",		"",			"",			"P17",	"P17",	"P17",	"P17",	"P17",	"",		"P17",	"",		"",		"",		"",		"",			"",		"",		"I26",	"I14",	"I15",	"",		""},
			/*I17*/	{"I16",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"I17",	"",		"I18",	"",		"",		"",		"",			"",		"",		"I28",	"",		"I14",	"",		"I15"},
			/*I18*/	{"",	"",		"",		"",			"",			"P19",	"P19",	"P19",	"P19",	"P19",	"",		"P19",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I19*/	{"",	"",		"",		"",			"I11",		"I12",	"",		"",		"",		"",		"",		"",		"",		"",		"",		"",		"",			"I29",	"",		"",		"",		"",		"",		""},
			/*I20*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"P7",	"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I21*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"P8",	"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I22*/	{"",	"",		"",		"",			"",			"P9",	"",		"",		"",		"",		"",		"P9",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I23*/	{"I16",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"I17",	"",		"I18",	"",		"",		"",		"",			"",		"",		"",		"",		"I30",	"",		"I15"},
			/*I24*/	{"I16",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"I17",	"",		"I18",	"",		"",		"",		"",			"",		"",		"",		"",		"I31",	"",		"I15"},
			/*I25*/	{"",	"",		"",		"",			"",			"P13",	"P13",	"P13",	"",		"",		"",		"P13",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I26*/	{"I16",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"I17",	"",		"I18", 	"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		"I32"},
			/*I27*/	{"I16",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"I17",	"",		"I18",	"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		"I33"},
			/*I28*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"I34",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I29*/	{"",	"",		"",		"",			"",			"",		"",		"",		"",		"",		"",		"",		"",		"",		"P6",	"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I30*/	{"",	"",		"",		"",			"",			"P12",	"I23",	"I24",	"",		"",		"",		"P12",	"",		"",		"",		"",		"",			"",		"",		"",		"I35",	"",		"",		""},
			/*I31*/	{"",	"",		"",		"",			"",			"P12",	"I23",	"I24",	"",		"",		"",		"P12",	"",		"",		"",		"",		"",			"",		"",		"",		"I36",	"",		"",		""},
			/*I32*/	{"",	"",		"",		"",			"",			"P16",	"P16",	"P16",	"I26",	"I27",	"",		"P16",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"I37",	""},
			/*I33*/	{"",	"",		"",		"",			"",			"P16",	"P16",	"P16",	"I26",	"I27",	"",		"P16",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"I38",	""},
			/*I34*/	{"",	"",		"",		"",			"",			"P18",	"P18",	"P18",	"P18",	"P18",	"",		"P18",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I35*/	{"",	"",		"",		"",			"",			"P10",	"",		"",		"",		"",		"",		"P10",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I36*/	{"",	"",		"",		"",			"",			"P11",	"",		"",		"",		"",		"",		"P11",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I37*/	{"",	"",		"",		"",			"",			"P14",	"P14",	"P14",	"",		"",		"",		"P14",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			/*I38*/	{"",	"",		"",		"",			"",			"P15",	"P15",	"P15",	"",		"",		"",		"P15",	"",		"",		"",		"",		"",			"",		"",		"",		"",		"",		"",		""},
			}; 
	String textoSintactico = "", textoNotacion = "", textoCodigoIntermedio = "", textoSemantico = "";		
	
	private void Error(int pos,String cad)	
	{
		for(int x=0;x<entrada.size()-1;x++)
		{
			if(x==pos)
				obd.Print("??"+entraUser.get(x)+"??");
			else
				obd.Print(entraUser.get(x)+" ");
		}
		if(entrada.get(pos)=="null0")
		{
			obd.Println("La variable no ha sido declarada");
			
		}else
			if(entrada.get(pos)=="null1")
			{
				obd.Println("La variable se repite");
				
			}
			else
				if(!cad.isBlank())
				{
					obd.Println(cad);
				}
				else
				{
					textoSintactico += (entrada.get(pos)=="$"?"Se debe continuar la expresion":"\nERROR sintactico en el token: " + entraUser.get(pos) + "\n");
					textoSintactico += ("\nSe esperaba un:\n");
					int rango = Integer.parseInt(pilaT.getLast().substring(1)),y;
					for(int x=0;x<tablaSint[rango].length;x++)
					{
						if(!tablaSint[rango][x].isBlank())
						{
							for(y=0;y<noterminales.length && !noterminales[y].equals(columnas[x]) ;y++);
							if(y<noterminales.length)
							{
								//obd.Print("Siguientes "+columnas[x]);
							}
							else
								textoSintactico += (columnas[x]) + " ";
							textoSintactico += "\n";
						}
					}
				}
	}
			
	public void Accion (LinkedList<String>pila)
	{
		pilaT.add("$");
		pilaT.add("I00");
		int x,y,z,cantEliminar;
		
		while(!pila.getFirst().equals("Q"))
		{
			for(x=0;x<estados.length && !pilaT.getLast().equals(estados[x]) ;x++);//////Tabla sintactica
			for(y=0;y<columnas.length && !pila.getFirst().equals(columnas[y]) ;y++);//////Tabla sintactica
			
			if(x<estados.length && y<columnas.length)//////Tabla sintactica
			{
				//obd.Println(pilaT.getLast()+"/"+pila.getFirst());//////Tabla sintactica
				
				
				
				//System.out.print("///////////////////////////////////");//////Tabla sintactica
				this.mostrarSintactico(pilaT);//////Tabla sintactica
				
				if(!tablaSint[x][y].isBlank())//////Tabla sintactica
				{
					if(tablaSint[x][y].charAt(0)=='I')//////Tabla sintactica
					{
						
						pilaT.add(tablaSint[x][y]);//////Tabla sintactica
						pos++;//////Tabla sintactica
						this.eleInter(pila.getFirst());//agrega elementos codigo intermedio
						pila.removeFirst();//////Tabla sintactica
						this.elementosAsignacion(pila.getFirst());//manda los elementos para analizar antes de la asignacion
						//Comprueba q los elemetos si se puedan asignar
						if(this.ComprobarAsigna(pila.getFirst()))//si es error truena
							break;
						
					}else
						if(tablaSint[x][y].charAt(0)=='P')//////Tabla sintactica
						{
							pos--;
							cantEliminar = Integer.parseInt(tablaSint[x][y].substring(1));//////Tabla sintactica
							for(z=0;z<prodTam[cantEliminar];z++)//////Tabla sintactica
								pilaT.removeLast();//////Tabla sintactica
							if(tablaSint[x][y].equals("P0"))//////Tabla sintactica
							{
								//System.out.println("CADENA ACEPTADA->Resultado"+ntPos.Resolver());//////Tabla sintactica
                                                                textoSintactico += "\n// CADENA ACEPTADA! //";
                                                                textoNotacion += "El resultado es: " + ntPos.Resolver();
								//obd.Println("Intermedio code");                                                                
								this.mostrarCodigoIntermedio(codInter.Traducir());
								break;//////Tabla sintactica
							}
							pila.addFirst(producciones[cantEliminar]);//////Tabla sintactica
						}
				}
				else//////Tabla sintactica
				{
					this.Error(pos, "");//////Tabla sintactica
					break;//////Tabla sintactica
				}
			}else//////Tabla sintactica
			{
				this.Error(pos, "");//////Tabla sintactica
				break;//////Tabla sintactica
			}
		}
	}
	
	private void elementosAsignacion(String ele)
	{
		int ind;
		
		if((pilaT.getLast()=="I12" && ele.equals("id") || ban))
		{
			ban = true;
			//obd.Println("Asignando");
			for(ind=0;ind<operadores.length && !operadores[ind].equals(ele);ind++);
			if(ind<operadores.length)
			{
				if(!temp.equals(ele))
				{
					anaAsig.Nuevo(ele);//mandarlo igual para analisisAgignacion
					ntPos.Nuevo(ele);//mandarlo igual para notacionposfija
					temp = ele;
				}
			}else
			{
				if(ele.equals("id"))
				{
					for(ind=0;ind<datos.size() && !datos.get(ind).getId().equals(entraUser.get(pos));ind++);
					if(ind<datos.size())
					{
						anaAsig.Nuevo(Tipos[Integer.parseInt(datos.get(ind).getTip())]);//manda el valor numerico del tipo de datos a la Asignacion
						ntPos.Nuevo(datos.get(ind).getId());//manda el nombre de la id 
						temp = "";
					}
					
				}
				else
					if(ele.equals("num"))
					{
						anaAsig.Nuevo(entraUser.get(pos));//mandarlo igual para analisisAgignacion
						ntPos.Nuevo(entraUser.get(pos));//mandarlo igual para notacionposfija
						temp = "";
					}
			}
			
		}

	}
	
	private void eleInter(String ele)//agrega elementos uno por uno al codigo intermedio
	{
		//obd.Println(ele+"/"+entrada.get(contador));
		if(ele.equals(entrada.get(contador)))//pasa elementos para la notacionPosfija 
		{
			if(!ban)
			{
				codInter.Nuevo(entraUser.get(contador++));
			}
		}
	}

	private boolean ComprobarAsigna(String ele)
	{
		if((pilaT.getLast().equals("I18") && ele.equals(";"))
				|| (pilaT.getLast().equals("I34") && ele.equals(";")))
		{
			ban = false;
			if(anaAsig.Analizar())
			{
				this.Error(pos,"Error en la asignacion anaAsig");		
				return true;//retorna la confirmacaion de error
			}else
			{
				obd.Println("\nLa asignacion fue exitosa!");
				ntPos.Orden();//Crea la Notacion Posfija
				codInter.Nuevos(ntPos.OrdenAsig());//manda la Notacion Posfija al generador de codigo intermedio
                                this.mostrarNotacion(ntPos.ordenEst);
				this.mostrarPilas(codInter.Elementos());//muestra los datos del codigo intermedio
				
				return false;//retorna la confirmacion de error
			}
		}
		else
			return false;
	}

	public LinkedList<String> Entrada(LinkedList<String> pila)
	{
                String tipod="", id="";
                boolean ban=false;
		String temp="";
		int x;
		LinkedList<String> pilatrans = new LinkedList<String>();
		/////////////////////////////////////////////////////////////////////	
		for(int y=0; y<pila.size(); y++)
		{
                 if(pila.get(y).equals("float") || pila.get(y).equals("int") || pila.get(y).equals("char")) {
				tipod=pila.get(y);
				ban=true;
				}
			else if(pila.get(y).matches(expId) && ban && tablaSimbolos.indexOf(pila.get(y))==-1) {
					id=pila.get(y);
					tablaSimbolos.add(tipod);
					tablaSimbolos.add(id);
				}
			else if(pila.get(y).matches(";")) {
				ban=false;
				id=tipod="";
			}
			//obd.Println(pila.get(y));
			for(x=0;x<terminales.length && !terminales[x].equals(pila.get(y)) ;x++);
			if(x<terminales.length)
			{
				if(pila.get(y).equals(";"))
					temp = "";
				for(x=0;x<Tipos.length && !Tipos[x].equals(pila.get(y)) ;x++);
					if(x<Tipos.length)
						temp = pila.get(y);
				pilatrans.add(pila.get(y));
			}
			else
				if(pila.get(y).matches(expId))
				{
					for(x=0;x<datos.size() && !datos.get(x).getId().equals(pila.get(y));x++);
					if(temp!="")
					{
						if(x<datos.size())
						{
							pilatrans.add("null1");
						}
						else
						{
							dat = new DatosIDS();
							dat.setId(pila.get(y));
							for(x=0;x<Tipos.length && !Tipos[x].equals(temp);x++);
							if(x<Tipos.length)
								dat.setTip(x+"");//se guarda el tipo con su valor numerico si quisieramos guardarlo con su valor en cadena usaremos temp
							datos.add(dat);
							dat = null;
							pilatrans.add("id");
						}
					}
					else
					{
						if(x<datos.size())
						{
							pilatrans.add("id");
						}
						else
						{
							pilatrans.add("null0");
						}
					}			
				}
				else
					if(pila.get(y).matches(expNum))
						pilatrans.add("num");
					else
						if(pila.get(y).matches(elchar))
						{
							pilatrans.add("char");
						}
					else
						pilatrans.add("null");
		}
		pilatrans.add("$");
		this.mostrarPilas(pilatrans);
		entrada.addAll(pilatrans);//entrada es la pila modifica para usar la tabla sintactica
		return pilatrans;
		
	}
	
	public LinkedList<String> Separar(String txt)
	{
		txt+=" ";
		LinkedList<String> LisTemp = new LinkedList<String>();
		int temp;
		for(int x=0;x<txt.length();x++)
		{
			temp = txt.indexOf(" ",x);
			//obd.Println(temp+"");
			if(temp!=-1)
				if(!txt.substring(x,temp).isBlank())
					 LisTemp.add(txt.substring(x,x=temp));
		}
		entraUser.addAll(LisTemp);//Entrada user es la pila como la entro el usuario
		return LisTemp;
			
	}
        
	public void mostrarSintactico(LinkedList<String> pila)
	{
		for(int x = 0; x<pila.size();x++)
			textoSintactico += (pila.get(x) + " ");
                textoSintactico += "\n";
	}
        
        public void mostrarNotacion(LinkedList<String> pila)
	{
		for(int x = 0; x<pila.size();x++)
			textoNotacion += (pila.get(x) + "\n");
                textoNotacion += "\n";
	}
        
        public void mostrarCodigoIntermedio(LinkedList<String> pila)
	{
		for(int x = 0; x<pila.size();x++)
			textoCodigoIntermedio += (pila.get(x) + "\n");
                textoCodigoIntermedio += "\n";
	}
        
        public void mostrarSemantico(LinkedList<String> pila)
	{
		for(int x = 0; x<pila.size();x++)
			textoSemantico += (pila.get(x) + "\n");
                textoSemantico += "\n";
	}
        
        public void mostrarSimbolos()
	{
		System.out.println("TABLA SIMBOLOS");
		for(int x = 0; x<tablaSimbolos.size();x++) {
			System.out.print(tablaSimbolos.get(x)+"\t");
			if(x%2!=0)
				System.out.println();
		}
	}
        
        
	public void mostrarPilas(LinkedList<String> pila)
	{
		System.out.println("Mostrar");
		for(int x = 0; x<pila.size();x++)
			System.out.println(pila.get(x));
	}
        
     
	//obp.Accion(obp.Entrada(obp.Separar("int a ; float b ;  a = 8 * 4 + 10 * 5 ;")));
	//obp.Accion(obp.Entrada(obp.Separar("int c ; float b , d ;  c =  1 / ( 5 * 1 ) + 2 ;")));
}

class CodigoIntermedio
{

	private LinkedList<String> entrada = new LinkedList<String>();
	private Datos obd = new Datos();
	
	private String 
	//expDec=("(int|float|char)([a-z]([a-z]|[A-Z])*[0-9]*)(;)"),
	expTip=("(int|float|char|,)"),
	expNum=("-?[0-9][0-9]*(.[0-9]*[1-9])?"),
	expId=("[a-z]([a-z]|[A-Z])*[0-9]*"),
	expOp="*/+-";
	
	private String[] operadores = new String[] {"+","-","*","/"};
	
	public void Nuevo(String ele)
	{
		entrada.add(ele);
	}
	
	public void Nuevos(LinkedList<String> nuevos)
	{
		entrada.addAll(nuevos);
	}
	
	public LinkedList<String> Elementos()
	{
		return entrada;
	}
	
	public LinkedList<String> Traducir()
	{
		int x,pos=0,y;
		String cad="", tipo="";
		entrada.add("$");
		LinkedList<String> pila = new LinkedList<String>();
		while(!entrada.getFirst().equals("$"))
		{
			if(entrada.getFirst().matches(expTip))
			{
				if(!entrada.getFirst().equals(","))
					tipo=entrada.removeFirst();
				else
					entrada.removeFirst();
				if(entrada.getFirst().matches(expId))
					pila.add(tipo+" "+ entrada.removeFirst()+";");
					//pila.add(entrada.removeFirst()+" DB");
				else
					obd.Println("Algo esta muy mal");
			}
			else
				if(entrada.getFirst().matches(expId))
				{
					cad=entrada.removeFirst();
					if(entrada.getFirst().equals("="))
					{
						cad=cad+entrada.removeFirst()+"V0;";
					}
				}else
					if(entrada.getFirst().matches(expNum))
					{
						for(x=0;x<entrada.size() && !expOp.contains(entrada.get(x));x++);
						if(x>1)
						{
							pila.add("float "+"V"+pos+";");
							pila.add("V"+(pos++)+"= "+ entrada.get(x-2)+";");//0
							pila.add("float "+"V"+pos+";");
							pila.add("V"+(pos--)+"= "+ entrada.get(x-1)+";");//1
							pila.add("V"+(pos) +"= V"+(pos)+""+entrada.get(x) +" V"+(++pos)+";");//0-0-1
							entrada.remove(x);
							entrada.remove(x-1);
							entrada.remove(x-2);
							pos++;
						}
						else
						{
							pos=pos-2;
							pila.add("V"+(pos) +"= V"+(pos)+""+entrada.remove(x) +""+entrada.remove(x-1)+";");
							pos=pos+2;
						}
					}
					else
					{
						for(y=0;y<operadores.length && !operadores[y].equals(entrada.getFirst());y++);
						if(y<operadores.length)
						{
							pos=pos-4;
							pila.add("V"+(pos) +"= V"+(pos)+""+entrada.removeFirst()+"V"+(pos+2)+";");
							pos=pos+4;
						}
						else
						{
							entrada.removeFirst();
						}
					}
		}
		pila.add(cad);
		return pila;
	}
	
}

class AnalisisAsiganacion
{
	
	private Datos obd = new Datos();
	private String op="",
			Tipos[] = new String[] {"int","float","char"},
			numEnt=("-?[0-9][0-9]*"),
			numFloat=("-?[0-9][0-9]*(.[0-9]*[1-9])");
			
			
	private int digd,digi;
	private LinkedList<String> entrada = new LinkedList<String>();//entrada Principal
	public LinkedList<String> convertidos = new LinkedList<String>();//entrada modificada con valores de la prioridad del operador
	
	
	private int[][]
			asig =  new int[][] //este devuelve valores de true=1 y false=0
			{
				{1,1,1},
				{1,1,1},
				{1,0,1},
			},
			suma =  new int[][]//devuelve valores segun el tipo de dato
			{
				{0,1,0},
				{1,1,1},
				{0,1,0},
			},
			resta =  new int[][] //devuelve valores segun el tipo de dato
			{
				{0,1,0},
				{1,1,1},
				{0,1,0},
			},
			multi =  new int[][] //devuelve valores segun el tipo de dato
			{
				{0,1,0},
				{1,1,1},
				{0,1,0},
			},
			div =  new int[][] //devuelve valores segun el tipo de dato
			{
				{1,1,1},
				{1,1,1},
				{1,1,1},
			};

	public void Nuevo(String ele)
	{
		entrada.add(ele);
	}
	
	public boolean Analizar()
	{
		this.Convertir();
		convertidos.addFirst("$");
		obd.Println("\nSIMBOLOS A EVALUAR:\n");
		this.mostrarPilas(convertidos);
		while(!convertidos.getLast().equals("$"))
		{
			digd = Integer.parseInt(convertidos.removeLast());
			op = convertidos.removeLast();
			if(op=="$")
				break;
			digi = Integer.parseInt(convertidos.removeLast());
			convertidos.addLast(this.Seleccionar()+"");
		}
		convertidos.clear();
		if(digd==1)
			return false;
		else
			return true;

	}
	
	private void Convertir()
	{
		int x;
		for(String T:entrada)
		{
			if(!T.equals("(") && !T.equals(")"))
			{
				for(x=0;x<Tipos.length && !Tipos[x].equals(T);x++);
				if(x<Tipos.length)
					convertidos.add(x+"");
				else
					if(T.matches(numEnt)) {//mandar su tipo de dato
						convertidos.add("0");
                                                }
					else
						if(T.matches(numFloat)){
							convertidos.add("1");
                                                        }
						else
							convertidos.add(T);	
			}
		}
			
	}
	
	private int Seleccionar()
	{
		int res=-2;
		switch(op)
		{
			case "+" :
				res = suma[digi][digd];
				break;
				
			case "-" :
				res = resta[digi][digd];
				break;
				
			case "*":
				res = multi[digi][digd];
				break;
			
			case "/":
				res = div[digi][digd];
				break;
				
			case "=":
				res = asig[digi][digd];
		}
		return res;
	}
	
	public void mostrarPilas(LinkedList<String> pila)
	{
		System.out.println("Simbolos");
		for(int x = 0; x<pila.size();x++)
			System.out.print(pila.get(x)+" ");
	}
}

class DatosIDS
{
	private String id,tip,val;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getTip()
	{
		return tip;
	}

	public void setTip(String tip)
	{
		this.tip = tip;
	}

	public String getVal()
	{
		return val;
	}

	public void setVal(String val)
	{
		this.val = val;
	}
}

class NotacionPosfija
{
        String textoNotacion;
	private Datos obd = new Datos();
	private LinkedList<String> entrada = new LinkedList<String>();
	private LinkedList<String> orden = new LinkedList<String>();
	public LinkedList<String> ordenEst = new LinkedList<String>();
	private String 
		expNum=("-?[0-9][0-9]*(.[0-9]*[1-9])?"),
		operadores[] = new String[] {"+","-","*","/"},
		prioridad[] = new String[] {"+-","*/","()"};
	
	public void Nuevo(String ele)
	{
		entrada.add(ele);
	}
	
	public void Orden()
	{
		LinkedList<String> tempSimb = new LinkedList<String>();
		entrada.add("$");
		ordenEst.add( entrada.removeFirst());
		ordenEst.add(entrada.removeFirst());
		
		//this.mostrarPilas(entrada);
		int p1,p2;
		
		while(!entrada.getFirst().equals("$"))
		{
			if(entrada.getFirst().matches(expNum))
			{
				orden.add(entrada.getFirst());
			}
			else
			{
				if(tempSimb.isEmpty())
					tempSimb.add(entrada.getFirst());
				else
				{
					p1 = this.Prioridad(entrada.getFirst());
					p2 = this.Prioridad(tempSimb.getFirst());
					//obd.Println("<"+p1+"/"+entrada.getFirst()+">"+"<"+p2+"/"+tempSimb.getFirst()+">");
					if(p1!=2 && p2!=2)
					{
						if(p2>=p1)
						{
							orden.add(tempSimb.removeFirst());
							tempSimb.addFirst(entrada.getFirst());
						}
						else
						{
							tempSimb.addFirst(entrada.getFirst());
						}
					}
					else
						if(entrada.getFirst().equals(")"))
						{
							//this.mostrarPilas(tempSimb);
							while(!tempSimb.getFirst().equals("("))
							{
								orden.add(tempSimb.removeFirst());
							}
							tempSimb.removeFirst();
							//orden.addAll(tempSimb);
						}
						else
							tempSimb.addFirst(entrada.getFirst());			
				}
			}
			entrada.removeFirst();
		}
		orden.addAll(tempSimb);
		ordenEst.addAll(orden);
		//obd.Println("////////////");
		//this.mostrarPilas(orden);
		//obd.Println(this.Resolver());
		
	}
	
	public String Resolver()
	{
		int x,y;
		String res="",ope;
		double der,izq;
		orden.add("$");
		
		for(x=0;x<orden.size();x++)
		{
			for(y=0;y<operadores.length && !operadores[y].equals(orden.get(x));y++);
			if(y<operadores.length)
			{
				if(!orden.get(x).equals("$"))
				{
					ope=orden.get(x);
					izq= Double.parseDouble(orden.get(x-1));
					der= Double.parseDouble(orden.get(x-2));
					//obd.Println(ope+"/"+izq+"/"+der+"/");
					orden.add(x, this.SeleccionarR(ope, izq, der)+"");
					//obd.Println(orden.get(x));
					orden.remove(x+1);
					orden.remove(x-1);
					orden.remove(x-2);
					x=0;
					//this.mostrarPilas(orden);
				}
			}
		}
		res=orden.getFirst();
		return res;
	}
	
	private double SeleccionarR(String op,double izq,double der)
	{
		double res=-2;
		switch(op)
		{
			case "+" :
				res = der + izq ;
				break;
				
			case "-" :
				res = der - izq ;
				break;
				
			case "*":
				res = der * izq ;
				break;
			
			case "/":
				res = der / izq ;
				break;
		}
		return res;
	}
	
	private int Prioridad(String cad)
	{
		int x;
		for(x=0;x<prioridad.length && !prioridad[x].contains(cad);x++);
			if(x<prioridad.length)
			{
				return x;
			}
			else
				return -1;
	}
	
	public LinkedList<String> OrdenAsig()
	{
		//obd.Print("Notacion Posfija");
		//this.mostrarNotacion(ordenEst);
		return ordenEst;
	}
	
	public void mostrarPilas(LinkedList<String> pila)
	{
		System.out.println("ORDEN");
		for(int x = 0; x<pila.size();x++)
			System.out.println(pila.get(x));
	}
}


