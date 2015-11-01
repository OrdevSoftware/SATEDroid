package com.ordevsoftware.satedroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import nodave.Nodave;
import nodave.PLCinterface;
import nodave.TCPConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FormularioPLC extends Activity {
	 
	private SatePLCBDAdapter databaseAdapter;

	// Modo del formulario
	private int modo ;

	// Identificador del registro que se edita cuando la opcion es MODIFICAR
	private long id ;

	// Elementos de la vista
	private EditText nombre;
	private EditText direccion;
	private EditText zona;

	private Button boton_conectar;
	private Button boton_desconectar;
	private Button boton_read;
	private Button boton_reset;

	private TextView txtstatus;
	private TextView txtstatus2;
	private EditText ipinput, portinput, input_txt;
	private ImageView leds;
	Socket miCliente;

	private boolean connected = false;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Mensaje_data mdata;




	public static boolean Connection = false;
	public static int i, j;
	public static long a, b, c;
	public static float d, e, f;
	public static char buf[];
	public static byte buf1[];
	public static PLCinterface di;
	public static TCPConnection dc;
	//public static Socket sock;
	public static int slot;
	public static byte[] by;
	public static String IP;
	 
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_formularioplc);

			Intent intent = getIntent();
			Bundle extra = intent.getExtras();

			Log.i("FormularioPLC", "Entramos en onCreate");

			if (extra == null) return;

			// Obtenemos los elementos de la vista
			nombre = (EditText) findViewById(R.id.nombre);
			direccion = (EditText) findViewById(R.id.direccion);
			zona = (EditText) findViewById(R.id.zona);

			txtstatus = (TextView) findViewById(R.id.txtstatus);
			txtstatus2 = (TextView) findViewById(R.id.txtstatus2);

			leds = (ImageView) findViewById(R.id.leds);

			boton_conectar = (Button) findViewById(R.id.boton_conectar);
			boton_desconectar = (Button) findViewById(R.id.boton_desconectar);
			boton_read = (Button) findViewById(R.id.boton_read);
			boton_reset = (Button) findViewById(R.id.boton_reset);

			// Creamos el adaptador
			databaseAdapter = new SatePLCBDAdapter(this);
			databaseAdapter.abrir();
	 
	     
			// Obtenemos el identificador del registro si viene indicado
			if (extra.containsKey(SatePLCBDAdapter.C_COLUMNA_ID))
			{
			 id = extra.getLong(SatePLCBDAdapter.C_COLUMNA_ID);
			 consultar(id);
			}
	 
			// Establecemos el modo del formulario
			establecerModo(extra.getInt(MainActivity.C_MODO));//

			//Al clickear en conectar
			boton_conectar.setOnClickListener(new OnClickListener() {
				@Override
				// conectar
				public void onClick(View v) {
					// Iniciamos la tarea asincrona
					TareaConectar tarea = new TareaConectar();
					tarea.execute(direccion.getText().toString());
				}
			});
			
			//Al clickear en desconectar
			boton_desconectar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Iniciamos la tarea asincrona
					TareaDesConectar tarea = new TareaDesConectar();
					tarea.execute(direccion.getText().toString());
				}
			});
			
			//Al clickear en boton read
			boton_read.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					/*
					// Iniciamos la tarea asincrona
					TareaReadfromPlc tarea = new TareaReadfromPlc();
					tarea.execute(direccion.getText().toString());
					*/
					// Iniciamos la tarea asincrona
					TareaReadData2 tarea = new TareaReadData2();
					tarea.execute(direccion.getText().toString());

				}
			});

			//Al clickear en boton reset
			boton_reset.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					/*
					// Iniciamos la tarea asincrona
					TareaReadfromPlc tarea = new TareaReadfromPlc();
					tarea.execute(direccion.getText().toString());
					*/
					// Iniciamos la tarea asincrona
					TareaWriteData tarea = new TareaWriteData();
					tarea.execute(direccion.getText().toString());

				}
			});

			databaseAdapter.cerrar();
		}
	   
	   
	   	//TAREAS ASINCRONAS
		
	   	// Clase para conectar un socket
		// Es decir, podemos seguir usando la interfaz de usuario.
	   	private class TareaConectar extends AsyncTask<String, Void, Boolean> {
			
			// Metodo que se ejecuta en segundo plano
			protected Boolean doInBackground(String... params) {
				return Connect(params[0]);
			}

			/** Cuando la tarea ha acabado se invoca automaticamente este metodo */
			protected void onPostExecute(Boolean resultado) {
			      
				//si nos pudimos conectar
				if (resultado) {//mostramos mensaje 
					Log.i("FormularioPLC", "Entro en onPostExecute resultado=true");
					Set_txtstatus("Conexion OK ", 1);
					Log.i("FormularioPLC", "Entro en onPostExecute antes de Change_leds(true)");
					Change_leds(true);//camiamos img a verde
					// Iniciamos la tarea asincrona
					TareaStartConnection tareaSC = new TareaStartConnection();
					tareaSC.execute(direccion.getText().toString());

				} else {//error al conectarse
					Log.e("FormularioPLC", "ERROR en onPostExecute antes de Change_leds(true)");
					Change_leds(false);//camiamos img a rojo
					//mostramos msg de error
					Set_txtstatus("Error lo siento.. ", 0);
				}
			} // end onPostExecute
		}

		//Conectamos
		public boolean Connect(String param1) {
			//Obtengo datos ingresados en campos
			//String IP = direccion.getText().toString();
			String IP = param1;
			int PORT = Integer.valueOf("102");
			//int PORT = 102;
			Log.i("FormularioPLC", "Entramos en connect)"+ IP + " " + PORT);
			try {//creamos sockets con los valores anteriores
				Log.i("FormularioPLC", "Paso1");
				miCliente = new Socket(IP, PORT);
				Log.i("FormularioPLC", "Paso2");
				//si nos conectamos
				if (miCliente.isConnected() == true) {
					Log.i("FormularioPLC", "miCliente.isConnected() OK OK");
					return true;
				} else {
					Log.e("FormularioPLC", "miCliente.isConnected() No OK");
					return false;
				}
			} catch (Exception e) {
				//Si hubo algun error mostrmos error
				txtstatus.setTextColor(Color.RED);
				txtstatus.setText(" !!! ERROR  !!!");
				Log.e("FormularioPLC","Error connect()" + e);
				return false;
			}
		}
		

	   	// Clase para desconectar un socket como una tarea asincrona.
		// Es decir, podemos seguir usando la interfaz de usuario.
		private class TareaDesConectar extends AsyncTask<String, Void, Boolean> {
			
			// M�todo que se ejecuta en segundo plano
			protected Boolean doInBackground(String... params) {
				return Disconnect(params[0]);
			}

			/** Cuando la tarea ha acabado se invoca autom�ticamente este m�todo */
			protected void onPostExecute(Boolean resultado) {
			      
				//Si nos pudimos Desconectar
				if (resultado) {//mostramos mensaje 
					Set_txtstatus("Desconectado", 0);
					//camibmos led a rojo
					Change_leds(false);
					Log.i("Disconnect() -> ", "!ok!");

				} else {//error al conectarse 
					Set_txtstatus(" Error  ", 0);
					Change_leds(false);
					Log.e("Disconnect() -> ", "!ERROR!");
				}
				

				if (!miCliente.isConnected())
					Change_leds(false);
				
			} // end onPostExecute
		}
		
		
		//Metodo de desconexion
		public boolean Disconnect(String param1) {
			try {
				//Prepramos mensaje de desconexion
				Mensaje_data msgact = new Mensaje_data();
				msgact.texto = "";
				msgact.Action = -1;
				msgact.last_msg = true;
				//avisamos al server que cierre el canal
				boolean val_acc = Snd_Msg(msgact);

				if (!val_acc) {//hubo un error
					return false;

				} else {//ok nos desconectamos
					//cerramos socket	
					miCliente.close();
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}	   
	   
	   	// Clase para realizar getOutputStream() y .getInputStream() como una tarea as�ncrona.
		// Es decir, podemos seguir usando la interfaz de usuario.
		private class TareaStartConnection extends AsyncTask<String, Void, Boolean> {
			
			// M�todo que se ejecuta en segundo plano
			protected Boolean doInBackground(String... params) {
				return  StartConnection(params[0]);
			}

			/** Cuando la tarea ha acabado se invoca autom�ticamente este m�todo */
			protected void onPostExecute(Boolean resultado) {
			      
				//si nos pudimos conectar
				if (resultado) {//mostramos mensaje 
					Connection = true;
					Set_txtstatus("Conexion OK sec ", 1);

				} else {//error al conectarse 
					txtstatus.setText("No connection");
				}
				
			} // end onPostExecute
		}	
		
		
		public  boolean  StartConnection (String param1) {
			Connection = false;
			OutputStream oStream = null;
			InputStream iStream = null;
			slot = 3;

			if (miCliente != null) {
				try
				{
					System.out.println("ENTRO 1");
					oStream = miCliente.getOutputStream();
				
				} 
				catch (IOException e) 
				{
					return false;
				}
				
				try 
				{
					System.out.println("ENTRO 2");
					iStream = miCliente.getInputStream();
				} 
				catch (IOException e) 
				{
					return false;
				}
				
				di = new PLCinterface(oStream, iStream, "IF1", 0, Nodave.PROTOCOL_ISOTCP);
				dc = new TCPConnection(di, 0, slot);
				
				int res = dc.connectPLC();
				
				if (0 == res) {

					return true;
				} else {
					return false;
				}
			}
			return false;
			
		}	   
	   
	   	// Clase para desconectar un socket como una tarea asincrona.
		// Es decir, podemos seguir usando la interfaz de usuario.
		private class TareaReadfromPlc extends AsyncTask<String, Void, Long> {
			
			// Metodo que se ejecuta en segundo plano
			protected Long doInBackground(String... params) {
				return Readfromplc(params[0]);
			}

			/** Cuando la tarea ha acabado se invoca autom�ticamente este m�todo */
			protected void onPostExecute(Long resultado) {
			      
				//si nos pudimos conectar
				if (resultado>0) {//mostramos mensaje 
					Set_txtstatus("Reading data",1);
					Set_txtstatus2(String.valueOf(resultado), 1);

				} else {//error al conectarse 
					Set_txtstatus("SIN Conexión",0);
					Set_txtstatus2("Problemas", 0);
				}

				
			} // end onPostExecute
		}
		
		
		public long Readfromplc(String params) {
			long var = 0;		
			
			if (Connection) {

				var = ReadData();
				return var;
			}else{
				return var;

			}
	 	}
		
		public static long ReadData() {
			
			
			dc.readBytes(Nodave.FLAGS, 0, 1170, 1, null);
			a = dc.getU32();
			return (long) a;
		}
	   	
		// Clase para desconectar un socket como una tarea asíncrona.
		// Es decir, podemos seguir usando la interfaz de usuario.
		private class TareaReadData2 extends AsyncTask<String, Void, String> {
			
			// Método que se ejecuta en segundo plano
			protected String doInBackground(String... params) {

				return ReadFromPLC2(params[0]);
			}

			/** Cuando la tarea ha acabado se invoca automáticamente este método */
			protected void onPostExecute(String resultado) {
			      
				//Si NO nos pudimos conectar
				if (resultado.compareToIgnoreCase("0") == 0) {//mostramos mensaje 
					Set_txtstatus("SIN Conexión",0);
					Set_txtstatus2("Problemas",0);		


				} else {
					Set_txtstatus("Reading data",1);
					Set_txtstatus2(String.valueOf(resultado),1);
				}

				
			} // end onPostExecute
		}
		
		
		public String ReadFromPLC2(String params) {
			String var = "";		
			
			if (Connection) {

				var = ReadData2(Nodave.FLAGS, 0, 1170, 1, 1);
				return var;
			}else{
				return var;

			}
	 	}

		//Read bytes
	    public static String ReadData2(int area, int DBNum, int number, int bytes, int repr) {
	        String tmp;
	        if (Connection) {
	            dc.readBytes(area, DBNum, number, bytes, null);
	            switch (bytes) {
	                case 1: {
	                    if (repr == 1) {
	                        tmp = Integer.toBinaryString(dc.getBYTE());
	                        for (int i = tmp.length(); i < 8; i++) {
	                            tmp = "0" + tmp;
	                        }
	                        tmp = new StringBuffer(tmp).insert(4, "_").toString();
	                        return "2#" + tmp;
	                    } else if (repr == 2) {
	                        return "B#16#" + Integer.toHexString(dc.getBYTE());
	                    } else if (repr == 3) {
	                        return Integer.toString(dc.getBYTE());
	                    } else if (repr == 4) {
	                        return Integer.toString(dc.getBYTE());
	                    } else {
	                        return Integer.toString(dc.getBYTE());
	                    }
	                }
	                case 2: {
	                    return Integer.toString(dc.getINT());
	                }
	                case 4: {
	                    return Long.toString(dc.getU32());
	                }
	                default: {
	                    return "unknown";
	                }
	            }
	        } else {
	            return "off-line";
	        }
	    }


	// Clase para desconectar un socket como una tarea asíncrona.
	// Es decir, podemos seguir usando la interfaz de usuario.
	private class TareaWriteData extends AsyncTask<String, Void, String> {

		// Método que se ejecuta en segundo plano
		protected String doInBackground(String... params) {

			return WriteFromPLC(params[0]);
		}

		/** Cuando la tarea ha acabado se invoca automáticamente este método */
		protected void onPostExecute(String resultado) {

			//si nos pudimos conectar
			if (resultado.compareToIgnoreCase("0") == 0) {//mostramos mensaje
				Set_txtstatus("Writing data",1);
				Set_txtstatus2(String.valueOf(resultado),1);

			} else {//error al conectarse
				Set_txtstatus("SIN Conexión",0);
				Set_txtstatus2("Problemas en Writing",0);
			}


		} // end onPostExecute
	}


	public String WriteFromPLC(String params) {
		String var = "";
		int iVar=1;

		if (Connection) {

			iVar = SetMBInfo(Nodave.FLAGS, 1170, 1, (byte)0x01);
			var=String.valueOf(iVar);
			Log.i("FormularioPLC", "WriteFromPLC var:"+var);

			return var;
		}else{
			Log.e("FormularioPLC", "Error Connection. WriteFromPLC var:" + var);
			return var;

		}
	}



	public static int SetMBInfo(int area,int byteStart, int numBytes, byte arrayBytes)
	{
		int LastError=0;
		byte[] bPrueba = {arrayBytes};


		LastError=dc.writeBytes(area, 0, byteStart, numBytes, bPrueba);

		return LastError;
	}


	private void establecerModo(int m)
	   {
	      this.modo = m ;
	 
	      if (modo == MainActivity.C_VISUALIZAR)
	      {
	         this.setTitle(nombre.getText().toString());
	         this.setEdicion(false);
	      }
	   }
	 
	   private void consultar(long id)
	   {
	       // Consultamos el taller por el identificador
		   ArrayList<plcs> arrayPLC = new ArrayList<plcs>();
		   arrayPLC = databaseAdapter.getPLC(id);
		   
	       plcs plc1 = arrayPLC.get(1);
	       
	       nombre.setText(plc1.getNombre());
	       direccion.setText(plc1.getDireccion());
	       zona.setText(plc1.getZona());
	   }

	   private void setEdicion(boolean opcion)
	   {
	      nombre.setEnabled(opcion);
	      direccion.setEnabled(opcion);
	      zona.setEnabled(opcion);

	      /*
	      // Controlamos visibilidad de botonera
	      LinearLayout v = (LinearLayout) findViewById(R.id.botonera);
	   
	      if (opcion)
	          v.setVisibility(View.VISIBLE);
	      else
	          v.setVisibility(View.GONE);
	          */
	   }
	   
	 //cambia el imageview segun status 
		public void Change_leds(boolean status) {
			if (status)
				leds.setImageResource(R.drawable.on);
			else
				leds.setImageResource(R.drawable.off);
		}

		/*Cambiamos texto de txtstatus segun parametro flag_status
		 * flag_status 0 error, 1 ok*/
		public void Set_txtstatus(String txt, int flag_status) {
			// cambiel color
			if (flag_status == 0) {
				txtstatus.setTextColor(Color.RED);
			} else {
				txtstatus.setTextColor(Color.GREEN);
			}
			txtstatus.setText(txt);
		}
		
		public void Set_txtstatus2(String txt, int flag_status) {
			// cambiel color
			if (flag_status == 0) {
				txtstatus2.setTextColor(Color.RED);
			} else {
				txtstatus2.setTextColor(Color.GREEN);
			}
			txtstatus2.setText(txt);
		}

	
		
		
		
		
		/*Metodo para enviar mensaje por socket
		 *recibe como parmetro un objeto Mensaje_data
		 *retorna boolean segun si se pudo establecer o no la conexion
		 */
		public boolean Snd_Msg(Mensaje_data msg) {

			try {
				//Accedo a flujo de salida
				oos = new ObjectOutputStream(miCliente.getOutputStream());
				//creo objeto mensaje
				Mensaje_data mensaje = new Mensaje_data();

				if (miCliente.isConnected())// si la conexion continua
				{
					//lo asocio al mensaje recibido
					mensaje = msg;
					//Envio mensaje por flujo
					oos.writeObject(mensaje);
					//envio ok
					return true;

				} else {//en caso de que no halla conexion al enviar el msg
					Set_txtstatus("Error...", 0);//error
					return false;
				}

			} catch (IOException e) {// hubo algun error
				Log.e("Snd_Msg() ERROR -> ", "" + e);

				return false;
			}
		}

		

	}

