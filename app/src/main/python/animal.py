from tensorflow import keras
import a
import os
def test():
    path = os.path.dirname(__file__)
    Model = keras.models.load_model(f"{path}/a/CatDogElefant.h5");
    return Model.summary;
