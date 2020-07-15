package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActivateUserController {
    public JFXTextField txtUserName;
  
    public AnchorPane ActivateUser;
    public JFXButton btnSignIn;
    public JFXButton btnSignUp;
    public JFXPasswordField txtPassword;

    public void initialize(){
        ArrayList<String> userNames=new ArrayList<>();

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String userName = resultSet.getString(1);
                userNames.add(userName);


            }
            if(userNames.size()==0){
                btnSignIn.setDisable(true);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void btnAddNewUser_OnAction(ActionEvent actionEvent) {
        if(validateUserNamePassword()==false){
            new Alert(Alert.AlertType.ERROR,"Enter A Valid UserName Or Password,Password Must Contain At Least 10 character & It Must Have At Least 1 Numeric Character,1 Uppercase character,1 Lowercase Character and 1 Special Character ", ButtonType.OK).show();
            return;
        }
        if(checkForDuplicateUserName(txtUserName.getText())==false || checkForDuplicatePassword(txtPassword.getText())==false){
            new Alert(Alert.AlertType.ERROR,"UserName Or Password Exists Already",ButtonType.OK).show();
            return;
        }




        else{
            btnSignIn.setDisable(false);
            try {
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user values (?,?)");
                preparedStatement.setObject(1,txtUserName.getText());
                preparedStatement.setObject(2,txtPassword.getText());
                int i = preparedStatement.executeUpdate();
                if(i>0){
                    new Alert(Alert.AlertType.INFORMATION,"User Added Successfully",ButtonType.OK).show();
                }


            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private boolean validateUserNamePassword() {
        boolean flag = false;
        if (txtUserName.getText().trim().length() == 0 || txtPassword.getText().trim().length() == 0) {
            flag = false;

        } else {
            if (txtPassword.getText().trim().length() < 10) {
                flag = false;
            } else {
                int digitCount = 0;
                int upperCaseCount = 0;
                int lowerCaseCount = 0;
                int otherSymbolCount = 0;
                for (int i = 0; i < txtPassword.getText().length(); i++) {
                    if (Character.isDigit(txtPassword.getText().charAt(i))) {
                        digitCount++;


                    } else if (Character.isAlphabetic(txtPassword.getText().charAt(i))) {
                        if (Character.isUpperCase(txtPassword.getText().charAt(i))) {
                            upperCaseCount++;

                        } else if (Character.isLowerCase(txtPassword.getText().charAt(i))) {
                            lowerCaseCount++;

                        }
                    } else {
                        otherSymbolCount++;

                    }

                }
                if (digitCount < 1 || upperCaseCount < 1 || lowerCaseCount < 1 || otherSymbolCount < 1) {
                    flag = false;
                } else {
                    flag = true;
                }


            }


        }
        return flag;
    }

    public void btnLogIn_OnAction(ActionEvent actionEvent) {

        try {
           Parent root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
            Scene newScene=new Scene(root);
            Stage stage=(Stage)ActivateUser.getScene().getWindow();
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private boolean checkForDuplicateUserName(String str) {
        ArrayList<String> userNames=new ArrayList<>();
        boolean flag=false;


        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String userName = resultSet.getString(1);
                userNames.add(userName);

            }
            for(int i=0;i<userNames.size();i++){
                if(str.equals(userNames.get(i))){
                    flag=false;
                    break;
                }
                else{
                    flag=true;

                }

            }



        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        return flag;
    }

    private boolean checkForDuplicatePassword(String str){
        ArrayList<String> passwords=new ArrayList<>();
        boolean flag=false;



        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String password= resultSet.getString(2);
                passwords.add(password);

            }
            for(int i=0;i<passwords.size();i++){
                if(str.equals(passwords.get(i))){
                    flag=false;
                    break;
                }
                else{
                    flag=true;

                }

            }



        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        return flag;





    }

























}


