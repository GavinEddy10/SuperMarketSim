import java.sql.Array;
import java.util.ArrayList;

public class Cashier extends Thread{
    private int id;
    private static int nextID = 1;
    boolean openForCustomer;
    long checkOutTime;
    private final long minCheckoutTime = 3000; //min time ms cashs checks out
    private final long maxCheckoutTime = 5000; //max time ms cashier checks out
    int numOfCustomersCheckedOut;
    public ArrayList<Customer> customersSeen;
    public ArrayList<Pair> downTimes;
    public long startDownTime;
    public long endDownTime;
    public boolean used;


    public Cashier() {
        id = nextID;
        nextID++;
        used = false;
        openForCustomer = true;
        startDownTime = System.currentTimeMillis();
        numOfCustomersCheckedOut = 0;
        checkOutTime = (long)(minCheckoutTime + Math.random()* (maxCheckoutTime-minCheckoutTime));//
        customersSeen = new ArrayList<>();
        downTimes = new ArrayList<>();
    }

    @Override
    public void run() {
        Customer current = null;

        while(Main.queue.size() == 0) {
        }//while waiting for next customer

        if (Main.queue.size() > 0) {//customer waiting to check out

            //current customer
            current = Main.queue.poll();//make current customer variable
            if (current != null) {

                endDownTime = System.currentTimeMillis();
                downTimes.add(new Pair(startDownTime, endDownTime));
                Main.cashiers.remove(this);//remove current cashier from cashier list
                used = true;


                current.doneWQueue();

                System.out.println("CASHIER: Added Customer " + current.getCustomerId() + " to cashier " + id + " with leaveQueuetime " + current.leaveQueueTime);//print
                Main.PrintCashiers();//print cashier
                System.out.println();
                openForCustomer = false;//taken in customer
            }
        }

        while(!openForCustomer && current != null) {//has customer
            try {
                sleep(checkOutTime);//sleep for 5 seconds
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            customersSeen.add(current);//adds customer just seen
            current.finished = true;
            System.out.println("FINISHED:" + current.finished);
            current.setCheckOutTime(System.currentTimeMillis() - current.getLeaveQueueTime());
            numOfCustomersCheckedOut++;//finished w customer
            System.out.println("CASHIER: Customer " + customersSeen.get(customersSeen.size() - 1).getCustomerId() + " done checking out");//current id of customer

            Main.cashiers.add(this);//add back cashier to list
            Main.PrintCashiers();//print; make sure cashier in list
            openForCustomer = true;//open for another customer
            startDownTime = System.currentTimeMillis();//start downtime waiting for next customer
        }

        run();//need to rerun

    }

    public long getCashierId() {
        return this.id;
    }
}
