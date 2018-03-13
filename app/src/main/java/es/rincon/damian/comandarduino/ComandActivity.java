package es.rincon.damian.comandarduino;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import es.rincon.damian.comandarduino.util.ComandAdapter;

public class ComandActivity extends AppCompatActivity {
    private Button send;
    private EditText command;
    private Vibrator vibe;
    private String sends;
    private Context context;
    private Handler bluetoothIn;
    private ConnectedThread mConnectedThread;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;
    private final int handlerState = 0;
    private ComandAdapter adapter;
    ArrayList<String> commands = new ArrayList<>();
    private ListView commanSends;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comand);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        this.setTitle("Enviar comando a Arduino");
        context = this;
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        String dataInprint = recDataString.substring(0, endOfLineIndex);
                        Log.d("BLUETOOTH", "handleMessage: " + dataInprint);

                        if (recDataString.charAt(0) == '#') {
                            String sensor = recDataString.substring(1, 5);
                            if (sensor.equals("1.00"))
                                Toast.makeText(context,"Conexión establecida",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context,"Fallo al establecer conexion", Toast.LENGTH_SHORT).show();
                        }
                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        send = findViewById(R.id.button_send);
        command = findViewById(R.id.comand_text);
        commanSends = findViewById(R.id.list_item_comand);
        listener();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceList.EXTRA_DEVICE_ADDRESS);
        String name = intent.getStringExtra(DeviceList.EXTRA_DEVICE_NAME);
        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(context,"Ocurrio un fallo",Toast.LENGTH_SHORT).show();
        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {

            }
        }

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
            Toast.makeText(context,"Finalizando conexion",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(context,"El dispositivo no soporta el Bluetooth",Toast.LENGTH_SHORT).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(context,"Fallo",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void listener(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sends = command.getText().toString();
                commands.add(sends);
                String replace = sends.replace(" ",":");
                mConnectedThread.write(replace+";");
                sends = "";
                command.setText("");
                vibe.vibrate(70);
                setAdapter();
            }
        });
    }
    public void setAdapter(){
        adapter = new ComandAdapter(context,commands);
        commanSends.setAdapter(adapter);
    }
}