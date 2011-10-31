package ch.x42.osgi.samples.osgi101.app.servlets;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.x42.osgi.samples.osgi101.core.Storage;

/** In-memory Storage using a HashMap, enabled by default */
@Component
@Service(value=Storage.class)
public class InMemoryStorage implements Storage {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final Map<String, Properties> data = new HashMap<String, Properties>();
    
    @Override
    public Properties get(String key) {
        final Properties result = data.get(key);
        log.info("Get ({}) returns {}", key, result);
        return result;
    }

    @Override
    public void put(String key, Properties value) {
        data.put(key, value);
        log.info("Stored with key={}: {}");
    }
}
