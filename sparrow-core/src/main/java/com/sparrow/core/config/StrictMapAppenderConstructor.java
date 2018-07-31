package com.sparrow.core.config;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/31.
 */
public class StrictMapAppenderConstructor extends Constructor {

    // Declared as public for use in subclasses
    public StrictMapAppenderConstructor() {
        super();
    }

    @Override
    protected Map<Object, Object> constructMapping(MappingNode node) {
        try {
            return super.constructMapping(node);
        }
        catch (IllegalStateException ex) {
            throw new ParserException("while parsing MappingNode",
                    node.getStartMark(), ex.getMessage(), node.getEndMark());
        }
    }

    @Override
    protected Map<Object, Object> createDefaultMap(int size) {
        final Map<Object, Object> delegate = super.createDefaultMap(size);
        return new AbstractMap<Object, Object>() {
            @Override
            public Object put(Object key, Object value) {
                if (delegate.containsKey(key)) {
                    throw new IllegalStateException("Duplicate key: " + key);
                }
                return delegate.put(key, value);
            }
            @Override
            public Set<Entry<Object, Object>> entrySet() {
                return delegate.entrySet();
            }
        };
    }
}