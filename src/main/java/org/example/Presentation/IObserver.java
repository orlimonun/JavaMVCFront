package org.example.Presentation;

import org.example.Utilities.EventType;

public interface IObserver {

    void update(EventType eventType, Object data);

}
