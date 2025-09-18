module com.example.vienneubahnassignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.vienneubahnassignment to javafx.fxml;
    exports com.example.vienneubahnassignment;
}