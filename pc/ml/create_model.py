# create_model.py
import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.datasets import mnist
from tensorflow.keras.utils import to_categorical
import numpy as np
import matplotlib.pyplot as plt
import json
import os
from datetime import datetime

class ModelCreator:
    def __init__(self, model_name="mnist_cnn"):
        self.model_name = model_name
        self.model = None
        self.history = None
        
    def create_cnn_model(self, input_shape=(28, 28, 1), num_classes=10):
        """Create a CNN model for MNIST digit recognition"""
        print("🔄 Creating CNN model architecture...")
        
        model = models.Sequential([
            # First Convolutional Layer
            layers.Conv2D(32, (3, 3), activation='relu', input_shape=input_shape),
            layers.MaxPooling2D((2, 2)),
            
            # Second Convolutional Layer  
            layers.Conv2D(64, (3, 3), activation='relu'),
            layers.MaxPooling2D((2, 2)),
            
            # Third Convolutional Layer
            layers.Conv2D(64, (3, 3), activation='relu'),
            
            # Dense Layers
            layers.Flatten(),
            layers.Dense(64, activation='relu'),
            layers.Dropout(0.5),
            layers.Dense(num_classes, activation='softmax')
        ])
        
        # Compile the model
        model.compile(
            optimizer='adam',
            loss='categorical_crossentropy',
            metrics=['accuracy']
        )
        
        self.model = model
        print("✅ CNN model created successfully!")
        print(f"📊 Model summary:")
        model.summary()
        
        return model
    
    def create_advanced_model(self, input_shape=(28, 28, 1), num_classes=10):
        """Create a more advanced CNN model"""
        print("🔄 Creating advanced CNN model...")
        
        model = models.Sequential([
            # First Conv Block
            layers.Conv2D(32, (3, 3), activation='relu', padding='same', input_shape=input_shape),
            layers.BatchNormalization(),
            layers.Conv2D(32, (3, 3), activation='relu', padding='same'),
            layers.MaxPooling2D((2, 2)),
            layers.Dropout(0.25),
            
            # Second Conv Block
            layers.Conv2D(64, (3, 3), activation='relu', padding='same'),
            layers.BatchNormalization(),
            layers.Conv2D(64, (3, 3), activation='relu', padding='same'),
            layers.MaxPooling2D((2, 2)),
            layers.Dropout(0.25),
            
            # Third Conv Block
            layers.Conv2D(128, (3, 3), activation='relu', padding='same'),
            layers.BatchNormalization(),
            layers.Dropout(0.25),
            
            # Classifier
            layers.Flatten(),
            layers.Dense(512, activation='relu'),
            layers.BatchNormalization(),
            layers.Dropout(0.5),
            layers.Dense(128, activation='relu'),
            layers.Dropout(0.5),
            layers.Dense(num_classes, activation='softmax')
        ])
        
        # Compile with different optimizers
        model.compile(
            optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
            loss='categorical_crossentropy',
            metrics=['accuracy', 'precision', 'recall']
        )
        
        self.model = model
        print("✅ Advanced CNN model created!")
        return model
    
    def load_data(self, use_augmentation=True):
        """Load and prepare MNIST data"""
        print("📥 Loading MNIST dataset...")
        
        # Load MNIST data
        (x_train, y_train), (x_test, y_test) = mnist.load_data()
        
        # Preprocess data
        x_train = x_train.astype('float32') / 255.0
        x_test = x_test.astype('float32') / 255.0
        
        # Reshape for CNN
        x_train = x_train.reshape(-1, 28, 28, 1)
        x_test = x_test.reshape(-1, 28, 28, 1)
        
        # Convert labels to categorical
        y_train = to_categorical(y_train, 10)
        y_test = to_categorical(y_test, 10)
        
        print(f"📊 Dataset loaded:")
        print(f"   Training samples: {x_train.shape[0]}")
        print(f"   Test samples: {x_test.shape[0]}")
        print(f"   Image shape: {x_train.shape[1:]}")
        
        return (x_train, y_train), (x_test, y_test)
    
    def train_model(self, epochs=10, batch_size=128, validation_split=0.1):
        """Train the created model"""
        if self.model is None:
            raise ValueError("No model created. Call create_model() first.")
        
        print("🎯 Starting model training...")
        
        # Load data
        (x_train, y_train), (x_test, y_test) = self.load_data()
        
        # Train the model
        self.history = self.model.fit(
            x_train, y_train,
            batch_size=batch_size,
            epochs=epochs,
            validation_split=validation_split,
            verbose=1,
            shuffle=True
        )
        
        # Evaluate on test set
        test_loss, test_accuracy = self.model.evaluate(x_test, y_test, verbose=0)
        print(f"✅ Training completed!")
        print(f"📈 Final Test Accuracy: {test_accuracy:.4f}")
        print(f"📉 Final Test Loss: {test_loss:.4f}")
        
        return self.history
    
    def save_model(self, version=None):
        """Save the trained model with versioning"""
        if self.model is None:
            raise ValueError("No model to save.")
        
        # Create version name
        if version is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            version = f"{self.model_name}_v{timestamp}"
        
        # Ensure models directory exists
        os.makedirs('models', exist_ok=True)
        
        # Save model
        model_path = f"models/{version}.h5"
        self.model.save(model_path)
        
        # Save training history
        history_path = f"models/{version}_history.json"
        with open(history_path, 'w') as f:
            json.dump(self.history.history, f, indent=2)
        
        # Save model info
        info = {
            'model_name': version,
            'timestamp': datetime.now().isoformat(),
            'test_accuracy': self.history.history['val_accuracy'][-1] if 'val_accuracy' in self.history.history else None,
            'test_loss': self.history.history['val_loss'][-1] if 'val_loss' in self.history.history else None,
            'total_params': self.model.count_params()
        }
        
        info_path = f"models/{version}_info.json"
        with open(info_path, 'w') as f:
            json.dump(info, f, indent=2)
        
        print(f"💾 Model saved as: {model_path}")
        print(f"📊 Training history saved: {history_path}")
        print(f"📋 Model info saved: {info_path}")
        
        return model_path
    
    def plot_training_history(self):
        """Plot training history"""
        if self.history is None:
            raise ValueError("No training history available.")
        
        fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 4))
        
        # Plot accuracy
        ax1.plot(self.history.history['accuracy'], label='Training Accuracy')
        if 'val_accuracy' in self.history.history:
            ax1.plot(self.history.history['val_accuracy'], label='Validation Accuracy')
        ax1.set_title('Model Accuracy')
        ax1.set_xlabel('Epoch')
        ax1.set_ylabel('Accuracy')
        ax1.legend()
        ax1.grid(True)
        
        # Plot loss
        ax2.plot(self.history.history['loss'], label='Training Loss')
        if 'val_loss' in self.history.history:
            ax2.plot(self.history.history['val_loss'], label='Validation Loss')
        ax2.set_title('Model Loss')
        ax2.set_xlabel('Epoch')
        ax2.set_ylabel('Loss')
        ax2.legend()
        ax2.grid(True)
        
        plt.tight_layout()
        
        # Save plot
        os.makedirs('models', exist_ok=True)
        plot_path = f"models/{self.model_name}_training_plot.png"
        plt.savefig(plot_path)
        print(f"📈 Training plot saved: {plot_path}")
        
        plt.show()

def main():
    """Main function to create and train a model"""
    print("🤖 DigiScrib Model Creator")
    print("=" * 40)
    
    # Create model creator instance
    creator = ModelCreator("digiscrib_mnist_cnn")
    
    # Choose model type
    print("\nSelect model type:")
    print("1. Standard CNN (Fast training)")
    print("2. Advanced CNN (Better accuracy)")
    
    choice = input("Enter choice (1 or 2): ").strip()
    
    if choice == "1":
        model = creator.create_cnn_model()
        epochs = 5
    else:
        model = creator.create_advanced_model()
        epochs = 10
    
    # Train model
    print(f"\n🚀 Training model for {epochs} epochs...")
    creator.train_model(epochs=epochs)
    
    # Save model
    print("\n💾 Saving model...")
    model_path = creator.save_model()
    
    # Plot results
    print("\n📊 Generating training plots...")
    creator.plot_training_history()
    
    print(f"\n🎉 Model creation completed!")
    print(f"📍 Model saved at: {model_path}")
    
    # Set as current model
    try:
        os.symlink(f"{os.path.basename(model_path)}", "models/current_model.h5")
        print("🔗 Set as current active model")
    except:
        print("⚠️  Could not create symlink (might exist already)")

if __name__ == "__main__":
    main()