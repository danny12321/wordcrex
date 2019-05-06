# Wordcrex
Repository for the second project at the Avans University of Applied Sciences, re-creating the mobile game Wordcrex in Java.

## Requirements
- Git ([Windows](https://git-scm.com/download/win) | [Linux](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) | [macOS](https://git-scm.com/download/mac)) installed
- [JDK 11 or higher](https://www.oracle.com/technetwork/java/javase/downloads/index.html) installed
- [MySQL server](https://dev.mysql.com/downloads/mysql/) running locally

## Set-up
You can use a Git client like [GitKraken](https://www.gitkraken.com/) or [GitHub Desktop](https://desktop.github.com/) to import the project.  
Otherwise you can open the terminal/command prompt in your projects folder (wherever that may be) and run the following:

```
git clone https://github.com/danny12321/wordcrex.git
```

Open the project in your IDE/editor of choice and make sure you import it as a **Gradle** project.
> Gradle takes care of external libraries used in the application like the *MariaDB connector*

## Running the application
Before running please make sure that `src/main/resources/db/` contains a config for your database.  
You can create a `[name].properties` file with the following content:

```properties
dataSourceClassName=org.mariadb.jdbc.MariaDbDataSource
dataSource.user=root
dataSource.password=password
dataSource.databaseName=wordcrex
dataSource.portNumber=3306
dataSource.serverName=localhost
```

Edit the file according to your MySQL server config and import the `db.sql` to fill your local database.
> Make sure you add the name of the `.properties` file to the program arguments. Otherwise it will use `prod.properties`

---

Now you should be able to run/debug the project and it should open the application login screen.  
You can login by creating an entry in the `user` table within your local database.  

## Resources
- See [the application design on Figma](https://www.figma.com/proto/DVMqm1dfQfkmF8OwhxuPGqnc/Wordcrex?node-id=0%3A1&scaling=scale-down)
- EER of the database (see below)

![](https://i.imgur.com/isPEUgz.png)
