package org.canova.api.records.reader.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.canova.api.io.data.Text;
import org.canova.api.records.reader.RecordReader;
import org.canova.api.split.FileSplit;
import org.canova.api.split.InputSplit;
import org.canova.api.split.StringSplit;
import org.canova.api.writable.Writable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

/**
 * Reads files line by line
 *
 * @author Adam Gibson
 */
public class LineRecordReader implements RecordReader {


    private URI[] locations;
    private int currIndex = 0;
    private Iterator<String> iter;


    @Override
    public void initialize(InputSplit split) throws IOException, InterruptedException {
        if(split instanceof FileSplit) {
            this.locations = split.locations();
            if (locations != null && locations.length > 0) {
                iter = IOUtils.lineIterator(new InputStreamReader(locations[0].toURL().openStream()));
            }
        }

        else if(split instanceof StringSplit) {
            StringSplit stringSplit = (StringSplit) split;
            iter = Arrays.asList(stringSplit.getData()).iterator();
        }

    }

    @Override
    public Collection<Writable> next() {
        List<Writable> ret = new ArrayList<>();

        if(iter.hasNext()) {
            ret.add(new Text(iter.next()));
            return ret;
        }
        else {
            currIndex++;
            try {
                close();
                iter = IOUtils.lineIterator(new InputStreamReader(locations[currIndex].toURL().openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(iter.hasNext()) {
                ret.add(new Text(iter.next()));
                return ret;
            }

        }

        throw new NoSuchElementException("No more elements found!");
    }

    @Override
    public boolean hasNext() {
        return iter != null && iter.hasNext();
    }

    @Override
    public void close() throws IOException {
        if(iter != null) {
            if(iter instanceof LineIterator) {
                LineIterator iter2 = (LineIterator) iter;
                iter2.close();
            }
        }
    }
}
