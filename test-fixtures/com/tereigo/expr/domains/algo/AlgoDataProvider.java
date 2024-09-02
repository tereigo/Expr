package com.tereigo.expr.domains.algo;

/*
  Provides access to Algo functions
 */
public class AlgoDataProvider {

    private final String algoType;

    private AlgoDataProvider(final String algoType) {
        this.algoType = algoType;
    }

    public String getAlgoType() {
        return algoType;
    }
}
