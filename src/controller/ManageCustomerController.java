package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerTM;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageCustomerController {
    public AnchorPane customerForm;
    public JFXButton btnAddNewCustomer;
    public JFXButton btnSaveCustomer;
    public JFXButton btnDeleteCustomer;
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public TableView<CustomerTM> tblCustomer;

    public void  initialize(){
        setInitialize();
        tblCustomer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {
                if(tblCustomer.getSelectionModel().getSelectedIndex()==-1){
                    btnSaveCustomer.setText("Save Customer");
                    txtCustomerId.clear();
                    txtCustomerName.clear();
                    txtCustomerAddress.clear();
                    btnSaveCustomer.setDisable(true);
                    btnDeleteCustomer.setDisable(true);
                }
                else {
                    CustomerTM selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
                    btnSaveCustomer.setDisable(false);
                    btnSaveCustomer.setText("Update Customer");
                    btnDeleteCustomer.setDisable(false);

                    txtCustomerId.setDisable(false);
                    txtCustomerName.setDisable(false);
                    txtCustomerAddress.setDisable(false);
                    txtCustomerId.setText(selectedCustomer.getCustomerId());
                    txtCustomerName.setText(selectedCustomer.getCustomerName());
                    txtCustomerAddress.setText(selectedCustomer.getCustomerAddress());

                }

            }
        });




    }

    public void btnAddNewCustomer_OnAction(ActionEvent actionEvent) {
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        btnSaveCustomer.setDisable(false);
        txtCustomerId.setDisable(false);
        txtCustomerName.setDisable(false);
        txtCustomerAddress.setDisable(false);
        txtCustomerName.requestFocus();
        setCustomerIds();


    }

    public void btnSaveCustomer_OnAction(ActionEvent actionEvent) {

        if (validateNameAddress() == false) {
            new Alert(Alert.AlertType.ERROR, "Enter Valid Name Or Address", ButtonType.OK).show();
        }
        else {
            if (btnSaveCustomer.getText().equalsIgnoreCase("Save Customer")) {

                ObservableList<CustomerTM> customers = tblCustomer.getItems();
                try {
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customer VALUES(?,?,?)");
                    preparedStatement.setObject(1, txtCustomerId.getText());
                    preparedStatement.setObject(2, txtCustomerName.getText());
                    preparedStatement.setObject(3, txtCustomerAddress.getText());
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                customers.add(new CustomerTM(txtCustomerId.getText(), txtCustomerName.getText(), txtCustomerAddress.getText()));
                tblCustomer.refresh();
                btnSaveCustomer.setDisable(true);
            }
            else{
                ObservableList<CustomerTM> customers = tblCustomer.getItems();
                Connection connection = null;
                try {
                    connection = DBConnection.getDbConnection().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE customer set customerName=(?),customerAddress=(?) where customerId=(?)");
                    preparedStatement.setObject(1,txtCustomerName.getText());
                    preparedStatement.setObject(2,txtCustomerAddress.getText());
                    preparedStatement.setObject(3,txtCustomerId.getText());
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                CustomerTM selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
                selectedCustomer.setCustomerName(txtCustomerName.getText());
                selectedCustomer.setCustomerAddress(txtCustomerAddress.getText());
                tblCustomer.refresh();
                tblCustomer.getSelectionModel().clearSelection();


            }


        }
        }








    public void btnDeleteCustomer_OnAction(ActionEvent actionEvent) {
        CustomerTM selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
        ObservableList<CustomerTM> customers = tblCustomer.getItems();

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM customer WHERE customerId=(?)");
            preparedStatement.setObject(1,selectedCustomer.getCustomerId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        customers.remove(selectedCustomer);
        tblCustomer.refresh();
        tblCustomer.getSelectionModel().clearSelection();

    }

    public void imgHome_OnClick(MouseEvent mouseEvent) {
        Parent root= null;
        try {
            root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene newScene=new Scene(root);
            Stage mainStage = (Stage) customerForm.getScene().getWindow();
            mainStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void setCustomerIds()  {
        ArrayList<String> customerIds=new ArrayList<>();
        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select  * from customer");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String customerId = resultSet.getString(1);
                customerIds.add(customerId);

            }
            if(customerIds.size()==0){
                txtCustomerId.setText("C001");
            }
            else{
                String lastCustomerId = customerIds.get(customerIds.size() - 1);
                int i = Integer.parseInt(lastCustomerId.substring(1, 4));
                if(i<9){
                    txtCustomerId.setText("C00"+(i+1));
                }
                else if(i<99){
                    txtCustomerId.setText("C0"+(i+1));
                }
                else{
                    txtCustomerId.setText("C"+(i+1));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }






    }
    private boolean validateNameAddress(){
        boolean flag=false;
        if(txtCustomerName.getText().trim().length()==0 || txtCustomerAddress.getText().trim().length()==0){
            flag=false;

        }
        else{
            for(int i=0;i<txtCustomerName.getText().trim().length();i++){
                char c = txtCustomerName.getText().charAt(i);
                if(Character.isDigit(c)){
                    flag=false;
                }
                else{
                    flag=true;
                }
            }
        }

        return flag;
    }
    private void setInitialize(){
        ObservableList<CustomerTM> customers = tblCustomer.getItems();
        btnSaveCustomer.setDisable(true);
        btnDeleteCustomer.setDisable(true);
        txtCustomerId.setDisable(true);
        txtCustomerName.setDisable(true);
        txtCustomerAddress.setDisable(true);

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from customer");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String customerId = resultSet.getString(1);
                String customerName = resultSet.getString(2);
                String customerAddress = resultSet.getString(3);
                customers.add(new CustomerTM(customerId,customerName,customerAddress));
                tblCustomer.refresh();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
    }


}
