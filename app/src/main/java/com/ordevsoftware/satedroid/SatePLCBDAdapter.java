package com.ordevsoftware.satedroid;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SatePLCBDAdapter {
		/**
	 * Definimos constante con el nombre de la tabla
	 */
	public static final String C_TABLA = "plcs" ;
	
	// Definimos constantes con el nombre de las columnas de la tabla
	public static final String C_COLUMNA_ID  = "_id";
	public static final String C_COLUMNA_NOMBRE = "nombre";
	public static final String C_COLUMNA_ZONA = "zona";
	public static final String C_COLUMNA_MCP = "mcp";
	public static final String C_COLUMNA_IP = "ip";
	public static final String C_COLUMNA_TCMIN = "tcmin";
	public static final String C_COLUMNA_TCMAX = "tcmax";
	public static final String C_COLUMNA_TCPREVIO = "tcprevio";
	public static final String C_COLUMNA_CT = "ct";
	
	//public static final String C_COLUMNA_OPERACION = "operaciones_id";	

	private Context contexto;
	private SatePLCBDHelper databaseHelper;
	private SQLiteDatabase database;
	
	// Definimos columnas para lista
	private String[] columnas = new String[]{C_COLUMNA_ID, C_COLUMNA_NOMBRE,C_COLUMNA_ZONA,C_COLUMNA_IP} ;
	
	public SatePLCBDAdapter(Context context)
	{
	    this.contexto = context;
	}
	
	
	public SatePLCBDAdapter abrir() throws SQLException
	{
		Log.d(this.getClass().toString(), "Abrimos la BBDD.");
	
	    databaseHelper = new SatePLCBDHelper(contexto);
	    database = databaseHelper.getWritableDatabase();
	    return this;
	}
	
	
	public void cerrar()
	{
		Log.d(this.getClass().toString(), "Cerramos la Base de Datos");
	   
	    databaseHelper.close();
	}
	
	/**
	 * Devuelve cursor con todos los registros y columnas de la tabla
	 */
	public Cursor getCursor(String filtro) throws SQLException
	{
	    Cursor c = database.query( true, C_TABLA, columnas, filtro, null, null, null, null, null);
	     
	    return c;
	}
	
	
	/**
	 * Devuelve cursor con todos las columnas del registro
	 */
	public Cursor getRegistro(long id) throws SQLException
	{
		Log.e("SatePLCBDAdapter", "1 Estamos en getRegistro");
		Cursor c = database.query( true, C_TABLA, columnas, C_COLUMNA_ID + "=" + id, null, null, null, null, null);
	
	   //Nos movemos al primer registro de la consulta
	   if (c != null) {
			Log.e("SatePLCBDAdapter", "3 Estamos en getRegistro id: "+id+"");
			c.moveToFirst();
	   }
	   return c;
	}
	
	
	/**
	 * Inserta los valores en un registro de la tabla
	 */
	public long insert(ContentValues reg)
	{
	   if (database == null)
	      abrir();
	    
	   return database.insert(C_TABLA, null, reg);
	}
	
	
	/**
	 * Eliminar el registro con el identificador indicado
	*/
	public long delete(long id)
	{
	  if (database == null)
	     abrir();
	   
	  return database.delete(C_TABLA, "_id=" + id, null);
	}
	
	/**
	* Modificar el registro
	*/
	public long update(ContentValues reg)
	{
	  long result = 0;
	   
	  if (database == null)
	     abrir();
	   
	  if (reg.containsKey(C_COLUMNA_ID))
	  {
	     //
	     // Obtenemos el id y lo borramos de los valores
	     //
	     long id = reg.getAsLong(C_COLUMNA_ID);
	      
	     reg.remove(C_COLUMNA_ID);
	      
	     //
	     // Actualizamos el registro con el identificador que hemos extraido 
	     //
	     result = database.update(C_TABLA, reg, "_id=" + id, null); 
	  }
	  return result;
	}
	
	public boolean exists(long id) throws SQLException
	{
	   boolean exists ;
	
	   if (database == null)
	       abrir();
	
	   Cursor c = database.query( true, C_TABLA, columnas, C_COLUMNA_ID + "=" + id, null, null, null, null, null);
	
	   exists = (c.getCount() > 0);
	
	   c.close();
	
	   return exists;
	}
	
	public ArrayList<plcs> getPLCS(String filtro1, String filtro2)
	{
	   String sentencia;
	   String[] filtros;
	   
	   sentencia="";
	   
	  
	   if (filtro2==null){
		   filtros = new String[]{ filtro1};
		   sentencia =  C_COLUMNA_NOMBRE + " =?";
	   }else{
		   filtros = new String[]{ filtro1, filtro2};
		   sentencia =  C_COLUMNA_NOMBRE + " =? AND " + C_COLUMNA_ZONA + " =?";	   
	   }
	   
 
		ArrayList<plcs> arrayPLC = new ArrayList<plcs>();
	
	   if (database == null)
	       abrir();
	
	   Cursor c = database.query(true, C_TABLA, columnas, sentencia, filtros, null, null, null, null);
	
	   for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
	   {
	       arrayPLC.add(plcs.cursorToPLCS(contexto, c));
	   }
	
	   c.close();
	
	   return arrayPLC;
	}
	
	public ArrayList<plcs> getPLC(long id) throws SQLException
	{
	   ArrayList<plcs> arrayPLC = new ArrayList<plcs>();
	
	   if (database == null)
	       abrir();
	
	   Cursor c = database.query( true, C_TABLA, columnas, C_COLUMNA_ID + "=" + id, null, null, null, null, null);
	   
	   //Nos movemos al primer registro de la consulta
	   if (c != null) {
			Log.e("SatePLCBDAdapter", "3 Estamos en getRegistro id: "+id+"");
			c.moveToFirst();
			arrayPLC.add(plcs.cursorToPLCS(contexto, c));
	   }
	   
	   for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
	   {
		   arrayPLC.add(plcs.cursorToPLCS(contexto, c));
	   }
	
	   c.close();
	
	   return arrayPLC;
	}
}
