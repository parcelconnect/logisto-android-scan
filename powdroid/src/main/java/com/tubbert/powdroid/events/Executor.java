package com.tubbert.powdroid.events;

/**
 * Use with {@link Publisher} to implement Publish-Subscribe.
 */
public interface Executor<SubscriberT> {

    public void execute(SubscriberT subscriberToExecuteOn);

}
