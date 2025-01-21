# LittlepayPayments Application

This application simulates a system for tracking and calculating charges for bus trips based on 
passenger "tap on" and "tap off" actions.\
When passengers board a bus, they "tap on" by scanning their credit card at a bus stop. 
When they get off, they "tap off" at another stop. The system processes these taps, determines the fare for each trip, 
and generates a summary of completed, cancelled, and incomplete trips.

All tap records are provided in the [taps.csv](src/main/resources/taps.csv) file, which can be found under 
the resources directory. When the application is run, the generated trip summary will be saved 
in the [trips.csv](src/main/resources/trips.csv) file under the same resources directory.

### Prerequisites
- Java 21 or newer
- Maven [recommended to use `./mvnw`]

### How to run tests
- Go to projects directory `.LittlepayTravelCharge`
- In Terminal, run `./mvnw clean install` or `./mvnw test`

### How to run application
- Go to projects directory `.LittlePayTravelCharge`
- In Terminal, run `./mvnw clean install`
- In Terminal, run `./mvnw exec:java`\
Note: The [trips.csv](src/main/resources/trips.csv) file has already been committed. If you run the application 
the file will be regenerated.

### Assumptions
- As mentioned in the problem description, I assumed that the input file will be well-formed and not missing data.
- Based on the example file data, I made an assumption that passenger can forget to tap on but taps off at another station.
- Based on the example file data, I made an assumption that tap IDs are numbers.
- I'm not currently checking the gap between the tap-on and tap-off events, but this should be taken into consideration 
    in a real system.

### Design decisions and trade-offs
- Added data validation logic to check if PANs in the input file are valid [passes Luhn algorithm check]
    and also check if DateTimeUTC values are valid.
- When a trip is INCOMPLETE, I made the decision to set the finished time to the end of the day, 
    instead of passing null. I believe that, in combination with the status, it clearly indicates that the trip 
    was not completed. This approach simplifies the code and eliminates doubts about missing data.
- Before building the Taps, all headers in the CSV file are converted to uppercase to ensure that the application
    does not fail if the header letters are changed to lowercase or uppercase.
- Added Maven Wrapper to eliminate the need for developers to set it up themselves and to ensure consistency by using the same Maven version.

### If I had more time
