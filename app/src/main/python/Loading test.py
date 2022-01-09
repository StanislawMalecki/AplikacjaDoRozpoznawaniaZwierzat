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
    model = keras.models.load_model(f"{path}/a/vgg.h5")
    model1 = keras.models.load_model(f"{path}/a/bottleneck_fc_model128.h5")
    pic = Image.open(io.BytesIO(bytes(zdj)))
    pic = pic.resize((128,128),Image.NEAREST)
    test_image = np.array(pic, dtype='float')
    test_image = np.expand_dims(test_image, axis = 0)
    test_image/=255
    result1 = model.predict(test_image)
    result = model1.predict(result1)
    return str(result)

    # if (result[0][0] == result[0][1] and result[0][0] == result[0][2]):
    #     return "none "
    # elif (result[0][0]>result[0][1] and result[0][0]>result[0][2] and result[0][0]):
    #     return "Koń "+ str(result[0][0])
    # elif (result[0][1]>result[0][0] and result[0][1]>result[0][2]):
    #     return "Kot "+ str(result[0][1])
    # elif (result[0][2]>result[0][1] and result[0][2]>result[0][0]):
    #     return "Krowa "+ str(result[0][2])
    # else:
    #     return "idk"
