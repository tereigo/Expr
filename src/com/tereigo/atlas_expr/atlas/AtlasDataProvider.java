package com.tereigo.atlas_expr.atlas;

import com.tereigo.atlas_expr.atlas.utils.EngineTimeProvider;
import com.tereigo.atlas_expr.atlas.utils.RandomDoubleProvider;
import com.tereigo.atlas_expr.atlas.utils.ReferenceDataCache;

import java.nio.ByteBuffer;

/*
  Provides access to Atlas functions
 */
public class AtlasDataProvider {
    private final EngineTimeProvider timeProvider;
    private final RandomDoubleProvider doubleProvider;
    private final ReferenceDataCache refData;
    private final String nodeName;

    public AtlasDataProvider(EngineTimeProvider timeProvider,
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
