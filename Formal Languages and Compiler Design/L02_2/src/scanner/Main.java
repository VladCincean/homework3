package scanner;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Main <source_file>");
        }

        String sf = new File(args[0]).getAbsolutePath().toString();

        LexicalScanner lexicalScanner = new LexicalScanner(sf);

        ProgramInternalForm pif = lexicalScanner.getPIF();
        SymbolTable st = lexicalScanner.getST();

        System.out.println(pif.toString());

        System.out.println(st.toString());
    }
}
