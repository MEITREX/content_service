package de.unistuttgart.iste.meitrex.content_service.utility.value_changed_detector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ObjectValueChangedDetector<O, V> {
    private final List<V> values = new ArrayList<V>();

    private final Function<O, V> valueMeasureFunction;

    public ObjectValueChangedDetector(Function<O, V> valueMeasureFunction) {
        this.valueMeasureFunction = valueMeasureFunction;
    }

    public void measureValue(O object) {
        values.add(valueMeasureFunction.apply(object));
    }

    public boolean hasValueChanged() {
        if(values.size() < 2)
            throw new TooFewValuesMeasuredException();

        return values.stream().anyMatch(value -> !value.equals(values.getFirst()));
    }
}
