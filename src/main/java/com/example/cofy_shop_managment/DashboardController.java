package com.example.cofy_shop_managment;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private PieChart salesChart;

    @FXML
    private Button logoutBtn;

    @FXML
    public void initialize() {
        // Sample chart data
        salesChart.getData().add(new PieChart.Data("Coffee", 45));
        salesChart.getData().add(new PieChart.Data("Snacks", 25));
        salesChart.getData().add(new PieChart.Data("Other", 30));
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.close(); // Just close for now, or you can redirect back to login screen
    }
}
