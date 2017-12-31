package scanner;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Codification {
    private Map<String, Integer> map;

    public Codification(String codificationFile) {
        this.map = new HashMap<>();
        this.loadFromFile(codificationFile);
    }

    private void loadFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            br.lines()
                    .filter(line -> line.trim().length() > 0)
                    .filter(line -> !line.startsWith("#"))
                    .forEach(line -> {
                        String[] tokens = line.split("@");
                        String atom = tokens[0].trim();
                        if (atom.startsWith("~")) {
                            atom = atom.substring(1);
                        }
                        Integer code = Integer.parseInt(tokens[1].trim());
                        map.put(atom, code);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getCodeOfAtom(String atom) {
        return map.get(atom);
    }

    public Integer getCodeOfIdentifier() {
        return map.get("identifier");
    }

    public Integer getCodeOfConstant() {
        return map.get("constant");
    }

    public Integer getCodeOfString() {
        return map.get("string");
    }
}
