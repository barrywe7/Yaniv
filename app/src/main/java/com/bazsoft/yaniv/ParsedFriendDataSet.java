package com.bazsoft.yaniv;

public class ParsedFriendDataSet {
    private boolean found;
    private boolean added;
    private boolean removed;
    private boolean valid;

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = Boolean.parseBoolean(added);
    }

    public boolean getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = Boolean.parseBoolean(found);
    }

    public boolean getRemoved() {
        return removed;
    }

    public void setRemoved(String removed) {
        this.removed = Boolean.parseBoolean(removed);
    }

}