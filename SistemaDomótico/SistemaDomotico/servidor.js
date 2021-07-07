var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io");

var MongoClient = require('mongodb').MongoClient;
var MongoServer = require('mongodb').Server;

var mimeTypes = { "html": "text/html",
 "jpeg": "image/jpeg",
 "jpg": "image/jpeg",
 "png": "image/png",
 "js": "text/javascript",
 "css": "text/css",
 "swf": "application/x-shockwave-flash",
 "ico": "image/x-icon"
};


class Agente{
	constructor() {
		this.umbralMinTemperatura = 30;
		this.umbralMaxTemperatura = 45;

		this.umbralMinLuminosidad = 60;
		this.umbralMaxLuminosidad = 80;

		this.temperaturaActual = null;
		this.luminosidadActual = null;
	}

	comprobarTemperatura(){
		if(this.temperaturaActual >= this.umbralMinTemperatura && this.temperaturaActual!=null){
			ioUsers.sockets.emit('alertaTemperatura', "Tempertura elevada: " + this.temperaturaActual);

			if(!estadoAire){
				estadoAire = !estadoAire;
				ioUsers.sockets.emit('estadoAire', estadoAire);
				ioSensores.sockets.emit('estadoAireSensores', estadoAire);
			}
		}
	}

	comprobarLuminosidad(){
		if(this.luminosidadActual >= this.umbralMinLuminosidad && this.luminosidadActual!=null){
			ioUsers.sockets.emit('alertaLuminosidad', "Luminosidad elevada: " + this.luminosidadActual);
		}
	}

	comprobarMaximos(){
		if (this.temperaturaActual >= this.umbralMaxTemperatura && this.luminosidadActual >= this.umbralMaxLuminosidad && estadoPersiana){
			estadoPersiana = !estadoPersiana;
			ioUsers.sockets.emit('estadoPersiana', estadoPersiana);
			ioSensores.sockets.emit('estadoPersianaSensores', estadoPersiana);
			ioUsers.sockets.emit('alertaPersiana', "Persiana bajada automáticamente (Temperatura: " + this.temperaturaActual + " || Luminosidad: " + this.luminosidadActual + ").");
		}
	}

	setTemperaturaActual(temp){
		this.temperaturaActual = temp;
		this.comprobarTemperatura();
		this.comprobarMaximos();
	}

	setLuminosidadActual(luz){
		this.luminosidadActual = luz;
		this.comprobarLuminosidad();
		this.comprobarMaximos();
	}

	setUmbralMinTemperatura(umbral){
		this.umbralMinTemperatura = umbral;
		ioUsers.sockets.emit('umbralMinTemperatura', umbral);
		this.comprobarTemperatura();
	}

	setUmbralMaxTemperatura(umbral){
		this.umbralMaxTemperatura = umbral;
		ioUsers.sockets.emit('umbralMaxTemperatura', umbral);
		this.comprobarMaximos();
	}

	setUmbralMinLuminosidad(umbral){
		this.umbralMinLuminosidad = umbral;
		ioUsers.sockets.emit('umbralMinLuminosidad', umbral);
		this.comprobarLuminosidad();
	}

	setUmbralMaxLuminosidad(umbral){
		this.umbralMaxLuminosidad = umbral;
		ioUsers.sockets.emit('umbralMaxLuminosidad', umbral);
		this.comprobarMaximos();
	}

	datosInicialesCliente(cliente){
		var cadTemperatura = "No se ha registrado ninguna temperatura";
		var cadLuminosidad = "No se ha registrado un valor de luminosidad";

		cliente.emit('umbralMinTemperatura', this.umbralMinTemperatura);
		cliente.emit('umbralMaxTemperatura', this.umbralMaxTemperatura);
		cliente.emit('umbralMinLuminosidad', this.umbralMinLuminosidad);
		cliente.emit('umbralMaxLuminosidad', this.umbralMaxLuminosidad);

		if (this.temperaturaActual != null){
			cliente.emit('nuevaTemperatura', "La temperatura actual es de " + this.temperaturaActual + ".");
		} else{
			cliente.emit('nuevaTemperatura', cadTemperatura);
		}
		if(this.luminosidadActual != null){
			cliente.emit('nuevaLuminosidad', "La luminosidad actual es de " + this.luminosidadActual + ".");
		}else{
			cliente.emit('nuevaLuminosidad',cadLuminosidad);
		}
		if(this.temperaturaActual >= this.umbralMinTemperatura){
			cliente.emit('alertaTemperatura', "Tempertura elevada: " + this.temperaturaActual);
		}
		if(this.luminosidadActual >= this.umbralMinLuminosidad){
			cliente.emit('alertaLuminosidad', "Luminosidad elevada: " + this.luminosidadActual);
		}
	}
}

var agente = new Agente();
var estadoAire = false;
var estadoPersiana = true;

var ioSensores;
var ioUsers;

var httpServerSensores = http.createServer(function (request, response) {
	var pathname = url.parse(request.url).pathname;
	var ext = path.extname(pathname);
		if(ext){
			if(ext === '.css'){
				response.writeHead(200, {'Content-Type': 'text/css'});
			}
			else if(ext === '.js'){
				response.writeHead(200, {'Content-Type': 'text/javascript'});
			}
			else if(ext === '.ico'){
				//No hacemos nada con el favicon
				response.writeHead(200, {'Content-Type': 'image/x-icon'});
			}
	
			response.write(fs.readFileSync(__dirname + pathname, 'utf8'));
	  }
	  else{
			response.writeHead(200, {'Content-Type': 'text/html'});
			response.write(fs.readFileSync('./sensores/sensores.html', 'utf8'));
	  }
	response.end();
});

var httpServerUsers = http.createServer(function (request, response) {
	var pathname = url.parse(request.url).pathname;
	var ext = path.extname(pathname);
	  	if(ext){
			if(ext === '.css'){
				response.writeHead(200, {'Content-Type': 'text/css'});
			}
			else if(ext === '.js'){
				response.writeHead(200, {'Content-Type': 'text/javascript'});
			}
			else if(ext === '.ico'){
				//No hacemos nada con el favicon
				response.writeHead(200, {'Content-Type': 'image/x-icon'});
			}
	
			response.write(fs.readFileSync(__dirname + pathname, 'utf8'));
		}
		else{
			response.writeHead(200, {'Content-Type': 'text/html'});
			response.write(fs.readFileSync('./usuarios/usuarios.html', 'utf8'));
		}
	response.end();
});

MongoClient.connect("mongodb://localhost:27017/", {useUnifiedTopology:true}, function(err, db) {

	httpServerSensores.listen(8080);
	httpServerUsers.listen(8081);

	ioSensores = socketio.listen(httpServerSensores);
	ioUsers = socketio.listen(httpServerUsers);

	var dbo = db.db("sistemaDomotico_P4");

	ioSensores.sockets.on('connection', function(client){

		client.emit('estadoAireSensores', estadoAire);
		client.emit('estadoPersianaSensores', estadoPersiana);

		client.on('enviarTemperatura', function(temp){
			var temperatura = temp['temperatura'];
			console.log("Nueva temperatura recibida: " + temperatura);
			ioUsers.sockets.emit('nuevaTemperatura', "La temperatura actual es de " + temperatura + ".");
			agente.setTemperaturaActual(temperatura);
			dbo.createCollection("temperatura", function(err, collection){
				collection.insertOne({temperatura:temperatura, time:new Date(temp['time'])}, {safe:true}, function(err, result) {});
				collection.find().sort({_id:-1}).limit(6).toArray(function(err, results){
					ioUsers.emit('actualizarDatosTemperatura', results);
				});
			});
		});

		client.on('enviarLuminosidad', function(luz){
			var luminosidad = luz['luz'];
			console.log("Nueva luminosidad recibida: " + luminosidad);
			ioUsers.sockets.emit('nuevaLuminosidad', "La luminosidad actual es de " + luminosidad + ".");
			agente.setLuminosidadActual(luminosidad);
			dbo.createCollection("luminosidad", function(err, collection){
				collection.insertOne({luminosidad:luminosidad, time:new Date(luz['time'])}, {safe:true}, function(err, result) {});
				collection.find().sort({_id:-1}).limit(6).toArray(function(err, results){
					ioUsers.emit('actualizarDatosLuminosidad', results);
				});
			});
		});
	});

	ioUsers.sockets.on('connection', function(client){
		client.emit('estadoAire', estadoAire);
		client.emit('estadoPersiana', estadoPersiana);

		dbo.createCollection("temperatura", function(err, collection){
			collection.find().sort({_id:-1}).limit(6).toArray(function(err, results){
				client.emit('datosTemperatura', results);
			});
		});
		dbo.createCollection("luminosidad", function(err, collection){
			collection.find().sort({_id:-1}).limit(6).toArray(function(err, results){
				client.emit('datosLuminosidad', results);
			});
		});
		agente.datosInicialesCliente(client);

		client.on('cambiarAire', function(cambiar){
			estadoAire = !estadoAire;
			ioUsers.sockets.emit('estadoAire', estadoAire);
			ioSensores.sockets.emit('estadoAireSensores', estadoAire);
		});
		client.on('cambiarPersiana', function(cambiar){
			estadoPersiana = !estadoPersiana;
			ioUsers.sockets.emit('estadoPersiana', estadoPersiana);
			ioSensores.sockets.emit('estadoPersianaSensores', estadoPersiana);
		})
		client.on('enviarUmbralMinTemperatura', function(umbralTemp){
			agente.setUmbralMinTemperatura(umbralTemp);
		});
		client.on('enviarUmbralMaxTemperatura', function(umbralTemp){
			agente.setUmbralMaxTemperatura(umbralTemp);
		});
		client.on('enviarUmbralMinLuminosidad', function(umbralLuz){
			agente.setUmbralMinLuminosidad(umbralLuz);
		});
		client.on('enviarUmbralMaxLuminosidad', function(umbralLuz){
			agente.setUmbralMaxLuminosidad(umbralLuz);
		});
	});
});

console.log("Servicio iniciado");

