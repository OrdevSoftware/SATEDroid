package com.ordevsoftware.satedroid;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	public static final String C_MODO  = "modo" ;
    public static final int C_VISUALIZAR = 551 ;


	private SatePLCBDAdapter databaseAdapter;
	
	private PLCsAdapter plcAdapter;
	private ListView lista;
	
	private SateNombresBDAdapter databaseNombresAdapter ;
    private Cursor cursorListaNombresPLCs ;
    
    private Spinner plcs ;
    String[] plcString;
	
	private String filtroPreferencias ;			//Para las preferencias
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    //Al m�todo getPreferencias lo llamaremos en el m�todo onCreate 
	    getPreferencias();   
	 
	    ImageView img= (ImageView)findViewById(R.id.icon_Siemens);
	    img.setImageResource(R.drawable.siemens_logo1);
	    lista = (ListView) findViewById(android.R.id.list);
	    databaseAdapter = new SatePLCBDAdapter(this);
	    databaseAdapter.abrir();
	    
	    plcs = (Spinner) findViewById(R.id.spinner_PLC_nombre);
	    // Creamos el adaptador de 'provincias' para ir cumplimentando el Spinner
	    databaseNombresAdapter = new SateNombresBDAdapter(this) ;
	    databaseNombresAdapter.abrir();
	    cursorListaNombresPLCs =databaseNombresAdapter.getLista();
	    SimpleCursorAdapter adapterPLCS = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,cursorListaNombresPLCs,new String[] { SateNombresBDAdapter.C_COLUMNA_NOMBRE}, new int[] {android.R.id.text1});
	    adapterPLCS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    plcs.setPrompt("Elige una PLC");
	    plcs.setAdapter(adapterPLCS);	 

        plcs.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
	                // TODO Auto-generated method stub
	    		  Cursor c=(Cursor) plcs.getSelectedItem();
	    		  String est=c.getString(c.getColumnIndex( SateNombresBDAdapter.C_COLUMNA_NOMBRE));
	    		  Toast.makeText(plcs.getContext(), "Has seleccionado..." + est, Toast.LENGTH_LONG).show();
	    		  //consultar_datos(est,filtroPreferencias) ;
	    		  consultar_datos(est,filtroPreferencias) ;
	    	  }

	          @Override
	          public void onNothingSelected(AdapterView<?> arg0) {
	             // TODO Auto-generated method stub
	          }
        });
	    
	    //Mostrar� el men� contextual
	    //registerForContextMenu(this.getListView());
	}
	
	private void consultar_datos(String param1,String param2)
	{
		
        plcAdapter = new PLCsAdapter(this, databaseAdapter.getPLCS(param1,param2));
        plcAdapter.notifyDataSetChanged();
   	 
        lista.setAdapter(plcAdapter);
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		visualizar(id);
	    plcAdapter.notifyDataSetChanged();
	}


	private void visualizar(long id)
	{
		// Llamamos a la Actividad HFormularioTaller indicando el modo visualizaci�n y el identificador del registro
		Intent i = new Intent(MainActivity.this, FormularioPLC.class);
	    i.putExtra(C_MODO, C_VISUALIZAR);
	    i.putExtra(SatePLCBDAdapter.C_COLUMNA_ID, id);
	 
	    startActivityForResult(i, C_VISUALIZAR);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        Cursor c=(Cursor) plcs.getSelectedItem();
		String est=c.getString(c.getColumnIndex(SatePLCBDAdapter.C_COLUMNA_NOMBRE));
		
		// Nos aseguramos que es la petici�n que hemos realizado
		switch(requestCode)
		{



         case C_VISUALIZAR:
             	if (resultCode == RESULT_OK){
					Toast.makeText(plcs.getContext(), "Has seleccionado..." + est, Toast.LENGTH_LONG).show();
			  		consultar_datos(est,filtroPreferencias) ;					
				}           
         
         default:
        	 	super.onActivityResult(requestCode, resultCode, data);
      }
      plcAdapter.notifyDataSetChanged();

	}
	

	private void getPreferencias()
	{
	    /*
		// Recuperamos las preferencias
	    SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
	 
	    if (preferencias.getBoolean("ocultar_talleres_pasivos", false))
	    {
	        // Si se ocultan registros pasivos, filtramos solamente los que tengan el valor 'S'
	        this.filtroPreferencias = "S" ;
	    }
	    else
	    {
	        // Si no se ocultan registros pasivos, no filtramos
	        this.filtroPreferencias = null ;
	    }
	    */
		this.filtroPreferencias = null ;
	}
}


