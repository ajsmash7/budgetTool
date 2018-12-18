import java.sql.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class Bill extends Bank implements Budget{

    public Bill(String addName, double addAmt, Date dateDue){
        super(addName, addAmt, dateDue);

    }

    public Bill(int addID, String addName, double addAmt, Date dateDue){
        super(addID, addName, addAmt, dateDue);

    }

    public Bill() {
        super();
    }

    public String getExpenseType(){
        return "Bill Payment";
    }

    @Override
    public String toString() {
        return ("NEXT BILL DUE ON: " + this.date);
    }
}
