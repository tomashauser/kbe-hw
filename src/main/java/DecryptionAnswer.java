public class DecryptionAnswer {
    private final String key;
    private final String decryptedMessage;

    public DecryptionAnswer(String key, String decryptedMessage) {
        this.key = key;
        this.decryptedMessage = decryptedMessage;
    }

    public String getKey() {
        return key;
    }

    public String getDecryptedMessage() {
        return decryptedMessage;
    }
}


