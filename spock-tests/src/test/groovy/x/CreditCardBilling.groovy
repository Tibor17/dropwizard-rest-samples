package x

class CreditCardBilling {
    private dollars = 0;

    def charge(Client client, int dollars) {
        this.dollars += dollars
    }

    def getCurrentRevenue() {
        return dollars
    }
}
