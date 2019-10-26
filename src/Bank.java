import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Bank{
    //With a few changes I should be able to have the numbers of customer/tellers/loan officers be passed in and the whole system be dynamic.
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


    public static int sleepTenthSeconds(int i){
        return (i * 100);
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
                //Removed per professor feedback. I've left the code so that you could see that I did originally include it.
                //Feedback stated that threads that have the daemon set to true will close when the program closes.
                /*
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
                */

                for(int i = 0; i < TOTAL_CUSTOMERS; i++){
                    try{
                        customerThreads[i].join();
                        System.out.println("Customer " + i + " is joined by main"); //Not sure how to get the customer thread itself to print this with my design having main in a different class
                    } catch (InterruptedException e){

                    }
                }
                System.out.println();
                //Output for summary table
                System.out.printf("%35s%n%n", "Bank Simulation Summary");
                System.out.printf("%26s %12s%n%n", "Ending Balance", "Loan Amount");

                int totalBalance = 0;
                int totalLoans = 0;
                for (int i = 0; i < loanAmount.length; i++) {
                    totalBalance = totalBalance + balance[i];
                    totalLoans = totalLoans + loanAmount[i];
                    System.out.printf("%-11s %-15d %-10d%n", ("Customer " + i), balance[i], loanAmount[i]);
                }
                System.out.println();
                System.out.printf("%-11s %-15s %-10d%n", "Totals", totalBalance, totalLoans);
            }
        } catch (InterruptedException e){

        }
    }
}

