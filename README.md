# java-shareit
ShareIt is a service that allows users list their items, and rent other ones. Owners can view rental requests for their
items and then approve or reject bookings. Bookers can leave comments about items they rented.

---

## Technologies
The project is developed using:

- Java 11
- Spring Boot
- Hibernate
- Maven
- Lombok
- PostgreSQL
- H2

## Features

### User
- Create a user
- Get a user by id
- Update a user
- Delete a user
- Get a list of all users

### Item
- Create an item
- Update an item
- Get an item by id
- Get all items by owner
- Search for items by text
- Add a comment to an item

### Booking
- Create a booking
- Confirm a booking
- Get all bookings for a user
- Get all bookings for items owned by a user
- Get a booking by id

### Request
- Add a booking request
- Get a booking request by id
- Get booking requests with answers
- Get booking requests of other users

## DB schema

![shareIt](https://github.com/MelTrevelyan/java-shareit/assets/114815793/77fa1b57-010b-4484-a7be-4af584e99381)
