package x

import spock.lang.*

@Title('This is just a Fake test to test spock-reports')
@Narrative("""
As a user
I want foo
So that bar""")
class AcceptanceFeatureIT extends Specification {

    def 'charging a credit card - happy path'() {
        def money = 150

        given: 'a billing service and a customer with a valid credit card'
        CreditCardBilling billing = new CreditCardBilling();
        Client client = new Client();

        when: "client buys something with ${money} dollars"
        billing.charge(client, money);

        then: 'we expect the transaction to be recorded'
        billing.getCurrentRevenue() == 10 * money
    }

    def 'charging a credit card - two transactions'() {
        given: 'a billing service ready to accept payments'
        CreditCardBilling billing = new CreditCardBilling();

        and: 'two customers with valid credit cards'
        Client client1 = new Client();
        Client client2 = new Client();

        when: 'first client buys something with 150 dollars'
        billing.charge(client1,150);

        then: 'we expect the transaction to be recorded'
        billing.getCurrentRevenue() == 150

        when: 'second client buys something with 100 dollars'
        billing.charge(client2,100);

        then: 'we expect the transaction to be recorded'
        billing.getCurrentRevenue() == 250
    }

    def 'find books with similar titles'() {
        given: 'A book that contains the word murder'
        Book book = new Book('The Murder on the Links')

        when: 'we search similar books'
        List<String> similar = book.findSimilarTitles()

        then: 'similar books should have murder in the title'
        !similar.isEmpty()
        similar.get(0) == 'Murder on the Orient Express'
    }

    @Unroll('Running image #pictureFile with result #validPicture')
    def 'Valid images are PNG and JPEG files'() {
        given: 'an image extension checker'
        ImageNameValidator validator = new ImageNameValidator()

        expect: 'that only valid filenames are accepted'
        validator.isValidImageExtension(pictureFile) == validPicture

        where: 'sample image names are'
        pictureFile        | validPicture
        'scenery.jpg'      | true
        'house.jpeg'       | true
        'car.png'          | true
        'sky.tiff'         | false
        'dance_bunny.gif'  | false
    }

    @PendingFeature
    def 'Future feature'() {
        when:
        'the feature is ready'
        then:
        'the annotation will be removed'
        throw new RuntimeException('Not ready')
    }
}
