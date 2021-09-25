<div align="center">
  <img src="https://github.com/dotphin/MilkshakeORM/raw/main/docs/assets/icon.png" width="78px" alt="Milkshake Logo" />
  <h1>MilkshakeORM</h1>
  <p>MultiORM/ODM for Java. Supports MongoDB, MySQL, MSSQL, PostgreSQL, Oracle, SQLite.</p>
</div>

## What is MilkshakeORM?

Several of our projects use databases to persist data over time. It is very common that we use several or it also depends on the comfort of the client and that is why using the direct drivers to connect to this is very tedious, especially when adapting the different types of databases to the application and that everything will work as it should.

That is why we have created MilkshakeORM which imposes a single way to manipulate the database which will be shared with any engine within the library's capabilities. It also helps to validate and secure data against duplication or define complex schemes on how it will be stored. This undoubtedly helps us a lot when it comes to development.

## Helpful Resources

- [Bug Report](https://github.com/dotphin/MilkshakeORM/issues/)
- [Documentation](https://docs.dotphin.com/milkshake)
- [Donate](https://paypal.me/sammwy)
- [Dev Twitter](https://twitter.com/sammwy)
- [Website](https://dotphin.com/milkshake)

## Supported Database engine

- [x] MongoDB
- [ ] MySQL
- [ ] SQLite
- [ ] PostgreSQL
- [ ] MariaDB
- [ ] Microsoft SQL Server (Maybe)
- [ ] Redis (Maybe)
- [ ] Oracle (Maybe)
- [ ] DynamoDB (Maybe)
- [ ] Cassandra (Maybe)
- [ ] Couchbase (Maybe)

## ToDo List

- [ ] Entity validation (required, unique, trim, lower, upper, length, min, max)
- [ ] Entity CRUD operations.
- [ ] Repository CRUD operations.
- [ ] Restart database connections.
- [ ] Improve Nested objects in Entities.
- [ ] Documentation
