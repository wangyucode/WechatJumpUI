package sample;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;

public class Utils {

    public static <T> void onFXThread(final ObjectProperty<T> property, final T value)
    {
        Platform.runLater(() -> {
            property.set(value);
        });
    }
}
