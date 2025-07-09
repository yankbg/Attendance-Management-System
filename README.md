# 📝 Attendance Management System

Welcome to the **Attendance Management System**! 🎓📱  
A simple, flexible solution for tracking attendance using QR code scanning—no login required!

---

## 👥 Authors

- **Java Mobile App:** [Yannick Kabunga](https://github.com/yankbg) 📱
- **PHP Backend:** [Bwenge Muhima](https://github.com/buxo712) 🖥️

---

## ⚡️ How It Works: QR Code Attendance!

### 🖥️ Web (PHP Backend) — QR Code Generator

- The web backend is responsible for **generating unique QR codes** for each attendance session.
- Teachers or organizers display the QR code on a screen or projector during attendance time.
- Anyone can generate and display QR codes—no account or login needed!

### 📱 Mobile App (Java) — QR Code Scanner

- The mobile app is designed for **scanning the QR codes** shown by the web backend.
- Students/participants simply open the app, scan the QR code, and their attendance is recorded instantly!
- No login, no hassle—just scan and you’re done!

> **✨ The heart of the system is QR code scanning:**  
> - The web backend provides the QR code  
> - The mobile app scans it  
> - Attendance is tracked smoothly!

---

## ⚙️ Project Structure

```
Attendance-Management-System/
│
├── java_mobile_app/        # Java-based mobile app (QR scanner)
├── php_backend/            # Classic PHP backend (QR generator)
├── docs/                   # Documentation and guides
└── README.md
```

---

## 🛠️ Utilities & Features

- 🚀 **QR Code Powered:**  
  Seamlessly connects web and mobile using QR codes for attendance.
- 🆓 **No Login Needed:**  
  System is open—anyone can generate or scan!
- 🔗 **Configurable Backend:**  
  Switch between temporary and your own PHP backend by changing the URL in the app.
- 📈 **Simple Analytics:**  
  View or export attendance records from the backend.

---

## 📅 Important Note

> **The default backend (AttendanceSystemRail) will only be available for 21 days from 2025-07-08.**
>
> After this period, visitors **must switch to the PHP backend** by updating the API URL in the mobile app.

---

## 🔄 Switching to the PHP Backend

1. **Set up the PHP server:**
   - Host the contents of the `php_backend/` folder on your server.
   - Make sure your server supports PHP and has a database as described in `php_backend/README.md`.

2. **Change the API URL in the Java app:**
   - Open the Java mobile app source code.
   - Locate the API base URL (usually in a config or constants file).
   - Replace the current URL with your PHP server's endpoint.

   ```java
   // Example (change this line in your app)
   String BASE_URL = "https://yourdomain.com/php_backend/api/";
   ```

---

## 🌟 Why Use This Project?

- **No Authentication:** Open for anyone—no sign-up or login!
- **QR Code Simplicity:** Fast, contactless attendance via scanning.
- **Customizable & Portable:** Use any backend, deploy anywhere.
- **Perfect for Events, Classes, and More!**

---

## 🏁 Get Started

1. Clone this repo:  
   ```bash
   git clone https://github.com/yankbg/Attendance-Management-System.git
   ```
2. [Follow the setup instructions in `/docs/SETUP.md`](docs/SETUP.md)

---

## 🙏 Credits

- Web QR Generator: [Bwenge Muhima](https://github.com/buxo712)
- Mobile QR Scanner: [Yannick Kabunga](https://github.com/yankbg)
- Initial backend (temporary): [AttendanceSystemRail](https://attendancesystemrail.com)

---

## 🗒️ License

MIT — Feel free to use, modify, and contribute!

---

**Scan. Attend. Done!** 🚀📲
