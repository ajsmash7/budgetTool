/**
 * Created by Ashley Johnson on 12/10/2018.
 */

import java.sql.*;
import java.sql.Date;
import java.util.*;

 class BudgetDB {

    private static final String db_url = "jdbc:sqlite:C:\\Users\\Ashley Johnson\\IdeaProjects\\budgetTool\\src\\main\\java\\BudgetDatabase";
     static String BILL_TABLE = "billsDue";
     static String TRANSACTION_TABLE = "transactions";

    private static final String ID_COLUMN = "ID";
    private static final String BILL_NAME_COLUMN = "Bill_Name";
    private static final String BILL_AMT_COLUMN = "Bill_Amount";
    private static final String DUE_DATE_COLUMN = "Due_Date";
    private static final String TRANS_DESC_COLUMN = "Description";
    private static final String TRANS_AMT_COLUMN = "Amount";
    private static final String TRANS_TYPE_COLUMN = "Type";
    private static final String TRANS_PAID_COLUMN = "PaidOn_Date";
    static final String OK = "OK";


    //Create Table SQL strings
    private static final String CREATE_BILL_TABLE = "CREATE TABLE IF NOT EXISTS billsDue(ID INTEGER PRIMARY KEY , Bill_Name TEXT, Bill_Amount DECIMAL(10,2), Due_Date DATE)";
    private static final String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS transactions(ID INTEGER PRIMARY KEY, Description TEXT, Amount DECIMAL(10,2), Type TEXT, PaidOn_Date DATE)";

    //SQL Strings for Statements
    private static final String GET_ALL_TRANSACTIONS = "SELECT * FROM transactions";
    private static final String GET_ALL_BILLS = "SELECT * FROM billsDue";
    private static final String ADD_EXPENSE = "INSERT INTO transactions (?,?,?,?) VALUES (?,?,?,?)";
    private static final String ADD_BILL = "INSERT INTO billsDue (?,?,?) VALUES (?,?,?)";


    BudgetDB(){createTables();}

    private void createTables(){
        try (Connection conn = DriverManager.getConnection(db_url);
            Statement statement = conn.createStatement()){

            //Create tables
            statement.executeUpdate(CREATE_BILL_TABLE);
            statement.executeUpdate(CREATE_TRANSACTION_TABLE);


        }catch (SQLException e){
            System.out.println("Could not create tables");
            throw new RuntimeException(e);
        }
    }

    Vector getColumnNames(String table){
        Vector <String> columnNames = new Vector<>();

        switch (table){
            case "billsDue":
                columnNames.add("ID");
                columnNames.add("Bill_Name");
                columnNames.add("Bill_Amount");
                columnNames.add("Due_Date");
                break;
            case "transactions":
                columnNames.add("ID");
                columnNames.add("Description");
                columnNames.add("Amount");
                columnNames.add("Type");
                columnNames.add("PaidOn_Date");
                break;
            default:
                System.out.println("I'm throwing an error");
        }

        return columnNames;

    }

    Vector<Bank> getAll (String table){
        //request connection to database and statement connection
        try (Connection conn = DriverManager.getConnection(db_url);
        Statement statement = conn.createStatement()){
            //Declare a vector of vector objects. each vector object contains the data of one table row.
            Vector<Bank> allResults = new Vector<>();

            //Use a switch statement to pull the data from the table option passed to the method.
            //each case will query the corresponding table, and loop through all the rows in the table
            //for each row iteration, declare a new empty vector called row, and add each cell value to it
            //then add the loop local row vector to the method local allResults vector.
            //at the completion of the loop iterations, break out of the switch statement and return allResults vector
            switch (table){
                case "billsDue":
                    ResultSet bills = statement.executeQuery(GET_ALL_BILLS);
                    while (bills.next()){
                        int ID = bills.getInt(ID_COLUMN);
                        String name = bills.getString(BILL_NAME_COLUMN);
                        double amt = bills.getDouble(BILL_AMT_COLUMN);
                        Date date = bills.getDate(DUE_DATE_COLUMN);

                        Bill row = new Bill(ID, name, amt, date);

                        allResults.add(row);
                    }
                    break;
                case "transactions":
                    ResultSet rs = statement.executeQuery(GET_ALL_TRANSACTIONS);
                    while (rs.next()){
                        int ID = rs.getInt(ID_COLUMN);
                        String name = rs.getString(TRANS_DESC_COLUMN);
                        double amt = rs.getDouble(TRANS_AMT_COLUMN);
                        String type = rs.getString (TRANS_TYPE_COLUMN);
                        Date date = rs.getDate(TRANS_PAID_COLUMN);

                        Expense row = new Expense(ID, name, amt, date,type);

                        allResults.add(row);
                    }
                    break;
                default:
                    System.out.println("I'm throwing an error");
            }

            //Return the vector of vectors containing the contents of the database table requested.
            return allResults;

            }catch (SQLException e){
            System.out.println("Error fetching all records");
            throw new RuntimeException(e);
            }
    }

    public String addTransToDB(Bank transaction){
        Class <? extends Bank> c = transaction.getClass();
        if (c == Bill.class || c == Expense.class){
            double negAmt = transaction.getNegAmt(transaction.getAmount());
            transaction.setNegAmt(negAmt);
        }
        try (Connection conn = DriverManager.getConnection(db_url);
             PreparedStatement ps = conn.prepareStatement(ADD_EXPENSE)){

            ps.setString(1, TRANS_DESC_COLUMN);
            ps.setString(2, TRANS_AMT_COLUMN);
            ps.setString(3, TRANS_PAID_COLUMN);
            ps.setString(4, TRANS_TYPE_COLUMN);
            ps.setString(5, transaction.getName());
            ps.setDouble(6, transaction.getAmount());
            ps.setDate(7, transaction.getDate());
            ps.setString(8, transaction.getExpenseType());

            ps.executeUpdate();

            return OK;

        }catch (SQLException e){
            System.out.println("Could not add expense");
            throw new RuntimeException(e);
        }

    }

    public String BillToDB(Bill bill){
        try (Connection connection = DriverManager.getConnection(db_url);
             PreparedStatement p = connection.prepareStatement(ADD_BILL)){


            p.setString(1, BILL_NAME_COLUMN);
            p.setString(2, BILL_AMT_COLUMN);
            p.setString(3, DUE_DATE_COLUMN);
            p.setString(4, bill.getName());
            p.setDouble(5, bill.getAmount());
            p.setDate(6, bill.getDate());

            p.executeUpdate();

            return OK;

        }catch (SQLException sqle){
            System.out.println("Could not create tables");
            throw new RuntimeException(sqle);
        }

    }

    public String deleteFromDB(String table, int ID) {
        String deleteSQL = "DELETE FROM ? WHERE ?=?";
        try (Connection conn = DriverManager.getConnection(db_url);
             PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

            ps.setString(1, table);
            ps.setString(2, ID_COLUMN);
            ps.setInt(3, ID);

            ps.executeUpdate();

            return OK;

        } catch (SQLException e) {
            System.out.println("Could not delete from database");
            throw new RuntimeException(e);
        }
    }

        public static String updateDB(Bank transaction, int ID){

        Class<? extends Bank> c = transaction.getClass();
        String updateSQL = " ";

        if (c == Bill.class) {
             updateSQL = "UPDATE billsDue SET ?=? SET ?=? SET ?=? WHERE ?=?";
        }if (c == Expense.class || c == Credit.class) {
                updateSQL = "UPDATE transactions SET ?=? SET ?=? SET ?=? SET ?=? WHERE ?=?";
            }

            try (Connection conn = DriverManager.getConnection(db_url)) {
                try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {


                    if (c == Bill.class) {

                        ps.setString(1, BILL_NAME_COLUMN);
                        ps.setString(2, transaction.getName());
                        ps.setString(3, BILL_AMT_COLUMN);
                        ps.setDouble(4, transaction.getAmount());
                        ps.setString(5, DUE_DATE_COLUMN);
                        ps.setDate(6, transaction.getDate());
                        ps.setString(7, ID_COLUMN);
                        ps.setInt(8, ID);

                        ps.executeUpdate();

                        return OK;
                    }
                    if (c == Expense.class || c == Credit.class) {

                        ps.setString(1, TRANS_DESC_COLUMN);
                        ps.setString(2, transaction.getName());
                        ps.setString(3, TRANS_AMT_COLUMN);
                        ps.setDouble(4, transaction.getAmount());
                        ps.setString(5, TRANS_PAID_COLUMN);
                        ps.setDate(6, transaction.getDate());
                        ps.setString(7, TRANS_TYPE_COLUMN);
                        ps.setString(8, transaction.getExpenseType());
                        ps.setString(9, ID_COLUMN);
                        ps.setInt(10, ID);

                        ps.executeUpdate();

                        return OK;

                    }else{
                        return "Error";
                    }
                }
            } catch (SQLException e) {
                return ("Could not update the row");
            }
        }

    public double bankTotal() {
        double columnTotal = 0;
        String creditSQL = "SELECT * FROM transactions";
        try (Connection conn = DriverManager.getConnection(db_url);
             Statement ps = conn.createStatement()) {


            ResultSet rs = ps.executeQuery(creditSQL);
            while (rs.next()) {
                double amt = rs.getDouble(TRANS_AMT_COLUMN);
                columnTotal = columnTotal + amt;
            }




        } catch (SQLException e) {
            System.out.println("ERROR LOADING");
            throw new RuntimeException(e);
        }
        return columnTotal;
    }

    public double billTotal(){
        double total = 0;
        String billSQL = "SELECT * FROM billsDue";
        try (Connection conn = DriverManager.getConnection(db_url);
             Statement ps = conn.createStatement()) {

            ResultSet rs = ps.executeQuery(billSQL);
            while (rs.next()) {
                double amt = rs.getDouble(BILL_AMT_COLUMN);
                total = total + amt;
            }

            return total;

        } catch (SQLException e) {
            System.out.println("Could not load bill data");
            throw new RuntimeException(e);
        }


    }

    public ArrayList<Bill> earliestDate(){
        ArrayList<Bill> nextBill = new ArrayList<>();
        String findDateSQL = "SELECT * FROM billsDue ORDER BY ? ";
        try (Connection conn = DriverManager.getConnection(db_url);
             PreparedStatement ps = conn.prepareStatement(findDateSQL)) {

            ps.setString(1, DUE_DATE_COLUMN);

            ResultSet rs = ps.executeQuery();
            int ID = rs.getInt(ID_COLUMN);
            String name = rs.getString(BILL_NAME_COLUMN);
            Date date = rs.getDate(DUE_DATE_COLUMN);
            double amt = rs.getDouble(BILL_AMT_COLUMN);

            Bill bill = new Bill(ID, name, amt, date);

            nextBill.add(bill);

            return nextBill;



        } catch (SQLException e) {
            System.out.println("Could not pull data from database");
            throw new RuntimeException(e);
        }

    }







}
