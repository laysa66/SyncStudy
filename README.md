# SyncStudy

JavaFX study management application with admin functionality.

## Features

- **User Authentication**: Login with username/password
- **Admin Dashboard**: Manage user accounts (view, block, unblock, delete)
- **User Management**: Search, filter, and sort users
- **Activity Tracking**: View user activity and block history

## Installation and Launch

### 1. Clone the project

```bash
git clone git@github.com:laysa66/SyncStudy.git
cd SyncStudy
```

### 2. Configure Database Connection

Create a `.env` file in the project root with the database credentials


### 3. Launch the JavaFX application

In a new terminal window, run:

```bash
mvn clean javafx:run
```

### 4. Default Login

Use these credentials to test:

**Admin User:**
- **Username**: `admin`
- **Password**: `admin123`
- The admin user will be redirected to the Admin Dashboard.

**Test Users (regular users):**
````
createTestUserIfNotExists(conn, "laysa.matmar", "password123", "Laysa Matmar", "lm@university.edu", "polytech montpellier", "Computer Science");
createTestUserIfNotExists(conn, "omar.hussein", "password123", "omar hussein Smith", "omar.smith@university.edu", "Stanford", "logics");
createTestUserIfNotExists(conn, "bob.recardo", "password123", "Bob Recardo Tokyo", "tokyo@university.edu", "Harvard", "Physics");

````


## Admin Features

### View All Users
- TableView with search, filter, and pagination
- Columns: Name, Email, University, Registration Date, Status, Last Login, Actions

### Block/Unblock Users
- Block users with reason selection
- View block history
- Automatic session revocation

### Delete Users
- Critical action with DELETE confirmation
- Cascade deletion of all user data

### View User Activity
- Overview tab with user info
- Statistics tab with activity metrics
- Block history tab

## Generate Javadoc 

```bash
mvn javadoc:javadoc
open target/site/apidocs/index.html
```
