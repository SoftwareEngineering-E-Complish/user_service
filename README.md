# User Manager

This service provides functionality for user management, including user authentication, gathering user details and user account management.


## Prerequisites (skip if you want to run the mocked User Manager)

Before you begin, ensure you have met the following requirements:

1. **Setup the following environment variables:**

    ```bash
    USER_SERVICE_AWS_ACCESS_KEY_ID=***
    USER_SERVICE_AWS_SECRET_ACCESS_KEY=***
    USER_SERVICE_AWS_REGION=***

    COGNITO_CLIENT_ID=***
    COGNITO_USER_POOL_ID=***
    COGNITO_HOSTED_UI_BASE_URL=***
    ```
    

## Local Development

To run the application locally, follow these steps:

1. **Clone the repository:**

   ```bash
   git clone https://github.com/SoftwareEngineering-E-Complish/user_service.git
   cd your-repository
   ```

2. **Build the application:**
    
    ```bash
    mvn clean install
    ```

3. **Run the application:**
    
    ```bash
    mvn spring-boot:run
    ```

    The application will start and be accessible at http://localhost:8080.


## Docker Container

To run the application in a Docker container, you'll need Docker installed on your machine. Follow these steps:

1. **Build the Docker image:**
    
    ```bash
    docker build -t <your-image-name> .
    ```

2. **Run the Docker container:**
    
    ```bash
    # real User Manager (AWS Cognito)
    docker run -e USER_SERVICE_AWS_REGION=$USER_SERVICE_AWS_REGION -e USER_SERVICE_AWS_ACCESS_KEY_ID=$USER_SERVICE_AWS_ACCESS_KEY_ID -e USER_SERVICE_AWS_SECRET_ACCESS_KEY=$USER_SERVICE_AWS_SECRET_ACCESS_KEY -e COGNITO_CLIENT_ID=$COGNITO_CLIENT_ID -e COGNITO_USER_POOL_ID=$COGNITO_USER_POOL_ID -e COGNITO_HOSTED_UI_BASE_URL=$COGNITO_HOSTED_UI_BASE_URL -p 8080:8080 <your-image-name>
   
    # mocked User Manager
    docker run -p 8080:8080 <your-image-name>
    ```

The application will start within the Docker container and be accessible at http://localhost:8080.


## API Documentation

## Base URL
The base URL for all endpoints is: `http://localhost:8005`

## Endpoints

### Get Login URL
- URL: `/loginURL`
- Method: `GET`
- Description: Retrieves the login URL for the Hosted UI.
- Response: Returns the login URL as a string.
- Throws: `URISyntaxException` if there is an issue with the URI syntax.

### Get Signup URL
- URL: `/signupURL`
- Method: `GET`
- Description: Retrieves the signup URL for the Hosted UI.
- Response: Returns the signup URL as a string.
- Throws: `URISyntaxException` if there is an issue with the URI syntax.

### Get Logout URL
- URL: `/logoutURL`
- Method: `GET`
- Description: Retrieves the logout URL.
- Response: Returns the logout URL as a string.
- Throws: `URISyntaxException` if there is an issue with the URI syntax.

### Get Session
- URL: `/session`
- Method: `GET`
- Description: Retrieves the user's session.
- Request Parameter: `authorizationCode` (String)
- Response: Returns user session in the form of `UserSessionResponseDTO` (Fields: accessToken, tokenType, idToken, refreshToken, expiresIn).
- Throws: `URISyntaxException`, `IOException`, `InterruptedException`.

### Update User
- URL: `/updateUser`
- Method: `POST`
- Description: Updates user information.
- Request Body: `UpdateUserDTO` (Fields: username, email, name, phoneNumber)
- Response: Returns `true` if the user is successfully updated; otherwise, `false`.

### Change Password
- URL: `/changePassword`
- Method: `POST`
- Description: Changes user password.
- Request Body: `ChangePasswordDTO` (Fields: accessToken, oldPassword, newPassword)
- Response: Returns `true` if the password is successfully changed; otherwise, `false`.

### Delete User
- URL: `/deleteUser`
- Method: `GET`
- Description: Deletes a user.
- Request Parameter: `accessToken` (String)
- Response: Returns `true` if the user is successfully deleted; otherwise, `false`.

### Verify Access Token
- URL: `/verifyAccessToken`
- Method: `GET`
- Description: Verifies the validity of an access token.
- Request Parameter: `accessToken` (String)
- Response: Returns `true` if the access token is valid; otherwise, `false`.

### Refresh Access Token
- URL: `/refreshAccessToken`
- Method: `GET`
- Description: Refreshes the access token and retrieves new user session.
- Request Parameter: `refreshToken` (String)
- Response: Returns user session in the form of `UserSessionResponseDTO` (Fields: accessToken, tokenType, idToken, refreshToken, expiresIn).

### Get User Details
- URL: `/user`
- Method: `GET`
- Description: Retrieves user details.
- Request Parameter: `accessToken` (String)
- Response: Returns user details in the form of `UserResponseDTO` (Fields: username, email, name, phoneNumber).

### Get User ID
- URL: `/userId`
- Method: `GET`
- Description: Retrieves the user ID.
- Request Parameter: `accessToken` (String)
- Response: Returns the user ID as a string.

