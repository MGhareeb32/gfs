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

mv ./out/Master.jar ./Master.jar
mv ./out/Replica.jar ./Replica.jar

./Master.jar localhost:2000/Master localhost:2001/Replica localhost:2002/Replica
exit
./Replica.jar localhost:2001/Replica localhost:2002/Replica
./Replica.jar localhost:2002/Replica localhost:2001/Replica

jar cfe Client.jar Client 'Client.class' 'Client$1.class' \
                          'Hello_Stub.class' 'HelloInterface.class'
cd ..

mv src/Client.jar Client.jar
mv src/Server.jar Server.jar

(chmod -fR 777 . && chmod -fR u+x .)&
