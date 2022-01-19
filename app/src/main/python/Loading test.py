"""
Created on Sun Nov  7 17:58:27 2021

@author: Wojtek
"""


from tensorflow import keras
import numpy as np
from PIL import Image
import io
import os

def test(zdj):
    path = os.path.dirname(__file__)
    model = keras.models.load_model(f"{path}/a/Vgg16v2.1.h5")
    model1 = keras.models.load_model(f"{path}/a/bottleneck_fc_model128v2.1.h5")
    pic = Image.open(io.BytesIO(bytes(zdj)))
    pic = pic.resize((128,128),Image.NEAREST)
    test_image = np.array(pic, dtype='float')
    test_image = np.expand_dims(test_image, axis = 0)
    test_image/=255
    result = model.predict(test_image)
    preds = model1.predict(result)
    wynik = np.argmax(preds)
    if wynik == 0:
        return "Kot " + str(round(preds[0][0]*100,2)) + " %"
    elif wynik == 1:
        return"Koń " + str(round(preds[1][0]*100,2)) + " %"
    elif wynik == 2:
        return"Krowa " + str(round(preds[2][0]*100,2)) + " %"
    elif wynik == 3:
        return"Pies " + str(round(preds[3][0]*100,2)) + " %"
    elif wynik == 4:
        return"Sarna " + str(round(preds[4][0]*100,2)) + " %"
    elif wynik == 5:
        return"Lis " + str(round(preds[5][0]*100,2)) + " %"
    else:
        return "Błąd"
