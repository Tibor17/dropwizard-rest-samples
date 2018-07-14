package x

class Book {
    def final title

    Book(title) {
        this.title = title
    }

    List<String> findSimilarTitles() {
        def books = []
        books += 'Murder Is Easy'
        books += title
        books += 'A Murder is Announced'
        books
    }
}
