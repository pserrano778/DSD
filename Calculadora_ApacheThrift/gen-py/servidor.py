import glob
import sys

from calculadora import Calculadora
from calculadora.ttypes import DivideEntre0

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import logging

logging.basicConfig(level = logging.DEBUG)

class CalculadoraHandler:
	def __init__(self):
		self.log = {}

	def ping(self):
		print("ping")

	def suma(self, n1, n2):
		print("sumando " + str(n1) + " con " + str(n2))
		return n1 + n2

	def resta(self, n1, n2):
		print("restando " + str(n1) + " con " + str(n2))
		return n1 - n2

	def multiplica(self, n1, n2):
		print("multiplicando " + str(n1) + " con " + str(n2))
		return n1 * n2

	def divide(self, n1, n2):
		print("dividiendo " + str(n1) + " con " + str(n2))
		if n2 == 0:
			e = DivideEntre0()
			e.codigo = 1
			e.cadena = 'No se puede dividir entre 0'
			print("Lanzando excepcion al cliente: " + e.cadena)
			raise e
		return n1 / n2	

		
			

if __name__ == "__main__":
	handler = CalculadoraHandler()

	processor = Calculadora.Processor(handler)
	transport = TSocket.TServerSocket(host="127.0.0.1", port=9090)
	tfactory = TTransport.TBufferedTransportFactory()
	pfactory = TBinaryProtocol.TBinaryProtocolFactory()

	server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

	print("starting server...")

	server.serve()

	print("done")
