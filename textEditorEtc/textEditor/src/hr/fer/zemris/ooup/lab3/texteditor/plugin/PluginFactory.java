package hr.fer.zemris.ooup.lab3.texteditor.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class PluginFactory {
    private static final String packageName = "hr.fer.zemris.ooup.lab3.texteditor.plugin.impl.";

    public List<Plugin> createPlugins() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/")))));

        Set<Class<?>> set = reader.lines().filter(line -> line.endsWith(".class")).map(PluginFactory::getClass).filter(Objects::nonNull)
                .filter(Plugin.class::isAssignableFrom)
                .collect(Collectors.toSet());
        List<Plugin> plugins = new ArrayList<>();
        for(Class<?> clazz: set) {
            Plugin p = (Plugin) clazz.getConstructor().newInstance();
            plugins.add(p);
        }
        return plugins;
    }
    private static Class<?> getClass(String className) {
        try {
            return Class.forName(packageName + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
