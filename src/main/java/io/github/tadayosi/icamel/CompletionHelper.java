package io.github.tadayosi.icamel;

import org.eclipse.lsp4j.Position;

public class CompletionHelper {

    public static Position position(String code, int at) {
        String[] lines = code.split("\n");
        int line = 0;
        int offset = at;
        for (String s : lines) {
            if (offset <= s.length()) {
                break;
            }
            offset -= s.length() + 1;
            line++;
        }
        return new Position(line, offset);
    }
}
