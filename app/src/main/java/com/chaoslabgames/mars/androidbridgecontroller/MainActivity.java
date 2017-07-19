package com.chaoslabgames.mars.androidbridgecontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button upBtn;
    Button stopBtn;
    Button leftBtn;
    Button rightBtn;
    Button backBtn;

    Button slowBtn;
    Button mediumBtn;
    Button maxBtn;

    BluetoothSocket clientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //"Соединям" вид кнопки в окне приложения с реализацией
        upBtn = (Button) findViewById(R.id.upBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        leftBtn = (Button) findViewById(R.id.leftBtn);
        rightBtn = (Button) findViewById(R.id.rightBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        slowBtn = (Button) findViewById(R.id.slowBtn);
        mediumBtn = (Button) findViewById(R.id.mediumBtn);
        maxBtn = (Button) findViewById(R.id.maxBtn);

        //Добавлем "слушатель нажатий" к кнопке
        upBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        maxBtn.setOnClickListener(this);
        mediumBtn.setOnClickListener(this);
        slowBtn.setOnClickListener(this);


        //Включаем bluetooth. Если он уже включен, то ничего не произойдет
        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);

        //Мы хотим использовать тот bluetooth-адаптер, который задается по умолчанию
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Пытаемся проделать эти действия
        try{
            //Устройство с данным адресом - наш Bluetooth Bee
            //Адрес опредеяется следующим образом: установите соединение
            //между ПК и модулем (пин: 1234), а затем посмотрите в настройках
            //соединения адрес модуля. Скорее всего он будет аналогичным.
            BluetoothDevice device = bluetooth.getRemoteDevice("98:D3:31:FC:43:A1");

            //Инициируем соединение с устройством
            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[] {int.class});

            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();

            //В случае появления любых ошибок, выводим в лог сообщение
        } catch (IOException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (SecurityException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (InvocationTargetException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }

        //Выводим сообщение об успешном подключении
        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View v) {
        Log.d("onClick", v.toString());


        //Пытаемся послать данные
        try {
            //Получаем выходной поток для передачи данных
            OutputStream outStream = clientSocket.getOutputStream();

            String cmd = "2";

            //В зависимости от того, какая кнопка была нажата,
            //изменяем данные для посылки
            if (v == upBtn) {
                cmd = "direction:FORWARD;";
            } else if (v == rightBtn) {
                cmd = "dir-right:;";
            } else if (v == leftBtn ) {
                cmd = "dir-left:;";
            } else if (v == stopBtn) {
                cmd = "direction:RELEASE;";
            } else if (v == backBtn) {
                cmd = "direction:BACKWARD;";
            } else if (v == slowBtn) {
                cmd = "speed:100;";
            } else if (v == mediumBtn) {
                cmd = "speed:200;";
            } else if (v == maxBtn) {
                cmd = "speed:254;";
            }

            //Пишем данные в выходной поток
            outStream.write(cmd.getBytes());

        } catch (IOException e) {
            //Если есть ошибки, выводим их в лог
            Log.d("BLUETOOTH", e.getMessage());
        }
    }
}
