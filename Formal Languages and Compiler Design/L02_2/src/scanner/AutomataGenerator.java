package scanner;

import fa.FiniteStateAutomaton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AutomataGenerator {

    private AutomataGenerator() {
    }

    public static FiniteStateAutomaton identifierFSA() {
        FiniteStateAutomaton fsa = new FiniteStateAutomaton(null);

        // init states
        fsa.addState("q0", true, false); // initial state
        for (int i = 1; i <= 250; i++) {
            fsa.addState("q" + Integer.toString(i), false, true); // all can be final states
        }

        // init transitions
        // upper-case letters
        for (char a = 'A'; a <= 'Z'; a++) {
            for (int i = 0; i < 250; i++) {
                fsa.addTransition(
                        "q" + Integer.toString(i),
                        "q" + Integer.toString(i + 1),
                        Character.toString(a)
                );
            }
        }

        // lower-case letters
        for (char a = 'a'; a <= 'z'; a++) {
            for (int i = 0; i < 250; i++) {
                fsa.addTransition(
                        "q" + Integer.toString(i),
                        "q" + Integer.toString(i + 1),
                        Character.toString(a)
                );
            }
        }

        // digits /// first character in the sequence can't be a digit
        for (char a = '0'; a <= '9'; a++) {
            for (int i = 1; i < 250; i++) {
                fsa.addTransition(
                        "q" + Integer.toString(i),
                        "q" + Integer.toString(i + 1),
                        Character.toString(a)
                );
            }
        }

        // underscore
        for (int i = 0; i < 250; i++) {
            fsa.addTransition(
                    "q" + Integer.toString(i),
                    "q" + Integer.toString(i + 1),
                    "_"
            );
        }

        return fsa;
    }

    public static FiniteStateAutomaton integerFSA() {
        FiniteStateAutomaton fsa = new FiniteStateAutomaton(null);

        // init states
        fsa.addState("q0", true, false);
        fsa.addState("q1", false, false);
        fsa.addState("q2", false, true);
        fsa.addState("q3", false, true);
        fsa.addState("q4", false, true);
        fsa.addState("q5", false, false);
        fsa.addState("q6", false, true);

        // init transitions
        // decimal
        fsa.addTransition("q0", "q1", "+");
        fsa.addTransition("q0", "q1", "-");
        for (int i = 1; i <= 9; i++) {
            fsa.addTransition("q0", "q2", Integer.toString(i));
            fsa.addTransition("q1", "q2", Integer.toString(i));
            fsa.addTransition("q2", "q2", Integer.toString(i));
        }
        fsa.addTransition("q2", "q2", "0");

        // 0
        fsa.addTransition("q0", "q2", "0");

        // octal
        for (int i = 0; i <= 7; i++) {
            fsa.addTransition("q3", "q4", Integer.toString(i));
            fsa.addTransition("q4", "q4", Integer.toString(i));
        }

        // hexadecimal
        fsa.addTransition("q3", "q5", "x");
        fsa.addTransition("q3", "q5", "X");
        for (int i = 0; i <= 9; i++) {
            fsa.addTransition("q5", "q6", Integer.toString(i));
            fsa.addTransition("q6", "q6", Integer.toString(i));
        }
        for (char a = 'a'; a <= 'f'; a++) {
            fsa.addTransition("q5", "q6", Character.toString(a));
            fsa.addTransition("q6", "q6", Character.toString(a));
        }
        for (char a = 'A'; a <= 'F'; a++) {
            fsa.addTransition("q5", "q6", Character.toString(a));
            fsa.addTransition("q6", "q6", Character.toString(a));
        }

        return fsa;
    }

    public static FiniteStateAutomaton floatingPointFSA() {
        FiniteStateAutomaton fsa = new FiniteStateAutomaton(null);

        // init states
        fsa.addState("q0", true, false);
        fsa.addState("q1", false, false);
        fsa.addState("q2", false, false);
        fsa.addState("q3", false, false);
        fsa.addState("q4", false, true);
        fsa.addState("q5", false, false);
        fsa.addState("q6", false, false);
        fsa.addState("q7", false, true);

        // init transitions
        // sign
        fsa.addTransition("q0", "q1", "+");
        fsa.addTransition("q0", "q1", "-");

        // digits
        for (int i = 0; i <= 9; i++) {
            fsa.addTransition("q0", "q2", Integer.toString(i));
            fsa.addTransition("q1", "q2", Integer.toString(i));
            fsa.addTransition("q2", "q2", Integer.toString(i));
        }

        // decimal point
        fsa.addTransition("q0", "q3", ".");
        fsa.addTransition("q1", "q3", ".");
        fsa.addTransition("q2", "q3", ".");

        // fraction digits
        for (int i = 0; i <= 9; i++) {
            fsa.addTransition("q3", "q4", Integer.toString(i));
            fsa.addTransition("q4", "q4", Integer.toString(i));
        }

        // exponent
        // exponent mark
        fsa.addTransition("q4", "q5", "e");
        fsa.addTransition("q4", "q5", "E");

        // exponent sign
        fsa.addTransition("q5", "q6", "+");
        fsa.addTransition("q5", "q6", "-");

        // exponent digits
        for (int i = 0; i <= 9; i++) {
            fsa.addTransition("q5", "q7", Integer.toString(i));
            fsa.addTransition("q6", "q7", Integer.toString(i));
            fsa.addTransition("q7", "q7", Integer.toString(i));
        }

        return fsa;
    }

    public static FiniteStateAutomaton stringFSA() {
        FiniteStateAutomaton fsa = new FiniteStateAutomaton(null);

        // init states
        fsa.addState("q0", true, false);
        fsa.addState("q1", false, false);
        fsa.addState("q2", false, true);

        // init transitions
        fsa.addTransition("q0", "q1", "\"");
        for (char a = ' '; a <= '~'; a++) {
            if (a != '\"') {
                fsa.addTransition("q1", "q1", Character.toString(a));
            }
        }
        fsa.addTransition("q1", "q2", "\"");

        return fsa;
    }

    public static FiniteStateAutomaton languageFSA(String codificationFile) {
        FiniteStateAutomaton fsa = new FiniteStateAutomaton(null);

        // init initial and final states
        fsa.addState("#q0", true, false);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(codificationFile)))) {
            br.lines()
                    .filter(line -> line.trim().length() > 0)
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> !line.startsWith("~"))
                    .map(line -> line.split("@")[0].trim())
                    .forEach(a -> fsa.addState(a, false, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // init intermediary states and transitions
        String initialState = "#q0";
        fsa.addState(initialState, true, false);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(codificationFile)))) {
            br.lines()
                    .filter(line -> line.trim().length() > 0)
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> !line.startsWith("~"))
                    .map(line -> line.split("@")[0].trim())
                    .forEach(atom -> {
                        fsa.addState(atom.substring(0, 1), false, false);
                        fsa.addTransition(initialState, atom.substring(0, 1), atom.substring(0, 1));

                        for (int i = 1; i < atom.length(); i++) {
                            fsa.addState(atom.substring(0, i + 1), false, false); // will not add if already exists
                            fsa.addTransition(
                                    atom.substring(0, i),
                                    atom.substring(0, i + 1),
                                    atom.substring(i, i + 1)
                            );
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fsa;
    }
}
