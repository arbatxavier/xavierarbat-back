#!/bin/bash
mkdir -p uploads/projects uploads/blogs uploads/contacts uploads/home

for folder in projects blogs contacts home; do
  echo "--- Carpeta: $folder ---"
  images=$(curl -s "https://api.xavierarbat.com/api/v1/images/$folder" | sed 's/[\[\]"]//g' | tr ',' '\n')
  
  for path in $images; do
    if [ ! -z "$path" ]; then
      filename=$(basename "$path")
      echo "Descargando $filename"
      curl -s "https://api.xavierarbat.com$path" -o "uploads/$folder/$filename"
    fi
  done
done
