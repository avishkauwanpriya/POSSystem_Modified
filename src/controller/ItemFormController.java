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
import model.ItemTM;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemFormController {
    public AnchorPane itemForm;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemDescription;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public TableView <ItemTM>tblItem;
    public JFXButton btnSaveItem;
    public JFXButton btnDeleteItem;
    public JFXButton btnAddNewItem;

    public void initialize(){
        setInitializer();
        tblItem.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                if(tblItem.getSelectionModel().getSelectedIndex()==-1){
                    txtItemCode.clear();
                    txtItemDescription.clear();
                    txtQtyOnHand.clear();
                    txtUnitPrice.clear();
                    btnSaveItem.setDisable(true);
                    btnDeleteItem.setDisable(true);

                }
                else {
                    txtItemCode.setDisable(false);
                    txtItemDescription.setDisable(false);
                    txtUnitPrice.setDisable(false);
                    txtQtyOnHand.setDisable(false);
                    btnSaveItem.setDisable(false);
                    btnSaveItem.setText("Update Item");
                    btnDeleteItem.setDisable(false);
                    ItemTM selectedItem = tblItem.getSelectionModel().getSelectedItem();
                    txtItemCode.setText(selectedItem.getItemCode());
                    txtItemDescription.setText(selectedItem.getDescription());
                    txtUnitPrice.setText(selectedItem.getUnitPrice() + "");
                    txtQtyOnHand.setText(selectedItem.getQtyOnHand() + "");
                }
            }
        });

    }

    public void btnSaveItem_OnAction(ActionEvent actionEvent) {
        ObservableList<ItemTM> items = tblItem.getItems();
        if(txtItemDescription.getText().trim().length()==0||txtUnitPrice.getText().trim().length()==0||txtQtyOnHand.getText().trim().length()==0){
            new Alert(Alert.AlertType.ERROR,"Description,QtyOnHand,UnitPrice Cannot Be Empty", ButtonType.OK).show();
            return;
        }
        if(validateUnitPrice(txtUnitPrice.getText())==false || validateQtyOnHand(txtQtyOnHand.getText())==false){
            new Alert(Alert.AlertType.ERROR,"Enter Valid UnitPrice,QtyOnHand", ButtonType.OK).show();
            return;

        }
        if(btnSaveItem.getText().equals("Save Item")){
            try {
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");
                preparedStatement.setObject(1,txtItemCode.getText());
                preparedStatement.setObject(2,txtItemDescription.getText());
                preparedStatement.setObject(3,txtUnitPrice.getText());
                preparedStatement.setObject(4,txtQtyOnHand.getText());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            items.add(new ItemTM(txtItemCode.getText(),txtItemDescription.getText(),Double.parseDouble(txtUnitPrice.getText()),Double.parseDouble(txtQtyOnHand.getText())));
            tblItem.refresh();
            btnSaveItem.setDisable(true);


        }
        else {
            Connection connection = null;
            try {
                connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE item SET itemDescription=(?),unitPrice=(?),QtyOnHand=(?) WHERE itemCode=(?)");

                preparedStatement.setObject(1,txtItemDescription.getText());
                preparedStatement.setObject(2,txtUnitPrice.getText());
                preparedStatement.setObject(3,txtQtyOnHand.getText());
                preparedStatement.setObject(4,tblItem.getSelectionModel().getSelectedItem().getItemCode());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            tblItem.getSelectionModel().getSelectedItem().setDescription(txtItemDescription.getText());
            tblItem.getSelectionModel().getSelectedItem().setQtyOnHand(Double.parseDouble(txtQtyOnHand.getText()));
            tblItem.getSelectionModel().getSelectedItem().setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
            tblItem.refresh();
            tblItem.getSelectionModel().clearSelection();



        }

        


    }

    public void btnDeleteItem_OnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM item WHERE itemCode=(?)");
            preparedStatement.setObject(1,tblItem.getSelectionModel().getSelectedItem().getItemCode());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        tblItem.getItems().remove(tblItem.getSelectionModel().getSelectedItem());
        tblItem.refresh();
    }

    
    
    
    public void btnAddNewItem_OnAction(ActionEvent actionEvent) {
        btnSaveItem.setText("Save Item");
        setItemCode();



    }

    public void imgHome_OnAction(MouseEvent mouseEvent) {

        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene newScene=new Scene(root);
            Stage mainStage = (Stage) itemForm.getScene().getWindow();
            mainStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setInitializer(){
        ObservableList<ItemTM> items = tblItem.getItems();
        txtItemCode.setDisable(true);
        txtItemDescription.setDisable(true);
        txtUnitPrice.setDisable(true);
        txtQtyOnHand.setDisable(true);
        btnSaveItem.setDisable(true);
        btnDeleteItem.setDisable(true);
        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String itemCode = resultSet.getString(1);
                String description = resultSet.getString(2);
                String unitPrice = resultSet.getString(3);
                String qtyOnHand = resultSet.getString(4);
                items.add(new ItemTM(itemCode,description,Double.parseDouble(unitPrice),Double.parseDouble(qtyOnHand)));
                tblItem.refresh();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
    private void setItemCode() {
        txtItemDescription.requestFocus();
        txtItemDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        txtItemCode.setDisable(false);
        txtItemDescription.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtQtyOnHand.setDisable(false);
        btnSaveItem.setDisable(false);
        ArrayList<ItemTM> items = new ArrayList<>();
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String itemCode = resultSet.getString(1);
                String description = resultSet.getString(2);
                double unitPrice = resultSet.getDouble(3);
                double qtyOnHand = resultSet.getDouble(4);
                items.add(new ItemTM(itemCode, description, unitPrice, qtyOnHand));

            }
            if(items.size()==0){
                txtItemCode.setText("I001");

            }
            else{
                ItemTM lastItemTM = items.get(items.size() - 1);
                String lastItemCode = lastItemTM.getItemCode().substring(1,4);
                int lastItemCodeNumber = Integer.parseInt(lastItemCode);

                if(lastItemCodeNumber<9){
                    txtItemCode.setText("I00"+(lastItemCodeNumber+1));
                }
                else if(lastItemCodeNumber<99){
                    txtItemCode.setText("I0"+(lastItemCodeNumber+1));
                }
                else{
                    txtItemCode.setText("I"+(lastItemCodeNumber+1));
                }
            }






        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

        private boolean validateUnitPrice(String unitPrice){
        boolean flag=false;
        boolean is=false;
        int dotCount=0;
            for(int i=0;i<txtUnitPrice.getText().trim().length();i++){
                if(!Character.isDigit(txtUnitPrice.getText().charAt(i))){
                    if(txtUnitPrice.getText().charAt(i)=='.'){
                        dotCount++;
                    }
                    else{
                        is=false;
                        break;
                    }

                }
                else{
                    is=true;


                }


        }
            if(is==true && dotCount<=1){
                flag=true;
            }
            return flag;


        }
    private boolean validateQtyOnHand(String qtyOnHand){
        boolean flag=false;
        boolean is=false;
        int dotCount=0;
        for(int i=0;i<txtQtyOnHand.getText().trim().length();i++){
            if(!Character.isDigit(txtQtyOnHand.getText().charAt(i))){
                if(txtQtyOnHand.getText().charAt(i)=='.'){
                    dotCount++;
                }
                else{
                    is=false;
                    break;
                }

            }
            else{
                is=true;


            }


        }
        if(is==true && dotCount<=1){
            flag=true;
        }
        return flag;


    }





    }




