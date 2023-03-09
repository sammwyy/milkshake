<div align="center">
  <img src="https://github.com/sammwyy/milkshake/raw/main/docs/assets/icon128.png" width="78px" alt="Milkshake Logo" />
  <h1>Milkshake</h1>
  <p>MongoDB ORM/ODM for Java.</p>
</div>

## What is Milkshake?

Several of our projects use databases to persist data over time. It is very common that we use several or it also depends on the comfort of the client and that is why using the direct drivers to connect to this is very tedious, especially when adapting the different types of databases to the application and that everything will work as it should.

That is why we have created MilkshakeODM which imposes a single way to manipulate the database which will be shared with any engine within the library's capabilities. It also helps to validate and secure data against duplication or define complex schemes on how it will be stored. This undoubtedly helps us a lot when it comes to development.

## Helpful Resources

- [Bug Report](https://github.com/sammwyy/milkshake-odm/issues/)
- [Documentation](https://github.com/sammwyy/milkshake-odm/wiki)
- [Donate](https://paypal.me/2lstudios)
- [Dev Twitter](https://twitter.com/sammwy)
- [Website](https://sammwy.com/milkshake)

## ToDo List

- [ ] Entity validation (required, unique, trim, lower, upper, length, min, max)
- [x] Entity CRUD operations.
- [x] Repository CRUD operations.
- [ ] Restart database connections.
- [ ] Improve Nested objects in Entities.
- [x] Documentation.
- [x] Reuse connections.
