package com.ordevsoftware.satedroid;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SateNombresBDAdapter {
	
	// Definimos constante con el nombre de la tabla
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
	
	private Context contexto;
	private SatePLCBDHelper databaseHelper;
	private SQLiteDatabase db;
	
	// Definimos columnas para lista
	private String[] lista = new String[]{C_COLUMNA_ID, C_COLUMNA_NOMBRE} ;
	
	public SateNombresBDAdapter(Context context)
	{
	  this.contexto = context;
	}
	
	public SateNombresBDAdapter abrir() throws SQLException
	{
	  databaseHelper = new SatePLCBDHelper(contexto);
	  db = databaseHelper.getWritableDatabase();
	  return this;
	}
	
	public void cerrar()
	{
	  databaseHelper.close();
	}
	
	// Devuelve una lista (_id, nombre) con todos los registros
	public Cursor getLista() throws SQLException
	{
	    Cursor c = db.query( true, C_TABLA, lista, null, null, null, null, null, null);
	
	    return c;
	}
}

