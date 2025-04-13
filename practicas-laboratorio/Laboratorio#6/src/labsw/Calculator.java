package labsw;

import java.util.Scanner;

public class Calculator {
    Scanner entrada = new Scanner(System.in);
    double n1, n2;
    public double Sum(double n1, double n2){
        return n1 + n2;
    }
    public double Substract(double n1, double n2){
        return n1 - n2;
    }
    public double Multiply(double n1, double n2){
        return n1 * n2;
    }
    public double Divide(double n1, double n2){
        do {
            if(n2 == 0){
            System.out.println("No se puede dividr por cero, por favor ingrese otro numero\n");
            System.out.print(">>");
            n2 = entrada.nextDouble();
            }
        } while (n2 == 0);
        return n1 / n2;
    }
}
