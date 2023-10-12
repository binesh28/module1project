// Cryptology, cryptography and cryptanalysis - Course one

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EncryptDecryptFile {

    private static final Character[] alphabets = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final Character[] specialChars = {'.', ',', '"', '!', '-', ':', '?'};
    private static final int OFFSET = 3;
    private static final String pathToFile = "C:\\Binesh\\myfile.txt";
    private static final String pathToEncryptedFile = "C:\\Binesh\\myencryptedfile.txt";
    private static final String pathToDecryptedFile = "C:\\Binesh\\mydecryptedfile.txt";

    public static void main(String[] args) throws IOException {
        boolean isDone = false;
        do {
            System.out.println("---Please select an option---");
            System.out.println("#1 : Encrypt a file");
            System.out.println("#2 : Decrypt a file");
            System.out.println("#3 : Brute force");
            System.out.println("#4 : Exit");
            System.out.println();
            System.out.print("Enter your choice: ");
            Scanner scanner = new Scanner(System.in);
            int input = Integer.parseInt(scanner.next());
            switch (input) {
                case 1 -> encryptFile();
                case 2 -> decryptFile();
                case 3 -> bruteforce();
                case 4 -> isDone = true;
                default -> throw new IllegalArgumentException("Unexpected value: " + input);
            }
        } while (!isDone);
    }

    private static void encryptFile() throws IOException {
        Path path = Paths.get(pathToFile);
        List<Character> alphabetList = Arrays.asList(alphabets);
        List<Character> specialCharsList = Arrays.asList(specialChars);
        for (String line : Files.readAllLines(path)) {
            StringBuilder newString = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ' ') {
                    if (Character.isAlphabetic(line.charAt(i))) {
                        int originalCharacterPosition = alphabetList.indexOf(Character.toLowerCase(line.charAt(i)));
                        int newCharacterPosition = (originalCharacterPosition + OFFSET) % alphabets.length;
                        newString.append(alphabets[newCharacterPosition]);

                    } else {
                        int originalCharacterPosition = specialCharsList.indexOf(line.charAt(i));
                        int newCharacterPosition = (originalCharacterPosition + OFFSET) % specialChars.length;
                        newString.append(specialChars[newCharacterPosition]);
                    }

                } else {
                    newString.append(line.charAt(i));
                }
            }
            Files.writeString(Paths.get(pathToEncryptedFile), newString.toString().trim() + System.lineSeparator(),
                    StandardOpenOption.APPEND);
        }
        System.out.println("File encrypted successfully.");
        System.out.println();
    }

    private static void decryptFile() throws IOException {
        Path path = Paths.get(pathToEncryptedFile);
        List<Character> alphabetList = Arrays.asList(alphabets);
        List<Character> specialCharsList = Arrays.asList(specialChars);
        for (String line : Files.readAllLines(path)) {
            StringBuilder newString = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ' ') {
                    if (Character.isAlphabetic(line.charAt(i))) {
                        int charPosition = alphabetList.indexOf(Character.toLowerCase(line.charAt(i)));
                        int keyVal = (charPosition - OFFSET) % alphabets.length;
                        if (keyVal < 0) {
                            keyVal = alphabets.length + keyVal;
                        }
                        newString.append(alphabets[keyVal]);

                    } else {
                        int originalCharacterPosition = specialCharsList.indexOf(line.charAt(i));
                        int keyVal = (originalCharacterPosition - OFFSET) % specialChars.length;
                        if (keyVal < 0) {
                            keyVal = specialChars.length + keyVal;
                        }
                        newString.append(specialChars[keyVal]);
                    }

                } else {
                    newString.append(line.charAt(i));
                }
            }
            Files.writeString(Paths.get(pathToDecryptedFile), newString.toString().trim() + System.lineSeparator(),
                    StandardOpenOption.APPEND);
        }
        System.out.println("File decrypted successfully.");
        System.out.println();
    }

    private static void bruteforce() throws IOException {
        Path path = Paths.get(pathToEncryptedFile);
        List<Character> alphabetList = Arrays.asList(alphabets);
        List<String> decodedText = new ArrayList<>();
        Pattern p = Pattern.compile("[^a-z0-9\s+]", Pattern.CASE_INSENSITIVE);
        List<String> specialCharLines = new ArrayList<>();

        for (String line : Files.readAllLines(path)) {
            char[] cipher = line.trim().toLowerCase().toCharArray();
            for (int key = 0; key < alphabets.length; key++) {
                char[] plaintext = new char[cipher.length];
                for (int i = 0; i < cipher.length; i++) {
                    if (cipher[i] != ' ') {
                        if (Character.isAlphabetic(cipher[i])) {
                            plaintext[i] = alphabets[(alphabetList.indexOf(cipher[i]) + key) % alphabets.length];
                        } else {
                            plaintext[i] = cipher[i];
                        }
                    } else {
                        plaintext[i] = cipher[i];
                    }
                }
                decodedText.add(String.valueOf(plaintext));
            }
        }

        for (String text : decodedText) {
            System.out.println("Decoded text: " + text);
            Matcher m = p.matcher(text);
            boolean found = m.find();
            if (found) {
                specialCharLines.add(text);
            }
        }

        if (specialCharLines.size() > 0) {
            decodeSpecialChars(specialCharLines); //decode special characters
        }
        System.out.println("\nBrute force action complete.");
        System.out.println();
    }

    private static void decodeSpecialChars(List<String> decodeList) {
        List<Character> specialCharsList = Arrays.asList(specialChars);
        for (String s : decodeList) {
            char[] text = s.toCharArray();
            char[] newMessage = new char[text.length];
            for (int specialCharKey = 0; specialCharKey < specialChars.length; specialCharKey++) {
                StringBuilder newString = new StringBuilder();
                for (int k = 0; k < text.length; k++) {
                    if ((text[k] != ' ') && (!Character.isAlphabetic(text[k]))) {
                        newMessage[k] = specialChars[(specialCharsList.indexOf(text[k]) + specialCharKey) % specialChars.length];
                    } else {
                        newMessage[k] = text[k];
                    }
                }
                newString.append(String.valueOf(newMessage));
                System.out.println("Decoded text: " + newString);
            }
        }
        System.out.println();
    }
}