package org.canova.spark.functions;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.input.PortableDataStream;
import org.canova.api.conf.Configuration;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.records.reader.impl.CSVSequenceRecordReader;
import org.canova.api.split.FileSplit;
import org.canova.api.split.InputSplit;
import org.canova.api.util.ClassPathResource;
import org.canova.api.writable.Writable;
import org.canova.codec.reader.CodecRecordReader;
import org.canova.image.recordreader.ImageRecordReader;
import org.canova.spark.BaseSparkTest;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSequenceRecordReaderFunction extends BaseSparkTest {

    @Test
    public void testSequenceRecordReaderFunctionCSV() throws Exception {
        JavaSparkContext sc = getContext();

        ClassPathResource cpr = new ClassPathResource("/csvsequence/csvsequence_0.txt");

        String path = cpr.getFile().getAbsolutePath();
        String folder = path.substring(0, path.length() - 17);
        path = folder + "*";

        JavaPairRDD<String,PortableDataStream> origData = sc.binaryFiles(path);
        assertEquals(3,origData.count());    //3 CSV files

        SequenceRecordReaderFunction srrf = new SequenceRecordReaderFunction(new CSVSequenceRecordReader(1,","));    //CSV, skip 1 line
        JavaRDD<Collection<Collection<Writable>>> rdd = origData.map(srrf);
        List<Collection<Collection<Writable>>> listSpark = rdd.collect();

        assertEquals(3,listSpark.size());
        for( int i=0; i<3; i++ ){
            Collection<Collection<Writable>> thisSequence = listSpark.get(i);
            assertEquals(4, thisSequence.size());   //Expect exactly 4 time steps in sequence
            for(Collection<Writable> c : thisSequence){
                assertEquals(3,c.size());   //3 values per time step
            }
        }

        //Load normally, and check that we get the same results (order not withstanding)
        InputSplit is = new FileSplit(new File(folder),new String[]{"txt"}, true);
//        System.out.println("Locations:");
//        System.out.println(Arrays.toString(is.locations()));

        SequenceRecordReader srr = new CSVSequenceRecordReader(1,",");
        srr.initialize(is);

        List<Collection<Collection<Writable>>> list = new ArrayList<>(3);
        while(srr.hasNext()){
            list.add(srr.sequenceRecord());
        }
        assertEquals(3, list.size());

        System.out.println("Spark list:");
        for(Collection<Collection<Writable>> c : listSpark ) System.out.println(c);
        System.out.println("Local list:");
        for(Collection<Collection<Writable>> c : list ) System.out.println(c);

        //Check that each of the values from Spark equals exactly one of the values doing it normally
        boolean[] found = new boolean[3];
        for( int i=0; i<3; i++ ){
            int foundIndex = -1;
            Collection<Collection<Writable>> collection = listSpark.get(i);
            for( int j=0; j<3; j++ ){
                if(collection.equals(list.get(j))){
                    if(foundIndex != -1) fail();    //Already found this value -> suggests this spark value equals two or more of local version? (Shouldn't happen)
                    foundIndex = j;
                    if(found[foundIndex]) fail();   //One of the other spark values was equal to this one -> suggests duplicates in Spark list
                    found[foundIndex] = true;   //mark this one as seen before
                }
            }
        }
        int count = 0;
        for( boolean b : found ) if(b) count++;
        assertEquals(3,count);  //Expect all 3 and exactly 3 pairwise matches between spark and local versions
    }



    @Test
    public void testSequenceRecordReaderFunctionVideo() throws Exception {
        JavaSparkContext sc = getContext();

        ClassPathResource cpr = new ClassPathResource("/video/shapes_0.mp4");

        String path = cpr.getFile().getAbsolutePath();
        String folder = path.substring(0, path.length() - 12);
        path = folder + "*";

        JavaPairRDD<String,PortableDataStream> origData = sc.binaryFiles(path);
        System.out.println(origData.collectAsMap().keySet());
        assertEquals(4, origData.count());    //4 video files

        //Load 64x64, 25 frames - originally, 130x130, 150 frames
        SequenceRecordReader sparkSeqReader = new CodecRecordReader();
        Configuration conf = new Configuration();
        conf.set(CodecRecordReader.RAVEL, "true");
        conf.set(CodecRecordReader.START_FRAME, "0");
        conf.set(CodecRecordReader.TOTAL_FRAMES, "25");
        conf.set(CodecRecordReader.ROWS, "64");
        conf.set(CodecRecordReader.COLUMNS, "64");
        Configuration confCopy = new Configuration(conf);
        sparkSeqReader.setConf(conf);

        SequenceRecordReaderFunction srrf = new SequenceRecordReaderFunction(sparkSeqReader);
        JavaRDD<Collection<Collection<Writable>>> rdd = origData.map(srrf);
        List<Collection<Collection<Writable>>> listSpark = rdd.collect();

        assertEquals(4,listSpark.size());
        for( int i=0; i<4; i++ ){
            Collection<Collection<Writable>> thisSequence = listSpark.get(i);
            assertEquals(25, thisSequence.size());   //Expect exactly 25 time steps (frames) in sequence
            for(Collection<Writable> c : thisSequence){
                assertEquals(64*64*3,c.size());   //64*64 videos, RGB
            }
        }

        //Load normally, and check that we get the same results (order not withstanding)
        InputSplit is = new FileSplit(new File(folder),new String[]{"mp4"}, true);
//        System.out.println("Locations:");
//        System.out.println(Arrays.toString(is.locations()));

        SequenceRecordReader srr = new CodecRecordReader();
        srr.initialize(is);
        srr.setConf(confCopy);


        List<Collection<Collection<Writable>>> list = new ArrayList<>(4);
        while(srr.hasNext()){
            list.add(srr.sequenceRecord());
        }
        assertEquals(4, list.size());

//        System.out.println("Spark list:");
//        for(Collection<Collection<Writable>> c : listSpark ) System.out.println(c);
//        System.out.println("Local list:");
//        for(Collection<Collection<Writable>> c : list ) System.out.println(c);

        //Check that each of the values from Spark equals exactly one of the values doing it locally
        boolean[] found = new boolean[4];
        for( int i=0; i<4; i++ ){
            int foundIndex = -1;
            Collection<Collection<Writable>> collection = listSpark.get(i);
            for( int j=0; j<4; j++ ){
                if(collection.equals(list.get(j))){
                    if(foundIndex != -1) fail();    //Already found this value -> suggests this spark value equals two or more of local version? (Shouldn't happen)
                    foundIndex = j;
                    if(found[foundIndex]) fail();   //One of the other spark values was equal to this one -> suggests duplicates in Spark list
                    found[foundIndex] = true;       //mark this one as seen before
                }
            }
        }
        int count = 0;
        for( boolean b : found ) if(b) count++;
        assertEquals(4,count);  //Expect all 4 and exactly 4 pairwise matches between spark and local versions
    }
}