This application processes the data that is provided by the Norwegian parliament at [https://data.stortinget.no]().
It processes all supplied XML files and links the data as a connected graph in a Neo4J database. This application
can be run either:

1. Without arguments. This triggers a dry run where all collected information is printed to the console.
2. With a single argument representing a folder on the local file system. This triggers an extraction into a graph database. The folder must be writable and empty.