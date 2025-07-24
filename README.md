# 🌦️ Android Weather App

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Java-blue)
![Build](https://img.shields.io/badge/Build-Gradle-ff69b4)
![API](https://img.shields.io/badge/API-OpenWeatherMap-orange)

A simple and elegant Android Weather App developed using **Java** in **Android Studio**, utilizing the **OpenWeatherMap API** to provide real-time weather data with clean UI elements and modern weather icons.

<p align="center">
  <img src="https://github.com/user-attachments/assets/4da5c689-99a3-4623-a05b-ef5dcaee5a21" alt="Home Screen" width="300" />
</p>

## 📱 Features

- 🌍 Get real-time weather updates by city or GPS location  
- 🌡️ Display current temperature, feels like, humidity, wind speed, and pressure  
- 🌤️ Auto-updating weather icons based on conditions  
- 🔄 Manual refresh button for live sync  
- 🌙 Day/Night theme adaptation  
- 📦 Gradle handles all dependencies automatically  

## 🗂️ Project Structure

```
WeatherApp/          ← Root Project Directory
├── app/             ← App module
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   ├── java/
│   │   ├── com.example.weatherapp/
│   │   │   └── MainActivity.java (or .kt)
│   │   ├── com.example.weatherapp (androidTest)/
│   │   └── com.example.weatherapp (test)/
│   ├── res/         ← Resource files
│   │   ├── drawable/   ← Images, shapes, etc.
│   │   ├── layout/     ← XML layout files (e.g., activity_main.xml)
│   │   ├── mipmap/     ← App icons
│   │   ├── values/     ← Strings, colors, styles, etc.
│   │   └── xml/        ← Additional XML files (e.g., preferences)
│   └── res (generated)/
├── java (generated)/
└── Gradle Scripts/   ← build.gradle files, settings.gradle, etc.
```

## 🧰 Tech Stack & Tools

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![XML](https://img.shields.io/badge/XML-E44D26?style=for-the-badge&logo=xml&logoColor=white)
![OpenWeatherMap](https://img.shields.io/badge/API-OpenWeatherMap-orange?style=for-the-badge)
![Material Design](https://img.shields.io/badge/UI-Material%20Design-blue?style=for-the-badge&logo=material-design)

## 🚀 Getting Started

Follow these steps to get the project up and running on your local machine.

### 1. Clone the Repository
```bash
git clone https://github.com/ShiningStar-AI/WeatherApp.git
```

### 2. 🧑‍💻 Open in Android Studio

   1. Open Android Studio.

   1. Select File → Open, then choose the root folder of the cloned project.

   1. Allow Gradle to sync and download all necessary dependencies.

### 3. 🔧 Configure API Key

This project requires a key from OpenWeatherMap to fetch weather data. The setup is designed to keep your key secure and out of the main source code.

   1. Sign up on OpenWeatherMap and get your free API key.

   1. In the root directory of your project in Android Studio (the same level as app and gradle folders), create a new file named local.properties.

   1. Add your API key to this local.properties file in the following format:
```
apiKey="YOUR_OWN_API_KEY_HERE"
```
   4. Sync your project with Gradle one more time. The app will now be able to securely access your key.

### 4. 📱 Required Permissions

For the app to function as intended, the following permissions are required and are declared in the `AndroidManifest.xml` file:

* **Internet Access**: Allows the app to connect to the OpenWeatherMap API and fetch live weather data.
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    ```

* **Fine Location Access**: Allows the app to get the device's precise GPS location to provide location-based weather updates.
    ```xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ```

### 5. ▶️ Run the App
You can now build and run the app on a physical Android device or an emulator using the green '▶️' Run button in Android Studio.
