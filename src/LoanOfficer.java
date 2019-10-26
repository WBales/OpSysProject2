public class LoanOfficer implements Runnable{
    private Customer customer;
    int amount;

    LoanOfficer (){
        System.out.println("Loan Officer created");
    }

    public void customerAction(int amount, Customer customer){
            this.customer = customer;
            this.amount = amount;
    }

    public void startMessage(){
        System.out.println("Loan Officer begins serving customer " + customer.getCustomerNum());
    }

    public void actionMessage(){
        System.out.println("Loan Officer approves loan for customer " + customer.getCustomerNum());
    }

    public void changeLoanAmount(int amount){
        //Updates bank balances. Semaphores are used in the run()
        Bank.balance[customer.getCustomerNum()] = Bank.balance[customer.getCustomerNum()] + amount;
        Bank.loanAmount[customer.getCustomerNum()] = Bank.loanAmount[customer.getCustomerNum()] + amount;
        try{
            Thread.sleep(Bank.sleepTenthSeconds(4));
        }
        catch (InterruptedException e){

        }
    }

    public void run(){
        while(true){
            try{
                Bank.loanReady.acquire();               //Waits until a customer is available in line
                Bank.loanMutex.acquire();
                customerAction(Bank.loanLine.peek().getValue(), Bank.loanLine.remove()); //Critical - Officer is taking the next person in line
                Bank.loanMutex.release();
                startMessage();                      //Officer is serving customer
                customer.requestOfficer(amount);     //Customer makes request
                Bank.bankProcessing.acquire();
                changeLoanAmount(amount);           //Critical - Officer is changing bank values
                Bank.bankProcessing.release();
                actionMessage();                //Officer has completed action
                customer.loanApproved();        //Customer gets receipt
                Bank.custCountMutex.acquire();
                Bank.custCount++;               //Critical - Increment customer count
                customer.stop();                 //releases the customer, might release bank main
                Bank.custCountMutex.release();

            } catch (InterruptedException e){

            }
        }
    }
}
