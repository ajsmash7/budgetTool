/**
 * Created by Ashley Johnson on 12/10/2018.
 */
public class Bank {

    protected int ID;
    protected String name;
    protected double amount;

    public Bank(int addID, String addName, double addAmt){
        this.ID = addID;
        this.name = addName;
        this.amount = addAmt;
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
}
