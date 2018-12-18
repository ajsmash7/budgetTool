import java.util.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 * This is the Object subclass called Bill. a bill is not yet an expense, because it hasn't been paid yet.
 * When it is paid, it calls its overridden interface method "getExpenseType", and is assigned as "Bill Payment", and
 * becomes a new Expense (ActionListener upon BillPayButton push)
 * Until it is paid, it is a Bill, with a due date. It is an extension of Bank, and implements the Budget interface so
 * that it can be treated as a Bill and a bank object.
 */
public class Bill extends Bank implements Budget{

    //Constructor
    public Bill(String addName, double addAmt, Date dateDue){
        super(addName, addAmt, dateDue);

    }
    //Constructor when read back from the database
    public Bill(int addID, String addName, double addAmt, Date dateDue){
        super(addID, addName, addAmt, dateDue);

    }
    //empty constructor
    public Bill() {
        super();
    }
    //custom interface method that it calls when it is paid, to go from a bill to a paid expense.
    public String getExpenseType(){
        return "Bill Payment";
    }

    //Display when next bill is due
    @Override
    public String toString() {
        return ("NEXT BILL DUE ON: " + this.date);
    }
}
