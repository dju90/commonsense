#!/bin/bash

file="$1"
n=0
while [read line -a n < 25]
do
    name=$line
    echo "File $n: $line"
    ((n++))
done < $file
