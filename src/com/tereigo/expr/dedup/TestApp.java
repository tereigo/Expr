package com.tereigo.expr.dedup;

import com.tereigo.expr.dedup.node.NodeInfoProviderImpl;
import com.tereigo.expr.dedup.producer.DelayedFactory;
import com.tereigo.expr.dedup.producer.FactoryImpl;
import com.tereigo.expr.dedup.producer.SenderImpl;
import com.tereigo.expr.dedup.producer.StoreImpl;

import java.util.function.LongSupplier;

import static com.tereigo.expr.dedup.MathUtils.randomBetween;

public class TestApp {

    public static void main(String[] args) {
        StoreImpl store = new StoreImpl();
        NodeInfoProviderImpl nodeInfoProvider = new NodeInfoProviderImpl();
        FactoryImpl realFactory = new FactoryImpl(nodeInfoProvider);
        realFactory.setListener(store);

        DelayedFactory delayedFactory = new DelayedFactory(realFactory);
        SenderImpl sender = new SenderImpl();
        Client client = new Client(delayedFactory);
        ManualClient manualClientBefore = new ManualClient(delayedFactory, "before");

        nodeInfoProvider.setNodeId(1);
        nodeInfoProvider.setSessionName("session1");
        nodeInfoProvider.setNodeName("node1");

        delayedFactory.onReady();

        Client2 client2 = new Client2(delayedFactory);
        AppMetrics appMetrics = new AppMetrics(delayedFactory);
        ManualClient manualClientAfter = new ManualClient(delayedFactory, "after");
        MetricsHandler handler = new MetricsHandler(store);

        handler.publish(sender);
        manualClientBefore.send(sender);
        manualClientAfter.send(sender);
        System.out.println();

        LongSupplier supplier = () -> randomBetween(0, 2);
        //LongSupplier supplier = () -> 0;
        client.update(supplier);
        client2.update(supplier);
        manualClientBefore.update(supplier);
        manualClientAfter.update(supplier);
        appMetrics.update(supplier);

        handler.publish(sender);
        manualClientBefore.send(sender);
        manualClientAfter.send(sender);
        System.out.println();

        client.update(supplier);
        client2.update(supplier);
        manualClientBefore.update(supplier);
        manualClientAfter.update(supplier);
        appMetrics.update(supplier);

        handler.publish(sender);
        manualClientBefore.send(sender);
        manualClientAfter.send(sender);
        System.out.println();

        client.update(supplier);
        client2.update(supplier);
        manualClientBefore.update(supplier);
        manualClientAfter.update(supplier);
        appMetrics.update(supplier);

        handler.publish(sender);
        manualClientBefore.send(sender);
        manualClientAfter.send(sender);
        System.out.println();

        client.update(supplier);
        client2.update(supplier);
        manualClientBefore.update(supplier);
        manualClientAfter.update(supplier);
        appMetrics.update(supplier);

        handler.publish(sender);
        manualClientBefore.send(sender);
        manualClientAfter.send(sender);
    }
}
