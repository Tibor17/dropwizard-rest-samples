package x

class ImageNameValidator {
    private final extensions = ['.jpeg', '.jpg', '.png']
    private final Map qa = ['png':'top', 'gif':'high', 'jpeg':'medium', 'jpg':'medium', 'tiff':'low']

    def isValidImageExtension(fileName) {
        def ext = extensions.stream()
                .filter { e -> fileName.endsWith(e) }
                .findFirst();

        ext.isPresent()
    }

    def measurePictureQuality(String fileName) {
        def ext = fileName.substring(1 + fileName.lastIndexOf('.'))
        qa[ext]
    }
}
