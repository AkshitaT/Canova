package org.canova.api.io.serializers;


import org.canova.api.conf.Configuration;
import org.canova.api.conf.Configured;
import org.canova.api.util.ReflectionUtils;
import org.canova.api.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A factory for {@link Serialization}s.
 * </p>
 */
public class SerializationFactory extends Configured {

    private static final Logger LOG =
            LoggerFactory.getLogger(SerializationFactory.class.getName());

    private List<Serialization<?>> serializations = new ArrayList<>();

    /**
     * <p>
     * Serializations are found by reading the <code>io.serializations</code>
     * property from <code>conf</code>, which is a comma-delimited list of
     * classnames.
     * </p>
     */
    public SerializationFactory(Configuration conf) {
        super(conf);
        for (String serializerName : conf.getStrings("io.serializations",
                new String[]{"org.apache.hadoop.io.serializer.WritableSerialization"})) {
            add(conf, serializerName);
        }
    }

    @SuppressWarnings("unchecked")
    private void add(Configuration conf, String serializationName) {
        try {

            Class<? extends Serialization> serializationClass =
                    (Class<? extends Serialization>) conf.getClassByName(serializationName);
            serializations.add(
                    ReflectionUtils.newInstance(serializationClass, getConf()));
        } catch (ClassNotFoundException e) {
            LOG.warn("Serialization class not found: " +
                    StringUtils.stringifyException(e));
        }
    }

    public <T> Serializer<T> getSerializer(Class<T> c) {
        return getSerialization(c).getSerializer(c);
    }

    public <T> Deserializer<T> getDeserializer(Class<T> c) {
        return getSerialization(c).getDeserializer(c);
    }

    @SuppressWarnings("unchecked")
    public <T> Serialization<T> getSerialization(Class<T> c) {
        for (Serialization serialization : serializations) {
            if (serialization.accept(c)) {
                return (Serialization<T>) serialization;
            }
        }
        return null;
    }
}