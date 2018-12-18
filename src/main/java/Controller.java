/**
 * Created by Ashley Johnson on 12/10/2018.
 */
public class Controller {

    protected BankGUI gui;
    protected BudgetDB db;

    public static void main(String[] args) {new Controller().startApp(); }

    //start the application by declaring a new instance of the database, and passing it to the gui
    private void startApp(){
        db = new BudgetDB();
        gui = new BankGUI(db);
    }
}
