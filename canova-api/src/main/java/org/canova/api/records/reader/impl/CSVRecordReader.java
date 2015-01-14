package org.nd4j.api.records.reader.impl;

import org.nd4j.api.io.data.Text;
import org.nd4j.api.writable.Writable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple csv record reader.
 *
 * @author Adam Gibson
 */
public class CSVRecordReader extends LineRecordReader {


    @Override
    public Collection<Writable> next() {
        Text t =  (Text) super.next().iterator().next();
        String val = new String(t.getBytes());
        String[] split = val.split(",");
        List<Writable> ret = new ArrayList<>();
        for(String s : split)
            ret.add(new Text(s));
        return ret;
    }
}
