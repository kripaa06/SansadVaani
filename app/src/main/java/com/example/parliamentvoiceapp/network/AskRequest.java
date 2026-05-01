package com.example.parliamentvoiceapp.network;

public class AskRequest {
    private String query;

    public AskRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}