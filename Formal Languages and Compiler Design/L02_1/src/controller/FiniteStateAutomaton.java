package controller;

import model.State;
import repository.Repository;
import util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class FiniteStateAutomaton {
    private Repository states;
    private Set<String> alphabet;
    private Map<Pair<State, String>, State> transitions;
    private State initialState;

    /**
     * Constructor for FiniteStateAutomaton
     * @param file - can be null
     */
    public FiniteStateAutomaton(String file) {
        this.states = new Repository();
        this.alphabet = new HashSet<>();
        this.transitions = new HashMap<>();
        this.initialState = null;
        if (file != null) {
            this.loadFromFile(file);
        }
    }

    private void loadFromFile(String file) {
        try(
                FileInputStream fin = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin))
        ) {
            String line = null;

            int numberOfStates = 0;
            int numberOfTransitions = 0;

            // read number of states (first line)
            line = reader.readLine();
            numberOfStates = Integer.parseInt(line);

            // read number of transitions (second line)
            line = reader.readLine();
            numberOfTransitions = Integer.parseInt(line);

            // process states (next 'numberOfStates' lines)
            for (int i = 0; i < numberOfStates; i++) {
                line = reader.readLine();

                String[] tokens = line.split(" ");
                boolean isInitial = false;
                boolean isFinal = false;

                if (tokens.length > 3) {
                    System.err.println("Invalid file format.");
                    return;
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

                if (this.states.contains(tokens[0])) {
                    System.err.println("Duplicate state found in file: " + tokens[0]);
                    return;
                }

                this.states.add(new State(tokens[0], isInitial, isFinal));

                if (isInitial) {
                    if (this.initialState != null) {
                        System.err.println("Invalid file format. Two invalid initial states found.");
                        return;
                    }

                    this.initialState = this.states.get(tokens[0]);
                }
            }

            if (this.initialState == null) {
                System.err.println("Invalid file format. No initial state found.");
                return;
            }

            // process transitions (next 'numberOfTransitions' lines)
            for (int i = 0; i < numberOfTransitions; i++) {
                line = reader.readLine();

                String[] tokens = line.split(" ");

                if (tokens.length != 3) {
                    System.err.println("Invalid file format.");
                    return;
                }

                if (!this.states.contains(tokens[0]) || !this.states.contains(tokens[2])) {
                    System.err.println("Unknown states found in transition list.");
                    return;
                }

                this.transitions.put(new Pair<>(this.states.get(tokens[0]), tokens[1]), this.states.get(tokens[2]));
                this.alphabet.add(tokens[1]);
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean addState(String label, boolean isInitial, boolean isFinal) {
        State state = new State(label, isInitial, isFinal);

        if (this.states.contains(label)) {
            return false;
        }

        if (isInitial) {
            if (this.initialState != null) {
                return false;
            }

            this.initialState = state;
        }

        this.states.add(state);
        return true;
    }

    public boolean addTransition(String stateFrom, String stateTo, String label) {
        if (!this.states.contains(stateFrom) || !this.states.contains(stateTo)) {
            return false;
        }

        this.transitions.put(new Pair<>(this.states.get(stateFrom), label), this.states.get(stateTo));
        this.alphabet.add(label);
        return true;
    }

    public Set<State> getTheSetOfStates() {
        return this.states.getAll();
    }

    public Set<State> getTheSetOfFinalStates() {
        return this.states.getFinalStates();
    }

    public Set<String> getAlphabet() {
        return this.alphabet;
    }

    public Map<Pair<State, String>, State> getTransitions() {
        return this.transitions;
    }

    /**
     * Verifies if a sequence is accepted or not by the deterministic finite automaton
     * @param sequence cannot be null; for empty sequence give empty string ""
     * @return true|false
     */
    public boolean isAccepted(String sequence) {
        if (sequence == null) {
            return false;
        }

        if (this.initialState == null) {
            return false;
        }

        State currentState = this.initialState;

        for (int i = 0; i < sequence.length(); i++) {
            String label = "" + sequence.charAt(i);

            if (!this.transitions.containsKey(new Pair<>(currentState, label))) {
                return false;
            }

            currentState = this.transitions.get(new Pair<>(currentState, label));
        }

        return currentState.isFinal();
    }

    /**
     * Determines the longest prefix of a sequence that is accepted by the automaton.
     * @param sequence cannot be null; for empty sequence give empty string ""
     * @return the longest prefix of a sequence that is accepted by the automaton, or null if there isn't any
     */
    public String getLongestAcceptedPrefix(String sequence) {
        if (sequence == null || this.initialState == null) {
            return null;
        }

        String result = null;
        State currentState = this.initialState;

        for (int i = 0; i < sequence.length(); i++) {
            String label = "" + sequence.charAt(i);

            if (currentState.isFinal()) {
                result = sequence.substring(0, i);
            }

            if (!this.transitions.containsKey(new Pair<>(currentState, label))) {
                return result;
            }

            currentState = this.transitions.get(new Pair<>(currentState, label));
        }

        if (currentState.isFinal()) {
            result = "" + sequence;
        }

        return result;
    }
}
