# jrDesktop
Java Remote Desktop - is a cross-platform software for remote desktop control, remote assistance and desktop sharing

jrDesktop is useful for home networking, helpdesk, system administration and collaboration

jrDesktop is an open source software licensed under the terms of the GNU Public License (GPL)

Please note that jrDesktop is discontinued and published for educational purposes  

## Features

Beside the basic functions that comes with a classical remote desktop software such as taking screen shots, transferring mouse and keyboard events, jrDesktop comes with many additional features like multisessions, file transfer, clipboard synchronization, reverse connection, web client and proxy support and more

jrDesktop comes with a graphical user interface that supports many themes and a tray icon that provides ease of access to main functions

jrDesktop can be deployed in many formats, such as

- A standalone application
- An applet embedded in a web page
- A Java Web Start application or applet
- A multi-threaded HTTP server
- A Windows service and/or a *nix daemon
- A Windows portable application
- A Mozilla Firefox extension

Both server and viewer come in one tiny file, no installation is required

Because jrDesktop is 100 % built on java; it can run on many operating systems such as Windows, Solaris OS, Linux and Mac OS

Launching jrDesktop can be customized by many command line arguments and applet parameters, also server and viewer configuration profiles can be saved for a future usage

jrDesktop supports also many graphical features such as image resizing, partial extraction and encoding, color depth selection and JPEG compression

Communication on jrDesktop is established using its own RMI-based protocol and secured with SSL/TLS encryption protocols

## Usage guide

### Requirements
- JDK 1.5 (JRE 1.5) or above
- A desktop environment : Windows, Gnome or KDE ... etc.

### Execution
N.B: If you are running Windows, Make sure that you already added java bin directory to the environment variable : PATH
(example : PATH = ...; c:\Program Files\Java\jdk1.6.0_03\bin)

Simply double click on jrdesktop.jar to start the application
	
### Advanced

* Running the application from command line
	
```sh
	usage: java -jar jrdesktop.jar  [options]
        Commands:
			server | viewer       		start server (or viewer) using default parameters
		    server | viewer [options]	customized start of server (or viewer)
			pwd-gen                		password generation utility
			--version | -v          	display version information
			--help | -?              	display usage information
			
		Options:
			-a:address              	server address
			-d                      	set as the default JVM IP address
			-i                      	auto detect server IP address
			-p:port                 	servers port
			-http:port              	servers http port
			-u:user                		user name
			-w:pwd                  	user password
			-s                      	secured connection using SSL
			-r                      	reverse connection
			--hide                  	hide main window
			--noicon                	disable system tray icon
			--noexit                	disable system exit
			--pxserver:address      	proxy servers address
			--pxport:port           	proxy servers port
			--home:directory        	jrdesktops home directory
			--conf:file             	load configurations file
			--downloads:directory   	downloads location
			--lookAndFeel:laf       	look and feel theme
```

Example: starting jrdesktop server without displaying main window
	
```sh		
    java -jar jrdesktop server --hide
```

* Running the application from a java applet

```
	- Applet parameters
		home	 			jrdesktops home directory
		noicon 	 			disable system tray icon
		noexit 	 			disable system exit
		pxserver 			proxy servers address
		pxport	 			proxy servers port
		downloads			downloads location
		lookAndFeel			look and feel theme
		server	 			servers address
		default				set as the default JVM IP address
		multihome			auto detect server IP address
		port	 			servers port
		httpPort			servers http port
		username			user name
		password			user password
		ssl				    secured connection using SSL
		reverse				reverse connection
		config				load configuration file
		side				launches a server or a viewer side
```

Example: running jrdekstop viewer without displaying systray icon
		
```html 
	<applet 
		 code="jrdesktop.mainApplet.class"
		 archive="lib/jrdesktop.jar"
		 width="490" height="320">
			<param name="side" value="viewer" />
			<param name="server" value="192.168.1.2" />
			<param name="port" value="1099" />
			<param name="username" value="admin" />
			<param name="password" value="admin" />
			<param name="noicon" value="false" />
	</applet>
```				  

* jrdesktop as a Windows service
```
	- bin/
	- Install-jrdesktop-service.bat		Install jrdesktop service
	- Start-jrdesktop-service.bat		Start jrdesktop service
	- Stop-jrdesktop-service.bat		Stop jrdesktop service
	- Uninstall-jrdesktop-service.bat	Uninstall jrdesktop service
	- conf/wrapper.conf				    Service configuration file
	- logs/wrapper.log				    Service log file
```

* jrdesktop as a Linux / UNIX daemon
```
	- bin/jrdesktop-daemon			Manage jrdesktop daemon (start | stop | restart | status)
	- conf/wrapper.conf				Service configuration file
	- logs/wrapper.log				Service log file
```

* jrdesktop as a HTTP server
```
   - http://IP-ADDRESS:PORT/			           jrdesktop web applet
   - http://IP-ADDRESS:PORT/jrdesktop.jar		   download binary file (.jar)
   - http://IP-ADDRESS:PORT/jrdesktop.jnlp    	  	download JWS application
   - http://IP-ADDRESS:PORT/jrdesktop_applet.jnlp	download JWS applet
``` 
Example: running jrdekstop embedded http server
```
	http://127.0.0.1:6666/
```

* Others ways to start jrdesktop	
```
	- docs/
	- jrdesktop.jnlp	    						start jrdesktop as a JWS application
	- jrdesktop_applet.jnlp   						start jrdesktop as a JWS applet
	- jrdesktop.html	    						start jrdesktop as an applet
	- jrdesktop_for_firefox-1.0-fx.xpi				jrdesktop as a Firefox extension
	- jrdesktopPortable_0.3_Rev_10_English.paf.exe	jrdesktop as a Portable application
```

## Developement Guide
You can use A Java IDE like eclipse or netbeans to compile & build jrdesktop. However, this is quick guide that helps you to that manually from command line

```sh
echo "Decompression ..."
tar xzvf jrdesktop-source-0.3.1.0.tar.gz

echo "Create build & distribution directories"
mkdir -v jrdesktop/build jrdesktop/dist

echo "Changing to source directory"
cd jrdesktop/src

echo "Compiling all files at once ..."
javac -verbose jrdesktop/main.java -d ../build

echo "Changing to build directory"
cd ../build

echo "Generating stub & skltn files"
rmic jrdesktop.rmi.server.ServerImpl

echo "Copying images directory"
cp -Rv ../src/jrdesktop/images/ jrdesktop/

echo "Generating a new keystore and self-signed certificate"
keytool -genkeypair -keyalg RSA -alias sdo -keystore keystore -storepass password -keypass password -dname "CN=CN, OU=OU, O=O, L=L, S=S, C=C"

echo "Exporting the self-signed certificate"
keytool -export -alias sdo -keystore keystore -storepass password -file temp.key
	
echo "Importing the certificate into a new truststore"
keytool -import -noprompt -alias sdo -keystore truststore -storepass trustword -file temp.key

echo "Moving the certificate files"
mv keystore truststore jrdesktop/

echo "Removing the certificate file"
rm temp.key

echo "Creating a mainfast file"
echo -e "Manifest-Version: 1.0\nAnt-Version: Apache Ant 1.7.1\nCreated-By: 10.0-b22 (Sun Microsystems Inc.)\nMain-Class: jrdesktop.main\nBuilt-Date: 2009-09-15 08:42:40\n" > ../manifest.mf

echo "Building the application"
jar cvfm ../dist/jrdesktop.jar ../manifest.mf jrdesktop/* org/*

echo "Generating key for singing"
keytool -genkey -keystore nb-jws.ks -storepass storepass -keypass keypass -alias nb-jws -dname "CN=CN, OU=OU, O=O, L=L, S=S, C=C"

echo "Singing the jar file"
jarsigner -keystore nb-jws.ks -storepass storepass -keypass keypass ../dist/jrdesktop.jar nb-jws 

echo "Running the application"
java -jar ../dist/jrdesktop.jar
```
