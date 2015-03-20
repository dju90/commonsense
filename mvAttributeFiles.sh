#!/bin/bash

file="$1"
mkdir -p "attributeFiltrate"
while IFS=',' read -ra line; do
    s="${line[0]}"
    mv "$2/$s" "attributeFiltrate"
    echo "$s"
done < "$file"
