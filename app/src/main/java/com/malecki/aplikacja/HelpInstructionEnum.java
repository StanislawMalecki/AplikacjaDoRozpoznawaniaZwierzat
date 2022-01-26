package com.malecki.aplikacja;

public enum HelpInstructionEnum {
//    Main Activity
    CONTENT_TITLE_CAMERA_VIEW_BUTTON("Otwórz kamerę"),
    CONTENT_TEXT_CAMERA_VIEW_BUTTON("Kliknięcie tego przycisku przenosi do widoku kamery gdzie " +
            "można rozpoznawac zwierzeta poprzez widok z kamerki"),
    CONTENT_TITLE_ANIMAL_RECOGNITION_BUTTON("Rozpoznaj zwierzę"),
    CONTENT_TEXT_ANIMAL_RECOGNITION_BUTTON("Rozpoznaj gatunek zwierzecia na załadowanym obrazie"),
    CONTENT_TITLE_LOAD_IMAGE_BUTTON("Załaduj obraz"),
    CONTENT_TEXT_LOAD_IMAGE_BUTTON("Kliknięcie tego przycisku otworzy galerię, aby wybarć zdjęcie" +
            " na którym ma zostać rozpoznany gatunek zwierzęcia"),
//    Camera Activity
    CONTENT_TITLE_GO_BACK_BUTTON("Wróć"),
    CONTENT_TEXT_GO_BACK_BUTTON("Przyciśnięcie przenosi spowrotem do głównego menu"),
    CONTENT_TITLE_ANIMAL_RECOGNITION_SWITCH("Przełącznik"),
    CONTENT_TEXT_ANIMAL_RECOGNITION_SWITCH("Włączenie przełącznika uruchamia rozpoznawania zwierzat" +
            " na widocznych na kamerze"),
    CONTENT_TITLE_CAMERA_VIEW("Widok z kamery"),
    CONTENT_TEXT_CAMERA_VIEW("Widok z tylnej kamery telefonu");

    String text;

    HelpInstructionEnum(String text)
    {
        this.text = text;
    }

}
