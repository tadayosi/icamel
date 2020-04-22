package io.github.tadayosi.icamel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import io.github.spencerpark.jupyter.kernel.BaseKernel;
import io.github.spencerpark.jupyter.kernel.LanguageInfo;
import io.github.spencerpark.jupyter.kernel.ReplacementOptions;
import io.github.spencerpark.jupyter.kernel.display.DisplayData;
import io.github.spencerpark.jupyter.messages.Header;
import org.apache.camel.k.Runtime;
import org.apache.camel.k.Source;
import org.apache.camel.k.Sources;
import org.apache.camel.k.listener.ContextConfigurer;
import org.apache.camel.k.listener.RoutesConfigurer;
import org.apache.camel.k.main.ApplicationRuntime;

public class CamelKernel extends BaseKernel {

    public static final String VERSION;
    public static final String CAMEL_VERSION;
    public static final String CAMEL_K_VERSION;

    static {
        Properties metadata = new Properties();
        try {
            metadata.load(CamelKernel.class.getResourceAsStream("/metadata.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VERSION = metadata.getProperty("project.version");
        CAMEL_VERSION = metadata.getProperty("camel.version");
        CAMEL_K_VERSION = metadata.getProperty("camel.k.version");
    }

    private final LanguageInfo languageInfo;
    private final String banner;
    private final List<LanguageInfo.Help> helpLinks;

    private final ApplicationRuntime runtime;

    public CamelKernel() throws Exception {
        languageInfo = new LanguageInfo.Builder("Camel")
            .version(CAMEL_VERSION)
            .mimetype("text/javascript")
            .fileExtension(".js")
            .pygments("javascript")
            .codemirror("javascript")
            .build();
        banner = new StringBuilder()
            .append(String.format("ICamel kernel %s\n", VERSION))
            .append(String.format("Camel %s\n", CAMEL_VERSION))
            .append(String.format("Camel K %s\n", CAMEL_K_VERSION))
            .append(String.format("Protocol v%s implementation by %s %s\n",
                Header.PROTOCOL_VERISON,
                KERNEL_META.getOrDefault("project", "UNKNOWN"),
                KERNEL_META.getOrDefault("version", "UNKNOWN")))
            .append(String.format("Java %s", java.lang.Runtime.version()))
            .toString();
        helpLinks = List.of(
            new LanguageInfo.Help("ICamel", "https://github.com/tadayosi/icamel")
        );

        runtime = new ApplicationRuntime();
        runtime.addListener(new ContextConfigurer());
    }

    @Override
    public LanguageInfo getLanguageInfo() {
        return languageInfo;
    }

    @Override
    public String getBanner() {
        return this.banner;
    }

    @Override
    public List<LanguageInfo.Help> getHelpLinks() {
        return this.helpLinks;
    }

    @Override
    public void onShutdown(boolean isRestarting) {
        if (isRestarting) {
            return;
        }
        stopRuntime();
    }

    private void stopRuntime() {
        try {
            runtime.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DisplayData eval(String expr) throws Exception {
        runtime.addListener(new RoutesConfigurer() {
            @Override
            protected void accept(Runtime runtime) {
                Source source = Sources.fromBytes("js", expr.getBytes(StandardCharsets.UTF_8));
                load(runtime, source);
            }
        });
        runtime.run();

        return null;
    }

    @Override
    public DisplayData inspect(String code, int at, boolean extraDetail) {
        // TODO: to be implemented
        return null;
    }

    @Override
    public ReplacementOptions complete(String code, int at) {
        // TODO: to be implemented
        return null;
    }
}
