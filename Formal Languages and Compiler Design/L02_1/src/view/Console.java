package view;

import controller.FiniteStateAutomaton;
import model.State;
import util.Pair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Console {
    private FiniteStateAutomaton fsa;
    private Scanner scanner;

    public Console() {
        this.fsa = null;
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            int option = this.mainMenu();

            switch (option) {
                case 1:
                    readFromKeyboard();
                    break;
                case 2:
                    readFromFile();
                    break;
                case 3:
                    displaySetOfStates();
                    break;
                case 4:
                    displayAlphabet();
                    break;
                case 5:
                    displayTransitions();
                    break;
                case 6:
                    displaySetOfFinalStates();
                    break;
                case 7:
                    verifyIfASequenceIsAcceptedOrNot();
                    break;
                case 8:
                    determineTheLongestPrefixOfAnAcceptedSequence();
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
            }
        }
    }

    /**
     * Prints the main menu and provides the user input
     * @return user option
     */
    private int mainMenu() {
        System.out.println("Please choose an option: ");
        System.out.println("1 - read FA from keyboard");
        System.out.println("2 - read FA from file");
        System.out.println("3 - display the set of states");
        System.out.println("4 - display the alphabet");
        System.out.println("5 - display the transitions");
        System.out.println("6 - display the set of final states");
        System.out.println("7 - verify if a sequence is accepted or not");
        System.out.println("8 - determine the longest prefix of a sequence that is accepted");
        System.out.println("0 - exit");

        int option = -1;
        while (option < 0 || option > 8) {
            System.out.print("Your option: ");
            while (!scanner.hasNextInt()) {
                System.out.println("You must give a number between 0 and 8 inclusively");
                System.out.print("Your option: ");
                scanner.nextLine();
            }
            option = scanner.nextInt();

            if (option < 0 || option > 8) {
                System.out.println("Wrong!");
            }
        }

        return option;
    }

    private void readFromKeyboard() {
        this.fsa = new FiniteStateAutomaton(null);

        int numberOfStates = 0;
        int numberOfTransitions = 0;

        // read number of states
        System.out.print("Number of states: ");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print("Number of states: ");
        }
        numberOfStates = scanner.nextInt();
        scanner.nextLine();

        // read states
        System.out.println("Please input the states, one per line.");
        System.out.println("If a state is initial or final please specify 'initial' and/or 'final' on the same line");
        for (int i = 0; i < numberOfStates; i++) {
            String line = scanner.nextLine();

            String[] tokens = line.split(" ");
            boolean isInitial = false;
            boolean isFinal = false;

            if (tokens.length > 3) {
                System.err.println("Wrong input! Please try again!");
                i--;
                continue;
            }

            Set<String> special = Arrays.stream(tokens)
                    .filter(s -> s.equals("initial") || s.equals("final"))
                    .collect(Collectors.toSet());

            if (special.contains("initial")) {
                isInitial = true;
            }

            if (special.contains("final")) {
                isFinal = true;
            }

            if (!this.fsa.addState(tokens[0], isInitial, isFinal)) {
                System.out.println("Wrong input. Cannot add this state: " + tokens[0]);
                i--;
                continue;
            }
        }

        // read number of transitions
        System.out.print("Number of transitions: ");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print("Number of transitions: ");
        }
        numberOfTransitions = scanner.nextInt();
        scanner.nextLine();

        // read transitions
        System.out.println("Please input the transitions, one per line, by the following format:");
        System.out.println("\t<state-from> <label> <stsate-to>");
        for (int i = 0; i < numberOfTransitions; i++) {
            String line = scanner.nextLine();

            String[] tokens = line.split(" ");

            if (tokens.length != 3) {
                System.err.println("Wrong input! Please try again!");
                i--;
                continue;
            }

            if (!this.fsa.addTransition(tokens[0], tokens[2], tokens[1])) {
                System.out.println("Wrong input. Cannot add this transition.");
                i--;
                continue;
            }
        }

        System.out.println("Finite automaton succesfully read :)");
    }

    private void readFromFile() {
        Pattern pattern = Pattern.compile("(\\.\\.\\\\)|(\\.\\./)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = null;

        System.out.print("File: ");
        String fileName = scanner.next();
        matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            System.out.println("Wrong input! Directory traversal attack attempt detected!");
            return;
        }

        if (!Files.exists(Paths.get(fileName))) {
            System.out.println("Inexistent file!");
            return;
        }

        this.fsa = new FiniteStateAutomaton(fileName);

        System.out.println("Finite automaton successfully read :)");
    }

    private void displaySetOfStates() {
        if (null == this.fsa) {
            System.err.println("FA not loaded.");
            return;
        }

        System.out.println("The set of states: ");
        this.fsa.getTheSetOfStates()
                .forEach(System.out::println);
        System.out.println("---");
    }

    private void displayAlphabet() {
        if (null == this.fsa) {
            System.err.println("FA not loaded.");
            return;
        }

        System.out.print("The alphabet: ");
        this.fsa.getAlphabet()
                .forEach(System.out::print);
        System.out.print("\n");
    }

    private void displayTransitions() {
        if (null == this.fsa) {
            System.err.println("FA not loaded.");
            return;
        }

        System.out.println("The set of transitions: ");
        this.fsa.getTransitions()
                .forEach(new BiConsumer<Pair<State, String>, State>() {
                    @Override
                    public void accept(Pair<State, String> stateStringPair, State state) {
                        System.out.println(stateStringPair.getFirst().getLabel() + " - " + stateStringPair.getSecond()
                                + " - " + state.getLabel()
                        );
                    }
                });
        System.out.println("---");
    }

    private void displaySetOfFinalStates() {
        if (null == this.fsa) {
            System.err.println("FA not loaded.");
            return;
        }

        System.out.println("The set of states: ");
        this.fsa.getTheSetOfFinalStates()
                .forEach(System.out::println);
        System.out.println("---");
    }

    private void verifyIfASequenceIsAcceptedOrNot() {
        if (null == this.fsa) {
            System.err.println("FA not loaded.");
            return;
        }

        System.out.print("The sequence: ");
        String sequence = scanner.next();

        if (this.fsa.isAccepted(sequence)) {
            System.out.println("Yes, it is accepted.");
        } else {
            System.out.println("No, it is not accepted.");
        }
    }

    private void determineTheLongestPrefixOfAnAcceptedSequence() {
        if (null == this.fsa) {
            System.err.println("FA not loaded.");
            return;
        }

        System.out.print("The sequence: ");
        String sequence = scanner.next();

        String longestPrefix = this.fsa.getLongestAcceptedPrefix(sequence);

        if (longestPrefix == null) {
            System.err.println("Cannot get the result.");
            return;
        }

        System.out.println("Longest prefix: " + longestPrefix);
    }
}
