package com.per.epx.easytrain.models;

public class RelyPackage<T> {
    private boolean isValid;
    private String rawReceived;
    private T converted;

    public RelyPackage() {
    }

    public RelyPackage(boolean isValid, String rawReceived, T converted) {
        this.isValid = isValid;
        this.rawReceived = rawReceived;
        this.converted = converted;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getRawReceived() {
        return rawReceived;
    }

    public void setRawReceived(String rawReceived) {
        this.rawReceived = rawReceived;
    }

    public T getConverted() {
        return converted;
    }

    public void setConverted(T converted) {
        this.converted = converted;
    }
}
