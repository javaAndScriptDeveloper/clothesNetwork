package com.example.company.service;

public interface TransactionService {

    void execute(Runnable transactionOperation);
}
