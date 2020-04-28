package io.github.tadayosi.icamel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import io.github.spencerpark.jupyter.channels.JupyterConnection;
import io.github.spencerpark.jupyter.kernel.KernelConnectionProperties;

import static io.github.spencerpark.jupyter.channels.JupyterSocket.JUPYTER_LOGGER;

public class ICamel {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println(
                String.format("ICamel %s - Apache Camel kernel for Jupyter notebook\n"
                        + "\n"
                        + "Usage:\n"
                        + "  java -jar icamel-%s.jar [connection_file]\n"
                        + "\n"
                        + "For more info, see: https://github.com/tadayosi/icamel/",
                    CamelKernel.VERSION, CamelKernel.VERSION));
            return;
        }

        Path connectionFile = Paths.get(args[0]);

        if (!Files.isRegularFile(connectionFile)) {
            throw new IllegalArgumentException("Connection file '" + connectionFile + "' isn't a file.");
        }

        String contents = new String(Files.readAllBytes(connectionFile));

        JUPYTER_LOGGER.setLevel(Level.WARNING);

        KernelConnectionProperties connProps = KernelConnectionProperties.parse(contents);
        JupyterConnection connection = new JupyterConnection(connProps);

        CamelKernel kernel = new CamelKernel();
        kernel.becomeHandlerForConnection(connection);

        connection.connect();
        connection.waitUntilClose();
    }
}
