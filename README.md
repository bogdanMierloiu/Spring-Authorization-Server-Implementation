# Spring-Authorization-Server-Implementation

## OVERVIEW
The Spring Authorization Server project introduces a new technology for managing user authentication and authorization within applications. This server provides a user-friendly web interface where users can create accounts using their name, surname and email address. Upon registration, users receive an activation code via email to verify their accounts. Once logged in, users can manage their accounts, change passwords, and perform other account-related actions. As an administrator, you have access to the admin console, where you can manage client applications. This includes adding client applications with client ID, client secret, scope, and redirect URI. Client applications can then interact with the authorization server using the OAuth 2.0 flow.

## FEATURES
* User Registration: Users can create accounts with their personal information.
* Email Verification: Upon registration, users receive an activation code via email to verify their accounts.
* Reset Password: Users can reset their passwords using the "Forgot Password" feature.
* Help form: Users can send a message to the administrator.
* User Console: Logged-in users can manage their accounts for changing passwords or for request admin role.
* Admin Console: Administrators can manage client applications, including adding and editing their details.
* OAuth 2.0 Flow: Client applications can interact with the authorization server using the OAuth 2.0 protocol.
* Token Settings: Administrators can edit token settings, including time-to-live for access and refresh tokens.

## Getting Started
1. Clone the repository to your local machine.
2. Configure the necessary properties in the application.properties files(I left comments in the files to help you).
3. Ensure you have Liquibase installed and configured. In the "changelog-0.0.1.xml", replace "bogdan.mierloiu01@gmail" with your personal email and password encrypted with BcryptPasswordEncoder. This will automatically insert your credentials with an admin role.
4. Build and run the application using Maven.
5. Access the web interface to manage client applications.
6. Utilize the OAuth 2.0 flow to authorize client applications and obtain access tokens(I have [Oauth-2.0-Connect-Flow](https://github.com/bogdanMierloiu/OAuth-2.0-Connect-Flow) as an example).
7. Use server as issuer for JWT tokens in your backend application.(I have [Oauth-2.0-API-Flow](https://github.com/bogdanMierloiu/Oauth-2.0-API-Flow) as example).
8. Enjoy!

## Dependencies
Spring Boot: Provides the framework for building the application.
Spring Security: Implements security features, including user authentication and OAuth 2.0 support.
Thymeleaf: Used for server-side templating to generate web pages.
JavaMail API: Sends activation emails to users for account verification.

## Usage
Integrate client applications with the authorization server using the OAuth 2.0 flow.

## Contributing
Contributions to the project are welcome! Please fork the repository, make your changes, and submit a pull request.
