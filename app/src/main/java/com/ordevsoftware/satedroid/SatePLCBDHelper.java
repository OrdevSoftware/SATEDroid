package com.ordevsoftware.satedroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SatePLCBDHelper extends SQLiteOpenHelper {

	private static Context myContext;
	private static int version = 1;
	private static String nombreBD = "SatePLCDB" ;
	private static CursorFactory factory = null;
	

	
	// Contructor de la clase
	public SatePLCBDHelper(Context context) 	{
		super(context, nombreBD, factory, version);
		myContext = context;
	}

	// Método invocado por Android si no existe la BD
	@Override
	public void onCreate(SQLiteDatabase database) {
		
		String textoSQL = "";

		//Generacion de Tabla de talleres
		try
		{
			Log.e("SatePLCBDHelper", "Entramos a tomar sentencias SQL de bdnombres.sql");
			
			//Generamos la Tabla, leyendo del fichero .raw. Ubicado en /res/raw/
			InputStream ficheroraw = myContext.getResources().openRawResource(R.raw.bdnombres);
			BufferedReader brin = new BufferedReader(new InputStreamReader(ficheroraw));
			
			while (true) {
				textoSQL = brin.readLine();
				// Si ya no hay más líneas que leer hemos acabado de leer el fichero
				if (textoSQL==null) break;

				//Exec de cada sentencia SQL
				database.execSQL(textoSQL);
				Log.e("DaciaTallerBDHelper", textoSQL);			
				
			} // end while
			ficheroraw.close();
	
		}
		catch (Exception ex)
		{
			Log.e("SatePLCBDHelper", "Error al leer fichero bdnombres.sql");
		}

		/*
	    // Aplicamos las sucesivas actualizaciones
	    upgrade_2(database);
	    upgrade_3(database);
	    upgrade_4(database);
	    upgrade_5(database);
	    upgrade_6(database);
	    */	
	}

	/*
	// Método invocado por Android si hay un cambio de versión de la BD
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,	int newVersion) {
		// Eliminamos la BD y la volvemos a crear otra vez
		database.execSQL("DROP TABLE IF EXISTS talleres");
		onCreate(database);
	}
	*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	    /*
		// Actualización a versión 2
	    if (oldVersion < 2)
	    {
	        upgrade_2(db);
	    }
	    // Actualización a versión 3
	    if (oldVersion < 3)
	    {
	        upgrade_3(db);
	    }
	    // Actualización a versión 4
	    if (oldVersion < 4)
	    {
	        upgrade_4(db);
	    }	 
	    // Actualización a versión 5
	    if (oldVersion < 5)
	    {
	        upgrade_5(db);
	    }
	    
	    if (oldVersion < 6)
	    {
	        upgrade_6(db);
	    }
	    */
	}
	
	/*
	private void upgrade_2(SQLiteDatabase db)
	{
	    // Upgrade versión 2: definir algunos datos de ejemplo
	    db.execSQL( "UPDATE talleres SET telefono = '1111211'" +
	    			" WHERE _id = 1");
	 
	    Log.i(this.getClass().toString(), "Actualización versión 2 finalizada");
	}
	
	private void upgrade_3(SQLiteDatabase db)
	{
	    // Upgrade versión 3: Añado un nuevo campo
	    db.execSQL("ALTER TABLE talleres ADD visitado VARCHAR2(1) NOT NULL DEFAULT 'N'");
	 
	    Log.i(this.getClass().toString(), "Actualización versión 3 finalizada");
	}
	
	private void upgrade_4(SQLiteDatabase db)
	{
	    // Upgrade versión 4: definir algunos datos de ejemplo
	    db.execSQL( "UPDATE talleres SET telefono = '914739655'" +
	    			" WHERE _id = 1");
	 
	    Log.i(this.getClass().toString(), "Actualización versión 4 finalizada");
	}
	
	private void upgrade_5(SQLiteDatabase db)
	{
	    // Upgrade versión 5: añadp una nueva tabla 'operaciones' y añado un campo a la tabla
		// ya existente 'talleres'
		
		String textoSQL = "";

		//Generacion de Tabla de talleres
		try
		{
			Log.e("DaciaTallerBDHelper", "Entramos a tomar sentencias SQL de bdoperaciones.sql");
			
			//Generamos la Tabla, leyendo del fichero .raw. Ubicado en /res/raw/
			InputStream ficheroraw = myContext.getResources().openRawResource(R.raw.bdoperaciones);
			BufferedReader brin = new BufferedReader(new InputStreamReader(ficheroraw));
			
			while (true) {
				textoSQL = brin.readLine();
				// Si ya no hay más líneas que leer hemos acabado de leer el fichero
				if (textoSQL==null) break;

				//Exec de cada sentencia SQL
				db.execSQL(textoSQL);
				Log.e("DaciaTallerBDHelper", textoSQL);			
				
			} // end while
			ficheroraw.close();
			
			//db.execSQL("ALTER TABLE talleres ADD operaciones_id INTEGER NOT NULL DEFAULT 1");
	
		}
		catch (Exception ex)
		{
			Log.e("MantenimientoCocheBDHelper", "Error al leer fichero bdoperaciones.sql"+ex);
		}
	 
	    Log.i(this.getClass().toString(), "Actualización versión 5 finalizada");

	}
	
	private void upgrade_6(SQLiteDatabase db)
	{
	    // Upgrade versión 6: añadp una nueva tabla 'provincias' 
		
		String textoSQL = "";

		//Generacion de Tabla de talleres
		try
		{
			Log.e("DaciaTallerBDHelper", "Entramos a tomar sentencias SQL de bdprovincias.sql");
			
			//Generamos la Tabla, leyendo del fichero .raw. Ubicado en /res/raw/
			InputStream ficheroraw = myContext.getResources().openRawResource(R.raw.bdprovincias);
			BufferedReader brin = new BufferedReader(new InputStreamReader(ficheroraw));
			
			while (true) {
				textoSQL = brin.readLine();
				// Si ya no hay más líneas que leer hemos acabado de leer el fichero
				if (textoSQL==null) break;

				//Exec de cada sentencia SQL
				db.execSQL(textoSQL);
				Log.e("DaciaTallerBDHelper", textoSQL);			
				
			} // end while
			ficheroraw.close();
	
		}
		catch (Exception ex)
		{
			Log.e("MantenimientoCocheBDHelper", "Error al leer fichero bdprovincias.sql");
		}
	 
	    Log.i(this.getClass().toString(), "Actualización versión 6 finalizada");

	}
	*/
}
