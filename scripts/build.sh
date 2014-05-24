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

jar cfe Master.jar Master './out/gfs/Master.class'
exit
jar cfe Client.jar Client 'Client.class' 'Client$1.class' \
                          'Hello_Stub.class' 'HelloInterface.class'
cd ..

mv src/Client.jar Client.jar
mv src/Server.jar Server.jar

(chmod -fR 777 . && chmod -fR u+x .)&
