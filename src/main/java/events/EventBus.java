package events;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class, List<EventListener>> listeners;
    private Class listenerClass;

    public EventBus(){
        this.listeners = new HashMap<>();
    }


    @SuppressWarnings("unchecked")
    public void send(Event event){
        Class eventClass = event.getClass();
        if(!listeners.containsKey(eventClass)) return;
        List<EventListener> eventListeners = listeners.get(eventClass);
        for(EventListener e : eventListeners){
            e.handle(event);
        }
    }
    public <T extends Event> void subscribe(Class<T> eventClass, EventListener<T> listener){
        if(!listeners.containsKey(eventClass)){
            listeners.put(eventClass, new LinkedList<>());
        }
        listeners.get(eventClass).add(listener);
    }
}
