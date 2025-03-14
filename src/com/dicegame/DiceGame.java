package com.dicegame;

import java.security.SecureRandom;
import java.util.Scanner;

public class DiceGame {
    private DiceSet diceSet;
    private SecureRandom random;
    private Scanner scanner;
    private HMACGenerator hmacGenerator;

    public DiceGame() {
        diceSet = new DiceSet();
        random = new SecureRandom();
        scanner = new Scanner(System.in);
        hmacGenerator = new HMACGenerator();
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("0-5 - Select a number");
        System.out.println("X - Exit the game");
        System.out.println("? - Show this help message");
    }

    private int getUserInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("X")) {
                System.out.println("Exiting the game. Goodbye!");
                scanner.close();
                System.exit(0);
            } else if (userInput.equalsIgnoreCase("?")) {
                printHelp();
                continue;
            }
            try {
                int input = Integer.parseInt(userInput);
                if (input >= min && input <= max) {
                    return input;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    private void playRound(int computerDice, int userDice) throws Exception {
        // Computer's throw
        System.out.println("It's time for my throw.");
        int computerRandom = random.nextInt(6);
        String computerThrowKey = hmacGenerator.generateRandomKey();
        String computerThrowHMAC = hmacGenerator.generateHMAC(computerThrowKey, String.valueOf(computerRandom));
        System.out.println("I selected a random value in the range 0..5 (HMAC=" + computerThrowHMAC + ").");
        System.out.println("Add your number modulo 6.");
        int userNumber = getUserInput("Your selection: ", 0, 5);
        System.out.println("My number is " + computerRandom + " (KEY=" + computerThrowKey + ").");
        int computerThrowResult = (computerRandom + userNumber) % 6;
        System.out.println("The result is " + computerRandom + " + " + userNumber + " = " + computerThrowResult + " (mod 6).");
        int computerThrow = diceSet.getDice(computerDice).roll(random);
        System.out.println("My throw is " + computerThrow + ".");

        // Player's throw
        System.out.println("It's time for your throw.");
        int userRandom = random.nextInt(6);
        String userThrowKey = hmacGenerator.generateRandomKey();
        String userThrowHMAC = hmacGenerator.generateHMAC(userThrowKey, String.valueOf(userRandom));
        System.out.println("I selected a random value in the range 0..5 (HMAC=" + userThrowHMAC + ").");
        System.out.println("Add your number modulo 6.");
        userNumber = getUserInput("Your selection: ", 0, 5);
        System.out.println("My number is " + userRandom + " (KEY=" + userThrowKey + ").");
        int userThrowResult = (userRandom + userNumber) % 6;
        System.out.println("The result is " + userRandom + " + " + userNumber + " = " + userThrowResult + " (mod 6).");
        int userThrow = diceSet.getDice(userDice).roll(random);
        System.out.println("Your throw is " + userThrow + ".");

        // Determine the winner
        if (userThrow > computerThrow) {
            System.out.println("You win (" + userThrow + " > " + computerThrow + ")!");
        } else if (userThrow < computerThrow) {
            System.out.println("I win (" + userThrow + " < " + computerThrow + ")!");
        } else {
            System.out.println("It's a tie (" + userThrow + " == " + computerThrow + ")!");
        }
    }

    public void startGame() throws Exception {
        // Generate random choice (0 or 1) for first move
        int computerChoice = random.nextInt(2);
        String secretKey = hmacGenerator.generateRandomKey();
        String hmac = hmacGenerator.generateHMAC(secretKey, String.valueOf(computerChoice));

        System.out.println("Let's determine who makes the first move.");
        System.out.println("I selected a random value in the range 0..1 (HMAC=" + hmac + ").");
        System.out.println("Try to guess my selection.");
        System.out.println("0 - 0");
        System.out.println("1 - 1");
        System.out.println("X - exit");
        System.out.println("? - help");
        int userGuess = getUserInput("Your selection: ", 0, 1);

        System.out.println("My selection: " + computerChoice + " (KEY=" + secretKey + ").");

        boolean computerStarts = userGuess != computerChoice;

        // Computer selects dice first
        int computerDice = random.nextInt(diceSet.getNumberOfDiceSets());
        System.out.println("I make the first move and choose the " + diceSet.getDice(computerDice).format() + " dice.");

        // Player selects remaining dice
        System.out.println("Choose your dice:");
        for (int i = 0; i < diceSet.getNumberOfDiceSets(); i++) {
            if (i != computerDice) {
                System.out.println(i + " - " + diceSet.getDice(i).format());
            }
        }
        System.out.println("X - exit");
        System.out.println("? - help");
        int userDice = getUserInput("Your selection: ", 0, diceSet.getNumberOfDiceSets() - 1);

        System.out.println("You choose the " + diceSet.getDice(userDice).format() + " dice.");

        // Play the round
        playRound(computerDice, userDice);

        System.out.println("Thanks for playing! Goodbye.");
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            DiceGame game = new DiceGame();
            game.startGame();

            System.out.print("Do you want to play again? (Y/N): ");
            String playAgain = scanner.nextLine().trim();
            if (!playAgain.equalsIgnoreCase("Y")) {
                System.out.println("Exiting the game. Goodbye!");
                scanner.close();
                break;
            }
        }
    }
}