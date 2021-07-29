package io.github.tadayosi.icamel;

import java.util.Collection;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.k.Runtime;

public class CamelKernelRuntime implements Runtime {

    private final DefaultCamelContext context;

    public CamelKernelRuntime() {
        context = new DefaultCamelContext();
        context.setName("camel-kernel");
    }

    @Override
    public CamelContext getCamelContext() {
        return context;
    }
}
