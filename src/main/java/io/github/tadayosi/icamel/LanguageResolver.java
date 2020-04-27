package io.github.tadayosi.icamel;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.camel.k.Source;
import org.apache.camel.k.Sources;

public class LanguageResolver {

    public static final String DEFAULT_LANGUAGE = "js";

    public LanguageResolver() {
    }

    public Source resolve(String expr) {
        int eol = expr.indexOf(System.lineSeparator());
        if (eol < 0) {
            // one-liner
            return Sources.fromBytes(DEFAULT_LANGUAGE, expr.getBytes(StandardCharsets.UTF_8));
        }
        String begin = expr.substring(0, eol);
        Optional<String> language = Optional.empty();
        if (begin.startsWith("//") && begin.contains("language=")) {
            // check comment
            language = Optional.of(begin.trim().split("=")[1]);
        } else if (begin.startsWith("<")) {
            // xml
            language = Optional.of("xml");
        } else if (begin.startsWith("- ")) {
            // yaml
            language = Optional.of("yaml");
        }
        return Sources.fromBytes(language.orElse(DEFAULT_LANGUAGE), expr.getBytes(StandardCharsets.UTF_8));
    }
}
