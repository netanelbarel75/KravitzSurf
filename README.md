# KravitzSurf - Surf Class Management App

This Android application helps manage surf classes with features for scheduling, weather tracking, and notifications.

## Features

- **User Authentication**: Register and login with Firebase Authentication
- **Class Management**: View and enroll in three types of classes:
  - Group Classes
  - Private Classes
  - Parent & Child Classes
- **Weather Integration**: View current weather conditions and wave height estimations
- **Notifications**: Receive reminders 30 minutes before your scheduled classes
- **User Profiles**: Manage user information including age and gender

## Setup Instructions

### 1. Prerequisites

- Android Studio Bumblebee or later
- Android SDK 24 or higher
- Firebase account
- OpenWeatherMap API key

### 2. Firebase Setup

1. Create a new Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/)
2. Add an Android app with package name: `com.kravitzsurf`
3. Download the `google-services.json` file
4. Replace the placeholder `google-services.json` in the `app/` directory with your actual file
5. Enable the following Firebase services:
   - Authentication (Email/Password)
   - Realtime Database

### 3. Weather API Setup

1. Sign up for a free account at [OpenWeatherMap](https://openweathermap.org/api)
2. Get your free API key (Current Weather Data - Free tier includes):
   - 60 calls/minute
   - 1,000,000 calls/month
3. Open `WeatherActivity.java`
4. Replace `YOUR_API_KEY_HERE` with your actual API key:
   ```java
   private static final String WEATHER_API_KEY = "YOUR_ACTUAL_API_KEY";
   ```

### 4. Database Structure

Create the following structure in Firebase Realtime Database:

```json
{
  "users": {
    "userId": {
      "name": "User Name",
      "email": "user@email.com",
      "age": 25,
      "gender": "Male",
      "enrolledClasses": {
        "classId": true
      }
    }
  },
  "classes": {
    "classId": {
      "title": "Beginner Surf Class",
      "description": "Perfect for beginners",
      "instructor": "John Doe",
      "dateTime": 1735128000000,
      "duration": 90,
      "capacity": 10,
      "price": 50.0,
      "type": "group",
      "location": "Main Beach"
    }
  },
  "enrollments": {
    "classId": {
      "userId": true
    }
  }
}
```

### 5. Build and Run

1. Open the project in Android Studio
2. Sync project with Gradle files
3. Connect an Android device or start an emulator
4. Run the application

## Permissions

The app requires the following permissions:
- Internet access
- Location access (for weather data)
- Notification permissions (Android 13+)
- Alarm scheduling

## Architecture

- **Activities**: Handle UI and user interactions
- **Models**: Data classes for User, SurfClass, and WeatherData
- **Services**: Background services for notifications
- **Utils**: Helper classes for preferences and alarm scheduling
- **Adapters**: RecyclerView adapter for displaying classes

## Security Notes

- Never commit your actual `google-services.json` file
- Keep your API keys secure
- Use environment variables for sensitive data in production

## Development Notes

- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Uses Material Design components
- Built with Java

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
