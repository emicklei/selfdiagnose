# SelfDiagnose

SelfDiagnose is a library of diagnotistic tasks that can verify the availability of external resources required for the execution of a J2EE web application.

## How does it help?

When applications are deployed on a development, test or production environment, you need to perform integration tests to verify that all systems are working together and provide the application functionality.
Although your application is properly JUnit tested, the validity highly depends on its environment. For instance, the Web container should accept incoming requests, database connections can be established, log files are writeable, etc.
Depending on your level of automation in deployment, problems in the configuration of your environment will prevent the application from working as expected. And even if your environment is setup correctly, will it be available 24x7? SelfDiagnose can help detect such problems during both deployment and in production. Reports can provide information about possible causes of application problems that are related to the availability of external resources.

## How does it work? 

SelfDiagnose provides a collection of diagnostic tasks that check some external or internal aspect required by the application. For instance, the task **CheckURLReachable** verifies that a connection to a server can be opened. The task **CheckJNDIBinding** verifies that a resource can be found in a naming server. **CheckDatabaseTableExists** simply does what it says. Using a simple XML configuration, you specifiy which tasks should run. Running the tasks is requested through a servlet that responds with a report.          

[Full Documentation](http://selfdiagnose.sourceforge.net/)

&copy; 2004-2015. Ernest Micklei, Apache License 2.0