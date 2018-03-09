# Sashimi

Sashimi is scala based lightweight CMS for professional users.

# Installation

1. Clone repository

2. Install MariaDB (If not installed)

3. Write database setting on src/resources/application.properties

```
ctx.dataSource.url=jdbc:mariadb://localhost/sashimi
ctx.dataSource.user=root
ctx.dataSource.password=pass
```

4. Type sbt command

```
$ sbt reStart
```

5. Access to `http://localhost:9001/` and then access to `http://localhost:9001/admin/`.
user name is `debug` and password is `pass`


