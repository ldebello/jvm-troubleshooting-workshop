package ar.com.javacuriosities.labs.deadlock;

import ar.com.javacuriosities.utils.Utils;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Utils.start(args);

        BankAccount account1 = new BankAccount(1, 100d);
        BankAccount account2 = new BankAccount(2, 100d);

        Thread transfer1 = new Thread(new TransferTask(account1, account2), "TransferTask-1");
        transfer1.start();

        Thread transfer2 = new Thread(new TransferTask(account2, account1), "TransferTask-2");
        transfer2.start();
    }

    private static final class TransferTask implements Runnable {
        private BankAccount from;
        private BankAccount to;

        public TransferTask(BankAccount from, BankAccount to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            System.out.println("Executing transfer from: " + from + " to: " + to);
            BankAccount.transfer(from, to, 10d);
        }
    }

    private static final class BankAccount {
        private final int id;
        private double balance;

        public BankAccount(int id, double balance) {
            this.id = id;
            this.balance = balance;
        }

        public static void transfer(BankAccount from, BankAccount to, double amount) {
            synchronized (from) {
                System.out.println("Lock acquired: " + from + " by " + Thread.currentThread().getName());
                from.withdraw(amount);
                synchronized (to) {
                    System.out.println("Lock acquired: " + to + " by " + Thread.currentThread().getName());
                    to.deposit(amount);
                }
            }
        }

        public void withdraw(double amount) {
            // Simulamos algo de IO como por ejemplo acceso a una DB
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                // Log and Handle exception
                e.printStackTrace();
            }
            balance -= amount;
        }

        public void deposit(double amount) {
            // Simulamos algo de IO como por ejemplo acceso a una DB
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                // Log and Handle exception
                e.printStackTrace();
            }
            balance += amount;
        }

        @Override
        public String toString() {
            return "BankAccount{" +
                    "id=" + id +
                    ", balance=" + balance +
                    '}';
        }
    }
}
