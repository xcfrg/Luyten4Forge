package com.kbrewster.mc;

import us.deathmarine.luyten.Luyten;

import java.io.File;
import java.util.Scanner;
import java.util.TreeMap;


/**
 * Bad naming convention as its nothing to do with Decompiling but i cba to rename
 * Why do i even need the annotation tbh lol??
 */
@Metadata(name = "Luyten4Forge", version = "1.4.0")
public class ExtractMappings extends Extractor implements Runnable {

    /**
     * Stores all the old mappings and what to replace it with
     */
    public static TreeMap<String, String> mappings = new TreeMap<>();

    /**
     * Cant use enums because they're numbers ._. so heartbreaking
     */
    public static String[] stableVersions = {
            "Stable 12 (1.7.10)",
            "Stable 18 (1.8.X)",
            "Stable 20 (1.8.8)",
            "Stable 22 (1.8.9)",
            "Stable 24 (1.9.X)",
            "Stable 26 (1.9.4)",
            "Stable 29 (1.10.2)",
            "Stable 32 (1.11.X)",
            "Stable 39 (1.12.X)",
            "Stable 43 (1.13.0)",
            "Stable 45 (1.13.1)",
            "Stable 47 (1.13.2)",
            "Stable 49 (1.14.0)",
            "Stable 51 (1.14.1)",
            "Stable 53 (1.14.2)",
            "Stable 56 (1.14.3)",
            "Stable 58 (1.14.4)",
            "Stable 60 (1.15.X)"
    };

    public static String currentMapping = null;
    public static File currentFile;

    /**
     * Iterates through the needed mapping putting them in a map and reloads the current project
     */
    @Override
    public void run() {
        try {
            mappings = new TreeMap<>();

            String[] fileNames = {"fields.csv", "methods.csv", "params.csv"};

            for (String fileName : fileNames) {
                if (currentMapping == null)
                    return;

                File file = getResourceAsFile("mapping/" + currentMapping + "/" + fileName);

                if (file != null) {
                    try (Scanner scanner = new Scanner(file)) {

                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            String[] lineSplit = line.split(",");
                            mappings.put(lineSplit[0], lineSplit[1]);
                        }
                    }
                }
            }

            if (currentFile != null) {
                System.out.println("[Open]: Opening " + currentFile.getAbsolutePath());

                Luyten.mainWindowRef.get().getModel().loadFile(currentFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads mapping
     *
     * @param mappings the version
     */
    public static void reloadMappings(String mappings) {
        currentMapping = mappings;
        new Thread(new ExtractMappings()).start();
    }
}
