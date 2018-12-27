package com.faxe.livepad.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faxe.livepad.R;
import com.faxe.livepad.model.LivePadSession;
import com.faxe.livepad.model.User;
import com.faxe.livepad.service.MqttClientNotConnectedException;
import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.service.MqttServiceConnection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public class StartingActivity extends BasicServiceActivity {

    private Button btnScan;
    private EditText txtJoinAs;
    private LivePadSession livePadSession = new LivePadSession();
    private ProgressDialog waitingDialog;

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
        this.waitingDialog = new ProgressDialog(this);
        waitingDialog.setMessage("Waiting for master to start...");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("livePadSession", this.livePadSession);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.livePadSession = (LivePadSession) savedInstanceState.getSerializable("livePadSession");
        if(this.livePadSession!=null && this.livePadSession.isAccepted()){
            waitingDialog.show();
        }
    }

    private void scanJoinCode(){
        IntentIntegrator integrator = new IntentIntegrator(StartingActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scanning LivePad Code");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void onStartDrawing(){
        Intent drawingActivity = new Intent(this, DrawingActivity.class);
        drawingActivity.putExtra("livePadSession", this.livePadSession);
        startActivity(drawingActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null && result.getContents() != null) {

            if(this.txtJoinAs.getText().toString().isEmpty()){
                this.txtJoinAs.setError("Please enter a user name");
            }else{



                final String[] livePadCode = result.getContents().split("\\|");
                this.livePadSession = new LivePadSession(UUID.fromString(livePadCode[0]), livePadCode[1], new User(this.txtJoinAs.getText().toString()));

                try {
                    this.mqttServiceConnection.getService().subscribe(this.livePadSession.getJoinAcceptedTopic());
                    this.mqttServiceConnection.getService().subscribe(this.livePadSession.getStartTopic());
                    this.mqttServiceConnection.getService().publish(this.livePadSession.getJoinTopic(), "");
                } catch (MqttException | MqttClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onAttach(MqttConnectionManagerService service) {


        service.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(livePadSession.getJoinAcceptedTopic())){
                    livePadSession.setAccepted(true);
                    ObjectMapper mapper = new ObjectMapper();
                    livePadSession.getUser().setColor(mapper.readTree(message.toString()).get("color").asText());
                    waitingDialog.show();
                }else if (topic.equals(livePadSession.getStartTopic()) && livePadSession.isAccepted()){
                    waitingDialog.dismiss();
                    onStartDrawing();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });

    }

    @Override
    public void onDetach() {

    }
}
