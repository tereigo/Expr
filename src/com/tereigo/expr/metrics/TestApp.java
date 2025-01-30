package com.tereigo.expr.metrics;

import com.tereigo.expr.metrics.node.NodeInfoProviderImpl;
import com.tereigo.expr.metrics.producer.impl.*;
import com.tereigo.expr.metrics.serializers.MetricStringSerializer;

import java.util.function.LongSupplier;

import static com.tereigo.expr.metrics.MathUtils.randomBetween;

public class TestApp {

    public static void main(String[] args) {
        MetricStoreImpl store = new MetricStoreImpl();
        NodeInfoProviderImpl nodeInfoProvider = new NodeInfoProviderImpl();
        MetricFactoryImpl realFactory = new MetricFactoryImpl(nodeInfoProvider);
        realFactory.setListener(store);

        DelayedFactory delayedFactory = new DelayedFactory(realFactory);
        ConsolePublisher publisher = new ConsolePublisher(new MetricStringSerializer());
        MetricSenderImpl sender = new MetricSenderImpl(nodeInfoProvider, publisher);
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
