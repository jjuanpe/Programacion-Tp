package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// Saves and loads the whole tournament by Java serialization.
public final class TournamentStorage {

    private static final String DEFAULT_SAVE_PATH = "torneo.save";

    private TournamentStorage() {
    }

    public static boolean hasSavedTournament() {
        return new File(DEFAULT_SAVE_PATH).exists();
    }

    public static TournamentSession load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DEFAULT_SAVE_PATH))) {
            return (TournamentSession) in.readObject();
        }
    }

    public static void save(TournamentSession session) throws IOException {
        // same lock the session uses, so we never save a half-played stage
        synchronized (session) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DEFAULT_SAVE_PATH))) {
                out.writeObject(session);
            }
        }
    }
}
