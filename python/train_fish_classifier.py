from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from sklearn.utils.class_weight import compute_class_weight
import numpy as np
import os

# Parámetros
ruta_dataset = r'C:\Users\jesus\Pictures\imagenes_pez'
batch_size = 16
input_size = (224, 224)

# Generadores
datagen = ImageDataGenerator(
    rescale=1./255,
    validation_split=0.2,
    rotation_range=20,
    horizontal_flip=True
)

train_gen = datagen.flow_from_directory(
    ruta_dataset,
    target_size=input_size,
    batch_size=batch_size,
    class_mode='binary',
    subset='training'
)

val_gen = datagen.flow_from_directory(
    ruta_dataset,
    target_size=input_size,
    batch_size=batch_size,
    class_mode='binary',
    subset='validation'
)

# Mostrar el mapeo de clases
print("Mapeo de clases:", train_gen.class_indices)

# Calcular pesos de clase para contrarrestar el desbalance
labels = train_gen.classes
class_weights = compute_class_weight(class_weight='balanced', classes=np.unique(labels), y=labels)
class_weight_dict = {i: class_weights[i] for i in range(len(class_weights))}
print("Pesos de clase:", class_weight_dict)

# Modelo base
base_model = MobileNetV2(weights='imagenet', include_top=False, input_shape=(224, 224, 3))
x = base_model.output
x = GlobalAveragePooling2D()(x)
preds = Dense(1, activation='sigmoid')(x)
model = Model(inputs=base_model.input, outputs=preds)

# Congelar capas base
for layer in base_model.layers:
    layer.trainable = False

model.compile(optimizer=Adam(0.0001), loss='binary_crossentropy', metrics=['accuracy'])

# Entrenar
model.fit(
    train_gen,
    epochs=10,
    validation_data=val_gen,
    class_weight=class_weight_dict
)

model.save("fish_binary_classifier.h5")
