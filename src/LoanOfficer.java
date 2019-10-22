public class LoanOfficer implements Runnable{
    private Customer customer;
    int amount;
    boolean isRunning;

    LoanOfficer (){
        System.out.println("Loan Officer created");
    }

    public void customerAction(int amount, Customer customer){
            this.customer = customer;
            this.amount = amount;
            //System.out.println("Sets customer in loan officer");
            //Bank.loanWindow.release();
    }

    public void startMessage(){
        System.out.println("Loan Officer begins serving customer " + customer.getCustomerNum());
    }

    public void actionMessage(){
        System.out.println("Loan Officer approves loan for customer " + customer.getCustomerNum());
    }

    public void changeLoanAmount(int amount){
        Bank.loanAmount[customer.getCustomerNum()] = Bank.balance[customer.getCustomerNum()] + amount;
        Bank.loanAmount[customer.getCustomerNum()] = Bank.loanAmount[customer.getCustomerNum()] + amount;
    }

    public void stop(){
        isRunning = false;
    }

    public void run(){
        isRunning = true;
        while(isRunning){
            try{
                Bank.loanReady.acquire();
                Bank.loanMutex.acquire();
                System.out.println(Bank.loanLine.size() + " Loan Officer");
                customerAction(Bank.loanLine.peek().getValue(), Bank.loanLine.remove());
                Bank.loanMutex.release();
                //Bank.loanWindow.acquire();           //Someone at the window
                //System.out.println("Is Running");
                startMessage();
                customer.requestOfficer(amount);
                Bank.bankProcessing.acquire();
                changeLoanAmount(amount);
                Bank.bankProcessing.release();
                Thread.sleep(Bank.sleepRandom());
                actionMessage();
                customer.loanApproved();
                customer.stop();
                //customer = null;
                //Bank.loanReady.release();
            } catch (InterruptedException e){

            }
        }
    }
}
