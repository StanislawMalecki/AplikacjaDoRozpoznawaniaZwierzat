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
    Model = keras.models.load_model(f"{path}/a/CatDogElefant.h5");
    pic = Image.open(io.BytesIO(bytes(zdj)))
    pic = pic.resize((128,128),Image.NEAREST)
    test_image = np.array(pic, dtype='float')
    test_image = np.expand_dims(test_image, axis = 0)
    test_image/=255
    result = Model.predict(test_image)
    if (result[0][0] == result[0][1] and result[0][0] == result[0][2]):
        return "none " 
    elif (result[0][0]>result[0][1] and result[0][0]>result[0][2]):
        return "kot "+ str(result[0][0])
    elif (result[0][1]>result[0][0] and result[0][1]>result[0][2]):
        return "pies "+ str(result[0][1])
    elif (result[0][2]>result[0][1] and result[0][2]>result[0][0]):
        return "słoń "+ str(result[0][2])
    else:
        return "idk"
