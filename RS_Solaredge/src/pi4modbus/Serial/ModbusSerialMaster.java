package pi4modbus.Serial;

import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortException;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rossmann
 */
public class ModbusSerialMaster {

    public ModbusSerialMaster(String SerialPortName) {
        SerialConnection.open(SerialPortName);

    }


//    public void ReadHoldingRegister(int offset, int count) {
    public void ReadHoldingRegister() {
        byte[] byteStr1 = new byte[1];
        byteStr1[0] = (byte) 1;

        byte[] byteStr = new byte[8];
        byteStr[0] = (byte) 1;
        byteStr[1] = (byte) 3;

        byte[] b1 = ModbusTools.int16ToByteArray(40069);

        byteStr[2] = (byte) b1[1];
        byteStr[3] = (byte) b1[0];

        b1 = ModbusTools.int16ToByteArray(40);

        byteStr[4] = (byte) b1[1];
        byteStr[5] = (byte) b1[0];

        ModbusCRC crc = new ModbusCRC();

        crc.update(byteStr, 0, 6);
        byte[] crcbyte = new byte[2];
        crcbyte = crc.getCrcBytes();
        byteStr[6] = crcbyte[0];
        byteStr[7] = crcbyte[1];

        StringBuilder sb = new StringBuilder();
        for (byte b : byteStr) {
            sb.append(String.format("%02X ", b));
        }
        SerialConnection.sendData(byteStr);
    }

    public void Close() {
        SerialConnection.close();
    }
}
