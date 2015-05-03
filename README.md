Canova
======

## Description

A tool for vectorizing raw data into usable vector formats across machine learning tools.

## Example

 * Convert the CSV-based UCI Iris dataset into svmLight open vector text format
 * Convert the MNIST dataset from raw binary files to the svmLight text format.
 * Convert raw text into the Metronome vector format
 * Convert raw text into TF-IDF based vectors in a text vector format {svmLight, metronome, arff}
 * Convert raw text into the word2vec in a text vector format {svmLight, metronome, arff}

## Targeted Vectorization Engines

 * Any CSV to vectors with a scriptable transform language
 * MNIST to vectors
 * Text to vectors
    * TF-IDF
    * Bag of Words
    * word2vec

## CSV Transformation Engine

Below is an example of the CSV transform language in action from the command line

### UCI Iris Schema Transform

```
@RELATION UCIIrisDataset
@DELIMITER ,
 
   @ATTRIBUTE sepallength  NUMERIC   !NORMALIZE
   @ATTRIBUTE sepalwidth   NUMERIC   !NORMALIZE
   @ATTRIBUTE petallength  NUMERIC   !NORMALIZE
   @ATTRIBUTE petalwidth   NUMERIC   !NORMALIZE
   @ATTRIBUTE class        STRING   !LABEL
```

## Setting Up Canova

We need to do a *git pull* from this Github repo, and then build the dependencies with Maven

```
mvn -DskipTests=true -Dmaven.javadoc.skip=true install
```

Then we'd want to build the stand-alone Canova jar to run the CLI from the command line:

```
cd canova-cli/
mvn -DskipTests=true -Dmaven.javadoc.skip=true package
```

## Create the Configuration File

We need a file to tell the vectorization engine what to do. Create a text file containing the following lines in the *canova-cli* directory (you might name the file vec_conf.txt):

```
input.header.skip=false
input.statistics.debug.print=false
input.format=org.canova.api.formats.input.impl.LineInputFormat

input.directory=src/test/resources/csv/data/uci_iris_sample.txt
input.vector.schema=src/test/resources/csv/schemas/uci/iris.txt
output.directory=/tmp/iris_unit_test_sample.txt

output.format=org.canova.api.formats.output.impl.SVMLightOutputFormat
```

## Run Canova From the Command Line

Now we're going to take this [sample](https://github.com/deeplearning4j/Canova/blob/master/canova-cli/src/test/resources/csv/data/uci_iris_sample.txt) of [UCI's Iris dataset](https://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data).

```
5.1,3.5,1.4,0.2,Iris-setosa
4.9,3.0,1.4,0.2,Iris-setosa
4.7,3.2,1.3,0.2,Iris-setosa
7.0,3.2,4.7,1.4,Iris-versicolor
6.4,3.2,4.5,1.5,Iris-versicolor
6.9,3.1,4.9,1.5,Iris-versicolor
5.5,2.3,4.0,1.3,Iris-versicolor
6.5,2.8,4.6,1.5,Iris-versicolor
6.3,3.3,6.0,2.5,Iris-virginica
5.8,2.7,5.1,1.9,Iris-virginica
7.1,3.0,5.9,2.1,Iris-virginica
6.3,2.9,5.6,1.8,Iris-virginica
```

We transform it into the svmLight format from the command line like this

```
./bin/canova vectorize -conf [my_conf_file]
```

The output in your command prompt should look like

```
./bin/canova vectorize -conf /tmp/iris_conf.txt 
File path already exists, deleting the old file before proceeding...
Output vectors written to: /tmp/iris_svmlight.txt

```
If you cd into /tmp and open *iris_svmlight.txt*, you'll see something like this:

```
0.0 1:0.1666666666666665 2:1.0 3:0.021276595744680823 4:0.0
0.0 1:0.08333333333333343 2:0.5833333333333334 3:0.021276595744680823 4:0.0
0.0 1:0.0 2:0.7500000000000002 3:0.0 4:0.0
1.0 1:0.9583333333333335 2:0.7500000000000002 3:0.723404255319149 4:0.5217391304347826
1.0 1:0.7083333333333336 2:0.7500000000000002 3:0.6808510638297872 4:0.5652173913043479
1.0 1:0.916666666666667 2:0.6666666666666667 3:0.7659574468085107 4:0.5652173913043479
1.0 1:0.3333333333333333 2:0.0 3:0.574468085106383 4:0.47826086956521746
1.0 1:0.7500000000000001 2:0.41666666666666663 3:0.702127659574468 4:0.5652173913043479
2.0 1:0.6666666666666666 2:0.8333333333333333 3:1.0 4:1.0
2.0 1:0.45833333333333326 2:0.3333333333333336 3:0.8085106382978723 4:0.7391304347826088
2.0 1:1.0 2:0.5833333333333334 3:0.9787234042553192 4:0.8260869565217392
2.0 1:0.6666666666666666 2:0.5 3:0.9148936170212765 4:0.6956521739130436
```

## Execution

Runs as both a local serial process and a MapReduce (MR engine on the roadmap) scale-out process with no code changes.

## Targetted Vector Formats
* svmLight
* libsvm
* Metronome
* ARFF

## Built-In General Functionality
* Understands how to take general text and convert it into vectors with stock techniques such as kernel hashing and TF-IDF [TODO]
