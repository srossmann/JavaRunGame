/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs_solaredge;

import pi4modbus.Serial.ModbusSerialMaster;
import pi4modbus.Serial.SerialConnection;
import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.RaspiPin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author rossmann
 */
public class RS_Solaredge {

    public static double P_AC_Watt = 0;
    //--------------------------------------------------------------
    // MQTT Client
    //--------------------------------------------------------------
    static MqttClient Mqtt;
    // Adresse des IOBroker angeben    
    static String broker = "tcp://192.168.0.113:1883";
    // MQTT Client Name
    static String clientId = "RS_Solaredge";
    // MQTT Object Name
    static String content = "PVanlage";
    //--------------------------------------------------------------

    static Timer timer = null;
    //--------------------------------------------------------------
    // Modbus Protokoll zur Solaredge Wechselrichter
    //--------------------------------------------------------------
    static ModbusSerialMaster Modbus = null;
    public final static int Zyklus = 10; // Secunden 
    //--------------------------------------------------------------

    //--------------------------------------------------------------
    // LCD Anzeige
    //--------------------------------------------------------------
    static GpioLcdDisplay lcd = null;
    static SimpleDateFormat formatter = null;
    // LCD Type 4 Zeilen 20 Zeichen
    public final static int LCD_ROWS = 4;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_ROW_3 = 2;
    public final static int LCD_ROW_4 = 3;
    public final static int LCD_COLUMNS = 20;
    public final static int LCD_BITS = 4;
    //--------------------------------------------------------------

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Modbus = new ModbusSerialMaster("/dev/ttyAMA0");
        InitDisplay();
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            Mqtt = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Mqtt.connect(connOpts);

            TimerAction();

        } catch (MqttException ex) {
            System.out.println("MQTT_exception: " + ex.getMessage());
        }
    }

    private static void TimerAction() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                HoleModbusDaten(Modbus);
                System.out.println(formatter.format(new Date()) + " Leistung " + P_AC_Watt + " ");
                byte b[] = SerialConnection.Reg.buffer;
                lcd.writeln(LCD_ROW_2, formatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
                lcd.writeln(LCD_ROW_4, String.format("PV %4.0f Watt", P_AC_Watt));
            }
        };
        timer = new Timer(Zyklus * 1000, actionListener);
        timer.start();
    }

    private static void SetObject(String ID, String Value) {
        try {
            if (!Mqtt.isConnected()) {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                Mqtt.connect(connOpts);
            }
            MqttMessage message = new MqttMessage(Value.getBytes());
            message.setQos(0);
            Mqtt.publish(ID, message);
            lcd.writeln(LCD_ROW_3, "                    ");
        } catch (MqttException ex) {
            lcd.writeln(LCD_ROW_3, ex.getMessage());
            System.out.println("MQTT_exception: " + ex.getMessage());
        }
    }

    private static void HoleModbusDaten(ModbusSerialMaster Modbus) {
        Modbus.ReadHoldingRegister();
        P_AC_Watt = SerialConnection.Reg.I_AC_Power;
        try {
            SetObject(content + "/PV_I_AC_Current", String.valueOf(SerialConnection.Reg.I_AC_Current));
            SetObject(content + "/PV_I_AC_CurrentA", String.valueOf(SerialConnection.Reg.I_AC_CurrentA));
            SetObject(content + "/PV_I_AC_CurrentB", String.valueOf(SerialConnection.Reg.I_AC_CurrentB));
            SetObject(content + "/PV_I_AC_CurrentC", String.valueOf(SerialConnection.Reg.I_AC_CurrentC));
            SetObject(content + "/PV_P_AC_Power", String.valueOf(SerialConnection.Reg.I_AC_Power));

        } catch (Exception ex) {
            System.out.println("MQTT_exception: " + ex.getMessage());
        }
    }

    private static void InitDisplay() {
        formatter = new SimpleDateFormat("HH:mm:ss");
        // initialize LCD
        lcd = new GpioLcdDisplay(LCD_ROWS, // number of row supported by LCD
                LCD_COLUMNS, // number of columns supported by LCD
                RaspiPin.GPIO_05, // LCD RS pin
                RaspiPin.GPIO_04, // LCD strobe pin
                RaspiPin.GPIO_03, // LCD data bit 1
                RaspiPin.GPIO_02, // LCD data bit 2
                RaspiPin.GPIO_01, // LCD data bit 3
                RaspiPin.GPIO_00); // LCD data bit 4
        lcd.writeln(LCD_ROW_1, "  RS Heimsteuerung 4");
        lcd.writeln(LCD_ROW_2, formatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
    }

}
