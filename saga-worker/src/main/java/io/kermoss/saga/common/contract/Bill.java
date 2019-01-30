package io.kermoss.saga.common.contract;

public class Bill {
    int price;
    String invoiceRef;

    public Bill(int price, String invoiceRef) {
        this.price = price;
        this.invoiceRef = invoiceRef;
    }

    public Bill() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "price=" + price +
                ", invoiceRef='" + invoiceRef + '\'' +
                '}';
    }
}
