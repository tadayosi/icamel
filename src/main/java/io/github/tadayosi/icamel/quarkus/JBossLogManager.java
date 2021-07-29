package io.github.tadayosi.icamel.quarkus;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Enumeration;
import java.util.logging.LogManager;

import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;

/**
 * Customised JBoss LogManager. Mostly copied from {@link org.jboss.logmanager.LogManager}.
 * Not used right now.
 *
 * Simplified log manager.  Designed to work around the (many) design flaws of the JDK platform log manager.
 */
public class JBossLogManager extends LogManager {

    private Logger heartbeatChannelLogger;

    /**
     * Construct a new logmanager instance.  Attempts to plug a known memory leak in {@link java.util.logging.Level} as
     * well.
     */
    public JBossLogManager() {
    }

    // Configuration

    /**
     * Configure the log manager one time.
     */
    public void readConfiguration() throws IOException, SecurityException {
    }

    /**
     * Configure the log manager.
     *
     * @param inputStream the input stream from which the logmanager should be configured
     */
    public void readConfiguration(InputStream inputStream) throws IOException, SecurityException {
    }

    /**
     * Do nothing.  Properties and their listeners are not supported.
     *
     * @param l ignored
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        // no operation - properties are never changed
    }

    /**
     * Do nothing.  Properties and their listeners are not supported.
     *
     * @param l ignored
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        // no operation - properties are never changed
    }

    /**
     * Does nothing.  Properties are not supported.
     *
     * @param name ignored
     * @return {@code null}
     */
    public String getProperty(String name) {
        // no properties
        return null;
    }

    /**
     * Does nothing.  This method only causes trouble.
     */
    public void reset() {
        // no operation!
    }

    @Override
    public Enumeration<String> getLoggerNames() {
        return LogContext.getInstance().getLoggerNames();
    }

    /**
     * Do nothing.  Loggers are only added/acquired via {@link #getLogger(String)}.
     *
     * @param logger ignored
     * @return {@code false}
     */
    public boolean addLogger(java.util.logging.Logger logger) {
        return false;
    }

    /**
     * Get or create a logger with the given name.
     *
     * @param name the logger name
     * @return the corresponding logger
     */
    public Logger getLogger(String name) {
        Logger logger = LogContext.getInstance().getLogger(name);

        // Hack for using jupyter-jvm-basekernel with Quarkus
        if ("HeartbeatChannel".equals(name)) {
            if (heartbeatChannelLogger == null) {
                heartbeatChannelLogger = createSetParentableProxy(logger);
            }
            return heartbeatChannelLogger;
        }
        return logger;
    }

    private Logger createSetParentableProxy(Logger logger) {
        return (Logger) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { Logger.class },
            (proxy, method, args) -> {
                if ("setParent".equals(method.getName())) {
                    // Invoking setParent(...) on JBoss Logger causes SecurityException
                    return null;
                }
                return method.invoke(logger, args);
            }
        );
    }
}
