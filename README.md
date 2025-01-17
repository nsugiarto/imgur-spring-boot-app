# Imgur Integration Spring Boot App

This project is a Spring Boot application that integrates with the Imgur API to allow users to:

1. Register with a username, password, and basic information.

2. Upload, view, and delete images after authenticating.

3. Associate images with a user profile.

## Features

- User Registration: Users can register with a username, password, first name, last name, and email.

- Authentication: Protect endpoints using Basic Authentication.

- Image Management: Upload, view, and delete images via the Imgur API.

- Database Integration: User details are stored in an H2 in-memory database.

- Security: Passwords are securely hashed using BCrypt.

## Getting Started

### Prerequisites

1. Java 17 or higher

2. Maven

3. Postman or any REST client (for testing)

### Technologies Used

- Spring Boot 3.x

- Spring Security

- Spring Data JPA

- H2 Database

- OkHttp (for interacting with Imgur API)

## Running the Application

1. Clone the repository:

```
git clone https://github.com/yourusername/imgur-spring-boot-app.git
cd imgur-spring-boot-app
```

2. Build the application:

``` mvn clean install```

3. Run the application:

``` mvn spring-boot:run```

4. Access the H2 database console (optional):

- URL: http://localhost:8080/h2-console

- JDBC URL: jdbc:h2:mem:testdb

- Username: sa

- Password: password

## Configuration

### 1. Application Properties

Located in src/main/resources/application.properties:

```
# Server settings
server.port=8080

# H2 Database
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop

# Imgur API
imgur.client.id=YOUR_CLIENT_ID
imgur.base.url=https://api.imgur.com/3

# Security
spring.security.oauth2.client.registration.imgur.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.imgur.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.imgur.redirect-uri=http://localhost:8080/login/oauth2/code/imgur
spring.security.oauth2.client.registration.imgur.scope=write
```

Replace YOUR_CLIENT_ID and YOUR_CLIENT_SECRET with your Imgur API credentials.

### 2. Imgur API Setup

- Register an application on the Imgur Developer Portal.

- Set the Redirect URI to http://localhost:8080/login/oauth2/code/imgur.

- Copy the Client ID and Client Secret into application.properties.

## Endpoints

### User Management

| Method | Endpoint            | Description         |
|--------|---------------------|---------------------|
| POST   | /api/users/register | Register a new user |

Example Request for Registration:

```
{
"username": "testuser",
"password": "password123",
"firstName": "John",
"lastName": "Doe",
"email": "john.doe@example.com"
}
```

### Image Management

| Method | Endpoint                 | Description     |
|--------|--------------------------|-----------------|
| POST   | /api/images/upload       | Upload an image |
| GET    | /api/images/{imageId}    | View an image   |
| DELETE | /api/images/{deleteHash} | Delete an image |

**Example Request for Image Upload:**

- **Endpoint:** POST /api/images/upload

- **Headers:**

    - **Authorization:** Basic base64(username:password)

    - **Content-Type:** multipart/form-data

- **Body:**

    - image (file)

## Testing the Application

### 1. User Registration:

- Use Postman or curl to send a POST request to /api/users/register.

### 2. Basic Authentication:

- Use the registered username and password for Basic Authentication when accessing the image endpoints.

### 3. Image Management:

- Upload, view, and delete images using the respective endpoints.


## Future Improvements

- Add JWT-based authentication for stateless security.

- Implement image metadata storage in the database.

- Integrate a CI/CD pipeline.

## Contact

If you have any questions or feedback, please feel free to contact me:

- Email: nasugiarto@gmail.com

- GitHub: nsugiarto