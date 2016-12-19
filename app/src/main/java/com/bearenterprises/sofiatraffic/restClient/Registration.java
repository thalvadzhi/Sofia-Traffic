package com.bearenterprises.sofiatraffic.restClient;

/**
 * Created by thalv on 19-Dec-16.
 */

public class Registration {
    private String id, name, createdAt, updatedAt;
    private boolean disabled;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDisabled() {
        return disabled;
    }
}
