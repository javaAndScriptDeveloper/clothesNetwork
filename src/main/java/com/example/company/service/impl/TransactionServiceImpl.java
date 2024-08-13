package com.example.company.service.impl;

import com.example.company.service.TransactionService;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionTemplate transactionTemplate;

    @Override
    public void execute(Runnable transactionOperation) {
        transactionTemplate.executeWithoutResult(transactionStatus -> transactionOperation.run());
    }

    @Override
    public <T> T executeWithResult(Supplier<T> transactionOperation) {
        return transactionTemplate.execute(transactionStatus -> transactionOperation.get());
    }
}
