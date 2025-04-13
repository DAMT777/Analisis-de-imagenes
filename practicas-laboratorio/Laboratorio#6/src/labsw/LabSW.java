package labsw;

public class LabSW {
    public static void main(String[] args) {
        //Menu.mostrar_menu();
        Person p = new Person("Eso", "Tilin", 123456, 15);
        Calculator c = new Calculator();
        
        
        System.out.println("Suma: " + c.Sum(1, 2));
        System.out.println("Resta: " + c.Substract(3, 4));
        System.out.println("Division: " + c.Divide(12, 0));
        System.out.println("Multiplicacion: " + c.Multiply(1, 4));
        
        
        System.out.println("Nombre completo: " + p.getFullName());
    }
}


