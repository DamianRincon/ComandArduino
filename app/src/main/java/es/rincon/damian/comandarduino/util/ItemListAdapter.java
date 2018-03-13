package es.rincon.damian.comandarduino.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import es.rincon.damian.comandarduino.R;

public final class ItemListAdapter extends ArrayAdapter<String>{
    public ItemListAdapter(@NonNull Context context, ArrayList<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_list_device,parent,false);

        String dispositivo = getItem(position);
        TextView name = convertView.findViewById(R.id.name_device);
        ImageView imageView = convertView.findViewById(R.id.image_name);
        ColorGenerator generator = ColorGenerator.MATERIAL; // o usar DEFAULT
        int color = generator.getColor(dispositivo); //Genera un color según el nombre

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("", color);
        //imageView.setImageDrawable(drawable);
        imageView.setBackground(drawable);
        imageView.setImageResource(R.drawable.ic_bluetooth_connected);
        name.setText(dispositivo);
        return convertView;
    }
}
