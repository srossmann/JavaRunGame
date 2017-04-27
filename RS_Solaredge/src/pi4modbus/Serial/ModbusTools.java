/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4modbus.Serial;

import static pi4modbus.Serial.SerialConnection.buffer;

/**
 *
 * @author Rossmann
 */
public class ModbusTools {

    public static int CRCCheck() {
        return 12;
    }

     public static short byteArrayToShort(byte a,byte b) {
            short Factor = (short) ((a & 0xff) | (b << 8));
            return Factor;
        }
    
    public static int byteArrayToInt32(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static int byteArrayToInt16(byte[] b) {
        int value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static byte[] int32ToByteArray(int a) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[2] = (byte) ((a >> 16) & 0xFF);
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    public static byte[] int16ToByteArray(int a) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        return ret;
    }
}
