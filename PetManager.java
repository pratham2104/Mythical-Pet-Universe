import java.io.*;
import java.util.*;

public class PetManager {
    private static List<ExoticPet> pets = new ArrayList<>(); // List of available pets
    private static List<ExoticPet> adoptedPets = new ArrayList<>(); // List of adopted pets
    private static boolean hasFreeAdoption = true; // Tracks if free adoption is available
    

    // Load pets from the file into the 'pets' list
    public static void loadPets() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/pets.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split(",");
                if (parts.length != 6) { // Expecting exactly 6 fields
                    System.err.println("Skipping invalid line (incorrect number of fields): " + line);
                    continue; // Skip lines with incorrect format
                }
                try {
                    String name = parts[0].trim();
                    int health = Integer.parseInt(parts[1].trim());
                    int lifespan = Integer.parseInt(parts[2].trim());
                    String habitat = parts[3].trim();
                    String affinity = parts[4].trim();
                    int price = Integer.parseInt(parts[5].trim());

                    pets.add(new ExoticPet(name, health, lifespan, habitat, affinity, price)); // Add pet to list
                } catch (NumberFormatException e) {
                    System.err.println("Skipping line with invalid numbers: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading pets from file: " + e.getMessage());
        }
    }
    // Get the list of all available pets
    public static List<ExoticPet> getAllPets() {
        return pets;
    }

    // Get the list of adopted pets
    public static List<ExoticPet> getAdoptedPets() {
        return adoptedPets;
    }

    // Add a pet to the adopted pets list
    public static void addAdoptedPet(ExoticPet pet) {
        adoptedPets.add(pet);
    }

    // Remove a pet from the list of available pets
    public static void removePet(ExoticPet pet) {
        pets.remove(pet);
    }

    // Remove a pet from the list of adopted pets
    public static void removeAdoptedPet(ExoticPet pet) {
        adoptedPets.remove(pet);
    }

    // Check if a pet has already been adopted
    public static boolean isPetAdopted(ExoticPet pet) {
        return adoptedPets.contains(pet);
    }

    // Check if a free adoption is still available
    public static boolean canAdoptForFree() {
        return hasFreeAdoption;
    }

    // Mark free adoption as used
    public static void useFreeAdoption() {
        hasFreeAdoption = false;
    }
	
}
