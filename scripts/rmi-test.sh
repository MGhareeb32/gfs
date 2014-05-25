# clean
if [ -d "./out" ]; then
    rm -rf ./out
fi
mkdir ./out

# generate classes
find -name "*.java" > ./out/sources.txt
javac -d './out' @./out/sources.txt

cd ./out
jar cfe RmiServer.jar utils.rmitest.RmiServer \
'./utils/rmitest/RmiServer.class' \
'./utils/rmitest/State.class' \
'./utils/rmitest/StateInterface.class' \
'./utils/Rmi.class'

jar cfe RmiClient.jar utils.rmitest.RmiClient \
'./utils/rmitest/RmiClient.class' \
'./utils/rmitest/StateInterface.class' \
'./utils/Rmi.class'
cd ..

mv ./out/RmiServer.jar ./RmiServer.jar
mv ./out/RmiClient.jar ./RmiClient.jar

./RmiServer.jar &
sleep 1
./RmiClient.jar
./RmiClient.jar
pkill -f 'Rmi*'
