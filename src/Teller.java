import java.util.Random;

public class Teller implements Runnable {

    Customer customer;
    private int amount, tellerNum;
    private boolean isRunning, isAvailable = true;

    Teller (int num){
        tellerNum = num;
        System.out.println("Teller" + tellerNum + " created");
    }

    public void customerAction(int amount, Customer customer){
            this.customer = customer;
            this.amount = amount;
            Bank.tellerWindow[getTellerNum()].release();
    }

    public void stop(){
        isRunning = false;
    }

    public void changeBalance(int amount){
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

    public boolean getIsAvailable(){
        return isAvailable;
    }


    public void run(){
        isRunning = true;
        while(isRunning){
            /*
            if(customer != null){
                try{
                    Bank.tellerWindow[tellerNum].acquire();
                    startMessage();
                    customer.requestTeller(this, amount);
                    Bank.bankProcessing.acquire();
                    changeBalance(amount);
                    Bank.bankProcessing.release();
                    Thread.sleep(2000);
                    actionMessage();
                    customer.tellerReceipt(this, amount);
                    customer = null;
                    customer.stop();
                    Bank.tellerReady[tellerNum].release();
                } catch (InterruptedException e){

                }

            }
            */
            try{
                Bank.tellerWindow[getTellerNum()].acquire();
                isAvailable = false;
                startMessage();
                customer.requestTeller(this, amount);
                Bank.bankProcessing.acquire();
                changeBalance(amount);
                Bank.bankProcessing.release();
                Thread.sleep(Bank.sleepRandom());
                actionMessage();
                customer.tellerReceipt(this, amount);
                customer.stop();
                customer = null;
                Bank.tellerReady[getTellerNum()].release();
                Bank.tellers.release();
                isAvailable = true;
            } catch (InterruptedException e){

            }
        }
    }


}
