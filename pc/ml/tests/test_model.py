import base64
import io
import sys
from pathlib import Path

# Ensure project root is on sys.path for imports when running pytest from subdirs
ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

import numpy as np
import pytest
import tensorflow as tf
from fastapi.testclient import TestClient
from PIL import Image

from mnist_service import (
    MODELS_DIR,
    REGISTRY_FILE,
    TrainingConfig,
    app,
    decode_image,
    ensure_active_model,
    load_tf_model,
    model_registry,
    training_jobs,
    loaded_models,
    active_model_id,
    train_model_worker,
)


def _clear_state():
    model_registry.clear()
    loaded_models.clear()
    training_jobs.clear()
    # reset active model marker
    import mnist_service

    mnist_service.active_model_id = None


def _build_dummy_model(save_path: Path) -> Path:
    model = tf.keras.Sequential(
        [
            tf.keras.layers.Input(shape=(28, 28, 1)),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(10, activation="softmax"),
        ]
    )
    model.compile(optimizer="adam", loss="sparse_categorical_crossentropy", metrics=["accuracy"])
    model.save(save_path)
    return save_path


def test_decode_image_accepts_flat_array():
    payload = list(np.zeros(28 * 28, dtype=np.float32))
    arr = decode_image(payload)
    assert arr.shape == (28, 28, 1)
    assert arr.dtype == np.float32


def test_decode_image_rejects_wrong_size():
    payload = [0.0] * 10
    with pytest.raises(Exception):
        decode_image(payload)


def test_decode_image_accepts_base64_png():
    image = Image.new("L", (28, 28), color=255)
    buf = io.BytesIO()
    image.save(buf, format="PNG")
    b64 = base64.b64encode(buf.getvalue()).decode()
    arr = decode_image(b64)
    assert arr.shape == (28, 28, 1)
    # white background becomes zeros after inversion
    assert float(arr.mean()) == pytest.approx(0.0, abs=1e-6)


def test_ensure_active_model_raises_when_missing():
    _clear_state()
    with pytest.raises(Exception):
        ensure_active_model()


def test_load_tf_model_success(tmp_path: Path):
    _clear_state()
    weights = _build_dummy_model(tmp_path / "dummy.h5")
    from mnist_service import ModelInfo, active_model_id as active_marker

    model_registry["dummy"] = ModelInfo(
        id="dummy",
        name="Dummy",
        version="0.1",
        status="active",
        accuracy=0.0,
        prediction_count=0,
        training_samples=0,
        last_trained=None,
        architecture="cnn_simple",
        created_at="now",
        model_path=str(weights),
    )
    import mnist_service

    mnist_service.active_model_id = "dummy"

    model = load_tf_model("dummy")
    assert model is not None


def test_predict_digit_returns_result(tmp_path: Path):
    _clear_state()
    weights = _build_dummy_model(tmp_path / "dummy.h5")
    from mnist_service import ModelInfo

    model_registry["dummy"] = ModelInfo(
        id="dummy",
        name="Dummy",
        version="0.1",
        status="active",
        accuracy=0.0,
        prediction_count=0,
        training_samples=0,
        last_trained=None,
        architecture="cnn_simple",
        created_at="now",
        model_path=str(weights),
    )
    import mnist_service

    mnist_service.active_model_id = "dummy"

    client = TestClient(app)
    payload = {"image": [0.0] * 784}
    resp = client.post("/api/predict", json=payload)
    assert resp.status_code == 200
    body = resp.json()
    assert 0 <= body["predicted_digit"] <= 9
    assert 0.0 <= body["confidence"] <= 1.0


def test_train_model_worker_updates_registry(monkeypatch, tmp_path: Path):
    _clear_state()

    def fake_dataset():
        x_train = np.random.rand(20, 28, 28, 1).astype("float32")
        y_train = tf.keras.utils.to_categorical(np.random.randint(0, 10, size=(20,)), 10)
        x_test = np.random.rand(10, 28, 28, 1).astype("float32")
        y_test = tf.keras.utils.to_categorical(np.random.randint(0, 10, size=(10,)), 10)
        return (x_train, y_train), (x_test, y_test)

    monkeypatch.setattr("mnist_service.load_dataset", fake_dataset)
    monkeypatch.setattr("mnist_service.MODELS_DIR", tmp_path)
    monkeypatch.setattr("mnist_service.REGISTRY_FILE", tmp_path / "registry.json")
    monkeypatch.setattr("mnist_service.save_registry", lambda: None)

    from mnist_service import ModelInfo

    model_registry["train-id"] = ModelInfo(
        id="train-id",
        name="TrainDummy",
        version="0.1",
        status="training",
        accuracy=0.0,
        prediction_count=0,
        training_samples=0,
        last_trained=None,
        architecture="cnn_simple",
        created_at="now",
        model_path=None,
    )

    config = TrainingConfig(name="TrainDummy", architecture="cnn_simple", epochs=1, learning_rate=0.001, batch_size=16)
    train_model_worker("train-id", config)

    meta = model_registry["train-id"]
    assert meta.status == "idle"
    assert meta.model_path is not None
    assert Path(meta.model_path).exists()
