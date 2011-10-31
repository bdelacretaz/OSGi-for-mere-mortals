package ch.x42.osgi.samples.osgi101.core;

import java.util.Properties;

public interface Storage {
    public void put(String key, Properties props);
    public Properties get(String key);
}
