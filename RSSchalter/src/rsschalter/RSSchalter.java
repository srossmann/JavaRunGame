/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsschalter;

import com.pi4j.component.lcd.LCDTextAlignment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.BitSet;
import javax.swing.Timer;
import pi4modbus.Serial.ModbusSerialMaster;
import pi4modbus.Serial.SerialConnection;
import pi4modbus.Serial.SolarEdgeRegister;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author Rossmann
 */
public class RSSchalter {

    public static Timer timer = null;
    public static RCSwitch transmitter = null;
    public static int Anzahl = 0;
    public static int Summe_AC_Power = 0;
    public static double P_AC_Power = 0;
    public static double P_AC_Watt = 0;

    public final static int LCD_ROWS = 4;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_ROW_3 = 2;
    public final static int LCD_ROW_4 = 3;
    public final static int LCD_COLUMNS = 20;
    public final static int LCD_BITS = 4;

    final static GpioController gpio = null;
    static GpioLcdDisplay lcd = null;
    static SimpleDateFormat formatter = null;
    static RSHomatic RSH = null;

    static String topic = "Modbus PV";
    static String content = "12345";
    static int qos = 0;
    static String broker = "tcp://192.168.0.113:1883";
    static String clientId = "JavaSample";
    static MqttClient sampleClient;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("1");
        InitGpio();

        ModbusSerialMaster Modbus = new ModbusSerialMaster();

        //TimerAction();

        MemoryPersistence persistence = new MemoryPersistence();

        System.out.println("Start Connect MQTT");
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.connect(connOpts);
        } catch (MqttException ex) {
             System.out.println("MQTT_exception: " + ex.getMessage());
        }
        System.out.println("Ende Connect MQTT");

        while (true) {

            SetSchalter();
            System.out.println("Start HoleModbusDaten");
            HoleModbusDaten(Modbus);

            //Anzahl++;
            //RSH.SetVar(1599, (int) P_AC_Power);
            lcd.writeln(LCD_ROW_2, formatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
            //lcd.writeln(LCD_ROW_3, String.format("PV %4.0f Watt", P_AC_Power));
            lcd.writeln(LCD_ROW_4, String.format("PL %1$4.0f Watt %2$d", P_AC_Watt, Anzahl));
        }

    }


    private static void SetObject(String ID,String Value)
    {
        try {
            content = Value;
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(ID, message);
        } catch (MqttException ex) {
            System.out.println("MQTT_exception: " + ex.getMessage());
        }
    }
    
    private static void HoleModbusDaten(ModbusSerialMaster Modbus) {
        Modbus.ReadHoldingRegister(0, 0);
        P_AC_Watt = SerialConnection.Reg.I_AC_Power;
        Summe_AC_Power = Summe_AC_Power + SerialConnection.Reg.I_AC_Power;
        try {
                SetObject("PVanlage/PV_I_AC_Current",String.valueOf(SerialConnection.Reg.I_AC_Current));
                SetObject("PVanlage/PV_I_AC_CurrentA",String.valueOf(SerialConnection.Reg.I_AC_CurrentA));
                SetObject("PVanlage/PV_I_AC_CurrentB",String.valueOf(SerialConnection.Reg.I_AC_CurrentB));
                SetObject("PVanlage/PV_I_AC_CurrentC",String.valueOf(SerialConnection.Reg.I_AC_CurrentC));

                SetObject("PVanlage/PV_P_AC_Power",String.valueOf(SerialConnection.Reg.I_AC_Power));

        } catch (Exception ex) {
                    System.out.println("MQTT_exception: " + ex.getMessage());
                }
    }

    private static void SetSchalter() {
        SendSchalter("1859", "10001", 1);
        SendSchalter("1907", "10001", 2);
        SendSchalter("1908", "10001", 3);
        SendSchalter("1909", "10001", 4);
        SendSchalter("1910", "10001", 5);

        SendSchalter("1911", "01001", 1);
        SendSchalter("2050", "01001", 2);
        SendSchalter("2719", "01001", 3);
        SendSchalter("2720", "01001", 4);
        SendSchalter("2721", "01001", 5);
    }

    private static void SendSchalter(String Value, String Bereich, int Nummer) {
        if ("true".equals(RSH.GetValue(Value))) {
            LichtAn(Bereich, Nummer);
        } else {
            LichtAus(Bereich, Nummer);
        }
    }

    private static void TimerAction() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                P_AC_Power = (double) Summe_AC_Power / (double) Anzahl;

                byte b[] = SerialConnection.Reg.buffer;
                try {
                   // SetObject("PV_AC_Power ",String.valueOf(P_AC_Power));
                } catch (Exception ex) {
                    System.out.println("MQTT_exception: " + ex.getMessage());
                }
     
                //InsertDaten((int) P_AC_Power, SerialConnection.Reg);
                Anzahl = 0;
                Summe_AC_Power = 0;
            }
        };
        timer = new Timer(60000, actionListener);
        timer.start();
    }

    private static void InitGpio() {
        System.out.println("1.1");
        gpio = GpioFactory.getInstance();
        System.out.println("1.2");
       
        formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.println("1.3");
        // initialize LCD
        lcd = new GpioLcdDisplay(LCD_ROWS, // number of row supported by LCD
                LCD_COLUMNS, // number of columns supported by LCD
                RaspiPin.GPIO_05, // LCD RS pin
                RaspiPin.GPIO_04, // LCD strobe pin
                RaspiPin.GPIO_03, // LCD data bit 1
                RaspiPin.GPIO_02, // LCD data bit 2
                RaspiPin.GPIO_01, // LCD data bit 3
                RaspiPin.GPIO_00); // LCD data bit 4
        System.out.println("1.4");
        lcd.writeln(LCD_ROW_1, "  RS Heimsteuerung 3");
        System.out.println("1.5");
        lcd.writeln(LCD_ROW_2, formatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
        System.out.println("1.6");

        System.out.println("1.7");
        transmitter = new RCSwitch(RaspiPin.GPIO_06);
        System.out.println("1.8");
        RSH = new RSHomatic();
    }

    //-----------------------------------------------------------------------
    // Methode baut eine Verbindung zur MySQL auf
    //-----------------------------------------------------------------------
    public static Connection open_mysql() {

//        String URL = "jdbc:mysql://localhost/PVR";//   +properties.getProperty("MySQLIPAdr").trim()+"/RSHome";
        String URL = "jdbc:mysql://192.168.0.113/PVR";//   +properties.getProperty("MySQLIPAdr").trim()+"/RSHome";
        String Name = "root";//properties.getProperty("MySQLUser").trim();
        String Passw = "panama";//properties.getProperty("MySQLPassw").trim();
        Connection verbindung = null;
        try {
            verbindung = DriverManager.getConnection(URL, Name, Passw);
            System.err.println("Verbindung OK");
// 		    befehl = verbindung.createStatement();
        } catch (SQLException ex) {
            System.err.println("Verbindung fehlgeschlagen");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }

        return verbindung;
    }

    public static void test234() {
        Connection con = open_mysql();

    }

    //-----------------------------------------------------------------------
    // Methode schliesst eine Verbindung zur MySQL
    //-----------------------------------------------------------------------
    public static void close_mysql(Connection verbindung) {
        try {
            verbindung.close();
        } catch (Exception e) {

        }
    }

    //-----------------------------------------------------------------------
    // Dateneinfügen
    //-----------------------------------------------------------------------
    public static void InsertDaten(int Wert, SolarEdgeRegister Reg) {
        try {
            
            Connection con = open_mysql();
            String query = " insert into PVDaten (Datum,Leistung,"
                    + "I_AC_Current,"
                    + "I_AC_CurrentA,"
                    + "I_AC_CurrentB,"
                    + "I_AC_CurrentC,"
                    + "I_AC_VoltageAB,"
                    + "I_AC_VoltageBC,"
                    + "I_AC_VoltageCA,"
                    + "I_AC_VoltageAN,"
                    + "I_AC_VoltageBN,"
                    + "I_AC_VoltageCN,"
                    + "I_AC_Power,"
                    + "I_AC_Frequency,"
                    + "I_AC_VA,"
                    + "I_AC_VAR,"
                    + "I_AC_PF,"
                    + "I_AC_EnergyWH,"
                    + "I_DC_Current,"
                    + "I_DC_Voltage,"
                    + "I_DC_Power,"
                    + "I_Temp,"
                    + "I_Status1,"
                    + "I_Status2)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            java.util.Date dt = new java.util.Date();
            Timestamp timestamp = new Timestamp(dt.getTime());

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setTimestamp(1, timestamp);
            preparedStmt.setInt(2, Wert);
            preparedStmt.setInt(3, Reg.I_AC_Current);
            preparedStmt.setInt(4, Reg.I_AC_CurrentA);
            preparedStmt.setInt(5, Reg.I_AC_CurrentB);
            preparedStmt.setInt(6, Reg.I_AC_CurrentC);
            preparedStmt.setInt(7, Reg.I_AC_VoltageAB);
            preparedStmt.setInt(8, Reg.I_AC_VoltageBC);
            preparedStmt.setInt(9, Reg.I_AC_VoltageCA);
            preparedStmt.setInt(10, Reg.I_AC_VoltageAN);
            preparedStmt.setInt(11, Reg.I_AC_VoltageBN);
            preparedStmt.setInt(12, Reg.I_AC_VoltageCN);
            preparedStmt.setInt(13, Reg.I_AC_Power);
            preparedStmt.setInt(14, Reg.I_AC_Frequency);
            preparedStmt.setInt(15, Reg.I_AC_VA);
            preparedStmt.setInt(16, Reg.I_AC_VAR);
            preparedStmt.setInt(17, Reg.I_AC_PF);
            preparedStmt.setInt(18, Reg.I_AC_EnergyWH);
            preparedStmt.setInt(19, Reg.I_DC_Current);
            preparedStmt.setInt(20, Reg.I_DC_Voltage);
            preparedStmt.setInt(21, Reg.I_DC_Power);
            preparedStmt.setInt(22, Reg.I_Temp);
            preparedStmt.setInt(23, Reg.I_Status1);
            preparedStmt.setInt(24, Reg.I_Status2);

            preparedStmt.execute();
            System.err.println("Insert");
            close_mysql(con);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public static void LichtAn(String GroupAdrL, int AdressL) {
        BitSet address = RCSwitch.getSwitchGroupAddress(GroupAdrL);
        transmitter.switchOn(address, AdressL);
        //System.out.println("Test An");

    }

    public static void LichtAus(String GroupAdrL, int AdressL) {
        BitSet address = RCSwitch.getSwitchGroupAddress(GroupAdrL);
        transmitter.switchOff(address, AdressL); //switches the switch unit A off
        //System.out.println("Test Aus");
    }

    public static void timeractionPerformed(final ActionEvent e) {

    }
}
