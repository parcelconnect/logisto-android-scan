package com.tubbert.powdroid.events;

/**
 *
 */
public interface Subscribable<SubscriberT> {

    public void subscribe(SubscriberT subscriber);

    public void unsubscribe(SubscriberT subscriber);

}
