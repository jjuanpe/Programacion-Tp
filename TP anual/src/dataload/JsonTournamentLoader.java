package dataload;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.people.DocumentType;
import model.people.FieldPlayer;
import model.people.Goalkeeper;
import model.people.HeadCoach;
import model.people.Player;
import model.people.Position;
import model.people.Referee;
import model.common.Country;
import model.team.Team;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Random;
import java.time.format.DateTimeFormatter;

// Reads the course JSON. Extra players are cut at random and a warning is saved for the UI.
public class JsonTournamentLoader {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String path;
    private final Random random = new Random();

    public JsonTournamentLoader(String path) {
        this.path = path;
    }

    public TournamentData load() throws IOException {
        TournamentData data = new TournamentData();

        JsonObject root;
        try (Reader reader = new FileReader(path, StandardCharsets.UTF_8)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }

        JsonObject tournament = root.getAsJsonObject("torneo");

        JsonArray teamNodes = tournament.getAsJsonObject("equipos").getAsJsonArray("equipo");
        for (JsonElement node : teamNodes) {
            data.addTeam(readTeam(node.getAsJsonObject(), data));
        }

        JsonArray refereeNodes = tournament.getAsJsonObject("arbitros").getAsJsonArray("arbitro");
        for (JsonElement node : refereeNodes) {
            data.addReferee(readReferee(node.getAsJsonObject()));
        }

        return data;
    }

    private Team readTeam(JsonObject node, TournamentData data) {
        Team team = new Team(
                node.get("nombre").getAsString(),
                Country.of(node.get("pais").getAsString()),
                node.get("ranking").getAsInt());

        JsonObject squadNode = node.getAsJsonObject("plantel");

        team.setCoach(readCoach(squadNode.getAsJsonObject("dt")));

        JsonArray playerNodes = squadNode.getAsJsonObject("jugadores").getAsJsonArray("jugador");

        for (JsonElement playerNode : playerNodes) {
            JsonObject player = playerNode.getAsJsonObject();
            try {
                team.addPlayer(readPlayer(player));
            } catch (RuntimeException e) {
                data.addWarning(new StringBuilder(team.getName()).append(": left out \"")
                        .append(player.getAsJsonObject("persona").get("nombre").getAsString())
                        .append("\" - ").append(e.getMessage()).toString());
            }
        }

        removeSurplusPlayers(team, data);

        try {
            team.validateSquad();
        } catch (RuntimeException e) {
            data.addWarning(e.getMessage());
        }

        return team;
    }

    private void removeSurplusPlayers(Team team, TournamentData data) {
        for (Position position : Position.values()) {
            int required = position.getRequiredPerSquad();
            int found = team.countByPosition(position);
            while (team.countByPosition(position) > required) {
                Player removed = team.removeRandomPlayer(position, random);
                data.addWarning(new StringBuilder(team.getName())
                        .append(": had ").append(found).append(' ')
                        .append(position.getLabel().toLowerCase()).append("s instead of ").append(required)
                        .append("; \"").append(removed.getName())
                        .append("\" was removed at random to complete the squad.").toString());
            }
        }
    }

    private Player readPlayer(JsonObject node) {
        JsonObject person = node.getAsJsonObject("persona");
        JsonObject attributes = node.getAsJsonObject("caracteristicas");

        String fullName = person.get("nombre").getAsString();
        LocalDate birthDate = readBirthDate(person);
        DocumentType documentType = readDocumentType(person);
        String documentNumber = person.get("nroDocumento").getAsString();

        Position position = Position.fromFileValue(node.get("posicion").getAsString());

        Player player;
        if (position.isGoalkeeper()) {
            player = new Goalkeeper(fullName, birthDate, documentType, documentNumber,
                    attributes.get("reflejos").getAsInt(),
                    attributes.get("seguridadManos").getAsInt(),
                    attributes.get("juegoAereo").getAsInt(),
                    attributes.get("achique").getAsInt(),
                    attributes.get("ubicacion").getAsInt(),
                    attributes.get("juegoPies").getAsInt());
        } else {
            player = new FieldPlayer(fullName, birthDate, documentType, documentNumber,
                    position,
                    attributes.get("velocidad").getAsInt(),
                    attributes.get("resistenciaFisica").getAsInt(),
                    attributes.get("habilidad").getAsInt(),
                    attributes.get("definicion").getAsInt(),
                    attributes.get("potenciaDisparo").getAsInt(),
                    attributes.get("cabezazo").getAsInt(),
                    attributes.get("capacidadQuite").getAsInt(),
                    attributes.get("visionDeJuego").getAsInt());
        }
        return player;
    }

    private HeadCoach readCoach(JsonObject node) {
        JsonObject person = node.getAsJsonObject("persona");
        return new HeadCoach(
                person.get("nombre").getAsString(),
                readBirthDate(person),
                readDocumentType(person),
                person.get("nroDocumento").getAsString(),
                Country.of(node.get("pais").getAsString()),
                node.get("titulosObtenidos").getAsInt());
    }

    private Referee readReferee(JsonObject node) {
        JsonObject person = node.getAsJsonObject("persona");
        return new Referee(
                person.get("nombre").getAsString(),
                readBirthDate(person),
                readDocumentType(person),
                person.get("nroDocumento").getAsString(),
                Country.of(node.get("pais").getAsString()),
                node.get("aniosReferato").getAsInt());
    }

    private LocalDate readBirthDate(JsonObject person) {
        return LocalDate.parse(person.get("fechaNacimiento").getAsString(), DATE_FORMAT);
    }

    private DocumentType readDocumentType(JsonObject person) {
        return DocumentType.fromFileValue(person.get("tipoDocumento").getAsString());
    }
}
