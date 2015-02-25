#!/bin/bash

file="$1"
mkdir -p "attributeFiltrate"
while IFS=',' read -ra line; do
    s="${line[0]}"
    mv "data/transposeTables/$s" "attributeFiltrate"
    echo "$s"
done < "$file"
