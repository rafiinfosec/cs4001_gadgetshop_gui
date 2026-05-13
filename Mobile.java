
public class Mobile extends Gadget {

    private int callingCredit;


    public Mobile(String model, double price, int weight, String size, int callingCredit) {
        super(model, price, weight, size);
        this.callingCredit = callingCredit;
    }

    
    public int getCallingCredit() {
        return callingCredit;
    }


    public void addCredit(int amount) {
        if (amount > 0) {
            callingCredit += amount;
            System.out.println("Credit added. New balance: " + callingCredit + " minutes.");
        } else {
            System.out.println("Please enter a positive amount of credit.");
        }
    }

 
    public void makeCall(String phoneNumber, int duration) {
        if (callingCredit >= duration) {
            System.out.println("Calling " + phoneNumber + " for " + duration + " minute(s).");
            callingCredit -= duration;
            System.out.println("Remaining credit: " + callingCredit + " minutes.");
        } else {
            System.out.println("Insufficient credit to make this call.");
        }
    }

    
    @Override
    public void display() {
        super.display();
        System.out.println("Calling Credit: " + callingCredit + " minutes");
    }
}
