package ie.fastway.scansort.lifecycle;

/**
 * Listens for events that change the state of the [AppSessionService].
 */
public interface SessionEventListener {

    public void onAppSessionEvent(AppSessionEvent event);

}