# DigiScribe 🖋️  
**Handwritten Digit Recognition App**

---

## 📘 Overview

**DigiScribe** is a full-stack handwritten digit recognition system that allows users to draw digits on-screen and get real-time recognition results powered by a trained deep learning model.  
It supports both **mobile** and **PC** applications, combining AI with an intuitive interface for learning, management, and deployment.

---

## 🎯 Project Goal

- Build a deep learning–based digit recognition app with **>90% accuracy**.  
- Provide both **frontend (React/Vue)** and **backend (Flask + Spring Boot)** integration.  
- Enable **model management** and **training visualization** for administrators.  
- Support **continuous handwriting recognition** and user data management.

---

## 🧩 System Architecture

| Component | Description |
|------------|-------------|
| **Frontend (React/Vue)** | Canvas drawing, image capture, and result display. |
| **Backend (Flask API)** | Handles image preprocessing and model inference. |
| **Database (MySQL / SQLite)** | Stores user profiles and prediction history. |
| **Admin Console (Spring Boot)** | Provides management of users, models, and training workflows. |
| **AI Model (Python + CNN)** | Core deep learning model trained on MNIST dataset. |

### 🔄 Data Flow
1. User draws a digit on the canvas.  
2. The image is sent via POST to the Flask backend.  
3. Backend processes the image and predicts using a CNN model.  
4. The prediction and confidence score are returned and displayed.  
5. The user can **save results** to their personal dashboard.

---

## 🧠 Model Details

- **Dataset**: MNIST (60,000 training + 10,000 testing samples)
- **Model Type**: Convolutional Neural Network (CNN)
- **Accuracy**: ≥90%
- **Process**:
  1. Grayscale conversion and resize (28x28)
  2. Model inference
  3. Confidence score calculation

---

## 💻 Tech Stack

| Layer | Technology |
|--------|-------------|
| **Frontend** | React.js / Vue.js, HTML5 Canvas, Axios |
| **Backend** | Flask (Python), Spring Boot (Java) |
| **Model** | TensorFlow / Keras (CNN on MNIST) |
| **Database** | MySQL / SQLite |
| **Auth** | JWT (JSON Web Tokens) |
| **Tools** | Figma (UI Design), Postman (API Testing), VS Code, PyCharm |

---

## 🧑‍💻 Core Features

### User Features
- ✍️ Draw digits on an interactive canvas  
- 🔍 Recognize digits instantly  
- 💾 Save results to user profile  
- 📜 View prediction history  
- ⚙️ Settings for user preferences

### Admin Features
- 👤 Manage users and access control (RBAC)  
- 🧠 Model training and monitoring dashboard  
- 🔄 One-click model deployment/rollback  
- 📊 Statistical analysis and system logs  

---

## 🧪 Testing & Validation

- **Unit Testing**: Individual components (UI, API, model functions)
- **End-to-End Testing**: Full workflow from drawing to saving predictions
- **User Testing**: Evaluate usability, latency, and accuracy

---

## 🚀 Future Improvements

- 📈 Improve accuracy using advanced CNNs or data augmentation  
- 📱 Optimize mobile experience  
- 🗂️ Add visualization for user history  
- 🌐 Deploy as a WeChat Mini Program or standalone mobile app  

---

## 🏗️ Installation Guide

### Backend Setup (Flask)
```bash
cd backend
pip install -r requirements.txt
python app.py
