import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Bank{

    static final int TOTAL_CUSTOMERS = 5;
    static final int TOTAL_TELLERS = 2;
    static final int TOTAL_OFFICERS = 1;

    static Random rand = new Random();

    public static int[] balance = new int[]{1000, 1000, 1000, 1000, 1000};
    public static int[] loanAmount = new int[TOTAL_CUSTOMERS];
    public static int custCount = 0;

    public static Semaphore custCountMutex = new Semaphore(1, true);
    public static Semaphore loanMutex = new Semaphore(1, true);
    public static Semaphore tellerMutex = new Semaphore(1, true);
    public static Semaphore bankProcessing = new Semaphore(1, true);
    public static Semaphore bankRand = new Semaphore(1, true);

    public static Queue<Customer> tellerLine = new LinkedList<>();
    public static Semaphore tellerReady = new Semaphore(0, true);

    public static Queue<Customer> loanLine = new LinkedList<>();
    public static Semaphore loanReady = new Semaphore(0, true);

    public static Semaphore allCustomer = new Semaphore(0, true);

    Bank(){

    }

    public static int sleepRandom(){
        return rand.nextInt(2000) + 1000;
    }

    public static void main(String args[]){
        //Create Loan Officer Thread
        LoanOfficer loanOfficer = new LoanOfficer();
        Thread officerThread = new Thread(loanOfficer);
        officerThread.setDaemon(true);
        officerThread.start();

        //Create Teller Threads
        Teller teller[] = new Teller[TOTAL_TELLERS];
        Thread tellerThreads[] = new Thread[TOTAL_TELLERS];
        for(int i = 0; i < TOTAL_TELLERS; i++){
            teller[i] = new Teller(i);
            tellerThreads[i] = new Thread(teller[i]);
            tellerThreads[i].setDaemon(true);
            tellerThreads[i].start();
        }

        //Create Customer Threads
        Customer customer[] = new Customer[TOTAL_CUSTOMERS];
        Thread customerThreads[] = new Thread[TOTAL_CUSTOMERS];
        for(int i = 0; i < TOTAL_CUSTOMERS; i++){
            customer[i] = new Customer(i);
            customerThreads[i] = new Thread(customer[i]);
            customerThreads[i].setDaemon(true);
            customerThreads[i].start();
        }

        try{
            allCustomer.acquire();
            if(custCount == 15){
                for(int i = 0; i < TOTAL_OFFICERS; i++){
                    try{
                        officerThread.interrupt();
                        officerThread.join();
                        System.out.println("Loan Officer is joined by main");
                    } catch (InterruptedException e){

                    }
                }
                for(int i = 0; i < TOTAL_TELLERS; i++){
                    try{
                        tellerThreads[i].interrupt();
                        tellerThreads[i].join();
                        System.out.println("Teller " + i + " is joined by main");
                    } catch (InterruptedException e){

                    }
                }

                for(int i = 0; i < TOTAL_CUSTOMERS; i++){
                    try{
                        customerThreads[i].join();
                        System.out.println("Customer " + i + " is joined by main");
                    } catch (InterruptedException e){

                    }
                }
                //Output for summary table
                System.out.printf("%35s%n", "Bank Simulation Summary");
                System.out.printf("%18s %20s%n", "Ending", "Loan Amount");

                for (int i = 0; i < loanAmount.length; i++) {
                    System.out.printf("%-11s %-15d %-10d%n", ("Customer " + i), balance[i], loanAmount[i]);
                }
            }
        } catch (InterruptedException e){

        }
    }
}

