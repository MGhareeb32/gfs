# clean
if [ -d "./out" ]; then
    rm -rf ./out
fi
mkdir ./out

# generate classes
find -name "*.java" > ./out/sources.txt
javac -d './out' @./out/sources.txt

# generate stubs
rmic -classpath './out' -d './out' gfs.Master gfs.Replica

cd ./out

# generate master jar
jar cfe Master.jar gfs.Master \
'./gfs/Master.class' \
'./gfs/MasterClientInterface.class' \
'./gfs/ReplicaClientInterface.class' \
'./gfs/ReplicaMasterInterface.class' \
'./gfs/ReplicaReplicaInterface.class' \
'./gfs/data/FileContent.class' \
'./gfs/data/Host.class' \
'./gfs/data/HostRmi.class' \
'./gfs/data/HostTcp.class' \
'./gfs/data/MsgNotFoundException.class' \
'./gfs/data/ReadMsg.class' \
'./gfs/data/WriteMsg.class' \
'./gfs/data/WriteTxnState.class' \
'./gfs/hostprovider/MasterClientInterfaceProvider.class' \
'./gfs/hostprovider/ReplicaClientInterfaceProvider.class' \
'./gfs/hostprovider/ReplicaMasterInterfaceProvider.class' \
'./gfs/hostprovider/ReplicaReplicaInterfaceProvider.class' \
'./gfs/hostprovider/RmiHostProvider.class' \
'./gfs/hostprovider/SimpleHostInterfaceProvider.class' \
'./logger/DummyLogger.class' \
'./logger/FileLogger.class' \
'./logger/Logger.class' \
'./logger/StdLogger.class' \
'./utils/Exceptions.class' \
'./utils/Files.class' \
'./utils/Rmi.class' \

# generate replica jar
jar cfe Replica.jar gfs.Replica \
'./gfs/Replica.class' \
'./gfs/Replica$1.class' \
'./gfs/MasterClientInterface.class' \
'./gfs/ReplicaClientInterface.class' \
'./gfs/ReplicaMasterInterface.class' \
'./gfs/ReplicaReplicaInterface.class' \
'./gfs/data/FileContent.class' \
'./gfs/data/Host.class' \
'./gfs/data/HostRmi.class' \
'./gfs/data/HostTcp.class' \
'./gfs/data/MsgNotFoundException.class' \
'./gfs/data/ReadMsg.class' \
'./gfs/data/WriteMsg.class' \
'./gfs/data/WriteTxnState.class' \
'./gfs/hostprovider/MasterClientInterfaceProvider.class' \
'./gfs/hostprovider/ReplicaClientInterfaceProvider.class' \
'./gfs/hostprovider/ReplicaMasterInterfaceProvider.class' \
'./gfs/hostprovider/ReplicaReplicaInterfaceProvider.class' \
'./gfs/hostprovider/RmiHostProvider.class' \
'./gfs/hostprovider/SimpleHostInterfaceProvider.class' \
'./logger/DummyLogger.class' \
'./logger/FileLogger.class' \
'./logger/Logger.class' \
'./logger/StdLogger.class' \
'./utils/Exceptions.class' \
'./utils/Files.class' \
'./utils/Rmi.class' \

cd ..

# move up

mv ./out/Master.jar ./Master.jar
chmod -fR 777 ./Master.jar
chmod -fR u+x ./Master.jar

mv ./out/Replica.jar ./Replica.jar
chmod -fR 777 ./Replica.jar
chmod -fR u+x ./Replica.jar

# generate scripts
java -cp out scripts.GenerateUploadScript
java -cp out scripts.GenerateStartScript
java -cp out scripts.GenerateStopScript
