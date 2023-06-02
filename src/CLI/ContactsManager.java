package CLI;

import java.util.*;
import java.nio.file.*;
import java.io.*;

public class ContactsManager {
    private static final String FILE_NAME = "contacts.txt"; // File name where contacts are stored
    private static final Path PATH = Paths.get(FILE_NAME); // Path to the file
    private List<Contact> contacts; // List of Contact objects

    private static final String BANNER_FILE_NAME = "banner.txt";
    private static final Path BANNER_PATH = Paths.get(BANNER_FILE_NAME);
    private static String banner;
    private static final String Green = "\u001B[32m";
    private static final String Red = "\u001B[31m";
    private static final String ERed = "\033[41m";
    private static final String Reset = "\u001B[0m";

    static {
        banner = loadBanner();
    }
    public ContactsManager() {
        this.contacts = loadContacts(); // Loading contacts from file during initialization
    }

    private List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(PATH); // Reading all lines from file
            for (String line : lines) { // Iterating over each line
                String[] parts = line.split(" \\| "); // Splitting line into parts
                if (parts.length == 2) { // Valid contact should have two parts: name and phone number
                    String name = parts[0].trim();
                    String phoneNumber = formatPhoneNumber(parts[1].trim());
                    if (name.length() > 0 && phoneNumber != null) {
                        contacts.add(new Contact(name, phoneNumber));
                    } else {
                        System.out.println("Skipping invalid contact: " + line);
                    }
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read contacts. Starting with an empty list.");
        }
        return contacts;
    }
    private static String loadBanner() {
        try {
            return Files.readString(BANNER_PATH);
        } catch (IOException e) {
            System.out.println("Could not load banner. Defaulting to empty string.");
            return "";
        }
    }
    public void start() {
        System.out.println(banner);
        int option;
        do {
            option = showMenuOption();
            switch (option) {
                case 1 -> showContacts();
                case 2 -> addContact();
                case 3 -> searchContact();
                case 4 -> deleteContact();
            }
        } while (option != 5);
        saveContacts(); // Save contacts to file before exiting
    }

    private void addContact() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter name:");
            String name = scanner.nextLine();
            System.out.println("Enter phone number:");
            String phoneNumber = formatPhoneNumber(scanner.nextLine());
            if (phoneNumber == null) {
                System.out.println("Invalid phone number. Please enter a 7 or 10 digit number.");
                continue;
            } // Checking if contact already exists
            for (Contact contact : contacts) {
                if (contact.getName().equalsIgnoreCase(name)) {
                    System.out.println("Negative Ghost Rider, the contact is full. Do you want to overwrite it? (Yes/No)");
                    String answer = scanner.nextLine();
                    if (answer.equalsIgnoreCase("Yes")) {
                        contact.setPhoneNumber(phoneNumber);
                        System.out.println("Contact updated.");
                        saveContacts();
                        return;
                    } else {
                        System.out.println("Contact not updated.");
                    }
                    return;
                }
            }
            contacts.add(new Contact(name, phoneNumber));
            System.out.println(Green + "Contact added." + Reset);
            saveContacts();
            return;
        }
    }

    private void searchContact() {
        System.out.println("Enter the name of the contact to search:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(name)) {
                System.out.printf("%-20s | %s\n", contact.getName(), contact.getPhoneNumber());
                return;
            }
        }
        System.out.println("Hmmm, not the droid you're looking for..." );

    }

    private String formatPhoneNumber(String number) {
        // Remove all non-digits
        number = number.replaceAll("\\D", "");
        return switch (number.length()) { // Format the phone number based on length
            case 7 -> number.replaceAll("(\\d{3})(\\d{4})", "$1-$2");
            case 10 -> number.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
            default -> null;
        };
    }

    private void saveContacts() {
        List<String> lines = new ArrayList<>();
        for (Contact contact : contacts) {
            lines.add(contact.toString());
        }
        try {
            Files.write(PATH, lines); // Writing all contacts to file
        } catch (IOException e) {
            System.out.println("Could not save contacts.");
        }
    }

    private void showContacts(){
        System.out.printf("%-10s | %s\n", "Name", "Phone number");
        System.out.println("-----------|-------------");
        for (Contact contact : contacts) {
            System.out.printf("%-10s | %s\n", contact.getName(), contact.getPhoneNumber());
        }
    }
    private int showMenuOption() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Contacts Manager ---");
            System.out.println("1. View All Contacts");
            System.out.println("2. " + Green + "Add" + Reset + " New Contact");
            System.out.println("3. Search Contact by Name");
            System.out.println("4. " + Red + "Delete" + Reset + " an existing contact");
            System.out.println("5. " + ERed + "Exit" + Reset);
            System.out.println("Enter an option (1, 2, 3, 4 or 5):");
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.next(); // discard the invalid input
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
            }
        }
        return -1;
    }
    private void deleteContact() {
        System.out.println("\n---" + Red + "Delete" + Reset + " Contact?---");
        System.out.println("Enter name to " + Red + "Delete" + Reset + ": ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("Are you sure you want to " + Red + "Delete" + Reset + "" + name + "? (Yes/No)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Yes")) {
            Iterator<Contact> iterator = contacts.iterator();
            while (iterator.hasNext()) {
                Contact contact = iterator.next();
                if (contact.getName().equalsIgnoreCase(name)) {
                    iterator.remove();
                    System.out.println("Bye Felicia.");
                    return;
                }
            }
            System.out.println("Hmmm, not the droid you're looking for...");
            System.out.println("         _____\n" +
                    "       .'/L|__`.\n" +
                    "      / =[_]O|` \\\n" +
                    "      |\"+_____\":|\n" +
                    "    __:='|____`-:__\n" +
                    "   ||[] ||====| []||\n" +
                    "   ||[] | |=| | []||\n" +
                    "   |:||_|=|U| |_||:|\n" +
                    "   |:|||]_=_ =[_||:|\n" +
                    "   | |||] [_][]C|| |\n" +
                    "   | ||-'\"\"\"\"\"`-|| |\n" +
                    "   /|\\\\_\\_|_|_/_//|\\\n" +
                    "  |___|   /|\\   |___|\n" +
                    "  `---'  |___|  `---'\n" +
                    "         `---'\n");
        } else {
            System.out.println(Red + "Deletion cancelled." + Reset);
        }
    }
}

