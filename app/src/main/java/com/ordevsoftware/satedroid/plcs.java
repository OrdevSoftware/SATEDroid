package com.ordevsoftware.satedroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class plcs {
	
	 private Context context;
	 
	    private Long id;
	    private String nombre;
	    private String zona;
	    private String direccion;

	    public plcs(Context context)
	    {
	        this.context = context;
	    }
	 
	    public plcs(Context context, Long id, String nombre, String direccion, String zona){
	        this.context = context;
	        
	        this.id = id;
	        this.nombre = nombre;
	        this.direccion = direccion;
	        this.zona = zona;
	    }
	 
	    public Context getContext() {
	        return context;
	    }
	 
	    public void setContext(Context context) {
	        this.context = context;
	    }
	 
	    public Long getId() {
	        return id;
	    }
	 
	    public void setId(Long id) {
	        this.id = id;
	    }
	 
	    public String getNombre() {
	        return nombre;
	    }
	 
	    public void setNombre(String nombre) {
	        this.nombre = nombre;
	    }
	 
	    public String getDireccion() {
	        return direccion;
	    }
	 
	    public void setDireccion(String direccion) {
	        this.direccion = direccion;
	    }
	 
	    public String getZona() {
	        return zona;
	    }
	 
	    public void setZona(String zona) {
	        this.zona = zona;
	    }
	 
		 
	    
	 
	    public static plcs find(Context context, long id)
	    {

		     SatePLCBDAdapter dbAdapter = new SatePLCBDAdapter(context);
	 
	        Cursor c = dbAdapter.getRegistro(id);
	        plcs plcs1 = plcs.cursorToPLCS(context, c);
	 
	        c.close();
	        return plcs1;
	    }
	 
	    //permite obtener un objeto plcs a partir de un cursor 
	    public static plcs cursorToPLCS(Context context, Cursor c)
	    {
	        plcs plcs = null;
	 
	        if (c != null)
	        {
	            plcs = new plcs(context);
	 
	            plcs.setId(c.getLong(c.getColumnIndex(SatePLCBDAdapter.C_COLUMNA_ID)));
	            plcs.setNombre(c.getString(c.getColumnIndex(SatePLCBDAdapter.C_COLUMNA_NOMBRE)));
	            plcs.setDireccion(c.getString(c.getColumnIndex( SatePLCBDAdapter.C_COLUMNA_IP)));
	            plcs.setZona(c.getString(c.getColumnIndex( SatePLCBDAdapter.C_COLUMNA_ZONA)));
        }
	 
	        return plcs ;
	    }
	 
	    //Devuelve un objeto ContentValues del Taller para facilitar las operaciones con la DaciaTallerBDAdapter que gestiona las operaciones de base de datos.
	    private ContentValues toContentValues()
	    {
	        ContentValues reg = new ContentValues();
	 
	        reg.put(SatePLCBDAdapter.C_COLUMNA_ID, this.getId());
	        reg.put(SatePLCBDAdapter.C_COLUMNA_NOMBRE, this.getNombre());
	        reg.put(SatePLCBDAdapter.C_COLUMNA_IP, this.getDireccion());
	        reg.put(SatePLCBDAdapter.C_COLUMNA_ZONA, this.getZona());
	       
	        return reg;
	    }
	 
	    public long save()
	    {
	    	SatePLCBDAdapter dbAdapter = new SatePLCBDAdapter(this.getContext());
	 
	        // comprobamos si estamos insertando o actualizando según esté o no relleno el identificador
	        if ((this.getId() == null) || (!dbAdapter.exists(this.getId())))
	        {
	            long nuevoId = dbAdapter.insert(this.toContentValues());
	 
	            if (nuevoId != -1)
	            {
	                this.setId(nuevoId);
	            }
	        }
	        else
	        {
	            dbAdapter.update(this.toContentValues());
	        }
	 
	        return this.getId();
	    }
	 
	    public long delete()
	    {
	        // borramos el registro
	    	SatePLCBDAdapter dbAdapter = new SatePLCBDAdapter(this.getContext());
	 
	        return dbAdapter.delete(this.getId());
	    }
}
