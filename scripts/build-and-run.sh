#!/bin/bash

if [[ "$1" == "-h" || "$1" == "--help" ]] 
then
    echo "This script will:"
    echo " - run ./gradlew clean"
    echo " - run ./gradlew distZip"
    echo " - create a demo folder where it's easy to execute the binary"
    echo ""
    echo "If an argument is provided, this script will copy over the .arch-as-code directory from there."
    echo ""
    echo "Note that you can use 'cd -' from within the demo folder to go back to the repo root."
else
    COPY_INITIALIZE_FROM=$1

    # find and go to repo root dir
    dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
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

    if [ -z "$COPY_INITIALIZE_FROM" ]
    then
    else
        cp -r "$COPY_INITIALIZE_FROM/.arch-as-code" .
    fi

    # make the `cd -` command happy by going to repo root and back
    cur=$(pwd -P)
    cd "$dir"
    cd ..
    cd "$cur"

    echo "\n\nExecutable can be run with: ../arch-as-code"
fi
