package blps.duo.adapter;

import blps.duo.error.InvalidPercentageException;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleAdapter extends XmlAdapter<String, Double> {

    @Override
    public Double unmarshal(String v) throws Exception {
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            // При неудачном преобразовании выбрасываем наше исключение
            throw new InvalidPercentageException("Invalid percentage value: " + v);
        }
    }

    @Override
    public String marshal(Double v) throws Exception {
        return v.toString();
    }
}