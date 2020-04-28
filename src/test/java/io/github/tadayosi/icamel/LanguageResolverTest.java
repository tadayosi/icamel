package io.github.tadayosi.icamel;

import org.apache.camel.k.Source;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LanguageResolverTest {

    private LanguageResolver resolver;

    @Before
    public void setUp() {
        resolver = new LanguageResolver();
    }

    @Test
    public void resolve() {
        assertThat(resolver.resolve("// language=groovy\n").getLanguage()).isEqualTo("groovy");
        assertThat(resolver.resolve("// language=js\n").getLanguage()).isEqualTo("js");
        assertThat(resolver.resolve("// language=kts\n").getLanguage()).isEqualTo("kts");
        assertThat(resolver.resolve("// language=xml\n").getLanguage()).isEqualTo("xml");
        assertThat(resolver.resolve("// language=yaml\n").getLanguage()).isEqualTo("yaml");
        // default
        assertThat(resolver.resolve("console.log('hello')").getLanguage()).isEqualTo("js");
    }

    @Test
    public void resolveJava() {
        Source source = resolver.resolve("// language=java\n" + "public class Test {}");
        assertThat(source.getLanguage()).isEqualTo("java");
        assertThat(source.getName()).isEqualTo("Test");
    }
}
