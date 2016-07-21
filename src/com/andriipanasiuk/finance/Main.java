package com.andriipanasiuk.finance;

import java.util.ArrayList;
import java.util.List;

import static com.andriipanasiuk.finance.Main.Currency.гривен;
import static com.andriipanasiuk.finance.Main.Currency.долларов;
import static com.andriipanasiuk.finance.Main.IncomeItem.работу;
import static com.andriipanasiuk.finance.Main.OutcomeAccount.продукты;
import static com.andriipanasiuk.finance.Main.Printer.распечатать;

public class Main {

    static class OutcomeAccount extends Account {

        OutcomeAccount(String name) {
            this.name = name;
        }

        public static OutcomeAccount продукты() {
            return new OutcomeAccount("продукты");
        }
    }

    static class Transaction {
        protected double sum;
        protected Currency currency;

        protected Account from;
        protected Account to;

    }

    static class OutcomeTransaction extends Transaction {
        private OutcomeAccount outcomeAccount;

        public static OutcomeTransaction потратил(double sum, Currency currency) {
            OutcomeTransaction outcomeTransaction = new OutcomeTransaction();
            outcomeTransaction.sum = sum;
            outcomeTransaction.currency = currency;
            return outcomeTransaction;
        }

        public OutcomeTransaction на(OutcomeAccount outcomeAccount) {
            this.outcomeAccount = outcomeAccount;
            return this;
        }

        public String toString() {
            return String.format("Потратил %s %s на %s", DoubleUtils.toString(sum), currency, outcomeAccount.name);
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

    static abstract class Period {
        private List<Transaction> transactions = new ArrayList<>();

        public CurrencyExchangeOperation купил(double sum, Currency currency) {
            CurrencyExchangeOperation operation = CurrencyExchangeOperation.купил(sum, currency);
            transactions.add(operation);
            return operation;
        }

        public OutcomeTransaction потратил(double sum, Currency currency) {
            OutcomeTransaction outcomeTransaction = OutcomeTransaction.потратил(sum, currency);
            transactions.add(outcomeTransaction);
            return outcomeTransaction;
        }

        public IncomeTransaction получил(double sum, Currency currency) {
            IncomeTransaction transaction = IncomeTransaction.получил(sum, currency);
            transactions.add(transaction);
            return transaction;
        }

        protected abstract void расходы_и_доходы();
    }

    static class Cash extends Account {
        @Override
        public String toString() {
            return "Наличные";
        }
    }

    static class IncomeItem {
        final String name;

        IncomeItem(String name) {
            this.name = name;
        }

        public static IncomeItem работу() {
            return new IncomeItem("работу");
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class IncomeTransaction extends Transaction {

        IncomeItem forItem;

        public IncomeTransaction за(IncomeItem incomeItem) {
            forItem = incomeItem;
            return this;
        }

        public static IncomeTransaction получил(double sum, Currency currency) {
            IncomeTransaction incomeTransaction = new IncomeTransaction();
            incomeTransaction.sum = sum;
            incomeTransaction.currency = currency;
            return incomeTransaction;
        }

        @Override
        public String toString() {
            return String.format("Получил %s %s за %s", DoubleUtils.toString(sum), currency, forItem);
        }
    }

    static class July2016 extends Period {

        @Override
        protected void расходы_и_доходы() {
            потратил(20, гривен()).на(продукты());
            потратил(30, гривен()).на(продукты());
            купил(1000, долларов()).по_курсу(26.5);
            получил(10000, долларов()).за(работу());
        }
    }

    static class Printer {
        public static void распечатать(Class<? extends Period> expensesClass) {
            try {
                Period period = expensesClass.newInstance();
                period.расходы_и_доходы();
                period.transactions.forEach(System.out::println);
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
