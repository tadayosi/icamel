package io.github.tadayosi.icamel;

import java.util.Arrays;

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

    public static int at(String code, Position position) {
        String[] lines = code.split("\n");
        int at = position.getCharacter();
        for (String s : Arrays.asList(lines).subList(0, position.getLine())) {
            at += s.length() + 1;
        }
        return at;
    }
}
