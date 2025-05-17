# SkillShare App Server

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![OAuth2](https://img.shields.io/badge/OAuth2-4285F4?style=for-the-badge&logo=google&logoColor=white)
![REST API](https://img.shields.io/badge/REST_API-009688?style=for-the-badge&logo=fastapi&logoColor=white)

A powerful backend server for the SkillShare platform, enabling users to share knowledge, create posts, interact through comments and likes, and build personalized learning plans.

## Features

- **User Authentication**: Secure OAuth2 authentication system.
- **Post Management**: Create, read, update, and delete posts with multimedia support.
- **Social Interactions**: Comment on posts and like content.
- **Learning Plans**: Create and manage personalized learning plans.
- **Media Storage**: Efficient storage and retrieval of images and videos.
- **RESTful API**: Well-documented endpoints for frontend integration.

## Tech Stack

- **Spring Boot**: Backend framework.
- **Firebase Firestore**: NoSQL database.
- **Firebase Storage**: Media file storage.
- **Spring Security**: Authentication and authorization.
- **OAuth2**: User authentication.
- **RESTful API**: Frontend communication.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Firebase account with Firestore and Storage enabled
- OAuth2 credentials (Google, GitHub, etc.)

## Getting Started

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/skillshare-server.git
   cd skillshare-server
   ```

2. **Configure Firebase**:
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
   - Enable Firestore and Storage.
   - Go to **Project Settings** > **Service Accounts**, click **Generate new private key**, and download the JSON file.
   - Rename the file to `firebase-credentials.json` and place it in `src/main/resources/`.
   - **Important**: Add `firebase-credentials.json` to `.gitignore` to prevent committing sensitive data.
   - Update `src/main/resources/application.properties` with your Firebase Storage bucket name:
     ```properties
     firebase.storage.bucket-name=your-firebase-storage-bucket
     ```

3. **Configure OAuth2**:
   - Obtain OAuth2 credentials for Google and/or GitHub from their respective developer consoles.
   - Update `src/main/resources/application.properties` with your credentials:
     ```properties
     # Git OAuth
     spring.security.oauth2.client.registration.github.client-id=your-github-client-id
     spring.security.oauth2.client.registration.github.client-secret=your-github-client-secret
     spring.security.oauth2.client.registration.github.scope=read:user,user:email

     # Google OAuth
     spring.security.oauth2.client.registration.google.client-id=your-google-client-id
     spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
     ```

4. **Build and run the application**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The server will start on `http://localhost:8080`.

## Configuration

The application uses `application.properties` for configuration. A sample structure is:

```properties
spring.application.name=skillshare

spring.security.user.name=user
spring.security.user.password=password

firebase.storage.bucket-name=your-firebase-storage-bucket

# File upload size limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB

# Git OAuth
spring.security.oauth2.client.registration.github.client-id=your-github-client-id
spring.security.oauth2.client.registration.github.client-secret=your-github-client-secret
spring.security.oauth2.client.registration.github.scope=read:user,user:email

# Google OAuth
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
```

Place this file in `src/main/resources/application.properties` and replace placeholder values with your actual credentials.

## API Endpoints

### Authentication
- `GET /api/auth/login`: OAuth2 login.
- `GET /api/auth/logout`: Logout user.

### Posts
- `GET /api/posts/all`: Get all posts.
- `GET /api/posts/{postId}`: Get post by ID.
- `POST /api/posts`: Create a new post.
- `PUT /api/posts/{postId}`: Update a post.
- `DELETE /api/posts/{postId}`: Delete a post.

### Comments
- `GET /api/comments/post/{postId}`: Get comments for a post.
- `POST /api/comments/{postId}`: Add a comment to a post.
- `PUT /api/comments/{commentId}`: Update a comment.
- `DELETE /api/comments/{commentId}`: Delete a comment.

### Likes
- `POST /api/likes/{postId}`: Like a post.
- `DELETE /api/likes/{postId}`: Unlike a post.
- `GET /api/likes/{postId}/status`: Get like status and count.

### Learning Plans
- `GET /api/learning/plans`: Get user's learning plans.
- `POST /api/learning/plans`: Create a learning plan.
- `PUT /api/learning/plans/{planId}`: Update a learning plan.
- `DELETE /api/learning/plans/{planId}`: Delete a learning plan.

## Project Structure

```plaintext
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── skillshare
│   │               ├── config
│   │               ├── controller
│   │               ├── model
│   │               └── service
│   └── resources
│       ├── application.properties
│       └── firebase-credentials.json
```

## Key Components

### Models
- `Post`: Represents user posts with media attachments.
- `Comment`: User comments on posts.
- `Like`: User likes on posts.
- `User`: User profile information.
- `LearningPlan`: Personalized learning plans.

### Controllers
- `PostController`: Handles post-related endpoints.
- `CommentController`: Manages comment operations.
- `LikeController`: Handles like functionality.
- `AuthController`: Manages authentication.
- `LearningPlanController`: Handles learning plan operations.

### Services
- `PostService`: Business logic for posts.
- `CommentService`: Comment management logic.
- `LikeService`: Like functionality logic.
- `FirestoreService`: Database operations.
- `FirebaseStorageService`: Media storage operations.

## Testing

Run tests with Maven:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ for sharing skills and knowledge
