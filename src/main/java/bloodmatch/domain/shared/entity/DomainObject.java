package bloodmatch.domain.shared.entity;

import bloodmatch.domain.shared.valueObjects.DomainID;

public abstract class DomainObject {

    protected DomainID id;

    public DomainID getId() {
        return id;
    }

    protected void setId(DomainID id) {
        if (id == null)
            throw new IllegalArgumentException("Domain id cannot be null");
        this.id = id;
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