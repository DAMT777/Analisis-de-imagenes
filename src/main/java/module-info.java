module com.mycompany.prototiposoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires cloudinary.core;
    requires dotenv.java;
    requires jbcrypt;
    requires com.google.gson;
    requires java.sql;

    opens com.mycompany.prototiposoftware to javafx.fxml;
    exports com.mycompany.prototiposoftware;
}
