module com.bennetvella {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bennetvella to javafx.fxml;
    exports com.bennetvella;
}
