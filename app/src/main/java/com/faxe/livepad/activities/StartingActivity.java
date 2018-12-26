package com.faxe.livepad.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.faxe.livepad.R;
import com.faxe.livepad.model.LivePadSession;
import com.faxe.livepad.model.User;
import com.faxe.livepad.service.MqttClientNotConnectedException;
import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.service.MqttServiceConnection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public class StartingActivity extends AppCompatActivity {

    private Button btnScan;
    private EditText txtJoinAs;
    private LivePadSession livePadSession;
    private MqttServiceConnection mqttServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        this.txtJoinAs = findViewById(R.id.txtJoinAs);
        this.btnScan = findViewById(R.id.btnScan);
        this.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               scanJoinCode();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MqttConnectionManagerService.class);
        startService(intent);
        this.mqttServiceConnection = new MqttServiceConnection();
        bindService(intent, this.mqttServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private void scanJoinCode(){
        IntentIntegrator integrator = new IntentIntegrator(StartingActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning LivePad Code");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void onJoinSuccess(){
        startActivity(new Intent(this, DrawingActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null && result.getContents() != null) {

            if(this.txtJoinAs.getText().toString().isEmpty()){
                this.txtJoinAs.setError("Please enter a user name");
            }else{

                String[] livePadCode = result.getContents().split("\\|");
                this.livePadSession = new LivePadSession(UUID.fromString(livePadCode[0]), livePadCode[1], new User(this.txtJoinAs.getText().toString()));

                try {
                    this.mqttServiceConnection.getMqttConnectionManagerService().setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            if(topic.equals(livePadSession.getJoinAcceptedTopic())){
                                onJoinSuccess();
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

                    this.mqttServiceConnection.getMqttConnectionManagerService().subscribe(this.livePadSession.getJoinAcceptedTopic());
                    this.mqttServiceConnection.getMqttConnectionManagerService().publish(this.livePadSession.getJoinTopic(), "");
                } catch (MqttException | MqttClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
