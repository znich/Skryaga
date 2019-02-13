package by.laguta.skryaga.service;

import android.content.Context;

public interface BalanceService {

    void updateBalance(UpdateListener<Void> updateListener);
}
