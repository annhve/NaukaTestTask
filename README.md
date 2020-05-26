# NaukaTestTask
Simple Java Swing application that connects to local Oracle DB server and shows work time sheet of some abstract enterprise.

Application supports 3 different roles: time keeper (can change mark of a work day in a calendar for some employee), departments administrator (can see and manage departments in a separate window) and employees administrator (can see and manage employees in a separate window).

# Build
To build an executable JAR file run (under root folder):
```
gradle jar
```

# Run
To run an output JAR file run (under root folder):
```
java -jar build/libs/TestTask-1.0-SNAPSHOT.jar
```

The output will be in `build/libs/TestTask-1.0-SNAPSHOT.jar`

# Requirements
This app works in a tight couple with running Oracle DB server.
Connection configuration located in source file `src/main/java/com/annhve/naukatesttask/util/DbConnector.java`.

There is a helper method that provides `Connection` to DB. This method contains connection parameters as well, so it should be modified from here:
```
private Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.OracleDriver");
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@192.168.56.101:1521:XE",
                "somelogin",
                "somepassword"
        );
    }
```
Here:
* __192.168.56.101__ is a server url
* __1521__ is a server port
* __XE__ is a server SID
* __somelogin__ is your username at db server
* __somepassword__ is your password at db server
