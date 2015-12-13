# SageAxcessSampleTask
A sample task for SageAxcess, showing usage of plain Scala, Akka, and Spark for data processing

### Usage

#### Plain Scala
    sbt "plainscala/run sample.csv output_file"

#### Akka
    sbt "akka/run --help"
    ...
    Usage:
    sbt "akka/run --help"
    shows this message

    sbt "akka/run input.csv"
    provide input file name, default is "input.csv"

    sbt "akka/run input.csv output"
    provide both input and output file names, defaults are "input.csv", "output"
    
    
    cat ./input.csv 
    ColumnA,ColumnB,ColumnC
    some,data goes,here
    more,data,in row 2
    more,data
    more,data
    
    sbt "akka/run input.csv output"
    
    cat ./output
    in row 2:1
    data goes:1
    some:1
    data:3
    here:1
    more:3
    
#### Spark
    sbt spark/assembly

    docker cp ./spark/target/scala-2.11/spark-assembly-1.0.jar 60e3450c9478:/tmp/spark-assembly-1.0.jar
    docker cp ./input.csv 60e3450c9478:/tmp/input.csv
    
    //In docker container 
    spark-submit --class com.sageaxcess.sampletask.spark.TokensCount --master yarn-client --driver-memory 1g --executor-memory 1g --executor-cores 1 /tmp/spark-assembly-1.0.jar 
    cat /tmp/output
    in row 2:1
    data goes:1
    some:1
    data:3
    here:1
    more:3

### Testing
`sbt test`