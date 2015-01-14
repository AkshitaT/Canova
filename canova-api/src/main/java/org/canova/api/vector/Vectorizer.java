package org.nd4j.api.vector;

import org.nd4j.api.conf.Configuration;
import org.nd4j.api.records.reader.RecordReader;
import org.nd4j.api.writable.Writable;

import java.util.Collection;

/**
 * Vectorizer of a particular type.
 * Meant for converting individual records to vectors
 *
 * @author Adam Gibson
 */
public interface Vectorizer<VECTOR_TYPE> {


    /**
     * Create a vector based on the given arguments
     * @param args the arguments to create a vector with
     * @return the created vector
     *
     */
    VECTOR_TYPE createVector(Object[] args);

    /**
     * Initialize based on a configuration
     * @param conf the configuration to use
     */
    void initialize(Configuration conf);

    /**
     * Fit based on a record reader
     * @param reader
     */
    void fit(RecordReader reader);

    /**
     * Fit based on a record reader
     * @param reader
     */
    VECTOR_TYPE fitTransform(RecordReader reader);


    /**
     * Fit based on a record reader
     * @param reader
     * @param callBack
     */
    void fit(RecordReader reader,RecordCallBack callBack);

    /**
     * Fit based on a record reader
     * @param reader
     * @param callBack
     */
    VECTOR_TYPE fitTransform(RecordReader reader,RecordCallBack callBack);

    /**
     * Transform a record in to a vector
     * @param record the record to write
     * @return
     */
    VECTOR_TYPE transform(Collection<Writable> record);


    /**
     * On record call back.
     * This allows for neat inheritance and polymorphism
     * for fit and fit/transform among other things
     */
    public static interface RecordCallBack {
        /**
         * The record callback
         * @param record
         */
        void onRecord(Collection<Writable> record);
    }


}
