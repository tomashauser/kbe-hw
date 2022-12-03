package utils;

public class CipherTextAndIv {
    public byte[] cipherText;
    public byte[] iv;

    public CipherTextAndIv(byte[] cipherText, byte[] iv) {
        this.cipherText = cipherText;
        this.iv = iv;
    }
}
