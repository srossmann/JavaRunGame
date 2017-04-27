/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pi4modbus.Serial;

/**
 *
 * @author Rossmann
 */
public class SolarEdgeRegister {
    public byte buffer[];
    public int C_SunSpec_DID = 0;

    
    public int I_AC_Power = 0;
    public int I_AC_Current;
    public int I_AC_CurrentA;
    public int I_AC_CurrentB;
    public int I_AC_CurrentC;
    public int I_AC_VoltageAB;
    public int I_AC_VoltageBC;
    public int I_AC_VoltageCA;
    public int I_AC_VoltageAN;
    public int I_AC_VoltageBN;
    public int I_AC_VoltageCN;
    public int I_AC_Frequency;
    public int I_AC_VA;
    public int I_AC_VAR;
    public int I_AC_PF;
    public int I_AC_EnergyWH;
    public int I_DC_Current;
    public int I_DC_Voltage;
    public int I_DC_Power;
    public int I_Temp;
    public int I_Status1;
    public int I_Status2;
    

    
    
    
}
