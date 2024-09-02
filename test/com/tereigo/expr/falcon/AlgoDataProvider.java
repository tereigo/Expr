package com.tereigo.expr.falcon;

/*
  Provides access to Algo functions
 */
public class AlgoDataProvider {

    private final String algoType;

    private AlgoDataProvider(String algoType) {
        this.algoType = algoType;
    }

    public String getAlgoType() {
        return algoType;
    }
}
