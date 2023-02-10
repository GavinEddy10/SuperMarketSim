import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomerFactory extends Thread{
    private long timeSlice;
    private long chance;
    private long nextAttempt;
    private long tempTime;
    private long programRunTime;

    public CustomerFactory(long timeSlice, long chance,long programRunTime) {
        this.timeSlice = timeSlice;
        this.programRunTime = programRunTime;
        this.chance = chance;
        tempTime = System.currentTimeMillis();
        nextAttempt = System.currentTimeMillis() + this.timeSlice;
    }


    @Override
    public void run() {
        while (Main.runnings) {
            if (nextAttempt < System.currentTimeMillis()) {//current time in ms
                long randomNum = (long) (1 + Math.random() * 100);
                if (randomNum <= chance) {//random num 1-100, if random num is less than chance, customer made
                    System.out.println("Made customer");
                    Customer c = new Customer();
                    Main.allCustomers.add(c);
                    c.start();//starts customer thread
                }
                else {
                    System.out.println("Did not make customer");
                }
                nextAttempt = System.currentTimeMillis() + timeSlice;
            }
        }
    }


}
