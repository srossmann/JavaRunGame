/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4modbus.Serial;

import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author Rossmann
 */
public class SerialConnection {

    static SerialPort serialPort;

    static public byte buffer[];
    static public int status = 0;
    static public SolarEdgeRegister Reg = null;

    public static void open(String SerialPortName) {
        Reg = new SolarEdgeRegister();

        serialPort = new SerialPort(SerialPortName);
        try {
            serialPort.openPort();//Open port
            serialPort.setParams(9600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }

    public static void sendData(byte[] data) {
        try {
            status = 0;
            serialPort.writeBytes(data);
        } catch (SerialPortException ex) {
            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void close() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            //System.out.println("empfangen");
            if (event.isRXCHAR()) {//If data is available
                if (event.getEventValue() == 85) {//Check bytes count in the input buffer
                    try {
                        int   Wert = 0;
                        short Factor = 0;
                        buffer = serialPort.readBytes();
                        Reg.buffer = buffer;
                        ////////////////////////////////////////
                        ///// -----  Leistung in Watt ---- /////
                        ///// I_AC_Power                   ///// 
                        ////////////////////////////////////////
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[7], buffer[8]});
                        Factor = ModbusTools.byteArrayToShort(buffer[16],buffer[15]);
                        Reg.I_AC_Current = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[9], buffer[10]});
                        Factor = ModbusTools.byteArrayToShort(buffer[16],buffer[15]);
                        Reg.I_AC_CurrentA = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[11], buffer[12]});
                        Factor = ModbusTools.byteArrayToShort(buffer[16],buffer[15]);
                        Reg.I_AC_CurrentB = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[13], buffer[14]});
                        Factor = ModbusTools.byteArrayToShort(buffer[16],buffer[15]);
                        Reg.I_AC_CurrentC = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[17], buffer[18]});
                        Factor = ModbusTools.byteArrayToShort(buffer[30],buffer[29]);
                        Reg.I_AC_VoltageAB = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[19], buffer[20]});
                        Factor = ModbusTools.byteArrayToShort(buffer[30],buffer[29]);
                        Reg.I_AC_VoltageBC = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[21], buffer[22]});
                        Factor = ModbusTools.byteArrayToShort(buffer[30],buffer[29]);
                        Reg.I_AC_VoltageCA = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[23], buffer[24]});
                        Factor = ModbusTools.byteArrayToShort(buffer[30],buffer[29]);
                        Reg.I_AC_VoltageAN = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[25], buffer[26]});
                        Factor = ModbusTools.byteArrayToShort(buffer[30],buffer[29]);
                        Reg.I_AC_VoltageBN = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[27], buffer[28]});
                        Factor = ModbusTools.byteArrayToShort(buffer[30],buffer[29]);
                        Reg.I_AC_VoltageCN = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[31], buffer[32]});
                        Factor = ModbusTools.byteArrayToShort(buffer[34],buffer[33]);
                        Reg.I_AC_Power = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[35], buffer[36]});
                        Factor = ModbusTools.byteArrayToShort(buffer[38],buffer[37]);
                        Reg.I_AC_Frequency = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[39], buffer[40]});
                        Factor = ModbusTools.byteArrayToShort(buffer[42],buffer[41]);
                        Reg.I_AC_VA = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[43], buffer[44]});
                        Factor = ModbusTools.byteArrayToShort(buffer[46],buffer[45]);
                        Reg.I_AC_VAR = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[47], buffer[48]});
                        Factor = ModbusTools.byteArrayToShort(buffer[50],buffer[49]);
                        Reg.I_AC_PF = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt32(new byte[]{buffer[51], buffer[52], buffer[53], buffer[54]});
                        Factor = ModbusTools.byteArrayToShort(buffer[56],buffer[55]);
                        Reg.I_AC_EnergyWH = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[57], buffer[58]});
                        Factor = ModbusTools.byteArrayToShort(buffer[60],buffer[59]);
                        Reg.I_DC_Current = SetFacktor((short)0, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[61], buffer[62]});
                        Factor = ModbusTools.byteArrayToShort(buffer[64],buffer[63]);
                        Reg.I_DC_Voltage = SetFacktor(Factor, Wert);

                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[65], buffer[66]});
                        Factor = ModbusTools.byteArrayToShort(buffer[68],buffer[67]);
                        Reg.I_DC_Power = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[69], buffer[70]});
                        Factor = ModbusTools.byteArrayToShort(buffer[72],buffer[71]);
                        Reg.I_Temp = SetFacktor(Factor, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[73], buffer[74]});
                        Reg.I_Status1 = SetFacktor((short)0, Wert);
                        
                        Wert = ModbusTools.byteArrayToInt16(new byte[]{buffer[75], buffer[76]});
                        Reg.I_Status2 = SetFacktor((short)0, Wert);

                        status = 1;
                    } catch (SerialPortException ex) {
                        Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }

       

        private int SetFacktor(short Factor, int Wert) {
           int Leistung = 0;
            if (Factor == 0) {
                Leistung = Wert / 1;
            }
            if (Factor == -1) {
                Leistung = Wert / 10;
            }
            if (Factor == -2) {
                Leistung = Wert / 100;
            }
            return Leistung;
        }
        
    }

}
