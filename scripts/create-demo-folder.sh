#!/bin/bash

if [[ "$1" == "-h" || "$1" == "--help" ]] 
then
    echo "This script will:"
    echo " - run ./gradlew clean"
    echo " - run ./gradlew distZip"
    echo " - create a demo folder where it's easy to execute the binary"
    echo " - that folder will be as if `init` and `au init` had already been run"
else
    # find and go to repo root dir
    d="$(dirname "${BASH_SOURCE[0]}")"
    dir="$(cd "$(dirname "$d")" && pwd)/$(basename "$d")"
    cd "$dir"
    cd ..

    rm -rf $dir/../demo-folder/.arch-as-code
    rm -rf $dir/../demo-folder/.install
    mkdir -p $dir/../demo-folder/.install

    # remove existing
    ./gradlew clean
    rm -rf build

    # build
    ./gradlew distZip
    cd build/distributions
    unzip *.zip
    rm *.zip
    cd *
    mv ./* $dir/../demo-folder/.install/

    cd $dir/../demo-folder

    mv data-structure.yml data-structure.yml.bak

    .install/bin/arch-as-code init -i i -k i -s s .
    .install/bin/arch-as-code au init -c c -p p -s s .

    mv data-structure.yml.bak data-structure.yml

    # copy .arch-as-code from repo root
    rm -rf .arch-as-code
    cp -r $dir/../.arch-as-code .

    # add executable to folder
    echo 'd="$(dirname "${BASH_SOURCE[0]}")"; dir="$(cd "$(dirname "$d")" && pwd)/$(basename "$d")"; "${dir}"/.install/bin/arch-as-code "$@";' > arch-as-code.sh
    chmod +x arch-as-code.sh

    echo ""
    echo ""
    echo ""
    echo ""
    echo "Demo folder created. To cd there, run:"
    echo "   cd $(pwd)"
    echo "Run ./arch-as-code.sh as an alias for the execcutable"
fi
