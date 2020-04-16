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

# make the `cd -` command happy by going to repo root and back
cur=$(pwd -P)
cd "$dir"
cd ..
cd "$cur"

echo "\n\nExecutable can be run with: ../arch-as-code"
