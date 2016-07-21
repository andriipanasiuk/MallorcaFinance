package com.andriipanasiuk.finance;

import java.util.ArrayList;
import java.util.List;

import static com.andriipanasiuk.finance.Main.Currency.гривен;
import static com.andriipanasiuk.finance.Main.Currency.долларов;
import static com.andriipanasiuk.finance.Main.ExpenseAccount.продукты;
import static com.andriipanasiuk.finance.Main.Printer.распечатать;

public class Main {

    static class ExpenseAccount extends Account {

        ExpenseAccount(String name) {
            this.name = name;
        }

        public static ExpenseAccount продукты() {
            return new ExpenseAccount("продукты");
        }
    }

    static class Transaction {
        protected double sum;
        protected Currency currency;

        protected Account from;
        protected Account to;

    }

    static class Expense extends Transaction {
        private ExpenseAccount expenseAccount;

        public static Expense потратил(double sum, Currency currency) {
            Expense expense = new Expense();
            expense.sum = sum;
            expense.currency = currency;
            return expense;
        }

        public Expense на(ExpenseAccount expenseAccount) {
            this.expenseAccount = expenseAccount;
            return this;
        }

        public String toString() {
            return String.format("Потратил %s %s на %s", DoubleUtils.toString(sum), currency, expenseAccount.name);
        }

    }

    static class DoubleUtils {
        public static String toString(double number) {
            if (number == (long) number)
                return String.format("%d", (long) number);
            else
                return String.format("%s", number);
        }
    }

    static abstract class Currency {
        public static Currency гривен() {
            return new Hryvnia();
        }

        public static Currency долларов() {
            return new Dollar();
        }

    }

    static class Hryvnia extends Currency {
        @Override
        public String toString() {
            //TODO how to use plural forms here?
            return "гривен";
        }

    }

    static class Dollar extends Currency {

        @Override
        public String toString() {
            //TODO how to use plural forms here?
            return "долларов";
        }
    }

    static class Account {
        double sum;
        String name;
        Currency currency;
    }

    static class Balance {
        List<Account> accounts;
    }

    static class CurrencyExchangeOperation extends Transaction {

        double exchangeRate;
        Currency currencyBought;

        public static CurrencyExchangeOperation купил(double sum, Currency currency) {
            CurrencyExchangeOperation operation = new CurrencyExchangeOperation();
            operation.sum = sum;
            operation.currencyBought = currency;
            return operation;
        }

        public CurrencyExchangeOperation по_курсу(double rate) {
            exchangeRate = rate;
            return this;
        }

        @Override
        public String toString() {
            return String.format("Купил %s %s по %s", DoubleUtils.toString(sum), currencyBought, exchangeRate);
        }
    }

    static abstract class MonthExpenses {
        private List<Transaction> transactions = new ArrayList<>();

        public CurrencyExchangeOperation купил(double sum, Currency currency) {
            CurrencyExchangeOperation operation = CurrencyExchangeOperation.купил(sum, currency);
            transactions.add(operation);
            return operation;
        }

        public Expense потратил(double sum, Currency currency) {
            Expense expense = Expense.потратил(sum, currency);
            transactions.add(expense);
            return expense;
        }

        protected abstract void расходы();
    }

    static class July2016 extends MonthExpenses {

        @Override
        protected void расходы() {
            потратил(20, гривен()).на(продукты());
            потратил(30, гривен()).на(продукты());
            купил(1000, долларов()).по_курсу(26.5);
        }
    }

    static class Printer {
        public static void распечатать(Class<? extends MonthExpenses> expensesClass) {
            try {
                MonthExpenses monthExpenses = expensesClass.newInstance();
                monthExpenses.расходы();
                monthExpenses.transactions.forEach(System.out::println);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) {
        распечатать(July2016.class);
    }
}
