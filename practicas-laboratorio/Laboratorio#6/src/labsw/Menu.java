package labsw;

import java.util.Scanner;

public class Menu {
    public static void mostrarMenu(){
        int opc;
        double n1, n2;
        int edad, id;
        String nombre, apellido;
        Calculator c1 = new Calculator();
        Scanner entrada = new Scanner (System.in);
        do {
            System.out.println("Seleccion la opcion que desea");
            System.out.println("1) Sumar");
            System.out.println("2) Restar");
            System.out.println("3) Multiplicar");
            System.out.println("4) Dividir");
            System.out.println("5) Crear un objeto persona");
            System.out.println("6) Salir del menu");
            System.out.print(">> ");
            opc = entrada.nextInt();
            
            if(opc == 1)
            {
                System.out.println("Suma\n\n");
                System.out.println("Digite el primer numero");
                n1 = entrada.nextDouble();
                System.out.println("Digite el segundo numero");
                n2 = entrada.nextDouble();
                System.out.println("Resultado: " + n1 + " + " + n2 + " = " + c1.Sum(n1, n2));
            }
            else if(opc == 2)
            {
                System.out.println("Resta\n\n");
                System.out.println("Digite el primer numero");
                n1 = entrada.nextDouble();
                System.out.println("Digite el segundo numero");
                n2 = entrada.nextDouble();
                System.out.println("Resultado: " + n1 + " - " + n2 + " = " + c1.Substract(n1, n2));
            }
            else if(opc == 3){
                System.out.println("Multiplicacion\n\n");
                System.out.println("Digite el primer numero");
                n1 = entrada.nextDouble();
                System.out.println("Digite el segundo numero");
                n2 = entrada.nextDouble();
                System.out.println("Resultado: " + n1 + " * " + n2 + " = " + c1.Multiply(n1, n2));
            }
            else if(opc == 4)
            {
                System.out.println("Division\n\n");
                System.out.println("Digite el dividendo");
                n1 = entrada.nextDouble();
                System.out.println("Digite el divisor");
                n2 = entrada.nextDouble();
                System.out.println("Resultado: " + n1 + " / " + n2 + " = " + c1.Divide(n1, n2));
            }
            else if(opc == 5)
            {
                System.out.println("Persona\n\n");
                System.out.println("Ingrese el nombre");
                nombre = entrada.nextLine();
                System.out.println("Ingrese el apellido");
                apellido = entrada.nextLine();
                System.out.println("Ingrese la edad");
                edad = entrada.nextInt();
                System.out.println("Ingrese el ID");
                id = entrada.nextInt();
                Person p1 = new Person(nombre, apellido, edad, id);
                System.out.println("Datos completos\n " + "Nombre completo: " + p1.getFullName() + "\nEdad: " + edad + "\nID: " + id);
            }
            else
            {
                System.out.println("Valor no valido digite nuevamente");
            }
        } while (opc != 6);
        System.out.println("Ha seleccionado la opcion de salida");
    }
}
