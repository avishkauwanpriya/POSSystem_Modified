package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainFormController {
    public AnchorPane mainForm;

    public void imgManageCustomer_OnClick(MouseEvent mouseEvent) {
        try {
            Parent root= FXMLLoader.load(this.getClass().getResource("/view/ManageCustomerForm.fxml"));
            Scene newScene=new Scene(root);
            Stage mainStage = (Stage) mainForm.getScene().getWindow();
            mainStage.setScene(newScene);



        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void imgSearchOrder_OnClick(MouseEvent mouseEvent) {

        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/SearchOrderForm.fxml"));
            Scene newScene=new Scene(root);
            Stage window = (Stage) mainForm.getScene().getWindow();
            window.setScene(newScene);
            window.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void imgSignUp_OnClick(MouseEvent mouseEvent) {
        try {
            Parent root=FXMLLoader.load(this.getClass().getResource("/view/ActivateUsers.fxml"));
            Scene newScene=new Scene(root);
            Stage stage=new Stage();
            stage.setScene(newScene);
            stage.centerOnScreen();
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void imgPlaceOrder_OnClick(MouseEvent mouseEvent) {

        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/placeOrderForm.fxml"));
            Scene newScene=new Scene(root);
            Stage window = (Stage) mainForm.getScene().getWindow();
            window.setScene(newScene);
            window.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }






    }

    public void imgManageItem_OnClick(MouseEvent mouseEvent) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/ItemForm.fxml"));
            Scene newScene=new Scene(root);
            Stage window = (Stage) mainForm.getScene().getWindow();
            window.setScene(newScene);
            window.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
