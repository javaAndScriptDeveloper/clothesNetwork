package com.example.company.service;

import java.util.function.Supplier;

public interface TransactionService {

    void execute(Runnable transactionOperation);

    <T> T executeWithResult(Supplier<T> transactionOperation);
}
