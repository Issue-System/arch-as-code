#!/bin/bash

if [[ "$1" == "-h" || "$1" == "--help" ]] 
then
    echo "This script will:"
    echo " - run ./gradlew clean"
    echo " - run ./gradlew distZip"
    echo " - create a demo folder where it's easy to execute the binary"
    echo ""
    echo "Note that you can use 'cd -' from within the demo folder to go back to the repo root."
else
    COPY_INITIALIZE_FROM=$1

    # find and go to repo root dir
    d="$(dirname "${BASH_SOURCE[0]}")"
    dir="$(cd "$(dirname "$d")" && pwd)/$(basename "$d")"
    cd "$dir"
    cd ..

    pwd

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

    # make the `cd -` command happy by going to repo root and back
    cur=$(pwd -P)
    cd "$dir"
    cd ..
    cd "$cur"

    # copy .arch-as-code from repo root
    cp -r $dir/../.arch-as-code .

    echo "\n\nExecutable can be run with: ../arch-as-code"
fi
