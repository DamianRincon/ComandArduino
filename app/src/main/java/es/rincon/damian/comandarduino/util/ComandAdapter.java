package es.rincon.damian.comandarduino.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.rincon.damian.comandarduino.R;

public class ComandAdapter extends ArrayAdapter<String> {
    public ComandAdapter(@NonNull Context context, ArrayList<String> objects) {
        super(context, 0,objects);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        /* ¿Existe actualmente un View ? */
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.send_comand,parent,false);
        }

        String command = getItem(position);
        TextView name = convertView.findViewById(R.id.text_command);
        name.setText(command);
        return convertView;
    }
}
