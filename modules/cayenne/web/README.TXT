This is a JPublish demo web application using the new Cayenne support.

With a wealth of unique and powerful features, Cayenne can address a wide range of
persistence needs. Cayenne seamlessly binds one or more database schemas directly to
Java objects, managing atomic commit and rollbacks, SQL generation, joins,
sequences, and more. With Cayenne's Remote Object Persistence, those Java objects
can even be persisted out to clients via Web Services. Or, with native XML serialization,
objects can be even further persisted to non-Java clients - such as an Ajax-capable browser.

For more info regarding the Cayenne library, visit the following links:
http://cayenne.apache.org/


With this demo we will demonstrate the core Cayenne functionality with a simple TODO web
application where you can add/delete/edit users and their todos. This is just a very simple
demo, done in less than an hour, therefore for more details about Cayenne you should first
visit their site for updated documentation and examples.

DEPLOYING
We are presuming you're using Jetty 6.1.3, but any Servlet container will do it. The database
used by this demo (todos_dev) is using the H2 engine; http://www.h2database.com/html/frame.html,
but the application was also successfully tested with mySQL 4/5 (InnoDB).

1. after you unzipped the demo into the Jetty's webapps folder, copy the pre-initialized database
   files to a folder of your convenience. For example, on my machine the database is deployed in:
   ~/db, as:
		todos_dev.1.log.db
		todos_dev.data.db
		todos_dev.index.db
		todos_dev.trace.db

   The H2 database we're using for our demo is called: todos_dev, and can be found in your downloaded
   folder here: cayenne/db

2. modify the WEB-INF/cayenne/cayennedemoNode.driver.xml file and update the url value with the folder
   path where you installed the todos_dev database. In our demo, the url used is:
        <url value="jdbc:h2:~/db/todos_dev"/>

3. start Jetty and point your browser to http://localhost:8080 and select the jpcayenne application.

You're more than welcome to help us improve the JPublish-Cayenne support by sending us your feedback.

Thank you,
[2007.10.12] Florin