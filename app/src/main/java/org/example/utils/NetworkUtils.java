package org.example.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * NetworkUtils
 */
public class NetworkUtils {

    public static String getLocalIPAddress(String interfaceName) {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            if (networkInterface == null || !networkInterface.isUp()) {
                System.out.println("Invalid or unavailable network interface: " + interfaceName);
                return null;
            }

            // Get the list of IP addresses associated with the interface
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                // Return the first site-local address (ignoring loopback)
                if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                    return address.getHostAddress(); // Return the IP address
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving IP address: " + e.getMessage());
        }
        return null;
    }

    public static ObservableList<String> getInterfaceNames() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            ObservableList<String> interfaceNames = FXCollections.observableArrayList();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                if (networkInterface.isLoopback() || !networkInterface.isUp()
                        || networkInterface.getInetAddresses() == null)
                    continue;

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        interfaceNames.add(networkInterface.getName());
                    }
                }
            }

            return interfaceNames;
        } catch (Exception e) {
            System.out.println("Error retrieving Interface Names: " + e.getMessage());
        }
        return null;
    }

}
