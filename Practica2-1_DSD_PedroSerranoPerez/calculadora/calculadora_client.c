/********************************************************************************/
// Pedro Serrano Pérez
// DSD grupo 2
// Práctica 2 ejercicio 1 - Calculadora (Cliente)
/********************************************************************************/

#include "calculadora.h"
#include <stdlib.h>
#include <ctype.h>

double sumaprog_1(char *server, double num1, double num2)
{
	CLIENT *clnt;
	double * resultado;
	#ifndef DEBUG
	clnt = clnt_create(server, SUMAPROG, SUMAVER, "udp");
	if (clnt == (CLIENT *) NULL) {
		clnt_pcreateerror(server);
		exit(1);
	}
	#endif /* DEBUG */

	resultado = suma_1(num1, num2, clnt);
	if (resultado == (double *) NULL) {
		clnt_perror (clnt, "call failed");
	}

	#ifndef DEBUG
		clnt_destroy(clnt);
	#endif

	return *resultado;
}

double restaprog_1(char *server, double num1, double num2)
{
	CLIENT *clnt;
	double * resultado = 0;
	#ifndef DEBUG
	clnt = clnt_create(server, RESTAPROG, RESTAVER, "udp");
	if (clnt == (CLIENT *) NULL) {
		clnt_pcreateerror(server);
		exit(1);
	}
	#endif /* DEBUG */

	resultado = resta_1(num1, num2, clnt);
	if (resultado == (double *) NULL) {
		clnt_perror (clnt, "call failed");
	}

	#ifndef DEBUG
		clnt_destroy(clnt);
	#endif

	return *resultado;
}

double multiprog_1(char *server, double num1, double num2)
{
	CLIENT *clnt;
	double * resultado = 0;
	#ifndef DEBUG
	clnt = clnt_create(server, MULTIPROG, MULTIVER, "udp");
	if (clnt == (CLIENT *) NULL) {
		clnt_pcreateerror(server);
		exit(1);
	}
	#endif /* DEBUG */

	resultado = multi_1(num1, num2, clnt);
	if (resultado == (double *) NULL) {
		clnt_perror (clnt, "call failed");
	}

	#ifndef DEBUG
		clnt_destroy(clnt);
	#endif

	return *resultado;
}

double divprog_1(char *server, double num1, double num2)
{
	CLIENT *clnt;
	double * resultado;
	#ifndef DEBUG
	clnt = clnt_create(server, DIVPROG, DIVVER, "udp");
	if (clnt == (CLIENT *) NULL) {
		clnt_pcreateerror(server);
		exit(1);
	}
	#endif /* DEBUG */

	resultado = div_1(num1, num2, clnt);
	if (resultado == (double *) NULL) {
		clnt_perror (clnt, "call failed");
	}

	#ifndef DEBUG
		clnt_destroy(clnt);
	#endif

	return *resultado;
}

void eliminaElementoVector(double *vector, int elemento, int tam)
{
	for (int i=elemento; i<tam-1; i++){
		vector[i] = vector[i+1];
		vector[i+1] = 0;
	}
}

void procesaSumasRestas(double *numeros, char *operaciones, int tam_operaciones, int tam_numeros, char *server)
{
	for (int i=0, j=0; i<tam_operaciones; i++){ //Bucle que realiza las sumas y restas
		if (operaciones[i] == '+'){
			numeros[j] =  sumaprog_1(server, numeros[j], numeros[j+1]);
			eliminaElementoVector(numeros, j+1, tam_numeros);
		}
		else if (operaciones[i] == '-'){
			numeros[j] = restaprog_1(server, numeros[j], numeros[j+1]);
			eliminaElementoVector(numeros, j+1, tam_numeros);
		}

	}
}

void procesaMultiplicacionesDivisiones(double *numeros, char *operaciones, int tam_operaciones, int tam_numeros, char *server)
{
	for (int i=0, j=0; i<tam_operaciones; i++){ //Bucle que realiza las multiplicaciones y divisiones
		if (operaciones[i] == '*'){
			numeros[j] =  multiprog_1(server, numeros[j], numeros[j+1]);
			eliminaElementoVector(numeros, j+1, tam_numeros);
		}
		else if (operaciones[i] == '/'){
			numeros[j] = divprog_1(server, numeros[j], numeros[j+1]);
			eliminaElementoVector(numeros, j+1, tam_numeros);
	
		}
		else {
			j++;
		}
	}
}

int main (int argc, char *argv[])
{
	char *server;
	double num1;
	double num2;
	double resultado = 0.0;
	int indice_operaciones;
	int indice_numeros;

	if ((argc-2)%2 == 0 || argc < 5) { //la candidad de "entero operador entero" es par o hay menos de 5 elementos
		printf("uso: %s server_host entero operador(+, -, \"*\", /) entero ...\n", argv[0]);
		exit(1);
	}

	int tam_numeros = (argc-2)/2 + 1;
	int tam_operaciones = (argc-2)/2;

	double *numeros = (double*)malloc(sizeof(double)*(tam_numeros)); //2/3 serán números
	char *operaciones = (char*)malloc(sizeof(char)*(tam_operaciones)); //1/3 serán operaciones

	indice_operaciones = 0;
	indice_numeros = 0;

	int primera_iteracion = 1;

	for (int i=3; i<argc; i+=2){ //Bucle para comprobar las operaciones que se quieren realizar y organizar en vectores las operaciones y números
		char *recorre_numero = 0;
		char *recorre_operacion = argv[i];
		int primer_caracter = 1;

		if (primera_iteracion){ //Si es la primera iteracion
			recorre_numero = argv[i-1]; //Puntero en la cadena a recorrer
			while(*recorre_numero != '\0'){ //Se realiza un bucle para comprobar que es un numero entero
				if (!(isdigit(*recorre_numero)) && !(primer_caracter && *recorre_numero == '-')){ //Si no es un digito y no es el primer caracter no es negativo
					printf("Utilice números enteros\n");
					exit(2);
				}
				recorre_numero++;

				if (primer_caracter)
					primer_caracter = 0;
			}
			numeros[indice_numeros] = atoi(argv[i-1]); //Es un número entero válido
			indice_numeros ++;
			primera_iteracion = 0;

			primer_caracter = 1;
			recorre_numero = 0; //Puntero nulo
		}

		recorre_numero = argv[i+1]; //Puntero en el siguiente numero
		while (*recorre_numero != '\0'){ //Se comprueba el siguiente número es un entero
		
			if (!(isdigit(*recorre_numero)) && !(primer_caracter && *recorre_numero == '-')){ //Si no es un digito o el primer caracter no es negativo
				printf("Utilice números enteros\n");
				exit(3);
			}
			recorre_numero ++;

			if (primer_caracter)
					primer_caracter = 0;
		}
		
		if ((*recorre_operacion != '+' && *recorre_operacion != '-' && *recorre_operacion != '*' && *recorre_operacion != '/') || *(++recorre_operacion) != '\0'){ //Se comprueba el operador
			printf("Utilice un operador válido (+, -, \"*\", /)\n");
			exit(4);
		}

		numeros[indice_numeros] = atoi(argv[i+1]);
		indice_numeros ++;

		operaciones[indice_operaciones] = *argv[i];
		indice_operaciones ++;
	}

	server = argv[1];

	procesaMultiplicacionesDivisiones(numeros, operaciones, tam_operaciones, tam_numeros, server);
	procesaSumasRestas(numeros, operaciones, tam_operaciones, tam_numeros, server);
	
	resultado = numeros[0];
	printf("Resultado: %f\n", resultado);

	free(numeros);
	free(operaciones);
	exit (0);
}
