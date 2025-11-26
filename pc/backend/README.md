## DigiScrib Backend

### Prerequisites

- Java 17+
- Maven
- Python 3.10+ with `tensorflow`, `fastapi`, `uvicorn`

### Local Development

1. **Start the ML microservice**
   ```bash
   cd ../ml
   pip install -r requirements.txt  # ensure TF/FastAPI installed
   python mnist_service.py
   ```
   This service handles model training, activation, and inference.

2. **Run Spring Boot (H2 by default)**
   ```bash
   mvn spring-boot:run
   ```
   The API listens on `http://localhost:8081/api` and proxies ML requests.

3. **Front-end / Mobile**
   - Vue admin console: `cd ../frontend && npm install && npm run dev` (configure `src/services/api.js` if hosting elsewhere).
   - Expo mobile app: see `/mobile/README.md` for platform-specific steps.

### Key Endpoints

- `POST /api/recognition/predict` – routes drawing payloads through the ML service and records history
- `GET /api/models` – lists all trained models with their status/accuracy
- `POST /api/models/create` – triggers a new training job with configurable hyper-parameters
- `GET /api/analytics/overview` – aggregated metrics for dashboards

See `src/main/java/com/digiscrib/controller` for the complete API surface.
