#!/bin/bash

if [[ "$1" == "-h" || "$1" == "--help" ]] 
then
    echo "This script will:"
    echo " - run ./gradlew clean"
    echo " - run ./gradlew distZip"
    echo " - create a demo folder where it's easy to execute the binary"
    echo " - that folder will be as if `init` and `au init` had already been run"
else
    COPY_INITIALIZE_FROM=$1

    # find and go to repo root dir
    d="$(dirname "${BASH_SOURCE[0]}")"
    dir="$(cd "$(dirname "$d")" && pwd)/$(basename "$d")"
    cd "$dir"
    cd ..

    # remove existing
    ./gradlew clean
    rm -rf build

    # build
    ./gradlew distZip
    cd build/distributions
    unzip *.zip
    rm *.zip
    cd *
    cd bin

    mkdir demo-folder
    cd demo-folder
    mkdir architecture-updates
    touch data-structure.yml

    # copy .arch-as-code from repo root
    cp -r $dir/../.arch-as-code .

    echo ""
    echo ""
    echo ""
    echo ""
    echo "Demo folder created. To cd there, run:"
    echo "   cd $(pwd)"
    echo "In there, executable can be run with: ../arch-as-code"
fi
