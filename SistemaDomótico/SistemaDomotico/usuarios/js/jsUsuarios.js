
function cambiarAire(){
    socket.emit('cambiarAire', "cambiar");
}

function cambiarPersiana(){
    socket.emit('cambiarPersiana', "cambiar");
}

var serviceURL = document.URL;

var socket = io.connect(serviceURL);

function enviarUmbralMinTemperatura(){
    if (document.getElementById('umbralMinTemp').value != ""){
        document.getElementById("alertaTemperatura").innerText = "";

        var umbralMinTemperatura = document.getElementById('umbralMinTemp').value;
        document.getElementById('umbralMinTemp').value = "";
        socket.emit('enviarUmbralMinTemperatura', umbralMinTemperatura);
    } else{
        alert("No puede enviar un umbral nulo.");
    }
}

function enviarUmbralMaxTemperatura(){
    if (document.getElementById('umbralMaxTemp').value != ""){
        document.getElementById("alertaPersiana").innerText = "";

        var umbralMaxTemperatura = document.getElementById('umbralMaxTemp').value;
        document.getElementById('umbralMaxTemp').value = "";
        socket.emit('enviarUmbralMaxTemperatura', umbralMaxTemperatura);
    } else{
        alert("No puede enviar un umbral nulo.");
    }
}

function enviarUmbralMinLuminosidad(){
    if (document.getElementById('umbralMinLuz').value != ""){
        document.getElementById("alertaLuminosidad").innerText = "";

        var umbralMinLuz = document.getElementById('umbralMinLuz').value;
        document.getElementById('umbralMinLuz').value = "";
        socket.emit('enviarUmbralMinLuminosidad', umbralMinLuz);
    } else{
        alert("No puede enviar un umbral nulo.");
    }
}

function enviarUmbralMaxLuminosidad(){
    if (document.getElementById('umbralMaxLuz').value != ""){
        document.getElementById("alertaPersiana").innerText = "";

        var umbralMaxLuz = document.getElementById('umbralMaxLuz').value;
        document.getElementById('umbralMaxLuz').value = "";
        socket.emit('enviarUmbralMaxLuminosidad', umbralMaxLuz);
    } else{
        alert("No puede enviar un umbral nulo.");
    }
}

socket.on('nuevaTemperatura', function(temperatura) {
    document.getElementById("alertaPersiana").innerText = "";
    document.getElementById("alertaTemperatura").innerText = "";
    document.getElementById("temperaturaActual").innerText = temperatura;
});

socket.on('nuevaLuminosidad', function(luminosidad) {
    document.getElementById("alertaPersiana").innerText = "";
    document.getElementById("alertaLuminosidad").innerText = "";
    document.getElementById("luminosidadActual").innerText = luminosidad;
});

socket.on('alertaTemperatura', function(mensaje) {
    document.getElementById("alertaTemperatura").innerText = mensaje;
});

socket.on('alertaLuminosidad', function(mensaje) {
    document.getElementById("alertaLuminosidad").innerText = mensaje;
});

socket.on('alertaPersiana', function(mensaje) {
    document.getElementById("alertaPersiana").innerText = mensaje;
});

socket.on('estadoAire', function(estado) {
    if(estado){ //Aire encendido 
        document.getElementById("aire").innerText = "Aire acondicionado encendido.";
        document.getElementById("accionAire").value = "Apagar";
    } else{ //Aire apagado
        document.getElementById("aire").innerText = "Aire acondicionado apagado.";
        document.getElementById("accionAire").value = "Encender";
    }
});

socket.on('estadoPersiana', function(estado) {
    if(estado){ //Persiana subida
        document.getElementById("alertaPersiana").innerText = "";
        document.getElementById("persiana").innerText = "Persiana subida.";
        document.getElementById("accionPersiana").value = "Bajar";
    } else{ //Persiana bajada
        document.getElementById("persiana").innerText = "Persiana bajada.";
        document.getElementById("accionPersiana").value = "Subir";
    }
});

socket.on('umbralMinTemperatura', function(umbral) {
    document.getElementById("umbralMinTemperaturaTexto").innerText = "Umbral Minimo Temperatura: " + umbral;
    document.getElementById("umbralMaxTemp").min = umbral;
});	

socket.on('umbralMaxTemperatura', function(umbral) {
    document.getElementById("umbralMaxTemperaturaTexto").innerText = "Umbral Maximo Temperatura: " + umbral;
    document.getElementById("umbralMinTemp").max = umbral;
});	

socket.on('umbralMinLuminosidad', function(umbral) {
    document.getElementById("umbralMinLuminosidadTexto").innerText = "Umbral Minimo Luminosidad: " + umbral;
    document.getElementById("umbralMaxLuz").min = umbral;
});	

socket.on('umbralMaxLuminosidad', function(umbral) {
    document.getElementById("umbralMaxLuminosidadTexto").innerText = "Umbral Maximo Luminosidad: " + umbral;
    document.getElementById("umbralMinLuz").max = umbral;
});	