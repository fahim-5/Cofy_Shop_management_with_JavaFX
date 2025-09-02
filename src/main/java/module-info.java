module com.example.cofy_shop_managment {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.cofy_shop_managment to javafx.fxml;
    exports com.example.cofy_shop_managment;
}