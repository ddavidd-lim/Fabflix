package Entities;

import java.sql.Date;

public class CreditCard {

    private final String cardNumber;
    private final String firstName;
    private final String lastName;
    private final Date expiration;

    public CreditCard(String cardNumber, String firstName, String lastName, Date expiration) {
        this.cardNumber = cardNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = expiration;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getExpiration() {
        return expiration;
    }
}
