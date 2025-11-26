## DigiScribe ML Service

This FastAPI service hosts all deep-learning functionality (model registry, training, inference).  
Follow the steps below to run it on different platforms.

### 1. Prerequisites

- Python 3.11 (recommended). Install via `pyenv`, Homebrew, or the Microsoft Store.
- Git + basic build tools.

### 2. Create a virtual environment

```bash
cd /path/to/DigiScribe/pc/ml
python3.11 -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate
```

If Conda auto-activates `base`, run `conda deactivate` until the `(base)` prompt disappears before sourcing `.venv`.

### 3. Install dependencies

The `requirements.txt` automatically selects the correct TensorFlow build:

- Windows/Linux/Intel macOS → `tensorflow==2.16.1`
- Apple Silicon macOS → `tensorflow-macos==2.16.1` + `tensorflow-metal`

Install with:

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### 4. Run the service

```bash
python mnist_service.py
```

You should see `🚀 Starting DigiScrib ML Service...` and the API will listen on `http://localhost:8000`.

### 5. Troubleshooting

- **`ModuleNotFoundError: No module named 'numpy'`**  
  Ensure you are using the virtual environment's Python (`which python` should point inside `.venv`). If you see `(base)` in the prompt, run `conda deactivate`.

- **TensorFlow wheel not found**  
  Verify you are on Python 3.11. Apple Silicon users must use the `tensorflow-macos` build (already handled by `requirements.txt`).

- **Run in production**  
  Use Gunicorn/Uvicorn workers or a process manager:
  ```bash
  uvicorn mnist_service:app --host 0.0.0.0 --port 8000 --workers 2
  ```

### 6. Helpful commands

```bash
pip list                       # Show installed packages inside the venv
python -m pip show tensorflow  # Confirm TensorFlow build
uvicorn mnist_service:app      # Alternative dev runner with auto-reload
```

With the ML service running, you can start the Spring Boot backend and Vue/mobile clients to exercise the full system.
