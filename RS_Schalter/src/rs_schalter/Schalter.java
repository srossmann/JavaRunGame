/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs_schalter;

/**
 *
 * @author rossmann
 */
public class Schalter {
//--------------------------------------------------------------
// RC Switch
//--------------------------------------------------------------
    public static rsschalter.RCSwitch transmitter = null;
//--------------------------------------------------------------
    public String Topic;
    public String Bereich;
    public Integer Adresse;

    public Schalter(String Top, String Ber, Integer Adr,rsschalter.RCSwitch Transmitter)
    {
        Topic = Top;
        Bereich = Ber;
        Adresse = Adr;
       transmitter = Transmitter;

            
    //      transmitter = new rsschalter.RCSwitch(RaspiPin.GPIO_06);
    }
    
    public void Ein(){
        //System.out.println("Schalter Ein "+Bereich+" "+Adresse);
        transmitter.LichtAn(Bereich,Adresse );
    };
 
    public void Aus(){
        //System.out.println("Schalter Aus "+Bereich+" "+Adresse);
        transmitter.LichtAus(Bereich,Adresse );
    };
}
