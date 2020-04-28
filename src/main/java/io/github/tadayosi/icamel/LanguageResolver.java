package io.github.tadayosi.icamel;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.k.Source;
import org.apache.camel.k.Sources;

public class LanguageResolver {

    public static final String DEFAULT_LANGUAGE = "js";

    private static final Pattern CLASS_PATTERN =
        Pattern.compile("^\\s*public\\s+class\\s+([a-zA-Z_$][a-zA-Z_$\\d]*)\\s*.*$");

    public LanguageResolver() {
    }

    public Source resolve(String expr) {
        Optional<String> language = resolveLanguage(expr);
        if (language.filter("java"::equals).isPresent()) {
            return Sources.fromBytes(
                findJavaClassName(expr),
                language.get(),
                null,
                expr.getBytes(StandardCharsets.UTF_8));
        }
        return Sources.fromBytes(
            language.orElse(DEFAULT_LANGUAGE),
            expr.getBytes(StandardCharsets.UTF_8));
    }

    private Optional<String> resolveLanguage(String expr) {
        Optional<String> language = Optional.empty();
        int eol = expr.indexOf(System.lineSeparator());
        if (eol < 0) {
            // one-liner
            return language;
        }
        String begin = expr.substring(0, eol);
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
        return language;
    }

    private String findJavaClassName(String expr) {
        return Arrays.stream(expr.split(System.lineSeparator()))
            .map(CLASS_PATTERN::matcher)
            .filter(Matcher::find)
            .map(m -> m.group(1))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Java source requires public class."));
    }
}
