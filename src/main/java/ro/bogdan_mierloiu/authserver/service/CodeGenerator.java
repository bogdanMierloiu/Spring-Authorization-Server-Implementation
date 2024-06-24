package ro.bogdan_mierloiu.authserver.service;


import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class CodeGenerator {

    private CodeGenerator() {
    }

    // class used to generate a random alphanumeric string
    // used to generate the codes
    public static String generateRandomCode() {
        // create a string containing all the upper case and lower case letters and
        // digits
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // build the code by appending random characters from the alphanumeric string

        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(alphaNumeric.length());
            char randomChar = alphaNumeric.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

}
