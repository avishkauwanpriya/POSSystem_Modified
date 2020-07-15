package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginFormController {
    public JFXTextField txtUserName;
    
    public AnchorPane loginForm;
    public JFXPasswordField txtPassword;

    public void btnLogIn_OnAction(ActionEvent actionEvent) {
        if(validateUserName(txtUserName.getText(),txtPassword.getText())==true){
            try {
                Parent root=FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
                Scene newScene=new Scene(root);
                Stage stage=(Stage)loginForm.getScene().getWindow();
                stage.setScene(newScene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            new Alert(Alert.AlertType.ERROR,"Invalid UserName Or Password Entered", ButtonType.OK).show();
            return;
        }


    }


    private boolean validateUserName(String userName,String password){
        boolean bool=false;
        ArrayList<String> userNames=new ArrayList<>();
        ArrayList<String> passwords=new ArrayList<>();
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String userName1 = resultSet.getString(1);
                String password1=resultSet.getString(2);
                userNames.add(userName1);
                passwords.add(password1);

            }
            for(int i=0;i<userNames.size();i++){
                if(userName.equals(userNames.get(i))&& password.equals(passwords.get(i))){
                    bool=true;
                    break;
                }
                else{
                    bool=false;
                }

            }






        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return bool;
    }

    public void imgEye_OnClick(MouseEvent mouseEvent) {

    }
}
