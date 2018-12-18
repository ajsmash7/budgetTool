/**
 * Created by Ashley Johnson on 12/10/2018.
 *
 * My giant database File! It would have been longer had I not figured out how to use class wildcards.
 * This program would work beautifully.... if java.sql.Date weren't so difficult to parse. That is what is holding
 * this program up from executing. I can't get the dates to parse.
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

 class BudgetDB {


    //Database Link
    private static final String db_url = "jdbc:sqlite:C:\\Users\\Ashley Johnson\\IdeaProjects\\budgetTool\\src\\main\\java\\BudgetDatabase";

    //database table name constants
    static String BILL_TABLE = "billsDue";
     static String TRANSACTION_TABLE = "transactions";

     //Column name constants
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
    private static final String CREATE_BILL_TABLE = "CREATE TABLE IF NOT EXISTS billsDue(ID INTEGER PRIMARY KEY , Bill_Name TEXT, Bill_Amount DECIMAL(10,2), Due_Date TEXT)";
    private static final String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS transactions(ID INTEGER PRIMARY KEY, Description TEXT, Amount DECIMAL(10,2), Type TEXT, PaidOn_Date TEXT)";

    //SQL Strings for Statements
    private static final String GET_ALL_TRANSACTIONS = "SELECT * FROM transactions";
    private static final String GET_ALL_BILLS = "SELECT * FROM billsDue";
    private static final String ADD_EXPENSE = "INSERT INTO transactions (?,?,?,?) VALUES (?,?,?,?)";
    private static final String ADD_BILL = "INSERT INTO billsDue (?,?,?) VALUES (?,?,?)";


    BudgetDB(){createTables();} //call the constructor to create the instance of the database.

    //Call method to create tables. Use try-catch with resources to establish the connection with the database.
     //Since we are executing two tables, it is best to use a Statement statement
    private void createTables(){
        try (Connection conn = DriverManager.getConnection(db_url);
            Statement statement = conn.createStatement()){

            //Create tables
            statement.executeUpdate(CREATE_BILL_TABLE);
            statement.executeUpdate(CREATE_TRANSACTION_TABLE);


        }catch (SQLException e){
            System.out.println("Could not create tables");
            e.getStackTrace();
            throw new RuntimeException(e);
        }//always throw a new Runtime to see what the exact call was.
    }

    //create the Vector of column names, based on the table name it receives
    Vector getColumnNames(String table){
        //Declare a new vector of strings
        Vector <String> columnNames = new Vector<>();

        //use a switch statement to assign the column names for each table. break out of switch after assignments
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
                System.out.println("I'm throwing an error, table name incorrect"); //throw an error if table name doesn't match
        }

        return columnNames; //return vector

    }
    //call method to fetch all Bank objects from the table requested.
    Vector<Bank> getAll (String table){

        //request connection to database and statement connection, since we want all columns from more than one table.
        try (Connection conn = DriverManager.getConnection(db_url);
        Statement statement = conn.createStatement()){

            //Declare a vector of vector objects. each vector object contains the data of one table row.
            Vector<Bank> allResults = new Vector<>();

            //Use a switch statement to pull the data from the table option passed to the method.
            //each case will query the corresponding table, and loop through all the rows in the table
            //for each row iteration, declare and assign a corresponding Bank subclass object
            //then add the loop local object to the method local allResults vector.
            //at the completion of the loop iterations, break out of the switch statement and return allResults vector
            //to the GUI Table Model
            switch (table){
                case "billsDue":
                    ResultSet bills = statement.executeQuery(GET_ALL_BILLS);
                    while (bills.next()){
                        int ID = bills.getInt(ID_COLUMN);
                        String name = bills.getString(BILL_NAME_COLUMN);
                        double amt = bills.getDouble(BILL_AMT_COLUMN);

                        java.sql.Date date = bills.getDate(DUE_DATE_COLUMN);

                        java.util.Date addDate= convertSQLDate(date);

                        Bill row = new Bill(ID, name, amt, addDate);

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
                        java.sql.Date date = rs.getDate(TRANS_PAID_COLUMN);

                        java.util.Date addDate = convertSQLDate(date);

                        Expense row = new Expense(ID, name, amt,addDate,type);

                        allResults.add(row);
                    }
                    break;
                default:
                    System.out.println("I'm throwing an error");
            }

            //Return the vector of Bank objects containing the contents of the database table requested.
            return allResults;

            }catch (SQLException e){
            System.out.println("Error fetching all records");
            e.getStackTrace();
            throw new RuntimeException(e);
            }
    }
    /*
    DATABASE PREPARE STATEMENT METHODS FOR CRUD OPERATIONS

    They accept a generic Bank Object called transaction that uses a class wildcard to dynamically update database
    values based on class assignment.

    I'M CURRENTLY UNABLE TO GET THE DATES TO PARSE CORRECTLY
     */
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
            ps.setDate(7, new java.sql.Date(transaction.getDate().getTime())); //attempting to parse into sqlDate
            ps.setString(8, transaction.getExpenseType());

            ps.executeUpdate();

            return OK; //If successful return ok

        }catch (SQLException e){
            System.out.println("Could not add expense");
            e.getStackTrace();
            throw new RuntimeException(e);
        }

    }
    //add bill to database
    public String BillToDB(Bill bill){
        try (Connection connection = DriverManager.getConnection(db_url);
             PreparedStatement p = connection.prepareStatement(ADD_BILL)){

            //add name, amt, and date from Bill object to bill date
            p.setString(1, BILL_NAME_COLUMN);
            p.setString(2, BILL_AMT_COLUMN);
            p.setString(3, DUE_DATE_COLUMN);
            p.setString(4, bill.getName());
            p.setDouble(5, bill.getAmount());
            p.setDate(6, new java.sql.Date(bill.getDate().getTime()));

            p.executeUpdate();

            return OK;

        }catch (SQLException sqle){
            System.out.println("Could not add to Bills");
            sqle.getStackTrace();
            throw new RuntimeException(sqle);
        }

    }

    //Delete from the passed table name, where ID equals the selected row in the table. variable arguments from BankGUI
     //deleteSeletion and pay_billButton action event
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
            e.getStackTrace();
            throw new RuntimeException(e);
        }
    }

        //this method is called by the setValueAt method in the BanktableModel class, from the table listener event.
        // When a cell is updated, the table listener fires, and calls the setValueAt method in the model.
        // which calls this method to update the row in which the changes were made. The table is selected based on
        //the class of the Bank object. Nill updates the bill table, expense and credits update the transaction table.
        public static String updateDB(Bank transaction, int ID){

        Class<? extends Bank> c = transaction.getClass();
        String updateSQL = " ";
        //choose the appropriate update statement, based on the class
        if (c == Bill.class) {
             updateSQL = "UPDATE billsDue SET ?=? SET ?=? SET ?=? WHERE ?=?";
        }if (c == Expense.class || c == Credit.class) {
                updateSQL = "UPDATE transactions SET ?=? SET ?=? SET ?=? SET ?=? WHERE ?=?";
            }
            //open the database connection in a try with resources
            try (Connection conn = DriverManager.getConnection(db_url)) {
                try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {

                    //wrap the preparedStatement setters in if statements to assign based on class
                    if (c == Bill.class) {

                        ps.setString(1, BILL_NAME_COLUMN);
                        ps.setString(2, transaction.getName());
                        ps.setString(3, BILL_AMT_COLUMN);
                        ps.setDouble(4, transaction.getAmount());
                        ps.setString(5, DUE_DATE_COLUMN);
                        ps.setDate(6, new java.sql.Date(transaction.getDate().getTime()));
                        ps.setString(7, ID_COLUMN);
                        ps.setInt(8, ID);

                        ps.executeUpdate();

                        return OK; //return ok if successful
                    }
                    if (c == Expense.class || c == Credit.class) {

                        ps.setString(1, TRANS_DESC_COLUMN);
                        ps.setString(2, transaction.getName());
                        ps.setString(3, TRANS_AMT_COLUMN);
                        ps.setDouble(4, transaction.getAmount());
                        ps.setString(5, TRANS_PAID_COLUMN);
                        ps.setDate(6, new java.sql.Date(transaction.getDate().getTime()));
                        ps.setString(7, TRANS_TYPE_COLUMN);
                        ps.setString(8, transaction.getExpenseType());
                        ps.setString(9, ID_COLUMN);
                        ps.setInt(10, ID);

                        ps.executeUpdate();

                        return OK; //return ok if successful

                    }else{
                        return "Class Error"; //if the class was not properly assigned, through an error
                    }
                }
            } catch (SQLException e) {
                e.getStackTrace();
                return ("Could not update the row"); //throw an error if unsuccessful
            }
        }
    //a calculation method for the labe displaying the total of the transaction table.
     //when a new Expense is added to the transaction table, it calls a method called NegAmt() to turn the user
     //entry value into a negative value before applying to the database. This way, it can simply total the rows
     // while still user friendly.
    public double bankTotal() {
        double columnTotal = 0;
        String creditSQL = "SELECT * FROM transactions";
        try (Connection conn = DriverManager.getConnection(db_url);
             Statement ps = conn.createStatement()) {

            //while there are rows, add next value in the amount column to total
            ResultSet rs = ps.executeQuery(creditSQL);
            while (rs.next()) {
                double amt = rs.getDouble(TRANS_AMT_COLUMN);
                columnTotal = columnTotal + amt;
            }


            return columnTotal;//return the total amount

        } catch (SQLException e) {
            System.out.println("ERROR LOADING");
            e.getStackTrace();
            throw new RuntimeException(e);
        }

    }
    //add up all the bills to be able to deduct the total bills due from the available cash and display it in the
     //after bills paid column of available funds
    public double billTotal(){
        double total = 0;
        String billSQL = "SELECT * FROM billsDue";
        try (Connection conn = DriverManager.getConnection(db_url);
             Statement ps = conn.createStatement()) {
            //while there are still rows, add the value in the amount column to total.
            ResultSet rs = ps.executeQuery(billSQL);
            while (rs.next()) {
                double amt = rs.getDouble(BILL_AMT_COLUMN);
                total = total + amt;
            }

            return total;

        } catch (SQLException e) {
            System.out.println("Could not load bill data");
            e.getStackTrace();
            throw new RuntimeException(e);
        }


    }

    //Query the data in the current bills database, and order them by date. Take the values of the first row and store
     //them in an array list, return the array list to the gui to retrieve the data for display in the current_bill_due labels.

    public ArrayList<Bill> earliestDate(){
        ArrayList<Bill> nextBill = new ArrayList<>();
        String findDateSQL = "SELECT * FROM billsDue ORDER BY ? ";
        try (Connection conn = DriverManager.getConnection(db_url);
             PreparedStatement ps = conn.prepareStatement(findDateSQL)) {

            ps.setString(1, DUE_DATE_COLUMN);

            ResultSet rs = ps.executeQuery();
            int ID = rs.getInt(ID_COLUMN);
            String name = rs.getString(BILL_NAME_COLUMN);
            java.sql.Date date = rs.getDate(DUE_DATE_COLUMN);
            double amt = rs.getDouble(BILL_AMT_COLUMN);

            java.util.Date addDate = convertSQLDate(date);



            Bill bill = new Bill(ID, name, amt, addDate);

            nextBill.add(bill);

            return nextBill;



        } catch (SQLException e) {
            System.out.println("Could not pull data from database");
            e.getStackTrace();
            throw new RuntimeException(e);
        }

    }

    //custom method to convert date from java.util.Date to java.sql.Date. Doesn't work or vis a versa.

     public static java.util.Date convertSQLDate(
             java.sql.Date sqlDate) {
         java.util.Date javaDate = null;
         if (sqlDate != null) {
             javaDate = new Date(sqlDate.getTime());
         }
         return javaDate;
     }





}
