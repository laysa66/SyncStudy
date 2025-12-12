# SyncStudy

JavaFX study management application.

## Installation and Launch

### 1. Clone the project

```bash
git clone git@github.com:laysa66/SyncStudy.git
cd SyncStudy
```
### 2. Start PostgreSQL server
```bash
docker-compose up postgres
```
The database will be accessible on localhost:5432 with credentials:

    - Database: syncstudy
    - User: postgres
    - Password: postgres

### 3. Launch the JavaFX application
in a new terminal window, run:
```bash
mvn clean javafx:run
```
This will compile and launch the SyncStudy application and automatically connect to the PostgreSQL database.