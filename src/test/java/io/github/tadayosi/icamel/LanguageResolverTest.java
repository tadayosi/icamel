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
        assertThat(resolver.resolve("// language=js\n")).isEqualTo("js");
        assertThat(resolver.resolve("// language=groovy\n")).isEqualTo("groovy");
        assertThat(resolver.resolve("// language=js\n")).isEqualTo("js");
        assertThat(resolver.resolve("// language=kts\n")).isEqualTo("kts");
        assertThat(resolver.resolve("// language=xml\n")).isEqualTo("xml");
        assertThat(resolver.resolve("// language=yaml\n")).isEqualTo("yaml");
        // default
        assertThat(resolver.resolve("console.log('hello')")).isEqualTo("js");
        assertThat(resolver.resolve("<from uri=\"\"></from>")).isEqualTo("xml");
    }

    @Test
    public void toSource() {
        Source source = resolver.toSource("// language=java\n" + "public class Test {}");
        assertThat(source.getLanguage()).isEqualTo("java");
        assertThat(source.getName()).isEqualTo("Test");
        // default
        assertThat(resolver.toSource("console.log('hello')").getLanguage()).isEqualTo("js");
    }
}
