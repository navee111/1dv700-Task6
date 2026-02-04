package part_A;

import java.io.*;
import java.util.*;

/**
 * Simple 8-bit Hash Function
 * Takes text input and produces 8-bit hash values (0-255)
 * 
 * Hash Algorithm:
 * - XOR all characters together
 * - Add position-weighted characters
 * - Use modulo 256 to get 8-bit result
 */
public class simpleHash {
    
    /**
     * Computes an 8-bit hash value for a given string
     * 
     * Algorithm:
     * 1. Start with hash = 0
     * 2. For each character:
     *    - XOR with character's ASCII value
     *    - Add character multiplied by its position
     * 3. Return hash modulo 256 (to get 8-bit value)
     * 
     * @param input The string to hash
     * @return Hash value between 0 and 255
     */
    public static int hash(String input) {
        // Handle empty input
        if (input == null || input.length() == 0) {
            return 0;
        }
        
        int hash = 0;
        
        // Process each character
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            // XOR operation for mixing
            hash ^= c;
            
            // Add position-weighted value for avalanche effect
            hash += (c * (i + 1));
        }
        
        // Ensure result is 8-bit (0-255)
        return hash & 0xFF;  // Same as hash % 256
    }
    
    /**
     * Reads a file and computes hash for each line
     * 
     * @param filename Name of the file to process
     */
    public static void hashFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int lineNumber = 1;
            
            System.out.println("Hash values for file: " + filename);
            System.out.println("Line\tHash\tContent");
            System.out.println("----\t----\t-------");
            
            while ((line = reader.readLine()) != null) {
                int hashValue = hash(line);
                System.out.println(lineNumber + "\t" + hashValue + "\t" + line);
                lineNumber++;
            }
            
            reader.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + filename + "' not found.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Test the hash function with sample inputs
     */
    public static void testHash() {
        System.out.println("=== Simple Hash Function Test ===\n");
        
        // Test 1: Same input gives same hash
        String test1 = "Hello World";
        System.out.println("Test 1: Consistency");
        System.out.println("Input: \"" + test1 + "\"");
        System.out.println("Hash 1: " + hash(test1));
        System.out.println("Hash 2: " + hash(test1));
        System.out.println();
        
        // Test 2: Different inputs
        System.out.println("Test 2: Different inputs");
        System.out.println("\"Hello\" -> " + hash("Hello"));
        System.out.println("\"World\" -> " + hash("World"));
        System.out.println("\"Hello World\" -> " + hash("Hello World"));
        System.out.println();
        
        // Test 3: Small changes
        System.out.println("Test 3: Small changes in input");
        System.out.println("\"test\" -> " + hash("test"));
        System.out.println("\"Test\" -> " + hash("Test"));
        System.out.println("\"test1\" -> " + hash("test1"));
        System.out.println();
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== 8-bit Hash Function ===\n");
        
        System.out.println("Choose an option:");
        System.out.println("1. Test hash function");
        System.out.println("2. Hash a file");
        System.out.println("3. Hash a custom string");
        System.out.print("\nEnter choice (1-3): ");
        
        String choice = scanner.nextLine().trim();
        
        if (choice.equals("1")) {
            testHash();
        } 
        else if (choice.equals("2")) {
            System.out.print("Enter filename: ");
            String filename = scanner.nextLine().trim();
            hashFile(filename);
        }
        else if (choice.equals("3")) {
            System.out.print("Enter string to hash: ");
            String input = scanner.nextLine();
            int hashValue = hash(input);
            System.out.println("\nInput: \"" + input + "\"");
            System.out.println("Hash value: " + hashValue);
        }
        else {
            System.out.println("Invalid choice.");
        }
        
        scanner.close();
    }
}
