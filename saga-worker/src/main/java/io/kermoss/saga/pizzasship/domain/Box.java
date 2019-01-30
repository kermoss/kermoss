package io.kermoss.saga.pizzasship.domain;

import java.util.List;

public class Box {

    private String address;
    private List<String> item;

    public Box() {
    }

    public Box(String address, List<String> item) {
        this.address = address;
        this.item = item;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }
}
