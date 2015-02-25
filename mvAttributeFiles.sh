#!/bin/bash

n=0
while [read line -a n < 25]
do
    name=$line
    echo "File $n: $line"
    ((n++))
done 
