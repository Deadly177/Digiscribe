from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import tensorflow as tf
from PIL import Image
import io
import base64

app = Flask(__name__)
CORS(app)

# Load your trained MNIST model
model = tf.keras.models.load_model('models/mnist_model.h5')

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        image_data = data['image']
        
        # Convert to numpy array
        image_array = np.array(image_data, dtype=np.float32)
        image_array = image_array.reshape(1, 28, 28, 1)
        
        # Make prediction
        predictions = model.predict(image_array)
        predicted_digit = np.argmax(predictions[0])
        confidence = float(predictions[0][predicted_digit])
        
        return jsonify({
            'prediction': int(predicted_digit),
            'confidence': confidence,
            'all_predictions': predictions[0].tolist()
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'healthy'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)