import java.awt.desktop.SystemSleepEvent;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {//lets all customers finish when done//checkout time for cashier between 3000-5000ms random int
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("(long) How many (ms) should program run: ");
        long programRunTime = input.nextLong();
        programStartTime = System.currentTimeMillis(); //program start time

        System.out.println("(int) How many cashiers should there be: ");
        numCashiers = input.nextInt();
        createCashiers();
        System.out.println("(int) how often should customers spawn (ms): ");
        int timeSlice = input.nextInt();
        System.out.println("(int) what percent chance 0-99 should customers spawn: ");
        int chance = input.nextInt();

        System.out.println();
        System.out.println();
        System.out.println("SUPERMAKRETSIM");

        CustomerFactory customerFactory = new CustomerFactory(timeSlice,chance, programRunTime);//time slice is time between each customer
        customerFactory.start();

        while((programStartTime + programRunTime) > System.currentTimeMillis()) {

        }
        runnings = false;
        System.out.println("DONE WITH RUNTIME " + System.currentTimeMillis());
        System.out.println("FINISHING ALL CUSTOMERS");
        while(queue.size() >= 0 && !allCustomersFinsihed()) {

        }
        System.out.println("RAN");
        if (allCustomersFinsihed()) {
            printAllStats(programRunTime);
            System.exit(0);
        }
        //check time if cashier didn't check customer yet (end time = 0)
        //only include guys that checked out at least one person
        //don't include downtime if didn't check customer, above
        //find a way to stop finishing code
    }

    public static boolean runnings = true;
    public static ConcurrentLinkedQueue<Customer> queue = new ConcurrentLinkedQueue<>();//peek is 0 index and pull is 0 index and removes it from queue
    public static ArrayList<Cashier> cashiers = new ArrayList<>();
    public static ArrayList<Cashier> allCashiers = new ArrayList<>();
    public static ArrayList<Long> queueWaitTimes = new ArrayList<>();
    public static int numCashiers = 0;
    public static ArrayList<Customer> allCustomers = new ArrayList<>();
    public static long programStartTime;

    public static boolean allCustomersFinsihed() {//one not finished, false
        for (Customer c: allCustomers) {
            if (!c.finished)
                return false;
        }
        return true;
    }

    public static void printAllStats(long runTime) {
        System.out.println();
        System.out.println("STATS");
        System.out.println("Total Customers: " + allCustomers.size());
        System.out.println("Total Cashiers used: " + totalCashiersUsed());
        System.out.println("Average Shop Time Per Customer: " + averageShopTimeCustomers() + "ms");
        System.out.println("Average Process Time in Checkout Per Customer: " + averageProecessTimeCustomers() + "ms");
        System.out.println("Average Wait Time in the queue: " + averageQueueWait(runTime) + "ms");
        System.out.println("Average Down Time per Cashier: " + averageCashierDownTime() + "ms");
    }

    public static void createCashiers() {
        for (int i = 0; i < numCashiers; i++) {
            Cashier now = new Cashier();
            cashiers.add(now);
            allCashiers.add(now);
        }
    }


    public static int totalCashiersUsed() {
        int count = 0;
        for (int i = 0; i < allCashiers.size(); i++) {
            if (allCashiers.get(i).used)
                count++;
        }
        return count;
    }
    public static void stopAllCustomersCashiers() {
        /*for (Customer c: queue) {
            c.timeSpentInQueue = System.currentTimeMillis() - c.enterQueueTime;
        }
         */
        //stop all cashiers
        for (Cashier c : allCashiers){
            if (c.openForCustomer)
                c.endDownTime = System.currentTimeMillis();//check
            c.stop();
        }
        //stop all customers
        for (Customer c: allCustomers) {
            c.stop();
        }
    }

    public static long averageShopTimeCustomers() {
        long totalShopTime = 0;
        for (Customer c : allCustomers) {
            //System.out.println("SHOP TIME: " + c.getShopTime());
            totalShopTime += c.getShopTime();
        }
        if (allCustomers.size() == 0)
            return 0;
        return totalShopTime/allCustomers.size();
    }

    public static long averageProecessTimeCustomers() {//process time of all customers that have been checked out
        long totalProcessTime = 0;
        int totalCustomers = 0;

        for (int i = 0; i < allCashiers.size(); i++) {
            Cashier cCashier = allCashiers.get(i);

            for (int j = 0; j < cCashier.customersSeen.size(); j++) {
                Customer cCustomer = cCashier.customersSeen.get(j);
                if (cCustomer != null) {
                    totalProcessTime += cCustomer.getCheckOutTime();
                    totalCustomers++;
                }
            }
        }

        if (totalCustomers == 0)
            return 0;

        return totalProcessTime/totalCustomers;
    }

    public static long averageQueueWait(long runTime) {
        long sumWaitTime = 0;
        int totalCustomers = 0;

        for (int i = 0; i < queueWaitTimes.size(); i++) {
            long time = queueWaitTimes.get(i);
            //System.out.println("Time: " + time);
            sumWaitTime += time;
            totalCustomers++;
        }

        if (totalCustomers == 0)
            return runTime;

        //System.out.println("SUMWAITTIME = " + sumWaitTime);
        //System.out.println("TOTAL CUSTOMERS = " + totalCustomers);
        return sumWaitTime/totalCustomers;
    }

    public static long averageCashierDownTime() {
        long sumCashierDownTime = 0;
        int numOfWaits = 0;

        for (int i = 0; i < allCashiers.size();i++) {
            Cashier cCashier = allCashiers.get(i);
            for (int j = 0; j < cCashier.downTimes.size(); j++) {
                Pair CDT = cCashier.downTimes.get(j);
                long downTime = CDT.end - CDT.start;
                sumCashierDownTime += downTime;
                numOfWaits++;
            }
        }
        if (numOfWaits == 0) {
            return System.currentTimeMillis() - programStartTime;
        }
        return sumCashierDownTime/numOfWaits;
    }

    public static void PrintQueue() {
        System.out.print("QUEUE: ");
        for (Customer c: queue) {
            System.out.print(c + " and enterQueueTime " + c.getEnterQueueTime() + " ms; ");
        }
        System.out.println();
    }

    public static void PrintCashiers() {
        System.out.print("CASHIERS: ");
        for (Cashier c: cashiers) {
            System.out.print("Cashier " + c.getCashierId() + ", ");
        }
        System.out.println();
    }

}
