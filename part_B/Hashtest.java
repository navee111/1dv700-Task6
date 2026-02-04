package part_B;

import java.io.*;
import java.util.*;

/**
 * Statistical Tests for Hash Function
 * Tests for:
 * 1. Uniformity - hash values should be evenly distributed
 * 2. Avalanche Effect - small changes should cause big hash differences
 */
public class Hashtest {
    
    /**
     * Test uniformity of hash function
     * Counts how many times each hash value (0-255) appears
     * 
     * @param filename File with test strings
     */
    public static void testUniformity(String filename) {
        System.out.println("\n=== UNIFORMITY TEST ===\n");
        
        // Array to count occurrences of each hash value (0-255)
        int[] buckets = new int[256];
        int totalLines = 0;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            
            // Hash each line and count
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    int hashValue = line.hashCode() & 0xFF;
                    buckets[hashValue]++;
                    totalLines++;
                }
            }
            
            reader.close();
            
            // Calculate statistics
            System.out.println("Total lines processed: " + totalLines);
            System.out.println("Hash values used: 256 (0-255)");
            
            double expectedPerBucket = (double) totalLines / 256;
            System.out.println("Expected per bucket: " + String.format("%.2f", expectedPerBucket));
            
            // Find min, max, and count empty buckets
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int emptyBuckets = 0;
            
            for (int count : buckets) {
                if (count > 0) {
                    if (count < min) min = count;
                    if (count > max) max = count;
                } else {
                    emptyBuckets++;
                }
            }
            
            System.out.println("Min occurrences: " + min);
            System.out.println("Max occurrences: " + max);
            System.out.println("Empty buckets: " + emptyBuckets);
            
            // Calculate chi-square statistic for uniformity
            double chiSquare = 0;
            for (int count : buckets) {
                double diff = count - expectedPerBucket;
                chiSquare += (diff * diff) / expectedPerBucket;
            }
            System.out.println("Chi-square value: " + String.format("%.2f", chiSquare));
            
            // Show distribution (group into ranges)
            System.out.println("\n Distribution by range:");
            System.out.println("Range\t\tCount");
            System.out.println("-----\t\t-----");
            
            for (int i = 0; i < 256; i += 32) {
                int count = 0;
                for (int j = i; j < i + 32 && j < 256; j++) {
                    count += buckets[j];
                }
                System.out.println(i + "-" + (i+31) + "\t\t" + count);
            }
            
            // Save detailed distribution to file
            saveDistribution(buckets, "uniformity_results.txt");
            System.out.println("\nDetailed results saved to: uniformity_results.txt");
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Test avalanche effect
     * Small changes in input should produce very different hash values
     * 
     * @param filename File with test strings
     */
    public static void testAvalanche(String filename) {
        System.out.println("\n=== AVALANCHE EFFECT TEST ===\n");
        
        int totalTests = 0;
        int totalBitFlips = 0;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            PrintWriter writer = new PrintWriter(new FileWriter("avalanche_results.txt"));
            
            writer.println("Avalanche Effect Test Results");
            writer.println("==============================\n");
            writer.println("Original\tModified\tHash1\tHash2\tBits Changed\n");
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                
                // Original hash
                int originalHash = line.hashCode() & 0xFF;
                
                // Test 1: Change one character
                if (line.length() > 0) {
                    String modified = flipOneChar(line, 0);
                    int modifiedHash = modified.hashCode() & 0xFF;
                    int bitsChanged = countBitDifferences(originalHash, modifiedHash);
                    
                    totalTests++;
                    totalBitFlips += bitsChanged;
                    
                    writer.println(truncate(line, 20) + "\t" + 
                                 truncate(modified, 20) + "\t" + 
                                 originalHash + "\t" + 
                                 modifiedHash + "\t" + 
                                 bitsChanged);
                }
                
                // Test 2: Add one character
                String added = line + "x";
                int addedHash = added.hashCode() & 0xFF;
                int bitsChanged2 = countBitDifferences(originalHash, addedHash);
                
                totalTests++;
                totalBitFlips += bitsChanged2;
                
                writer.println(truncate(line, 20) + "\t" + 
                             truncate(added, 20) + "\t" + 
                             originalHash + "\t" + 
                             addedHash + "\t" + 
                             bitsChanged2);
            }
            
            reader.close();
            
            // Calculate average
            double avgBitsChanged = (double) totalBitFlips / totalTests;
            double percentage = (avgBitsChanged / 8.0) * 100;
            
            System.out.println("Total tests: " + totalTests);
            System.out.println("Average bits changed: " + String.format("%.2f", avgBitsChanged));
            System.out.println("Percentage: " + String.format("%.1f", percentage) + "%");
            System.out.println("Ideal is 50% (4 bits out of 8)");
            
            writer.println("\n=== SUMMARY ===");
            writer.println("Total tests: " + totalTests);
            writer.println("Average bits changed: " + String.format("%.2f", avgBitsChanged));
            writer.println("Percentage: " + String.format("%.1f", percentage) + "%");
            
            writer.close();
            
            System.out.println("\nDetailed results saved to: avalanche_results.txt");
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Count how many bits are different between two numbers
     */
    private static int countBitDifferences(int a, int b) {
        int xor = a ^ b;  // XOR gives 1 where bits differ
        int count = 0;
        
        // Count 1s in XOR result
        for (int i = 0; i < 8; i++) {
            if ((xor & (1 << i)) != 0) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Flip one character in a string
     */
    private static String flipOneChar(String str, int position) {
        if (str.length() == 0) return "a";
        
        char[] chars = str.toCharArray();
        char c = chars[position];
        
        // Change the character slightly
        if (c == 'z') {
            chars[position] = 'a';
        } else if (c == 'Z') {
            chars[position] = 'A';
        } else {
            chars[position] = (char)(c + 1);
        }
        
        return new String(chars);
    }
    
    /**
     * Truncate string for display
     */
    private static String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen) + "...";
    }
    
    /**
     * Save distribution data to file
     */
    private static void saveDistribution(int[] buckets, String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            
            writer.println("Hash Distribution Results");
            writer.println("=========================\n");
            writer.println("Hash Value\tOccurrences");
            
            for (int i = 0; i < 256; i++) {
                writer.println(i + "\t\t" + buckets[i]);
            }
            
            writer.close();
            
        } catch (IOException e) {
            System.out.println("Error saving distribution: " + e.getMessage());
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Hash Function Statistical Tests ===\n");
        
        System.out.println("Choose test:");
        System.out.println("1. Uniformity Test");
        System.out.println("2. Avalanche Effect Test");
        System.out.println("3. Both Tests");
        System.out.print("\nEnter choice (1-3): ");
        
        String choice = scanner.nextLine().trim();
        
        if (choice.equals("1")) {
            System.out.print("Enter filename for uniformity test: ");
            String filename = scanner.nextLine().trim();
            testUniformity(filename);
        }
        else if (choice.equals("2")) {
            System.out.print("Enter filename for avalanche test: ");
            String filename = scanner.nextLine().trim();
            testAvalanche(filename);
        }
        else if (choice.equals("3")) {
            System.out.print("Enter filename for uniformity test: ");
            String uniformFile = scanner.nextLine().trim();
            testUniformity(uniformFile);
            
            System.out.print("\nEnter filename for avalanche test: ");
            String avalancheFile = scanner.nextLine().trim();
            testAvalanche(avalancheFile);
        }
        else {
            System.out.println("Invalid choice.");
        }
        
        scanner.close();
    }
}
