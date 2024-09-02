package com.tereigo.expr.falcon;

import com.tereigo.expr.falcon.utils.EngineTimeProvider;
import com.tereigo.expr.falcon.utils.RandomDoubleProvider;
import com.tereigo.expr.falcon.utils.ReferenceDataCache;

import java.nio.ByteBuffer;

/*
  Provides access to global functions
 */
public class FalconDataProvider {
    private final EngineTimeProvider timeProvider;
    private final RandomDoubleProvider doubleProvider;
    private final ReferenceDataCache refData;
    private final String nodeName;

    public FalconDataProvider(EngineTimeProvider timeProvider,
                              RandomDoubleProvider doubleProvider,
                              ReferenceDataCache refData,
                              String nodeName) {
        this.timeProvider = timeProvider;
        this.doubleProvider = doubleProvider;
        this.refData = refData;
        this.nodeName = nodeName;
    }

    public long getEngineTime() {
      return timeProvider.getEngineTime();
  }

    public double getNextRandom() {
      return doubleProvider.getNext();
  }

    public ByteBuffer getTuidByClientId(int clientId) {
      return refData.getTuidByClientId(clientId);
  }

    public String getNodeName() {
        return nodeName;
    }
}
