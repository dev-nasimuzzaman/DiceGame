package com.dicegame;

import java.security.SecureRandom;
import java.util.Scanner;

public class DiceGame {
    private DiceSet diceSet;
    private SecureRandom random;
    private Scanner scanner;
    private HMACGenerator hmacGenerator;

    public DiceGame(int[][] diceFaces) {
        diceSet = new DiceSet(diceFaces);
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

    private int getUserInput(String prompt, int min, int max, int exclude) {
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
                if (input >= min && input <= max && input != exclude) { // Exclude the specified index
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
        int computerRandom = random.nextInt(6); // 0..5
        String computerThrowKey = hmacGenerator.generateRandomKey();
        String computerThrowHMAC = hmacGenerator.generateHMAC(computerThrowKey, String.valueOf(computerRandom));
        System.out.println("I selected a random value in the range 0..5 (HMAC=" + computerThrowHMAC + ").");
        System.out.println("Add your number modulo 6.");
        int userNumber = getUserInput("Your selection: ", 0, 5, -1); // No exclusion for this input
        System.out.println("My number is " + computerRandom + " (KEY=" + computerThrowKey + ").");
        int computerThrowResult = (computerRandom + userNumber) % 6;
        int computerThrow = diceSet.getDice(computerDice).getFace(computerThrowResult); // Get the face at index computerThrowResult
        System.out.println("The result is " + computerRandom + " + " + userNumber + " = " + computerThrowResult + " (mod 6).");
        System.out.println("My throw is " + computerThrow + ".");

        // Player's throw
        System.out.println("It's time for your throw.");
        int userRandom = random.nextInt(6); // 0..5
        String userThrowKey = hmacGenerator.generateRandomKey();
        String userThrowHMAC = hmacGenerator.generateHMAC(userThrowKey, String.valueOf(userRandom));
        System.out.println("I selected a random value in the range 0..5 (HMAC=" + userThrowHMAC + ").");
        System.out.println("Add your number modulo 6.");
        userNumber = getUserInput("Your selection: ", 0, 5, -1); // No exclusion for this input
        System.out.println("My number is " + userRandom + " (KEY=" + userThrowKey + ").");
        int userThrowResult = (userRandom + userNumber) % 6;
        int userThrow = diceSet.getDice(userDice).getFace(userThrowResult); // Get the face at index userThrowResult
        System.out.println("The result is " + userRandom + " + " + userNumber + " = " + userThrowResult + " (mod 6).");
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
        int computerChoice = random.nextInt(2); // 0..1
        String secretKey = hmacGenerator.generateRandomKey();
        String hmac = hmacGenerator.generateHMAC(secretKey, String.valueOf(computerChoice));

        System.out.println("Let's determine who makes the first move.");
        System.out.println("I selected a random value in the range 0..1 (HMAC=" + hmac + ").");
        System.out.println("Try to guess my selection.");
        System.out.println("0 - 0");
        System.out.println("1 - 1");
        System.out.println("X - exit");
        System.out.println("? - help");
        int userGuess = getUserInput("Your selection: ", 0, 1, -1); // No exclusion for this input

        System.out.println("My selection: " + computerChoice + " (KEY=" + secretKey + ").");

        boolean userStarts = (userGuess == computerChoice);
        if (userStarts) {
            System.out.println("You guessed correctly! You make the first move.");
        } else {
            System.out.println("You guessed incorrectly. I make the first move.");
        }

        // Determine who selects the dice first
        int firstMoveDice;
        if (userStarts) {
            // User makes the first move
            System.out.println("Choose your dice:");
            for (int i = 0; i < diceSet.getNumberOfDiceSets(); i++) {
                System.out.println(i + " - " + diceSet.getDice(i).format());
            }
            System.out.println("X - exit");
            System.out.println("? - help");
            firstMoveDice = getUserInput("Your selection: ", 0, diceSet.getNumberOfDiceSets() - 1, -1);
            System.out.println("You choose the " + diceSet.getDice(firstMoveDice).format() + " dice.");
        } else {
            // Computer makes the first move
            firstMoveDice = random.nextInt(diceSet.getNumberOfDiceSets());
            System.out.println("I choose the " + diceSet.getDice(firstMoveDice).format() + " dice.");
        }

        // Second player selects remaining dice
        int secondMoveDice;
        if (userStarts) {
            // Computer selects a different dice
            System.out.println("I choose my dice:");
            secondMoveDice = random.nextInt(diceSet.getNumberOfDiceSets());
            while (secondMoveDice == firstMoveDice) { // Ensure the computer selects a different dice
                secondMoveDice = random.nextInt(diceSet.getNumberOfDiceSets());
            }
            System.out.println("I choose the " + diceSet.getDice(secondMoveDice).format() + " dice.");
        } else {
            // User selects a different dice
            System.out.println("Choose your dice:");
            for (int i = 0; i < diceSet.getNumberOfDiceSets(); i++) {
                if (i != firstMoveDice) { // Exclude the computer's dice
                    System.out.println(i + " - " + diceSet.getDice(i).format());
                }
            }
            System.out.println("X - exit");
            System.out.println("? - help");
            secondMoveDice = getUserInput("Your selection: ", 0, diceSet.getNumberOfDiceSets() - 1, firstMoveDice);
            System.out.println("You choose the " + diceSet.getDice(secondMoveDice).format() + " dice.");
        }

        // Play the round
        if (userStarts) {
            playRound(secondMoveDice, firstMoveDice); // User's dice first, then computer's dice
        } else {
            playRound(firstMoveDice, secondMoveDice); // Computer's dice first, then user's dice
        }

        System.out.println("Thanks for playing! Goodbye.");
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Error: At least 3 dice sets are required.");
            System.out.println("Usage: java DiceGame <dice1> <dice2> <dice3> ...");
            System.out.println("Example: java DiceGame 2,2,4,4,9,9 6,8,1,1,8,6 7,5,3,7,5,3");
            System.exit(1);
        }

        int[][] diceFaces = new int[args.length][];
        for (int i = 0; i < args.length; i++) {
            String[] parts = args[i].split(",");
            diceFaces[i] = new int[parts.length];
            for (int j = 0; j < parts.length; j++) {
                try {
                    diceFaces[i][j] = Integer.parseInt(parts[j]);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid dice face value '" + parts[j] + "'.");
                    System.out.println("All dice faces must be integers.");
                    System.exit(1);
                }
            }
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            DiceGame game = new DiceGame(diceFaces);
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