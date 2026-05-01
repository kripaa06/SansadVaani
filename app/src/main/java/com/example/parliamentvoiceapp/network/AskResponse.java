package com.example.parliamentvoiceapp.network;

import java.util.List;

public class AskResponse {
    private String answer;
    private List<Source> sources;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public static class Source {
        private String id;
        private double similarity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(double similarity) {
            this.similarity = similarity;
        }
    }
}