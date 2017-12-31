package scanner;

import java.util.Map;
import java.util.TreeMap;

public class SymbolTable {
    private Map<String, Integer> st;

    private int nextId;

    public SymbolTable() {
        this.st = new TreeMap<>();
        this.nextId = 0;
    }

    private void add(String symbol) {
        this.st.put(symbol, this.nextId);
        this.nextId++;
    }

    public Integer poz(String symbol) {
        if (!this.st.containsKey(symbol)) {
            this.add(symbol);
        }
        return this.st.get(symbol);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-5s |\n", "Symb", "Pos"));
        sb.append(String.format("+-%-5s-+-%-5s-+\n", "-----", "-----"));
        for (String key : st.keySet()) {
            sb.append(String.format("| %-5s | %-5s |\n", key, st.get(key)));
        }
        sb.append(String.format("+-%-5s-+-%-5s-+\n", "-----", "-----"));

        return sb.toString();
    }
}
