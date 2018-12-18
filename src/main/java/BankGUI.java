import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class BankGUI extends JFrame {
    private JPanel MainPanel;
    private JComboBox<String> typeSelector;
    private JTextField desc_name;
    private JLabel type_name;
    private JTextField amount;
    private JTextField date_field;
    private JTable bankTable;
    private JLabel label_date;
    private JButton pay_now;
    private JLabel after_bill_pay;
    private JLabel available_cash;
    private JTable current_bills;
    private JButton addButton;
    private JButton deleteButton;
    private JLabel next_bill_date;
    private JLabel bill_due_name;
    private JButton delete_bill;
    private JLabel type_label;
    private JComboBox<String> expense_type;
    private JButton quitButton;

    private BudgetDB db;

    private String nameDefault = "Description Name:";
    private String dateDefault = "Date:";
    private String dateFieldDefault = "MM-dd-yyy";

    private BankTableModel bankModel;
    private BankTableModel billModel;
    private Vector<String> bankColNames;
    private Vector<String> billColNames;
    private String table;

    BankGUI (BudgetDB db){
        //Create a local instance
        this.db = db;

        setContentPane(MainPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        typeSelector.addItem("Select From the List");
        typeSelector.addItem("Bill");
        typeSelector.addItem("Deposit");
        typeSelector.addItem("Expense");


        expense_type.addItem("Grocery");
        expense_type.addItem("Gas");
        expense_type.addItem("Dining");
        expense_type.addItem("Shopping");
        expense_type.addItem("Entertainment");
        expense_type.addItem("Other");

        getRootPane().setDefaultButton(addButton);

        setDefaults();
        configureTables();
        addListeners();
        setVisible(true);


    }
    private void addListeners(){
        typeSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selection = (String) typeSelector.getSelectedItem();
                switch (selection){
                    case "Select From the List":
                        break;
                    case "Bill":
                        type_name.setText("Bill Name:");
                        amount.setText("Total Due:");
                        label_date.setText("Due Date:");
                        break;
                    case "Deposit":
                        type_name.setText("Deposit Description:");
                        amount.setText("Amount Deposited:");
                        label_date.setText("Deposited On:");
                        date_field.setText(getCurrentDate());
                        break;
                    case "Expense":
                        type_label.setVisible(true);
                        expense_type.setVisible(true);
                        amount.setText("Amount Spent:");
                        label_date.setText("Date of Purchase:");
                        break;
                }

            }
        });


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String selection = typeSelector.getSelectedItem().toString();
               validateData(selection);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String tableName = BudgetDB.TRANSACTION_TABLE;
                deleteSelection(tableName);
            }
        });
        current_bills.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = current_bills.getSelectedRow();
                int column = current_bills.getSelectedColumn();
                Object a = current_bills.getValueAt(row,column);
                current_bills.setValueAt(a, row, column);
                updateTable();
            }
        });

        bankTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = bankTable.getSelectedColumn();
                int row = bankTable.getSelectedRow();
                Object a = bankTable.getValueAt(row, column);
                bankTable.setValueAt(a, row, column);
                updateTable();
            }
        });
        pay_now.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = BudgetDB.BILL_TABLE;
                int row = current_bills.getSelectedRow();
                String name = (String)current_bills.getValueAt(row, 1);
                double amount = (double)current_bills.getValueAt(row, 2);
                Date date = convertDate(getCurrentDate());
                Bill bill = new Bill(name, amount, date);
                String result = db.addTransToDB(bill);

                if (result.equals(BudgetDB.OK)){
                    deleteSelection(tableName);
                    updateTable();
                }else{
                    errorDialog("Error adding expense to Database");
                }

            }
        });
        delete_bill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = BudgetDB.BILL_TABLE;
                deleteSelection(tableName);
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BankGUI.this.dispose();
            }
        });



    }
    public void setDefaults(){
        type_name.setText(nameDefault);
        amount.setText("");
        desc_name.setText("");
        typeSelector.setSelectedIndex(0);
        label_date.setText(dateDefault);
        date_field.setText(dateFieldDefault);
        type_label.setVisible(false);
        expense_type.setVisible(false);
        calculateFunds();
        nextBillDue();
    }

    public void validateData(String selection){
        String name = "";
        double amt = 0.0;
        Date date = (convertDate(getCurrentDate()));
        String expenseType = "";
        try {
            if (desc_name.getText().isEmpty() || desc_name.getText().equals("")) {
                errorDialog("You must enter a name");
            } else{
                name = desc_name.getText();
            }
            if (amount.getText().isEmpty() || amount.getText().equals("")) {
                errorDialog("You must enter an amount");
            } else {
                amt = Double.parseDouble(amount.getText());
            }
            if (date_field.getText().isEmpty() || date_field.getText().equals(dateFieldDefault)) {
                errorDialog("You must enter a date");
            } else {
                date = convertDate(date_field.getText());
            }
            if (selection.equals("Expense") && expense_type.getSelectedIndex() == -1) {
                errorDialog("You must select an expense type");
            } else {
                expenseType = expense_type.getSelectedItem().toString();
            }

            if (selection.equals("Expense")) {
                table = BudgetDB.TRANSACTION_TABLE;
                addExpense(name, amt, date, expenseType);
            }
            if (selection.equals("Deposit")) {
                table = BudgetDB.TRANSACTION_TABLE;
                addDeposit(name, amt, date);
            }
            if (selection.equals("Bill")) {
                table = BudgetDB.BILL_TABLE;
                addBill(name, amt, date);
            } else {
                errorDialog("You need to select an Option to Add from the drop down list");
            }
        }catch (NumberFormatException n){
            System.out.println("Invalid Data Entry, try again");
        }
    }


    public void addExpense(String name, double amt, Date datePaid, String expenseType){

        Expense expense = new Expense(name, amt, datePaid, expenseType);
        String result = db.addTransToDB(expense);

        if (result.equals(BudgetDB.OK)){
            updateTable();
        }else{
            errorDialog("Error adding expense to Database");
        }
    }
    public void addDeposit(String name, double amt, Date dateAdded){
        Credit deposit = new Credit(name, amt, dateAdded);

        String result = db.addTransToDB(deposit);
        if (result.equals(BudgetDB.OK)){
            updateTable();
        }else{
            errorDialog("Error adding deposit to Database");
        }
    }
    public void addBill(String name, double amt, Date due_date){

        Bill bill = new Bill (name, amt, due_date);

        String result = db.BillToDB(bill);

        if (result.equals(BudgetDB.OK)){
            updateTable();
        }else{
            errorDialog("Error adding Bill to Database");
        }

    }
    public void deleteSelection(String tableName) {
        int currentRow;
        if (tableName.equals(BudgetDB.BILL_TABLE)) {
            currentRow = current_bills.getSelectedRow();
            if (currentRow == -1) {
                errorDialog("Error: you have not selected a row");

            }else{
                db.deleteFromDB(tableName, currentRow);
            }

        }
        if (tableName.equals(BudgetDB.TRANSACTION_TABLE)){
            currentRow = bankTable.getSelectedRow();
            if (currentRow == -1){
                errorDialog("You haven't selected a row");
            }else{
                db.deleteFromDB(tableName,currentRow);
            }
        }

        updateTable();
    }


    public void calculateFunds(){
        String totalCash = String.valueOf(db.bankTotal());

        available_cash.setText(totalCash);

        String totalBills = String.valueOf(db.billTotal());

        after_bill_pay.setText(totalBills);

    }
    public void nextBillDue() {
        ArrayList<Bill> nextBill = new ArrayList<>();
        nextBill = db.earliestDate();


        for(Bill bill: nextBill){
            bill_due_name.setText(bill.getName() + bill.getAmount());
            next_bill_date.setText(bill.getDate().toString());

        }

    }

    private String getCurrentDate(){
        java.util.Date date = new java.util.Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = dateFormat.format(date);

        return currentDate;


    }
    private java.sql.Date convertDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            java.util.Date parsedDate = dateFormat.parse(date);
            java.sql.Date dbDate = new java.sql.Date(parsedDate.getTime());

            return dbDate;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private void configureTables(){

        current_bills.setGridColor(Color.GRAY);
        bankTable.setGridColor(Color.GRAY);

        billColNames = db.getColumnNames(BudgetDB.BILL_TABLE);
        Vector<Bank> billData = db.getAll(BudgetDB.BILL_TABLE);
        bankColNames = db.getColumnNames(BudgetDB.TRANSACTION_TABLE);
        Vector<Bank> bankData = db.getAll(BudgetDB.TRANSACTION_TABLE);

        bankModel = new BankTableModel(bankData,bankColNames);
        billModel = new BankTableModel(billData, billColNames);

        bankTable.setModel(bankModel);
        current_bills.setModel(billModel);

        bankModel.fireTableDataChanged();
        billModel.fireTableDataChanged();


    }
    public void updateTable(){
        Vector <Bank> bankData = db.getAll(BudgetDB.TRANSACTION_TABLE);
        Vector <Bank> billData = db.getAll(BudgetDB.BILL_TABLE);

        bankModel.resetData(bankData, bankColNames);
        billModel.resetData(billData, billColNames);

        setDefaults();


    }
    public void errorDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }


}
