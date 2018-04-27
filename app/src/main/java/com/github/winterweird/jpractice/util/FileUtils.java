package com.github.winterweird.jpractice.util;

import android.net.Uri;
import android.content.Context;
import android.database.Cursor;
import android.provider.DocumentsContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static String getContents(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        final StringBuilder stringBuilder = new StringBuilder();
        
        boolean done = false;
        
        while (!done) {
            final String line = reader.readLine();
            done = (line == null);
            
            if (line != null) {
                stringBuilder.append(line+"\n");
            }
        }
        
        reader.close();
        inputStream.close();
        
        return stringBuilder.toString();
    }
}
