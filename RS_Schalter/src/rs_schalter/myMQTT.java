/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs_schalter;

import com.pi4j.io.gpio.RaspiPin;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import static rs_schalter.Schalter.transmitter;

/**
 *
 * @author rossmann
 */
public class myMQTT implements MqttCallback {

//--------------------------------------------------------------
// MQTT Client
//--------------------------------------------------------------
    static MqttClient Mqtt;
    // Adresse des IOBroker angeben    
    static String broker = "tcp://RS-DISPLAY-2:1883";
    // MQTT Client Name
    static String clientId = "RS_Schalter";
    // MQTT Object Name
    static String content = "Schalter";
//--------------------------------------------------------------

//--------------------------------------------------------------
// Topics
//--------------------------------------------------------------
    public Schalter S1;
    public Schalter S2;
    public Schalter S3;
    public Schalter S4;
    public Schalter S5;
    public Schalter S6;
    public Schalter S7;
    public Schalter S8;
    public Schalter S9;
    public Schalter S10;

    public myMQTT() {
        transmitter = new rsschalter.RCSwitch(RaspiPin.GPIO_06);

        S1 = new Schalter("RS_Schalter/Steckdose2A", "01001", 1, transmitter);
        S2 = new Schalter("RS_Schalter/Steckdose2B", "01001", 2, transmitter);
        S3 = new Schalter("RS_Schalter/Steckdose2C", "01001", 3, transmitter);
        S4 = new Schalter("RS_Schalter/Steckdose2D", "01001", 4, transmitter);
        S5 = new Schalter("RS_Schalter/Steckdose2E", "01001", 5, transmitter);

        S6 = new Schalter("RS_Schalter/Steckdose1A", "10001", 1, transmitter);
        S7 = new Schalter("RS_Schalter/Steckdose1B", "10001", 2, transmitter);
        S8 = new Schalter("RS_Schalter/Steckdose1C", "10001", 3, transmitter);
        S9 = new Schalter("RS_Schalter/Steckdose1D", "10001", 4, transmitter);
        S10 = new Schalter("RS_Schalter/Steckdose1E", "10001", 5, transmitter);

        Init();

    }

    private void Init() {
        try {

            MemoryPersistence persistence = new MemoryPersistence();
            Mqtt = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Mqtt.connect(connOpts);

            Mqtt.subscribe(S1.Topic);
            Mqtt.subscribe(S2.Topic);
            Mqtt.subscribe(S3.Topic);
            Mqtt.subscribe(S4.Topic);
            Mqtt.subscribe(S5.Topic);
            Mqtt.subscribe(S6.Topic);
            Mqtt.subscribe(S7.Topic);
            Mqtt.subscribe(S8.Topic);
            Mqtt.subscribe(S9.Topic);
            Mqtt.subscribe(S10.Topic);
            Mqtt.setCallback(this);
        } catch (MqttException ex) {
            Logger.getLogger(RS_Schalter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("Connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String Value = new String(message.getPayload());

//        System.out.println("-------------------------------------------------");
//        System.out.println("| Topic:" + topic);
//        System.out.println("| Message: " + Value);
//        System.out.println("-------------------------------------------------");
        
        Integer iValue = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#");
        try {
            iValue = decimalFormat.parse(Value).intValue();
            //System.out.println("The number is: " + iValue);
        } catch (ParseException e) {
            System.out.println(Value + " is not a valid number.");
        }

        if (topic.equals(S1.Topic)) {
            if (iValue == 1) {
                S1.Ein();
            } else {
                S1.Aus();
            }
        }
        if (topic.equals(S2.Topic)) {
            if (iValue == 1) {
                S2.Ein();
            } else {
                S2.Aus();
            }
        }
        if (topic.equals(S3.Topic)) {
            if (iValue == 1) {
                S3.Ein();
            } else {
                S3.Aus();
            }
        }
        if (topic.equals(S4.Topic)) {
            if (iValue == 1) {
                S4.Ein();
            } else {
                S4.Aus();
            }
        }
        if (topic.equals(S5.Topic)) {
            if (iValue == 1) {
                S5.Ein();
            } else {
                S5.Aus();
            }
        }
        if (topic.equals(S6.Topic)) {
            if (iValue == 1) {
                S6.Ein();
            } else {
                S6.Aus();
            }
        }
        if (topic.equals(S7.Topic)) {
            if (iValue == 1) {
                S7.Ein();
            } else {
                S7.Aus();
            }
        }
        if (topic.equals(S8.Topic)) {
            if (iValue == 1) {
                S8.Ein();
            } else {
                S8.Aus();
            }
        }
        if (topic.equals(S9.Topic)) {
            if (iValue == 1) {
                S9.Ein();
            } else {
                S9.Aus();
            }
        }
        if (topic.equals(S10.Topic)) {
            if (iValue == 1) {
                S10.Ein();
            } else {
                S10.Aus();
            }
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {

    }

}
