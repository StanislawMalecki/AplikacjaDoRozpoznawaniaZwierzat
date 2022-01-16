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
    model = keras.models.load_model(f"{path}/a/CatDogElefant.h5")
    # model1 = keras.models.load_model(f"{path}/a/bottleneck_fc_model128.h5")
    pic = Image.open(io.BytesIO(bytes(zdj)))
    pic = pic.resize((128,128),Image.NEAREST)
    test_image = np.array(pic, dtype='float')
    test_image = np.expand_dims(test_image, axis = 0)
    test_image/=255
    # result = model.predict(test_image)
    preds = model.predict(test_image)
    wynik = np.argmax(preds)
    if wynik == 0:
        return "Kot " + str(preds[0][0]*100) + " %"
    elif wynik == 1:
        return "Pies "+ str(preds[0][1]*100) + " %"
    elif wynik == 2:
        return "Słoń "+ str(preds[0][2]*100) + " %"
    # elif wynik == 3:
    #     print("Kura ", preds[:wynik])
    # elif wynik == 4:
    #     print("Pies ", preds[:wynik])
    else:
        return "Błąd"
