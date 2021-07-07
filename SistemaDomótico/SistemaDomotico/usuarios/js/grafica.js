var graficaTemperatura;
var graficaLuminosidad;

function generarGrafica(datosMedidas, tipo){
	var ctx="";
	var backgroundColor="";
	var label="";
	var borderColor="";
	var min;
	var max;
	if (tipo == "temperatura"){
		ctx = document.getElementById('myChartTemperatura');
		label = 'Temperatura';
		backgroundColor = 'rgba(255, 99, 132, 0.2)';
		borderColor = 'rgba(255, 99, 132, 1)';
		min = -30;
		max = 70;
	} else{
		ctx = document.getElementById('myChartLuminosidad');
		label = 'Luminosidad';
		backgroundColor = 'rgba(255, 206, 86, 0.2)';
		borderColor = 'rgba(255, 206, 86, 1)';
		min = 0;
		max = 100;
	}

	var info = Array();
	var datos = Array();
	for (var i=0; i<datosMedidas.length; i++){
		var time = new Date(datosMedidas[datosMedidas.length-1-i]['time']);
		time = time.getDate()+"-"+time.getMonth()+"-"+time.getFullYear() + " "+time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();
		info.push (time);
    	datos.push(datosMedidas[datosMedidas.length-1-i][tipo]);
	}

	var myChart = new Chart(ctx, {
		type: 'line',
		data: {
			labels: info,
			datasets: [{
				label: label,
				data: datos,
				backgroundColor: [
					backgroundColor
				],
				borderColor: [
					borderColor
				],
				borderWidth: 2
			}]
		},
		options: {
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero: true,
						suggestedMin: min,
                    	suggestedMax: max
					}
				}]
			}
		}
	});

	return myChart;
}

function actualizarGrafica(datosMedidas, tipo, chart){

	for (var i=0; i<datosMedidas.length; i++){
		var time = new Date(datosMedidas[datosMedidas.length-1-i]['time']);
		time = time.getDate()+"-"+time.getMonth()+"-"+time.getFullYear() + " "+time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();
		chart.data.labels[i] = time;
    	chart.data.datasets[0].data[i]=(datosMedidas[datosMedidas.length-1-i][tipo]);
	}
	
    chart.update();
}

socket.on('datosTemperatura', function(datosMedidas) {
    graficaTemperatura = generarGrafica(datosMedidas, "temperatura");
});

socket.on('datosLuminosidad', function(datosMedidas) {
   graficaLuminosidad = generarGrafica(datosMedidas, "luminosidad");
});

socket.on('actualizarDatosTemperatura', function(datosMedidas) {
    actualizarGrafica(datosMedidas, "temperatura", graficaTemperatura);
});

socket.on('actualizarDatosLuminosidad', function(datosMedidas) {
    actualizarGrafica(datosMedidas, "luminosidad", graficaLuminosidad);
});