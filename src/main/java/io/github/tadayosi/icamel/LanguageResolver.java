package io.github.tadayosi.icamel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.k.Source;
import org.apache.camel.k.Sources;

public class LanguageResolver {

    public static final String DEFAULT_LANGUAGE = "js";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Pattern CLASS_PATTERN =
        Pattern.compile("^\\s*public\\s+class\\s+([a-zA-Z_$][a-zA-Z_$\\d]*)\\s*.*$");

    public LanguageResolver() {
    }

    public String resolve(String expr) {
        if (expr.startsWith("<")) {
            return "xml";
        } else if (expr.startsWith("- ")) {
            return "yaml";
        }

        int eol = expr.indexOf(System.lineSeparator());
        if (eol < 0) {
            // one-liner
            return DEFAULT_LANGUAGE;
        }

        String begin = expr.substring(0, eol);
        if (begin.startsWith("//") && begin.contains("language=")) {
            return begin.trim().split("=")[1];
        }
        return DEFAULT_LANGUAGE;
    }

    public Source toSource(String expr) {
        String language = resolve(expr);
        if ("java".equals(language)) {
            return Sources.fromBytes(findJavaClassName(expr), language, null, expr.getBytes(CHARSET));
        }
        return Sources.fromBytes(language, expr.getBytes(CHARSET));
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
