package com.example.parliamentvoiceapp.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/ask")
    Call<AskResponse> askQuestion(@Body AskRequest request);
}