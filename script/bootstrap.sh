#!/bin/bash

if [ -z "$GUROBI_JAR" ]
then
    echo "The GUROBI_JAR environment variable is empty"
else
    mvn install:install-file -Dfile=${GUROBI_JAR} \
                             -DgroupId=gurobi \
                             -DartifactId=gurobi \
                             -Dversion=8.1.1 \
                             -Dpackaging=jar \
                             -DgeneratePom=true
fi

if [ -z "$CPLEX_JAR" ]
then
    echo "The CPLEX_JAR environment variable is empty"
else
    mvn install:install-file -Dfile=${CPLEX_JAR} \
                             -DgroupId=cplex \
                             -DartifactId=cplex \
                             -Dversion=12.9.0 \
                             -Dpackaging=jar \
                             -DgeneratePom=true
fi
