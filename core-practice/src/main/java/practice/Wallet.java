package practice;

public class Wallet {

    private final String owner;
    private final String currency;

    public Wallet(String owner, String currency) {
        this.owner = owner;
        this.currency = currency;
    }

    public String getOwner() {
        return owner;
    }

    public String getCurrency() {
        return currency;
    }
}
