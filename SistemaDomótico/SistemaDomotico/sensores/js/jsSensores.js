
var serviceURL = document.URL;
var socket = io.connect(serviceURL);

var estadoAire = false;
var estadoPersiana = true;

socket.on('estadoAireSensores', function(estado){
    var mensaje = "";
    if(estado){
        mensaje = "Aire acondicionado encendido.";
        estadoAire = true;
    } else{
        mensaje = "Aire acondicionado apagado.";
        estadoAire = false;
    }
    document.getElementById("estadoAireAcondicionado").innerText = mensaje;
});

socket.on('estadoPersianaSensores', function(estado){
    var mensaje = "";
    if(estado){
        mensaje = "Persiana subida.";
        estadoPersiana = true;
    } else{
        mensaje = "Persiana bajada.";
        estadoPersiana = false;
    }
    document.getElementById("estadoPersiana").innerText = mensaje;
});

function enviarTemperatura(){
    if (document.getElementById('temp').value != ""){
        var temperatura = document.getElementById('temp').value;
        document.getElementById('temp').value = "";
        socket.emit('enviarTemperatura', {temperatura:temperatura, time:(new Date())});
    } else{
        alert("No puede enviar una temperatura nula.");
    }
    
}

function enviarLuminosidad(){
    if(document.getElementById('luz').value != ""){
        var luz = document.getElementById('luz').value;
        document.getElementById('luz').value = "";

        socket.emit('enviarLuminosidad', {luz:luz, time:new Date()});
    } else{
        alert("No puede enviar una luminosidad nula.");
    }
}

var timer;
var activo = false;
function timerDatos(){
    if(!activo){
        timer = setInterval(datosAutomaticos, 5000); //Cada 5 segundos
        document.getElementById("botonTemp").type = "hidden";
        document.getElementById("botonLuz").type = "hidden";
        document.getElementById("botonAuto").value = "Parar datos Automaticos";
        activo = true;
    }else{
        clearInterval(timer);
        document.getElementById("botonTemp").type = "submit";
        document.getElementById("botonLuz").type = "submit";
        document.getElementById("botonAuto").value = "Iniciar datos Automaticos";
        activo = false;
    }
}

function datosAutomaticos(){
    obtenerTemperaturaApi();
    generarLuminosidad();
}

function obtenerTemperaturaApi(){
    var xmlhttp = new XMLHttpRequest();
    var handleServerResponse = function() {
      if (this.readyState == 4 && this.status == 200) {
        var response = JSON.parse(this.responseText); 
        var temp = response['main']['temp'];
        temp = temp - 273.15;
        temp = aplicarFiltroTemperatura(temp);
        var eliminarDecimal = Math.round(temp * 10);
        temp = eliminarDecimal/10;
        socket.emit('enviarTemperatura', {temperatura:temp, time:(new Date())});
      }
    };
    xmlhttp.open("GET", "http://api.openweathermap.org/data/2.5/weather?id=2517115&appid=345080c9193dd650e71efbd9d99efe97", true);
    xmlhttp.onreadystatechange = handleServerResponse;
    xmlhttp.send();
}

function aplicarFiltroTemperatura(temp){
    var temperatura;
    if(estadoAire){
        temperatura = temp - (Math.random() * (10 - 0) + 0);
    }
    else{
        temperatura = temp + (Math.random() * (4 - 0) + 0);
    }

    if (temperatura < -30){ //Límites
        temperatura = -30;
    } else if(temperatura > 70){
        temperatura = 70;
    }

    return temperatura;
}

var luminosidadAnterior = 50;

function generarLuminosidad(){
    var luminosidad;

    if (!estadoPersiana){
        luminosidad = 0;
    } else{
        luminosidad = luminosidadAnterior + (Math.random() * (5 - (-5)) + (-5));
        var eliminarDecimal = Math.round(luminosidad* 10);
        luminosidad = eliminarDecimal/10;

        if (luminosidad < 0){ //Límites
            luminosidad = 0;
        } else if(luminosidad > 100){
            luminosidad = 100;
        }

        luminosidadAnterior = luminosidad;

    }
    socket.emit('enviarLuminosidad', {luz:luminosidad, time:new Date()});
}