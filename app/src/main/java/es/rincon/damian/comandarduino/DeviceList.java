package es.rincon.damian.comandarduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import es.rincon.damian.comandarduino.util.ItemListAdapter;

public class DeviceList extends AppCompatActivity {
    private static final String TAG = "DeviceListActivity";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME = "device_name";

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> devices;
    private Context context;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        context = this;
        checkBluetoothState();
        devices = new ArrayList<>();
        listView = findViewById(R.id.list_item_device);
        this.setTitle("Seleccione un dispositivo");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()){
            for (BluetoothDevice device : pairedDevices){
                // Add the name and address to an array adapter to show in a ListView
                devices.add(device.getName() +"\n" + device.getAddress());
            }
        }else{
            Toast.makeText(context,"No hay dispositivos vinculados en el dispositivo",
                    Toast.LENGTH_LONG).show();
        }
        // Initialize array adapter for paired devices
        ItemListAdapter adapter = new ItemListAdapter(context,devices);

        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context,"Tratando de conectar con el dispositivo",
                        Toast.LENGTH_LONG).show();
                String[] informacion = devices.get(position).split("\n");
                String nameDevice = informacion[0];
                String addressDevice = informacion[1];

                startActivity(new Intent(context,ComandActivity.class)
                        .putExtra(EXTRA_DEVICE_ADDRESS,addressDevice)
                        .putExtra(EXTRA_DEVICE_NAME,nameDevice)
                );

            }
        });

        // Get the local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void checkBluetoothState(){
        // Check device has Bluetooth and that it is turned on
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter==null) {
            Toast.makeText(context,"El dispositivo no cuenta con bluetooth",Toast.LENGTH_LONG).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                //Prompt user to turn on Bluetooth
                Toast.makeText(context,"Se ocupa activar el bluetooth",Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
