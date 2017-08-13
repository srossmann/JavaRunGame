pushover({
    message:"Das Fenster im Bad sollte geschlossen werden."
});
var exec = require('child_process').exec;
exec('sudo java -jar /opt/ccu.io/scripts/IOTest.jar 7 8', function (error, stdout, stderr) {
setState(1599, stdout);
});

 