import tensorflow as tf

# Crea un tensor de grandes dimensiones para forzar un cálculo en la GPU
with tf.device('/GPU:0'):
    a = tf.random.normal([10000, 10000])
    b = tf.random.normal([10000, 10000])
    c = tf.matmul(a, b)

print("Cálculo completado!")
