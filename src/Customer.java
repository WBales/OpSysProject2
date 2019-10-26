import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
    private int customerNum, task, value;
    private Semaphore customerSem = new Semaphore(0, true);

    Customer(int num){
        customerNum = num;
        System.out.println("Customer " + customerNum + " created");
    }

    public int getCustomerNum() {
        return customerNum;
    }

    public int getValue() {return value;}

    public void requestTeller(Teller teller, int amount){
        //Request statements at teller
        if(amount > 0){
            System.out.println("Customer " + customerNum + " requests of Teller " + teller.getTellerNum() + " to make a deposit of " + amount);
        } else {
            System.out.println("Customer " + customerNum + " requests of Teller " + teller.getTellerNum() + " to make a withdrawal of " + (amount * -1));
        }
        try{
            Thread.sleep(Bank.sleepTenthSeconds(1));
        }
        catch (InterruptedException e){

        }
    }

    public void tellerReceipt(Teller teller, int amount){
        //Receipt statements at teller
        if(amount < 0){
            System.out.println("Customer " + customerNum + " get cash and receipt from Teller " + teller.getTellerNum());
        } else {
            System.out.println("Customer " + customerNum + " get receipt from Teller " + teller.getTellerNum());
        }
        try{
            Thread.sleep(Bank.sleepTenthSeconds(1));
        }
        catch (InterruptedException e){

        }
    }

    public void leaveBank(){
        //Leave bank
        System.out.println("Customer " + customerNum + " departs the bank");
    }

    public void requestOfficer(int amount){
        //Request at loan officer
        System.out.println("Customer " + customerNum + " requests of loan officer to apply for a loan of " + amount);
        try{
            Thread.sleep(Bank.sleepTenthSeconds(1));
        }
        catch (InterruptedException e){

        }
    }

    public void loanApproved (){
        //Receipt at loan officer
        System.out.println("Customer " + customerNum + " gets loan from loan officer");
        try{
            Thread.sleep(Bank.sleepTenthSeconds(1));
        }
        catch (InterruptedException e){

        }
    }

    public void stop(){
        //Release customer and count to see if all customers have completed transactions
        if(Bank.custCount == 15){
            Bank.allCustomer.release();
        }
        customerSem.release();
    }

    public void run(){
        for(int i = 0; i < 3; i++){
            try{
                //Generate task and value for that task
                Bank.bankRand.acquire();                           //I had some suspicious results where customers had duplicate
                task = Bank.rand.nextInt(3);                    //values. I locked this in a mutex just incase
                value = ((Bank.rand.nextInt(4) + 1) * 100);
                Bank.bankRand.release();

                if(task == 0) {
                    Bank.tellerMutex.acquire();
                    Bank.tellerLine.add(this);  //Critical
                    Bank.tellerMutex.release();
                    Bank.tellerReady.release(); //Each teller is using this to see if customers in queue
                } else if(task == 1) {
                    value = (value * -1);
                    Bank.tellerMutex.acquire();
                    Bank.tellerLine.add(this);  //Critical
                    Bank.tellerMutex.release();
                    Bank.tellerReady.release(); //Each teller is using this to see if customers in queue
                } else if(task == 2) {
                    Bank.loanMutex.acquire();
                    Bank.loanLine.add(this);    //Critical
                    Bank.loanMutex.release();
                    Bank.loanReady.release();   //Loan officer is using this to see if customers in queue
                }
                customerSem.acquire();          //Locks until the Teller/Officer releases it at the end of processing

            } catch (InterruptedException e){

            }
            if(i == 2){
                //Ensuring counting and exclusion is working as intended
                //System.out.println("All visits for Customer" + customerNum);
            }
        }
        leaveBank();
    }
}
