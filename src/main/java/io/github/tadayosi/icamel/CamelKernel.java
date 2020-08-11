package io.github.tadayosi.icamel;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.github.cameltooling.lsp.internal.CamelLanguageServer;
import io.github.spencerpark.jupyter.kernel.BaseKernel;
import io.github.spencerpark.jupyter.kernel.LanguageInfo;
import io.github.spencerpark.jupyter.kernel.ReplacementOptions;
import io.github.spencerpark.jupyter.kernel.display.DisplayData;
import io.github.spencerpark.jupyter.messages.Header;
import org.apache.camel.k.Runtime;
import org.apache.camel.k.Source;
import org.apache.camel.k.listener.RoutesConfigurer;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelKernel extends BaseKernel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelKernel.class);

    public static final String VERSION;
    public static final String CAMEL_VERSION;
    public static final String CAMEL_K_VERSION;
    public static final String CAMEL_LSP_VERSION;

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
        CAMEL_LSP_VERSION = metadata.getProperty("camel.lsp.version");
    }

    private final LanguageInfo languageInfo;
    private final String banner;
    private final List<LanguageInfo.Help> helpLinks;

    private final Runtime runtime;
    private final LanguageResolver languageResolver;
    private final CamelLanguageServer languageServer;

    public CamelKernel() {
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
            .append(String.format("Camel Language Server %s\n", CAMEL_LSP_VERSION))
            .append(String.format("Protocol v%s implementation by %s %s\n",
                Header.PROTOCOL_VERISON,
                KERNEL_META.getOrDefault("project", "UNKNOWN"),
                KERNEL_META.getOrDefault("version", "UNKNOWN")))
            .append(String.format("Java %s", java.lang.Runtime.version()))
            .toString();
        helpLinks = List.of(
            new LanguageInfo.Help("ICamel", "https://github.com/tadayosi/icamel")
        );

        languageResolver = new LanguageResolver();

        languageServer = new CamelLanguageServer();
        startLanguageServer();

        runtime = new CamelKernelRuntime();
        startRuntime();
    }

    private void startLanguageServer() {
        languageServer.connect(new KernelLanguageClient());
        languageServer.startServer();
        InitializeParams params = new InitializeParams();
        params.setProcessId(Math.toIntExact(ProcessHandle.current().pid()));
        languageServer.initialize(params);
    }

    private void startRuntime() {
        runtime.getCamelContext().start();
    }

    private void stopRuntime() {
        try {
            runtime.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Runtime getRuntime() {
        return runtime;
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
        stopRuntime();
    }

    @Override
    public void interrupt() {
        stopRuntime();
    }

    @Override
    public DisplayData eval(String expr) {
        Source source = languageResolver.toSource(expr);
        RoutesConfigurer.load(runtime, source);
        return null;
    }

    @Override
    public DisplayData inspect(String code, int at, boolean extraDetail) {
        // TODO: to be implemented
        LOGGER.debug("inspect - code: {}, at: {}, extraDetail: {}", code, at, extraDetail);
        return null;
    }

    @Override
    public ReplacementOptions complete(String code, int at) throws Exception {
        LOGGER.debug("complete - code: {}, at: {}", code, at);

        String language = languageResolver.resolve(code);
        // adding suffix 'camelk.*' is an easy way to make lsp recognise the code
        String filename = "code.camelk." + language;
        if ("java".equals(language)) {
            // hack for java code with lsp
            code = "// camel\n" + code;
            at = at + "// camel".length() + 1;
        }

        TextDocumentService textDocumentService = getTextDocumentService(code, filename);
        List<CompletionItem> completions = getCompletions(textDocumentService, code, at, filename);
        List<String> options = completions.stream()
            .map(CompletionItem::getLabel)
            .collect(Collectors.toList());

        if (LOGGER.isDebugEnabled()) {
            int n = 10;
            LOGGER.debug("complete - options: {}{}",
                options.stream().limit(n).collect(Collectors.joining(" ")),
                options.size() <= n ? "" : " ...");
        }

        if (options.isEmpty()) {
            return null;
        }

        int start = CompletionHelper.at(code, completions.get(0).getTextEdit().getRange().getStart());
        if ("java".equals(language)) {
            // hack for java code with lsp
            int offset = "// camel".length() + 1;
            start -= offset;
            at -= offset;
        }
        return new ReplacementOptions(options, start, at);
    }

    private TextDocumentService getTextDocumentService(String code, String filename) {
        TextDocumentItem item = new TextDocumentItem(filename, CamelLanguageServer.LANGUAGE_ID, 0, code);
        languageServer.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(item));
        return languageServer.getTextDocumentService();
    }

    private List<CompletionItem> getCompletions(TextDocumentService textDocumentService, String code, int at, String filename) throws Exception {
        CompletionParams params = new CompletionParams(
            new TextDocumentIdentifier(filename), CompletionHelper.position(code, at));
        return textDocumentService.completion(params).get().getLeft();
    }

    private class KernelLanguageClient implements LanguageClient {
        @Override
        public void telemetryEvent(Object o) {
        }

        @Override
        public void publishDiagnostics(PublishDiagnosticsParams publishDiagnosticsParams) {
        }

        @Override
        public void showMessage(MessageParams messageParams) {
        }

        @Override
        public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams showMessageRequestParams) {
            return null;
        }

        @Override
        public void logMessage(MessageParams messageParams) {
        }
    }
}
