package io.github.tadayosi.icamel;

import io.github.spencerpark.jupyter.kernel.LanguageInfo;
import io.github.spencerpark.jupyter.kernel.ReplacementOptions;
import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.k.Runtime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CamelKernelTest {

    private CamelKernel kernel;
    private CamelContext context;

    @Before
    public void setUp() {
        kernel = new CamelKernel();
        Runtime runtime = kernel.getRuntime();
        context = runtime.getCamelContext();
    }

    @After
    public void tearDown() {
        if (kernel != null) {
            kernel.onShutdown(false);
        }
    }

    @Test
    public void testEval() throws Exception {
        MockEndpoint out = context.getEndpoint("mock:out", MockEndpoint.class);
        out.expectedMessageCount(5);

        kernel.eval("console.log('hello')");
        kernel.eval("from('timer:tick?repeatCount=5&period=100').to('mock:out')");

        assertThat(context.getStatus()).isEqualTo(ServiceStatus.Started);
        assertThat(context.getRoutes()).hasSize(1);
        out.assertIsSatisfied();
    }

    @Test
    public void testComplete() throws Exception {
        // js (default)
        ReplacementOptions options = kernel.complete(
            "from('timer:tick?period=3000').log('hello!')",
            6);
        assertThat(options.getReplacements()).isNotEmpty();

        // groovy
        options = kernel.complete(
            "// language=groovy\nfrom('timer:tick?period=3000').log('hello!')",
            25);
        assertThat(options.getReplacements()).isNotEmpty();

        // java
        options = kernel.complete(
            "// language=java\nfrom(\"timer:tick?period=3000\").log(\"hello!\")",
            23);
        assertThat(options.getReplacements()).isNotEmpty();

        // xml
        options = kernel.complete(
            "<from uri=\"\" xmlns=\"http://camel.apache.org/schema/spring\"></from>",
            11);
        assertThat(options.getReplacements()).isNotEmpty();
    }

    @Test
    public void testLanguageInfo() {
        LanguageInfo languageInfo = kernel.getLanguageInfo();
        assertThat(languageInfo).isNotNull();
        assertThat(languageInfo.getName()).isEqualTo("Camel");
    }

    @Test
    public void testBanner() throws Exception {
        assertThat(kernel.getBanner())
            .isNotNull()
            .startsWith("ICamel");
    }
}
