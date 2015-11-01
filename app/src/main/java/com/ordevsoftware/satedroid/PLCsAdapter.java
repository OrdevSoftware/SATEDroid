package com.ordevsoftware.satedroid;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



public class PLCsAdapter extends ArrayAdapter<plcs>
{
    Activity contexto;

    public PLCsAdapter(Activity context, ArrayList<plcs> plcss) {
        super(context, android.R.layout.simple_dropdown_item_1line, plcss);
        this.contexto = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Esta variable se usa para almacenar un objeto dentro de la Vista
        // que dibuja las opciones del objeto Taller
        VistaTag vistatag = null;

        if (convertView == null)
        {
            LayoutInflater vi = contexto.getLayoutInflater();
            // Definimos en la vista de vuelta el tipo de diseño
            convertView = vi.inflate(R.layout.plc_info, null);

            // Definimos el objeto que vamos a almacenar en el nuevo elemento
            vistatag = new VistaTag();
            // Obtenemos los punteros a las etiquetas recién infladas
            vistatag.lblNombre = (TextView) convertView.findViewById(R.id.lblNombre);
            vistatag.lblIPPLC = (TextView) convertView.findViewById(R.id.lblIpPLC);

            // Guardamos el objeto en el elemento
            convertView.setTag(vistatag);
        }else{
            // Si estamos reutilizando una Vista, recuperamos el objeto interno
            vistatag = (VistaTag) convertView.getTag();
        }

        // Cargamos las opciones del elemento del ArrayList
        plcs plc = getItem(position);


        vistatag.lblNombre.setText(plc.getNombre());
        vistatag.lblIPPLC.setText(plc.getDireccion());


        // Devolvemos la Vista (nueva o reutilizada) que dibuja la opción
        return convertView;
    }


    @Override
    public long getItemId(int position)
    {
        return getItem(position).getId();
    }

    private  class VistaTag
    {
        TextView lblNombre;
        TextView lblIPPLC;
    }
}