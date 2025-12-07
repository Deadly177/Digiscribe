# DigiScribe – Full‑Stack Digit Recognition

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

Thin, fast stack for handwritten digit recognition with a Java Spring Boot admin/API, a Python (FastAPI + TensorFlow) model service, and a Vue 3 + Vite frontend.

## Repo Layout
- `pc/backend` Spring Boot (REST API, admin, history, JPA/H2/MySQL)
- `pc/ml`   FastAPI model service (TensorFlow/Keras MNIST)
- `pc/frontend` Vue 3 + Vite UI

## Prerequisites
- Java 17+, Maven
- Python 3.11+ (virtualenv recommended)
- Node.js 20.19+ or 22.12+ (Vite requires this)

## Quick Start
### 1) Start the Java backend
```bash
cd pc/backend
mvn spring-boot:run    # serves http://localhost:8081/api
```

### 2) Start the Python model service
```bash
cd pc/ml
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn mnist_service:app --host 0.0.0.0 --port 8000
```

### 3) Start the frontend
```bash
cd pc/frontend
nvm use 22.21.1 || nvm use 20.19.0   # or install one of these
npm install
npm run dev    # http://localhost:5173
```

## Testing
- Java services: `cd pc/backend && mvn test`
- Python model: `cd pc/ml && source .venv/bin/activate && pytest -q`
- Frontend: `cd pc/frontend && npm run test` (if configured)

## API Smoke Checks
- Backend health: `curl http://localhost:8081/api/health`
- Model predict (FastAPI):
```bash
curl -X POST http://localhost:8000/api/predict \
  -H "Content-Type: application/json" \
  -d '{"image":[0,0,0,...784 values...],"model_id":"default"}'
```
- Gateway predict (Spring Boot):
```bash
curl -X POST http://localhost:8081/api/recognition/predict \
  -H "Content-Type: application/json" \
  -d '{"inputType":"BASE64","imageData":"<base64_png>","originalFilename":"sample.png","modelId":"default"}'

  website Link - https://digiscribe-frontend.onrender.com (User- admin , password - admin123 )
```

## Notes & Troubleshooting
- Vite requires Node ≥20.19; using older Node (e.g., 18) triggers `crypto.hash is not a function`.
- Large artifacts/virtualenvs should be excluded via `.gitignore` (e.g., `.venv/`, `node_modules/`, `target/`, `dist/`).
- If Maven warns about duplicate dependencies, resolve duplicates in `pc/backend/pom.xml`.

## License
MIT https://github.com/Deadly177/Digiscribe/blob/588a4b56c3fca23ebec3ad0cac8803237302decd/LICENSE.
