package org.canova.spark.util;

import org.apache.hadoop.io.Text;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.input.PortableDataStream;
import org.canova.spark.functions.pairdata.*;
import scala.Tuple3;

/** Utilities for using Canova with Spark
 * @author Alex Black
 */
public class CanovaSparkUtil {

    /**Same as {@link #combineFilesForSequenceFile(JavaSparkContext, String, String, PathToKeyConverter, PathToKeyConverter)}
     * but with the PathToKeyConverter used for both file sources
     */
    public static JavaPairRDD<Text,BytesPairWritable> combineFilesForSequenceFile(JavaSparkContext sc, String path1, String path2, PathToKeyConverter converter){
        return combineFilesForSequenceFile(sc,path1,path2,converter,converter);
    }

    /**This is a convenience method to create data (intended to write to a sequence file, using
     * {@link org.apache.spark.api.java.JavaPairRDD#saveAsNewAPIHadoopFile(String, Class, Class, Class) })<br>
     * A typical use case is to combine input and label data from different files, for later parsing by a RecordReader
     * or SequenceRecordReader.
     * A typical use case is as follows:<br>
     * Given two paths (directories), combine the files in these two directories into pairs.<br>
     * Then, for each pair of files, convert the file contents into a {@link BytesPairWritable}, which also contains
     * the original file paths of the files.<br>
     * The assumptions are as follows:<br>
     * - For every file in the first directory, there is an equivalent file in the second directory<br>
     * - The pairing of files can be done based on the file names of the<br>
     * <br><br>
     * <b>Example usage</b>: to combine all files in directory {@code dir1} with equivalent files in {@code dir2}, by file name:
     * <pre>
     * <code>JavaSparkContext sc = ...;
     * String path1 = "/dir1";
     * String path2 = "/dir2";
     * PathToKeyConverter pathConverter = new PathToKeyConverterFilename();
     * JavaPairRDD<Text,BytesPairWritable> toWrite = CanovaSparkUtil.combineFilesForSequenceFile(sc, path1, path2, pathConverter, pathConverter );
     * String outputPath = "/my/output/path";
     * toWrite.saveAsNewAPIHadoopFile(outputPath, Text.class, BytesPairWritable.class, SequenceFileOutputFormat.class);
     * </code>
     * </pre>
     * Result: the file contexts aggregated (pairwise), written to a hadoop sequence file at /my/output/path
     *
     *
     * @param sc Spark context
     * @param path1 First directory (passed to JavaSparkContext.binaryFiles(path1))
     * @param path2 Second directory (passed to JavaSparkContext.binaryFiles(path1))
     * @param converter1 Converter, to convert file paths in first directory to a key (to allow files to be matched/paired by key)
     * @param converter2 As above, for second directory
     * @return
     */
    public static JavaPairRDD<Text,BytesPairWritable> combineFilesForSequenceFile(JavaSparkContext sc, String path1, String path2, PathToKeyConverter converter1,
                                                                           PathToKeyConverter converter2){
        JavaPairRDD<String,PortableDataStream> first = sc.binaryFiles(path1);
        JavaPairRDD<String,PortableDataStream> second = sc.binaryFiles(path2);

        //Now: process keys (paths) so that they can be merged
        JavaPairRDD<String, Tuple3<String,Integer,PortableDataStream>> first2 = first.mapToPair(new PathToKeyFunction(0,converter1));
        JavaPairRDD<String, Tuple3<String,Integer,PortableDataStream>> second2 = second.mapToPair(new PathToKeyFunction(1,converter2));
        JavaPairRDD<String, Tuple3<String,Integer,PortableDataStream>> merged = first2.union(second2);

        //Combine into pairs, and prepare for writing
        JavaPairRDD<Text,BytesPairWritable> toWrite = merged.groupByKey().mapToPair(new MapToBytesPairWritableFunction());
        return toWrite;
    }

}
