package model.venue;

import java.util.List;

public class CityInUseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String cityName;
    private final List<String> stadiumNames;

    public CityInUseException(String cityName, List<String> stadiumNames) {
        super(buildMessage(cityName, stadiumNames));
        this.cityName = cityName;
        this.stadiumNames = List.copyOf(stadiumNames);
    }

    public String getCityName() {
        return cityName;
    }

    public List<String> getStadiumNames() {
        return stadiumNames;
    }

    private static String buildMessage(String cityName, List<String> stadiumNames) {
        StringBuilder message = new StringBuilder("The city ")
                .append(cityName)
                .append(" cannot be deleted because it has stadiums assigned:");
        for (String stadiumName : stadiumNames) {
            message.append("\n- ").append(stadiumName);
        }
        return message.toString();
    }
}
