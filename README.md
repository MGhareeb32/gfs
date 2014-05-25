# GFS

A simple implementation of the Google File System.

## How to run?

You can run on one of three modes:
* Local offline mode.
* Local RMI mode.
* Distributed RMI mode.

### Local offline mode

In this mode the code runs on one machine with nodes communicating directly
with each other. For examples see: `./src/sim/Sim0.java`,
`./src/sim/Sim1.java`.

### Local RMI mode

In this mode the code runs on one machine but with nodes using RMI for
communication. For examples see: `./src/sim/Sim2.java`, `./src/sim/Sim3.java`.

### Distributed RMI mode

In this mode, you specify remote machines that act as master and replicas. To
do so, modify `system.config`. It should look like this:

        cooluser@192.168.1.1:2000/master
        copycatuser@192.168.1.2:2001/replica-00
        lameuser@192.168.1.3:2002/replica-01
        lazyassuser@192.168.1.4:2003/replica-02

Where the first one, `cooluser@192.168.1.1:2000/master` is the master node. And
the rest are the replicas. The format goes as follows
`<user-name>@<ip-address>:<port>/<node-name>`. You can choose any `<node-name>`
for your mahines.

To build:

        gfs$ ./scripts/build.sh

To upload jars to machines:

        gfs$ ./scripts/upload.sh

To start the system:

        gfs$ ./scripts/start.sh

To stop the system:

        gfs$ ./scripts/stop.sh

In this mode, the working directory is `~/gfs`. You can find the files and logs
there.
