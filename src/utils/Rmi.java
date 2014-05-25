package utils;

import gfs.data.HostRmi;

import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

public class Rmi {

    // REGISTER AND EXPORT

    public static Registry getLocalRegistery(int port) throws RemoteException {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(port);
            registry.list();
        } catch (RemoteException e) {
            registry = LocateRegistry.createRegistry(port);
            registry.list();
        }
        return registry;
    }

    public static Remote getStubOrExport(int port, UnicastRemoteObject obj)
        throws RemoteException {

        Remote stub = null;
        try {
            stub = UnicastRemoteObject.exportObject(obj, port);
        } catch (ExportException e) {
            stub = UnicastRemoteObject.toStub(obj);
        }
        return stub;
    }

    public static String[] getLocalObjectList(int port)
        throws AccessException, RemoteException {

        return LocateRegistry.getRegistry(port).list();
    }

    public static void registerLocalObject
        (String host, int port, String objName, UnicastRemoteObject obj) {

        System.setProperty("java.rmi.server.hostname", host);
        try {
            String Uri = String.format("rmi://%s:%d/%s", host, port, objName);
            getLocalRegistery(port);
            Naming.rebind(Uri, getStubOrExport(port, obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerLocalObject
        (HostRmi h, UnicastRemoteObject obj) {

        registerLocalObject(h.ip, h.port, h.objName, obj);
    }

    // CONNECT AND OBTAIN

    public static Registry getRemoteRegistery(String host, int port)
        throws RemoteException {

        return LocateRegistry.getRegistry(host, port);
    }

    public static Remote getRemoteObject
        (String host, int port, String objName)
        throws RemoteException, NotBoundException {

        return getRemoteRegistery(host, port).lookup(objName);
    }

    public static Remote getRemoteObject(HostRmi h)
        throws RemoteException, NotBoundException {

        return getRemoteObject(h.ip, h.port, h.objName);
    }
}
