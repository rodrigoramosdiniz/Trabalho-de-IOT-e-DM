package com.example.trabalhodmeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.trabalhodmeiot.Model.Clima;
import com.google.gson.Gson;

import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.Inflater;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class MainActivity extends AppCompatActivity {

    private Gson gson;
    private Session session;
    private static String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarWebsocket();
    }

    private void inicializarWebsocket(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ClientManager client = ClientManager.createClient();
                try {
                    client.connectToServer(new ClimaEndPoint(), new URI("ws://192.168.43.117:1880/ws/test"));
                } catch (DeploymentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ClimaEndPoint extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            Log.d(TAG, "Conexao com Servidor WS Realizada");
            MainActivity.this.session = session;



         final  TextView campoTemperatura = (TextView) findViewById(R.id.activity_main_campoTemperatura);
         final TextView campoUmidade = (TextView) findViewById(R.id.activity_main_campoUmidade);

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {


                    Log.d(TAG,message);

                  Gson  gson = new Gson();

                 final Clima  clima = gson.fromJson(message, Clima.class);

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           campoTemperatura.setText(clima.getTemperatura()+ " °C");
                           campoUmidade.setText(clima.getUmidade()+  " %");

                       }
                   });

                }
            });
        }
    }
}
