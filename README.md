
# Red Restaurant App

The **Red Restaurant App** is a sophisticated Android-based food delivery application designed specifically for a restaurant's online ordering system. It provides a seamless experience for users to browse the restaurant's menu, customize their orders, and schedule deliveries. The app also supports both **Dark Mode** and **Light Mode** themes, includes a **notification system** for order updates, and integrates **Google Maps** location support. Furthermore, it uses **Firebase Authentication**, **Firestore**, and **Realtime Database** for secure user authentication, real-time order management, and storing order data.

This app is developed using **Android Studio** with **Java** as the programming language.

## Features

- **Browse Menu**: View the full menu with descriptions, images, and pricing for each item.
- **Customizable Orders**: Customize menu items (e.g., adding extra toppings, removing ingredients).
- **Google Maps Location Support**: Track the location of your delivery in real-time via Google Maps. Users can also choose their delivery location using the map.
- **Dark Mode and Light Mode**: Seamlessly switch between dark and light themes based on user preference.
- **Order Notifications**: Real-time push notifications to update users about the status of their orders (e.g., order confirmed, out for delivery).
- **Seamless Checkout**: Easy-to-use checkout system for completing orders securely.


## Prerequisites
To run the app, you need to have the following installed on your system:
- **Android Studio**(koala or higher)
- **Java SDK**(version 8 or higher)
- **Android SDK**(with API level 21 or higher)
## Installation
**1. Clone the Repository**

To get started, clone this repository to your local machine:

```bash
  git clone https://github.com/yourusername/RedRestaurantApp.git
```

**2. Open the Project in Android Studio**
- Open **Android Studio**.
- Select **Open an existing project** and navigate to the folder where you cloned the repository.

**3. Set Up Firebase**
To enable **Firebase Authentication**, **Firestore**, and **Realtime Database** in your app, follow these steps:

- Go to the [Firebase Console]("https://firebase.google.com/")
- Create a new Firebase project (or select an existing one).
- Enable the following Firebase services:
    - **Firebase Authentication** (Google Authentication)
    - **Firebase Firestore Database** (for storing menu and user order data).
    - **Realtime Database** (for status updates and notifications)
- Download the ```google-services.json``` file and place in the ```app/``` directory of your project.

**4. Install Google Maps SDK**

To enable Google Maps functionality, you need to integrate the **Google Maps SDK for Android**.
- Go to the [Google Cloud Console]("https://cloud.google.com/cloud-console")
- Create a new project (or select an existing one).
- Enable the **Google Maps Android API** for your project.
- Generate an **API Key** and add it to your ```secrets.properties``` file located in the ```root``` directory (if not exists, create a file add the API Key as follows).
```bash
MAPS_API_KEY=<YOUR_API_KEY>
```

**5. Sync Gradle Files**
Once the project is loaded, sync the project with the Gradle files to ensure all dependencies are downloaded.

**6. Build and Run**
- Select your preferred Android device or emulator.
- Click on **Run** button (the green play button) in Android Studio to install and run the app on your device.    
## Screenshots

<div>
  <img src="https://i.ibb.co/4WKH4Lc/Screenshot-2024-11-27-12-22-42-32-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/pyZBTGg/Screenshot-2024-11-27-12-22-26-59-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/j3qkxWZ/Screenshot-2024-11-27-12-23-02-35-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/WtvKG2Z/Screenshot-2024-11-27-12-20-15-56-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/mN6dwPf/Screenshot-2024-11-27-12-24-01-27-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/Tk2qtHH/Screenshot-2024-11-27-12-20-28-15-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/mS6Z0CC/Screenshot-2024-11-27-12-24-11-73-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/kc1Y6Fz/Screenshot-2024-11-27-12-20-40-79-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/YRnKPw5/Screenshot-2024-11-27-12-23-53-49-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/Y8VqtS0/Screenshot-2024-11-27-12-21-33-75-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/4NzFWFT/Screenshot-2024-11-27-12-23-45-22-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/HTYFssW/Screenshot-2024-11-27-12-22-06-00-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/c6CVyHq/Screenshot-2024-11-27-12-23-30-03-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/PjMYqCq/Screenshot-2024-11-27-12-21-43-07-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/x5SyrHb/Screenshot-2024-11-27-12-21-11-25-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
  <img src="https://i.ibb.co/xfCwqQF/Screenshot-2024-11-27-12-23-23-08-e33103b46cd9e081972a488dd1ee15bd.jpg" width="240">
</div>




## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/DilanSriyantha/red-restaurant-app?tab=MIT-1-ov-file) file for details.

## Acknowledgements

- **Firebase** for Authentication, Firestore, and Realtime Database.
- **Google Maps SDK** for location-based services.
- **Android Studio** for providing the development environment.
- **Material Design** for the user interface guidelines.
