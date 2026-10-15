package ap.exercises.finalproject_ap;

import java.io.*;

public class Persistence implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String savePath = "datafile.bin";

    public void save(LibrarySystem librarySystem) {

        try (FileOutputStream fileOut = new FileOutputStream(savePath);
             ObjectOutputStream writeObj = new ObjectOutputStream(fileOut)) {
            writeObj.writeObject(librarySystem);
            System.out.println("Saving completed.");
        } catch (Exception e) {
            System.out.println("Saving failed: " + e.getMessage());
        }
    }

    public LibrarySystem load() {
        LibrarySystem result;
        File file = new File(savePath);
        if (!file.exists()) {
            result = new LibrarySystem();
        } else {

            try (FileInputStream fileIn = new FileInputStream(savePath);
                 ObjectInputStream readObj = new ObjectInputStream(fileIn)) {
                result = (LibrarySystem) readObj.readObject();
                System.out.println("Loading completed.");
            } catch (Exception e) {
                System.out.println("Saved data could not be loaded (corrupted file or incompatible version). Starting with a fresh system.");
                result = new LibrarySystem();
            }
        }
        return result;
    }
}
