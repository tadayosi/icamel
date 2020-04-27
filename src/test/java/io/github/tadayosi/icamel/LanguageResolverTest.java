package io.github.tadayosi.icamel;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LanguageResolverTest {

    @Test
    public void resolve() {
        LanguageResolver resolver = new LanguageResolver();
        assertThat(resolver.resolve("// language=groovy" + System.lineSeparator()).getLanguage()).isEqualTo("groovy");
        assertThat(resolver.resolve("// language=js\n").getLanguage()).isEqualTo("js");
        assertThat(resolver.resolve("// language=java\n").getLanguage()).isEqualTo("java");
        assertThat(resolver.resolve("// language=kts\n").getLanguage()).isEqualTo("kts");
        assertThat(resolver.resolve("// language=xml\n").getLanguage()).isEqualTo("xml");
        assertThat(resolver.resolve("// language=yaml\n").getLanguage()).isEqualTo("yaml");
        // default
        assertThat(resolver.resolve("console.log('hello')").getLanguage()).isEqualTo("js");
    }
}
