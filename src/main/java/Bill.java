import java.util.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class Bill extends Bank {

    protected Date dueDate;

    public Bill(int addID, String addName, double addAmt, Date dateDue){
        super(addID, addName, addAmt);
        this.dueDate = dateDue;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
