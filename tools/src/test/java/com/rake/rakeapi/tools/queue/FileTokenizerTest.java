package com.rake.rakeapi.tools.queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 1002718 on 2016. 6. 3..
 */
public class FileTokenizerTest {
    FileTokenizer ft = null;
    String tempDir = null;

    @Before
    public void init() {
        ft = new FileTokenizer();
        tempDir = "/tmp";
    }

    @After
    public void close() {
        //   ft.close();
    }

    @Ignore
    @Test
    public void testSingleFile() {
        try {
            String originPath = "/tmp/singletest";
            File originDir = new File(originPath);
            File tempFile = File.createTempFile("/tmp/tokenizerTest", ".txt", new File(originPath));
            String resultPath = "/tmp/singleResult.dat";
            File removeResult = new File(resultPath);

            if (!originDir.exists()) {
                System.out.println("creating directory: " + "/tmp/singletest");
                boolean result = false;

                originDir.mkdir();
                result = true;

                if (result) {
                    System.out.println("DIR created");
                }
            }

            if (removeResult.exists()) {
                removeResult.delete();
            }

            tempFile.deleteOnExit();
            FileWriter fw = new FileWriter(tempFile);
            fw.write("{\"service_id\":\"test_id\",\"log\":\"loglog\"}{\"service_id\":\"test_id\",\"log\":\"loglog\"}");
            fw.flush();

            runProcess(new String[]{"--origin", originPath, "--target", resultPath});

            close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Ignore
    @Test
    public void testMultipleFile() {
        try {
            String originPath = "/tmp/multiple";

            File originDir = new File(originPath);
            String resultPath = "/tmp/multipleResult.dat";
            File removeResult = new File(resultPath);
            boolean result = false;
            if (removeResult.exists()) {
                removeResult.delete();
            }
            if (!originDir.exists()) {
                System.out.println("creating directory: " + "/tmp/singletest");


                originDir.mkdir();
                result = true;

                if (result) {
                    System.out.println("DIR created");
                }
            }
            for (int i = 0; i < 5; i++) {
                File tempFile = File.createTempFile("tokenizerTest" + i, ".dat", new File(originPath));
                tempFile.deleteOnExit();

                FileWriter fw = new FileWriter(tempFile);
                fw.write("{\"service_id\":\"test_id\",\"log\":\"loglog\"}{\"service_id\":\"test_id\",\"log\":\"loglog\"}");
                fw.flush();
            }
            runProcess(new String[]{"--origin", originPath, "--target", resultPath});
            close();
        } catch (IOException e) {
        }
    }

    public void runProcess(String args[]) {
        ft.getFilesInDirectory(ft.getOption(args));
    }
}
