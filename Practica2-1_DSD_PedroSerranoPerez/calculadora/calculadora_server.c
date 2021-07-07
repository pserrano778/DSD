/********************************************************************************/
// Pedro Serrano Pérez
// DSD grupo 2
// Práctica 2 ejercicio 1 - Calculadora (Servidor)
/********************************************************************************/

#include "calculadora.h"

double *suma_1_svc(double arg1, double arg2,  struct svc_req *rqstp)
{
	static double  result;
	result = arg1 + arg2;

	return &result;
}

double *resta_1_svc(double arg1, double arg2,  struct svc_req *rqstp)
{
	static double  result;
	result = arg1 - arg2;

	return &result;
}

double *multi_1_svc(double arg1, double arg2,  struct svc_req *rqstp)
{
	static double  result;
	result = arg1 * arg2;

	return &result;
}

double *div_1_svc(double arg1, double arg2,  struct svc_req *rqstp)
{
	static double  result;
	result = arg1 / arg2;

	return &result;
}