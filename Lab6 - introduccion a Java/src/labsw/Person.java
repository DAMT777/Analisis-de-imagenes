package labsw;
public class Person {
    String firstName, lastName;
    int personId, age;
    
    public Person(String firstName, String lastName, int personId, int age){
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.personId = personId;
    }
    
    public String getFullName(){
        return firstName + " " + lastName;
    }
}
