namespace java calculadora
namespace py calculadora

exception DivideEntre0 {
  1: i32 codigo,
  2: string cadena
}

service Calculadora {
    void ping (),
    double suma (1: double num1 , 2: double num2 ),
    double resta (1: double num1 , 2: double num2 ),
    double multiplica (1: double num1 , 2: double num2 ),
    double divide (1: double num1 , 2: double num2 ) throws (1:DivideEntre0 div0),
}