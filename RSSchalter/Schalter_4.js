var ID = 1909;
var Gruppe = 10001;
var Schalter = 4;

subscribe({id: ID }, function (data){codebox_0(data); });

 //Program_0
function codebox_0(data){ 
   var input_1_out= getState(ID );
   if ( input_1_out == true) Schalter_ein(data);
   if ( input_1_out == false) Schalter_aus(data);
};

function Schalter_ein(data){ 
    var exec = require('child_process').exec;
    exec('sudo java -jar /home/pi/RSSchalter.jar 1 '+Gruppe+' '+Schalter, function (error, stdout, stderr) {});
    log("Schalter 1 ein"+data.name);

};

function Schalter_aus(data){ 
    var exec = require('child_process').exec;
    exec('sudo java -jar /home/pi/RSSchalter.jar 0 '+Gruppe+' '+Schalter, function (error, stdout, stderr) {});
    log("Schalter 1 aus "+data.name);

};