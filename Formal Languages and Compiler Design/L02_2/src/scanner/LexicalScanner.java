package scanner;

import fa.FiniteStateAutomaton;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class LexicalScanner {
    private static final String CODIFICATION_FILE = new File("resources/codification.txt")
            .getAbsolutePath().toString();

    private FiniteStateAutomaton identifierFA;
    private FiniteStateAutomaton integerFA;
    private FiniteStateAutomaton floatingPointFA;
    private FiniteStateAutomaton stringFA;
    private FiniteStateAutomaton languageFA;
    private Codification codification;

    private ProgramInternalForm pif;
    private SymbolTable st;

    public LexicalScanner(String sourceFile) {
        codification = new Codification(CODIFICATION_FILE);

        languageFA = AutomataGenerator.languageFSA(CODIFICATION_FILE);
        identifierFA = AutomataGenerator.identifierFSA();
        integerFA = AutomataGenerator.integerFSA();
        floatingPointFA = AutomataGenerator.floatingPointFSA();
        stringFA = AutomataGenerator.stringFSA();

        try {
            this.initPIFandST(sourceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPIFandST(String sourceFile) throws Exception {
        this.pif = new ProgramInternalForm();
        this.st = new SymbolTable();

        // TODO: implement this...

        try (BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)))) {
            List<String> lines = br.lines().collect(Collectors.toList());

            for (int i = 0; i < lines.size(); i++) {
                String s = lines.get(i).trim();

                while (!"".equals(s)) {
                    s = s.trim();

                    String longestLanguageAtom = languageFA.getLongestAcceptedPrefix(s);
                    String longestFloatingPoint = floatingPointFA.getLongestAcceptedPrefix(s);
                    String longestInteger = integerFA.getLongestAcceptedPrefix(s);
                    String longestString = stringFA.getLongestAcceptedPrefix(s);
                    String longestIdentifier = identifierFA.getLongestAcceptedPrefix(s);

                    if (longestLanguageAtom.length() == 0 && longestFloatingPoint.length() == 0 &&
                            longestInteger.length() == 0 && longestString.length() == 0 &&
                            longestIdentifier.length() == 0)
                    {
                        this.st = null;
                        this.pif = null;
                        throw new Exception(String.format("Line %d: unexpected token: %s\n", i, s));
                    }

                    if (    // language atom is best
                            longestLanguageAtom.length() >= longestFloatingPoint.length()
                            && longestLanguageAtom.length() >= longestInteger.length()
                            && longestLanguageAtom.length() >= longestString.length()
                            && longestLanguageAtom.length() >= longestIdentifier.length()
                            ) {
                        int code = this.codification.getCodeOfAtom(longestLanguageAtom);
                        this.pif.add(code, null);
                        s = s.substring(longestLanguageAtom.length());
                    } else if ( // floating point is best
                            longestFloatingPoint.length() >= longestLanguageAtom.length()
                            && longestFloatingPoint.length() >= longestInteger.length()
                            && longestFloatingPoint.length() >= longestString.length()
                            && longestFloatingPoint.length() >= longestIdentifier.length()
                            ) {
                        int code = this.codification.getCodeOfConstant();
                        int ind = this.st.poz(longestFloatingPoint);
                        this.pif.add(code, ind);
                        s = s.substring(longestFloatingPoint.length());
                    } else if ( // integer is best
                            longestInteger.length() >= longestLanguageAtom.length()
                            && longestInteger.length() >= longestFloatingPoint.length()
                            && longestInteger.length() >= longestString.length()
                            && longestInteger.length() >= longestIdentifier.length()
                            ) {
                        int code = this.codification.getCodeOfConstant();
                        int ind = this.st.poz(longestInteger);
                        this.pif.add(code, ind);
                        s = s.substring(longestInteger.length());
                    } else if ( // string is best
                            longestString.length() >= longestLanguageAtom.length()
                            && longestString.length() >= longestFloatingPoint.length()
                            && longestString.length() >= longestInteger.length()
                            && longestString.length() >= longestIdentifier.length()
                            ) {
                        int code = this.codification.getCodeOfString();
                        int ind = this.st.poz(longestString);
                        this.pif.add(code, ind);
                        s = s.substring(longestString.length());
                    } else if ( // identifier is best
                            longestIdentifier.length() >= longestLanguageAtom.length()
                            && longestIdentifier.length() >= longestFloatingPoint.length()
                            && longestIdentifier.length() >= longestInteger.length()
                            && longestIdentifier.length() >= longestString.length()
                            ) {
                        int code = this.codification.getCodeOfIdentifier();
                        int ind = this.st.poz(longestIdentifier);
                        this.pif.add(code, ind);
                        s = s.substring(longestIdentifier.length());
                    }  else {
                        this.st = null;
                        this.pif = null;
                        throw new Exception(String.format("Line %d: unexpected token: %s\n", i, s));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProgramInternalForm getPIF() {
        return this.pif;
    }

    public SymbolTable getST() {
        return this.st;
    }
}
