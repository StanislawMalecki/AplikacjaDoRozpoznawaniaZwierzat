"""
Created on Sun Nov  7 17:58:27 2021

@author: Wojtek
"""


from tensorflow import keras
import numpy as np
from PIL import Image
import io
import os


def prepare():
    path = os.path.dirname(__file__)
    global model
    model = keras.models.load_model(f"{path}/a/Vgg16v2.1.h5")
    global model1
    model1 = keras.models.load_model(f"{path}/a/bottleneck_fc_model128v2.1.h5")


def test(zdj):
    pic = Image.open(io.BytesIO(bytes(zdj)))
    pic = pic.resize((128, 128), Image.NEAREST)
    test_image = np.array(pic, dtype='float')
    test_image = np.expand_dims(test_image, axis=0)
    test_image /= 255
    result = model.predict(test_image)
    preds = model1.predict(result)
    wynik = np.argmax(preds)
    if wynik == 0:
        return "Kot " + str(round(preds[0][0]*100, 2)) + " %"
    elif wynik == 1:
        return"Koń " + str(round(preds[0][1]*100, 2)) + " %"
    elif wynik == 2:
        return"Krowa " + str(round(preds[0][2]*100, 2)) + " %"
    elif wynik == 3:
        return"Pies " + str(round(preds[0][3]*100, 2)) + " %"
    elif wynik == 4:
        return"Lis " + str(round(preds[0][4]*100, 2)) + " %"
    elif wynik == 5:
        return"Sarna " + str(round(preds[0][5]*100, 2)) + " %"
    else:
        return "Błąd"
