package io.kermoss.saga.common.contract;

import java.util.List;

public class ToDilever {
    private String address;
    private List<String> item;

    public ToDilever() {
    }

    public ToDilever(String address, List<String> item) {
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

    @Override
    public String toString() {
        return "ToDilever{" +
                "address='" + address + '\'' +
                ", item=" + item +
                '}';
    }
}
