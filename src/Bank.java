import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class Bank{

    private int num;
    static final int TOTAL_CUSTOMERS = 5;
    static final int TOTAL_TELLERS = 2;
    static final int TOTAL_OFFICERS = 1;

    static Random rand = new Random();

    public static int[] balance = new int[]{1000, 1000, 1000, 1000, 1000};
    public static int[] loanAmount = new int[TOTAL_CUSTOMERS];
    //public static int[] currentTask = new int[TOTAL_CUSTOMERS];

    public static Semaphore custReady = new Semaphore(0, true);
    public static Semaphore receipt = new Semaphore(0, true);
    public static Semaphore mutex1 = new Semaphore(0, true);
    public static Semaphore mutex2 = new Semaphore(0, true);
    public static Semaphore bankProcessing = new Semaphore(1, true);


    public static Semaphore maxCustomers = new Semaphore(TOTAL_CUSTOMERS, true);
    public static Semaphore[] tellerWindow = new Semaphore[]{new Semaphore(0), new Semaphore(0)};
    public static Semaphore loanWindow = new Semaphore(0, true);
    public static Semaphore tellers = new Semaphore(2, true);

    public static Queue<Customer> tellerLine = new LinkedList<>();
    public static Semaphore[] tellerReady = new Semaphore[]{new Semaphore(1), new Semaphore(1)};
    public static Queue<Customer> loanLine = new LinkedList<>();
    public static Semaphore loanReady = new Semaphore(1, true);

    public static Semaphore bankQueueMutex = new Semaphore(1, true);
    public static Queue<Customer> bankQueue = new LinkedList<>();


    Bank(){

    }

    public static int sleepRandom(){
        return rand.nextInt(5000) + 1000;
    }

    public static void makeRequest(Customer customer, int amount, int task){
        if(task == 0){              //Deposit

        } else if (task == 1){      //Withdraw

        } else if (task == 2){      //Loan

        } else {
            System.out.println("Task designation error");
        }

    }

    public static void customerAction(){

    }

    public void customer(int num){
        int customerNumber = num;

    }
    /*
    public void teller(int num){
        int tellerNum = num;
    }

    public void loanOfficer(){
        int loanOfficerNum = num;
    }
    */

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

        //System.out.println("Test");
        int count = 0;
        boolean moreCustomers = true;
        while(moreCustomers) {
            if (bankQueue.size() != 0) {
                //Customer currentCustomer = bankQueue.remove();


                if (bankQueue.peek().getTask() == 2) {
                    loanLine.add(bankQueue.remove());
                } else if (bankQueue.peek().getTask() == 0 || bankQueue.peek().getTask() == 1) {
                    tellerLine.add(bankQueue.remove());
                }

                if (loanLine.size() != 0) {
                    try {
                        count++;
                        loanReady.acquire();
                        loanOfficer.customerAction(loanLine.peek().getValue(), loanLine.remove());
                        //loanLineReady.release();

                    } catch (InterruptedException e) {

                    }
                }
                if (tellerLine.size() != 0) {
                    try {
                        tellers.acquire();
                        if (teller[0].getIsAvailable() == true) {
                            count++;
                            tellerReady[0].acquire();
                            teller[0].customerAction(tellerLine.peek().getValue(), tellerLine.remove());
                        } else if (teller[1].getIsAvailable() == true) {
                            count++;
                            tellerReady[1].acquire();
                            teller[1].customerAction(tellerLine.peek().getValue(), tellerLine.remove());
                        }
                    } catch (InterruptedException e) {

                    }

                    //System.out.println("Was for teller");
                }
                //System.out.println(count);
                if (loanLine.size() == 0 && tellerLine.size() == 0 && bankQueue.size() == 0) {
                    moreCustomers = false;
                }
            }
        }

        if(bankQueue.size() == 0){
            for(int i = 0; i < TOTAL_OFFICERS; i++){
                try{
                    loanOfficer.stop();
                    officerThread.join();
                    System.out.println("Loan Officer is joined by main");
                } catch (InterruptedException e){

                }
            }

            for(int i = 0; i < TOTAL_TELLERS; i++){
                try{
                    teller[i].stop();
                    tellerThreads[i].join();
                    System.out.println("Teller " + i + " is joined by main");
                } catch (InterruptedException e){

                }
            }

            for(int i = 0; i < TOTAL_CUSTOMERS; i++){
                try{
                    customer[i].stop();
                    customerThreads[i].join();
                    System.out.println("Customer " + i + " is joined by main");
                } catch (InterruptedException e){

                }
            }
            System.out.println("Closed all?");

            for (int i = 0; i < loanAmount.length; i++) {
                System.out.println("Customer " + i + ": " + loanAmount[i]);
            }
        }
    }
}

