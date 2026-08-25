package dataload;

import model.team.Country;
import model.venue.City;
import model.venue.Stadium;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the temporary stadium catalog used until database persistence is implemented.
 */
public class SampleStadiumFactory {
    private static final int SAMPLE_STADIUM_CAPACITY = 50000;

    public List<Stadium> createStadiums() {
        List<Stadium> stadiums = new ArrayList<>();
        addStadium(stadiums, 1, "Buenos Aires", "Argentina", "Estadio Monumental");
        addStadium(stadiums, 2, "Buenos Aires", "Argentina", "La Bombonera");
        addStadium(stadiums, 3, "Rio de Janeiro", "Brazil", "Maracana");
        addStadium(stadiums, 4, "Sao Paulo", "Brazil", "Morumbi");
        addStadium(stadiums, 5, "Montevideo", "Uruguay", "Centenario");
        addStadium(stadiums, 6, "Santiago", "Chile", "Estadio Nacional");
        addStadium(stadiums, 7, "Bogota", "Colombia", "El Campin");
        addStadium(stadiums, 8, "Lima", "Peru", "Estadio Nacional de Lima");
        addStadium(stadiums, 9, "Asuncion", "Paraguay", "Defensores del Chaco");
        addStadium(stadiums, 10, "Guayaquil", "Ecuador", "Monumental Banco Pichincha");
        addStadium(stadiums, 11, "Madrid", "Spain", "Santiago Bernabeu");
        addStadium(stadiums, 12, "Milan", "Italy", "San Siro");
        addStadium(stadiums, 13, "Munich", "Germany", "Allianz Arena");
        return List.copyOf(stadiums);
    }

    private void addStadium(
            List<Stadium> stadiums,
            long id,
            String cityName,
            String countryName,
            String stadiumName) {
        City city = new City(id, cityName, new Country(countryName));
        stadiums.add(new Stadium(id, stadiumName, city, SAMPLE_STADIUM_CAPACITY));
    }
}
