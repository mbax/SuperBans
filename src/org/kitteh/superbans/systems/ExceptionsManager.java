package org.kitteh.superbans.systems;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.kitteh.superbans.SuperBans;

public class ExceptionsManager {

    private HashSet<String> exceptions;

    public ExceptionsManager() {
        this.load();
    }

    public boolean exception(String name) {
        if (this.exceptions.contains(name.toLowerCase())) {
            return true;
        }
        return false;
    }

    private void load() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("exceptionsTEMP.txt");
        } catch (final FileNotFoundException e2) {
            SuperBans.log("No exceptions loaded. No such file: exceptionsTEMP.txt");
            return;
        }
        final BufferedReader bufferedReader = new BufferedReader(fileReader);
        this.exceptions = new HashSet<String>();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                this.exceptions.add(line.toLowerCase());
            }
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
        try {
            bufferedReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
