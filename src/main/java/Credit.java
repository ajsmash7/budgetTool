import java.util.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class Credit extends Bank {

    protected final Date PaidOn_Date;

    public Credit(int addID, String addName, double addAmt, Date depositDate){
        super(addID, addName, addAmt);
        this.PaidOn_Date = depositDate;
    }

    public Date getPaidOn_Date() {
        return PaidOn_Date;
    }

    public void setPaidOn_Date(Date paidOn_Date) {
        PaidOn_Date = paidOn_Date;
    }
}
