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
    model = keras.models.load_model(f"{path}/a/Test.h5")
    model1 = keras.models.load_model(f"{path}/a/bottleneck_fc_model128.h5")
    pic = Image.open(io.BytesIO(bytes(zdj)))
    pic = pic.resize((128,128),Image.NEAREST)
    test_image = np.array(pic, dtype='float')
    test_image = np.expand_dims(test_image, axis = 0)
    test_image/=255
    result = model.predict(test_image)
    preds = model1.predict(result)
    wynik = np.argmax(preds)
    print(wynik)
    if wynik == 0:
        print("Koń ", preds[:wynik])
    elif wynik == 1:
        print("Kot ", preds[:wynik])
    elif wynik == 2:
        print("Krowa ", preds[:wynik])
    elif wynik == 3:
        print("Kura ", preds[:wynik])
    elif wynik == 4:
        print("Pies ", preds[:wynik])
    else:
        print("Błąd")
