package ch.x42.osgi.samples.osgi101.app.servlets;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import ch.x42.osgi.samples.osgi101.core.Storage;

@Component(enabled=false)
@Service(value=Storage.class)
public class PathsStorage implements Storage {

    private final Set<String> paths = new HashSet<String>();
    
    @Override
    public Properties get(String key) {
        final Properties props = new Properties();
        int index=0;
        for(String path : paths) {
            props.put("path." + index++, path);
        }
        return props;
    }

    @Override
    public void put(String key, Properties value) {
        paths.add(key);
    }
}
