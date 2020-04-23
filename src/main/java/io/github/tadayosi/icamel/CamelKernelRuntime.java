package io.github.tadayosi.icamel;

import java.util.Collection;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.k.Runtime;
import org.apache.camel.k.main.ApplicationRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelKernelRuntime implements Runtime {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelKernelRuntime.class);

    private final DefaultCamelContext context;

    public CamelKernelRuntime() {
        context = new DefaultCamelContext();
        context.setName("camel-kernel");
    }

    @Override
    public CamelContext getCamelContext() {
        return context;
    }

    @Override
    public void setPropertiesLocations(Collection<String> locations) {
    }
}
