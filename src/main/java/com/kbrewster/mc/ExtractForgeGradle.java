package com.kbrewster.mc;

import java.util.zip.*;
import java.io.*;
import java.util.*;

public class ExtractForgeGradle extends Extractor {

    public void setMappings(String directoryName) {
        for (File mapping : getMappings(directoryName)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(mapping));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\r\n");
                }
                reader.close();
                if (builder.toString().contains("net.minecraft")) {
                    for (String key : ExtractMappings.mappings.keySet()) {
                        builder = new StringBuilder(builder.toString().replaceAll(key, ExtractMappings.mappings.get(key)));
                    }
                }
                FileWriter writer = new FileWriter(mapping);
                writer.write(builder.toString());
                writer.close();

            } catch (Exception reader) {
                reader.printStackTrace();
            }
        }
    }

    public void extract(String version, String source, String destDir) throws IOException {
        this.unzip(Extractor.getResourceAsFile("mdk/" + version + ".zip").getAbsolutePath(), destDir);
        this.unZipIt(source, destDir + "/src/main/java");
        setMappings(destDir + "/src/main/java");
        if (!new File(source).delete()) {
            System.out.println("Failed to delete file " + source);
        }
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            if (!destDir.mkdirs()) {
                System.out.println("Failed to mkdir" + destDirectory);
            }
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                this.extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                if (!dir.mkdirs()) {
                    System.out.println("Failed to mkdir" + filePath);
                }
            }
            zipIn.closeEntry();
        }
        zipIn.close();
    }

    public static List<File> getMappings(String directoryName) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        List<File> resultList = new ArrayList<>(Arrays.asList(fList != null ? fList : new File[0]));
        for (File file : fList != null ? fList : new File[0]) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(getMappings(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public void unZipIt(String zipFile, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    System.out.println("Failed to mkdir" + outputFolder);
                }
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                System.out.println("File unzipped : " + newFile.getAbsoluteFile());
                if (!new File(newFile.getParent()).mkdirs()) {
                    System.out.println("Failed to mkdirs " + newFile.getParent());
                }
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}