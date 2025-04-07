# Smart Ugandan Health Companion App (SUHC)

A comprehensive health companion app designed specifically for Ugandans, providing health tracking, AI-powered symptom checking, emergency SOS features, and multilingual support.

## Features

- **Health Tracking**: Monitor blood pressure, blood sugar, weight, mood, and water intake
- **AI Symptom Checker**: Get AI-powered diagnosis based on symptoms
- **Emergency SOS**: Send location and health data to emergency contacts
- **Multilingual Support**: Available in English, Luganda, and Runyankole
- **Offline Functionality**: Works even without internet connection
- **QR Medical ID**: Generate a QR code with vital medical information

## Technical Stack

### Mobile App (Android)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend Integration**: Firebase (Auth, Firestore, Cloud Messaging)
- **Location Services**: Google Maps SDK
- **Charts**: MPAndroidChart
- **Notifications**: Firebase Cloud Messaging & WorkManager

### Backend API (Symptom Checker)
- **Language**: Python
- **Framework**: Flask
- **Deployment**: Can be deployed on PythonAnywhere, Render, or any other hosting service

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Python 3.8+ (for the API)
- Firebase account
- Google Maps API key

### Android App Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/SmartUgandanHealthCompanionAppSUHC.git
   ```

2. Open the project in Android Studio.

3. Create a `local.properties` file in the root directory and add your Google Maps API key:
   ```
   MAPS_API_KEY=your_google_maps_api_key
   ```

4. Set up Firebase:
   - Create a new Firebase project
   - Add an Android app to the project with package name `com.the4codexlabs.smartugandanhealthcompanionappsuhc`
   - Download the `google-services.json` file and place it in the `app` directory
   - Enable Authentication, Firestore, and Cloud Messaging in the Firebase console

5. Build and run the app on an emulator or physical device.

### API Setup

1. Navigate to the API directory:
   ```
   cd api
   ```

2. Create a virtual environment:
   ```
   python -m venv venv
   ```

3. Activate the virtual environment:
   - Windows: `venv\Scripts\activate`
   - macOS/Linux: `source venv/bin/activate`

4. Install dependencies:
   ```
   pip install -r requirements.txt
   ```

5. Run the Flask API:
   ```
   python app.py
   ```

6. The API will be available at `http://localhost:5000`.

## API Usage

The Symptom Checker API has a single endpoint:

### POST /diagnose

Request body:
```json
{
  "symptoms": ["fever", "cough", "fatigue"]
}
```

Response:
```json
{
  "condition": "Malaria or Flu",
  "recommendation": "Drink fluids, rest, and visit clinic if symptoms persist",
  "confidence": 78
}
```

## Multilingual Support

The app supports three languages:
- English (default)
- Luganda
- Runyankole

Users can change the language in the Profile/Settings screen.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Thanks to the Ugandan healthcare community for providing guidance on local health needs
- Special thanks to translators who helped with the Luganda and Runyankole versions