# LittlepayPayments Application

### Prerequisites
- Java 21 or newer
- Maven [recommended to use `./mvnw`]

### How to run tests
- Go to projects directory `.LittlepayTravelCharge`
- In Terminal, run `./mvnw clean install` or `./mvnw test`

### How to run application
- Go to projects directory `.LittlePayTravelCharge`
- In Terminal, run `./mvnw clean install`
- In Terminal, run `./mvnw exec:java`

### Assumptions

### Additional functionalities

### Design decisions and trade-offs
- When a trip is INCOMPLETE, I made the decision to set the finished time to the end of the day, 
    instead of passing null. I believe that, in combination with the status, it clearly indicates that the trip 
    was not completed. This approach simplifies the code and eliminates doubts about missing data.
- Added Maven Wrapper to eliminate the need for developers to set it up themselves and to ensure consistency by using the same Maven version.

### If I had more time
