package x

class ImageNameValidator {
    private final extensions = ['.jpeg', '.jpg', '.png']

    boolean isValidImageExtension(fileName) {
        def ext = extensions.stream()
                .filter { e -> fileName.endsWith(e) }
                .findFirst();

        ext.isPresent()
    }
}
