import java.sql.Date;

/**
 * Created by Ashley Johnson on 12/10/2018.
 */
public abstract class Bank implements Budget{
    int ID;
    String name;
    double amount;
    Date date;

    public Bank (){}

    public Bank(String addName, double addAmt, Date dateAdd){
        this.name = addName;
        this.amount = addAmt;
        this.date = dateAdd;
    }

    public Bank(int addID, String addName, double addAmt, Date dateAdd){
        this.ID = addID;
        this.name = addName;
        this.amount = addAmt;
        this.date = dateAdd;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getNegAmt(double amt){return 0-amt;}

    public void setNegAmt (double negAmt){this.amount = negAmt;}
}
