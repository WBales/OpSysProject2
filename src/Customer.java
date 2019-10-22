import javafx.scene.control.SeparatorMenuItem;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class Customer implements Runnable {
    private int customerNum, task, value;
    //static Random rand = new Random();
    boolean isPending;

    Customer(int num){
        customerNum = num;
        //balance = 1000;
        //loanAmount = 0;
        System.out.println("Customer " + customerNum + " created");
    }

    public int getCustomerNum() {
        return customerNum;
    }

    public int getTask() {return task;}

    public int getValue() {return value;}

    public void requestTeller(Teller teller, int amount){
        if(amount > 0){
            System.out.println("Customer " + customerNum + " requests of Teller" + teller.getTellerNum() + " to make a deposit of " + amount);
        } else {
            System.out.println("Customer " + customerNum + " requests of Teller" + teller.getTellerNum() + " to make a withdrawal of " + (amount * -1));
        }
    }

    public void tellerReceipt(Teller teller, int amount){
        if(amount > 0){
            System.out.println("Customer " + customerNum + " get cash and receipt from " + teller.getTellerNum());
        } else {
            System.out.println("Customer " + customerNum + " get receipt from " + teller.getTellerNum());
        }
    }

    public void requestOfficer(int amount){
        System.out.println("Customer " + customerNum + " requests of loan officer to apply for a loan of " + amount);
    }

    public void loanApproved (){
        System.out.println("Customer " + customerNum + " gets loan from loan officer");
    }

    /*
    public void changeBalance(int amount){
        balance = balance + amount;
    }

    public void changeLoanAmount(int amount){
        balance = balance + amount;
        loanAmount = loanAmount + amount;
    }
    */

    public void stop(){
        isPending = false;
    }

    public void run(){
        for(int i = 0; i < 3; i++){
            try{
                isPending = true;
                task = Bank.rand.nextInt(3);
                //task = 2;
                if(task > 2 || task < 0){
                    System.out.println("Task error");
                }

                if(task == 0 || task == 2) {
                    value = ((Bank.rand.nextInt(4) + 1) * 100);
                } else {
                    value = ((Bank.rand.nextInt(4) + 1) * -100);
                }
                //actions
                Bank.bankQueueMutex.acquire();
                Bank.bankQueue.add(this);
                Bank.bankQueueMutex.release();
                //Bank.makeRequest(this, value, task);
                while(isPending == true){

                }
            } catch (InterruptedException e){

            }
        }
    }
}
