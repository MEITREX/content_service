package de.unistuttgart.iste.meitrex.content_service.utility.value_changed_detector;

public class TooFewValuesMeasuredException extends RuntimeException {
    public TooFewValuesMeasuredException() {
        super("Fewer than 2 values were measured.");
    }
}
