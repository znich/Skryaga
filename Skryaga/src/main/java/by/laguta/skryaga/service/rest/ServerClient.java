package by.laguta.skryaga.service.rest;

import by.laguta.skryaga.service.rest.model.ExchangeRate;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ServerClient {

    @GET("/api/exchange")
    Call<ExchangeRate> getLowestRate();
}
