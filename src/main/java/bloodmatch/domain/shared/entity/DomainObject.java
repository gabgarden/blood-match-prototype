package bloodmatch.domain.shared.entity;

import bloodmatch.domain.shared.valueObjects.DomainID;

import java.util.ArrayList;
import java.util.List;

public abstract class DomainObject {

    protected DomainID id;
    private final List<Observer> observers = new ArrayList<>();

    public DomainID getId() {
        return id;
    }

    protected void setId(DomainID id) {
        if (id == null)
            throw new IllegalArgumentException("Domain id cannot be null");
        this.id = id;
    }

    public void addObserver(Observer observer) {
        if (observer == null)
            throw new IllegalArgumentException("Observer cannot be null");
        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        if (observer == null)
            throw new IllegalArgumentException("Observer cannot be null");
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof DomainObject))
            return false;

        DomainObject that = (DomainObject) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}