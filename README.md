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

### Testing
`sbt test`