### Description

This is a simple API to manage reservations for a hotel/airbnb.

The application is using Spring Boot, Spring Data JPA, H2, Lombok, Swagger and JUnit.

### API Documentation
To access the swagger, run the application and go to http://localhost:8080/swagger-ui.html

### Architecture
This application has 4 main entities:
* **Property**: Represents a property that can be rented.
* **Booking**: Represents a booking for a property.
* **Guest**: Represents a guest that can book a property.
* **Block**: Represents a blocking period for a property.

### TODO
* Improve update logic, currently when a new field is added to the entity whe need to change the update method.
* Improve status logic to use PENDING.
* Improve error message for overbooking.