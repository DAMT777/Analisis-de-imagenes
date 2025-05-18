import os
import pandas as pd
import numpy as np
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D, Input
from tensorflow.keras.models import Model
from tensorflow.keras.utils import Sequence
from sklearn.model_selection import train_test_split

# Leer el Excel con las etiquetas
df = pd.read_excel('C:/Users/jesus/IdeaProjects/Analisis-de-imagenes/src/main/python/app/etiquetas.xlsx')  # Cambia el nombre si es necesario
df['solo_ojo'] = df['solo_ojo'].astype(bool)
# Si las rutas no están completas, añade el prefijo de la carpeta de imágenes
img_dir = 'C:/Users/jesus/Pictures/cachamas'  
df['nombre_archivo'] = df['nombre_archivo'].astype(str).str.strip()
df['nombre_archivo'] = df['nombre_archivo'].astype(str).str.strip().str.replace("\\", "/")
df['ruta_completa'] = df['nombre_archivo'].apply(lambda x: os.path.normpath(os.path.join(img_dir, x)))
df = df[df['ruta_completa'].apply(os.path.exists)]
df = df[df['ruta_completa'].apply(os.path.exists)]
print(f"Total de imágenes válidas para entrenamiento y validación: {len(df)}")

# Prepara las etiquetas
def preparar_etiquetas(row):
    # Normaliza las calificaciones a [0,1] si es necesario
    ojo = row['calificacion_ojos'] / 5.0  # Asumiendo escala 0-5
    piel = row['calificacion_piel'] / 5.0 if not row['solo_ojo'] else 0.0
    return np.array([ojo, piel], dtype=np.float32)

df['etiquetas'] = df.apply(preparar_etiquetas, axis=1)
df = df[~df['etiquetas'].apply(lambda x: np.isnan(x).any())]
print(f"Total de imágenes válidas y sin etiquetas NaN: {len(df)}")
print("¿Hay etiquetas NaN?", df['etiquetas'].isnull().any())
print("Ejemplo de etiquetas:", df['etiquetas'].head())
print("Valores únicos de etiquetas:", np.unique(np.vstack(df['etiquetas']), axis=0))

# Divide en entrenamiento y validación
train_df, val_df = train_test_split(df, test_size=0.2, random_state=42)

# Generador personalizado
class FishDataGenerator(Sequence):
    def __init__(self, df, batch_size=16, img_size=(224,224), shuffle=True):
        self.df = df.reset_index(drop=True)
        self.batch_size = batch_size
        self.img_size = img_size
        self.shuffle = shuffle
        self.indexes = np.arange(len(self.df))
        self.on_epoch_end()
    def __len__(self):
        return int(np.ceil(len(self.df) / self.batch_size))
    def __getitem__(self, idx):
        batch_idx = self.indexes[idx*self.batch_size:(idx+1)*self.batch_size]
        batch_df = self.df.iloc[batch_idx]
        X = np.zeros((len(batch_df), *self.img_size, 3), dtype=np.float32)
        y = np.zeros((len(batch_df), 2), dtype=np.float32)
        for i, (_, row) in enumerate(batch_df.iterrows()):
            img = image.load_img(row['ruta_completa'], target_size=self.img_size)
            X[i] = image.img_to_array(img) / 255.0
            y[i] = row['etiquetas']
        return X, y
    def on_epoch_end(self):
        if self.shuffle:
            np.random.shuffle(self.indexes)

# Instancia los generadores
train_gen = FishDataGenerator(train_df, batch_size=16)
val_gen = FishDataGenerator(val_df, batch_size=16, shuffle=False)

# Modelo MobileNetV2 con dos salidas (ojos, piel)
base_model = MobileNetV2(weights='imagenet', include_top=False, input_shape=(224,224,3))
base_model.trainable = False
x = GlobalAveragePooling2D()(base_model.output)
x = Dense(512, activation='relu')(x)
output = Dense(2, activation='sigmoid')(x)  # 2 salidas: ojos, piel

model = Model(inputs=base_model.input, outputs=output)
model.compile(optimizer='adam', loss='mse', metrics=['mae'])

# Entrenamiento
history = model.fit(
    train_gen,
    validation_data=val_gen,
    epochs=10
)

model.save('modelo_entrenado.h5')
print("Entrenamiento completado")