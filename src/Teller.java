public class Teller implements Runnable {

    private Customer customer;
    private int amount, tellerNum;
    private boolean isRunning;

    Teller (int num){
        tellerNum = num;
        System.out.println("Teller" + tellerNum + " created");
    }

    public void customerAction(int amount, Customer customer){
            this.customer = customer;
            this.amount = amount;
    }

    public void stop(){
        isRunning = false;
    }

    public void changeBalance(int amount){
        //Updates balance. Semaphore used in run()
        Bank.balance[customer.getCustomerNum()] = Bank.balance[customer.getCustomerNum()] + amount;
    }

    public void startMessage(){
        System.out.println("Teller " + tellerNum + " begins serving Customer " + customer.getCustomerNum());
    }

    public int getTellerNum(){
        return tellerNum;
    }

    public void actionMessage(){
        if(amount > 0){
            System.out.println("Teller " + tellerNum + " processes deposit for Customer" + customer.getCustomerNum() + " of " + amount);
        } else {
            System.out.println("Teller " + tellerNum + " processes withdrawal for Customer " + customer.getCustomerNum() + " of " + (amount * -1));
        }
    }

    public void run(){
        isRunning = true;
        outerloop:
        while(isRunning){
            try{
                Bank.tellerReady.acquire();                                                  //Teller waiting for customer in line
                Bank.tellerMutex.acquire();
                customerAction(Bank.tellerLine.peek().getValue(), Bank.tellerLine.remove()); //Critical - Remove customer from line
                Bank.tellerMutex.release();
                startMessage();                                 //Teller starts action
                customer.requestTeller(this, amount);       //Customer makes request
                Bank.bankProcessing.acquire();
                changeBalance(amount);                        //Critical - Teller updated banks values
                Bank.bankProcessing.release();
                Thread.sleep(Bank.sleepRandom());
                actionMessage();                              //Teller has completed action
                customer.tellerReceipt(this, amount);   //Customer gets receipt
                Bank.custCountMutex.acquire();
                Bank.custCount++;              //Critical - Increment customer count
                Bank.custCountMutex.release();
                customer.stop();               //releases the customer, might release bank main
            } catch (InterruptedException e){
                break outerloop;
            }
        }
    }


}
