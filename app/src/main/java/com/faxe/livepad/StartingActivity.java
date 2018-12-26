package com.faxe.livepad;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.faxe.livepad.model.LivePadSession;
import com.faxe.livepad.model.User;
import com.faxe.livepad.service.MqttClientNotConnectedException;
import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.service.MqttServiceConnection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null && result.getContents() != null) {

            if(this.txtJoinAs.getText().toString().isEmpty()){
                this.txtJoinAs.setError("Please enter a user name");
            }else{

                String[] livePadCode = result.getContents().split("\\|");
                this.livePadSession = new LivePadSession(UUID.fromString(livePadCode[0]), livePadCode[1],
                        new User(this.txtJoinAs.getText().toString()));

                try {
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
