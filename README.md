# Eventology Android Application

This repository contains the **Android mobile application** for **Eventology**, a cultural event management platform developed in collaboration with the city council to manage the "Edifici de la Cultura." The app is developed in **Kotlin** and enables both users and event organizers to interact with the event system in a modern and efficient way.

## 📱 Features

### **User Roles & Functionality**
- **Normal Users**:
  - Self-register via the mobile app.
  - Log in and browse upcoming events.
  - Reserve tickets, including choosing specific seats (numbered or general).
  - Access personal profile and reservation history.

- **Event Organizers**:
  - All normal user capabilities.
  - Create and manage cultural events.
  - Reserve one or more venues for events (single-date or period).

- **Messaging System**:
  - Real-time chat with other users (see module from M09).

## 🧱 Technical Requirements

- Developed in **Kotlin** using **Android Studio**.
- Follows the MVVM architecture with **Activities**, **Fragments**, and **RecyclerView**.
- Data managed via remote API (RESTful) and locally via ViewModels.
- Event images stored on the server; UI icons and graphics in `drawable/`.
- Multi-language support: Catalan, Spanish, and English.
  - Auto-detects system language with option to change manually.
- **Responsive Design**:
  - Optimized layout for both mobile phones and tablets.
- **Multimedia Integration**:
  - Users can capture videos or photos and upload them to event profiles.
- **Seat Selection UI**:
  - Users can select seats graphically (optional integration with **LibGDX**).

## 🚀 Installation

1. Clone the repository:
```bash
git clone https://github.com/Eventolog/android-app.git
```
2. Open the project in Android Studio.
3. Configure the API endpoint in the application settings.
4. Build and run on an emulator or physical device.

## 📦 Dependencies
- Retrofit2 (API communication)
- Glide or Picasso (image loading)
- Room (local data caching)
- Material Components
- Optional: LibGDX (seat selection)

## 👥 Contributing
We welcome contributions! Feel free to fork the repository, submit issues, or create pull requests to improve the application.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📬 Contact
If you have any questions or suggestions, feel free to contact us at 148581386+rwxce@users.noreply.github.com.
