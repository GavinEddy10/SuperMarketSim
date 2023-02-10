import javax.swing.plaf.nimbus.State;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Customer extends Thread{//queue goes in here, add
    private int id;
    private static int nextID = 1;
    private long shopTime;  //how long ms cus shops for
    private long checkOutTime;   //how many ms cus takes to check out
    private long enterTime;   //time cus entered store
    private long leaveTime;   //time cus leaves store after checkout and seeing casheir
    private long minShopTime = 3000; //min time ms cucs shops
    private long maxShopTime = 20000; //max time ms cus shops
    public long enterQueueTime;
    public long leaveQueueTime;
    public long timeSpentInQueue;
    public boolean finished;

    public Customer() {
        id = nextID;
        nextID++;
        enterTime = System.currentTimeMillis();
        shopTime = (long)(minShopTime + Math.random()* (maxShopTime-minShopTime));
        enterQueueTime = 0;
        leaveQueueTime = 0;
        finished = false;
    }

    @Override
    public String toString() {
        return "Customer " + id;
    }

    @Override//adding override helps know it exists above you
    public void run() {
        while(enterTime + shopTime > System.currentTimeMillis()) {
        }
        System.out.println(this.toString() + " is done shopping with shop time " + shopTime);//customer done shopping

        Main.queue.add(this);
        enterQueueTime = System.currentTimeMillis();
        Main.PrintQueue();

        while(Main.cashiers.size() == 0) {
        }//wait unitl cashier spot open

        //cashiers
        if (Main.cashiers.size() > 0) {
            if (Main.cashiers.size() > 0) {
                Cashier currentCashier = Main.cashiers.get(0);

                if (currentCashier.openForCustomer && currentCashier != null) {
                    if (currentCashier.getState().equals(Thread.State.NEW))
                        currentCashier.start();//start current cashier in arraylist, take in this customer//don't need made method in main
                    else
                        currentCashier.run();
                }
            }
        }
    }

    public void doneWQueue() {
        this.leaveQueueTime = System.currentTimeMillis();
        this.timeSpentInQueue = (this.leaveQueueTime - this.enterQueueTime);
        Main.queueWaitTimes.add(this.timeSpentInQueue);
        System.out.println();
        System.out.println("TimeSpentInQueue" + this.timeSpentInQueue);
        System.out.println();
    }

    public long getShopTime() {
        return shopTime;
    }

    public void setCheckOutTime(long checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public long getCheckOutTime() {
        return checkOutTime;
    }

    public long getEnterQueueTime() {
        return enterQueueTime;
    }


    public long getLeaveQueueTime() {
        return leaveQueueTime;
    }

    public int getCustomerId() {
        return id;
    }
}
