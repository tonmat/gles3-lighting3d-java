package com.tonmatsu.gles3lighting3d.util;

import android.content.*;
import android.content.res.*;

import java.io.*;
import java.util.logging.*;

public final class AssetUtil {
    private static final Logger LOGGER = Logger.getLogger("AssetUtil");
    private static AssetManager assetManager;

    private AssetUtil() {
    }

    public static void initialize(Context context) {
        assetManager = context.getAssets();
    }

    public static void dispose() {
        assetManager.close();
    }

    public static InputStream getStream(String name) {
        try {
            return assetManager.open(name, AssetManager.ACCESS_BUFFER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "could not get asset as stream\n" + name, e);
        }
        return null;
    }

    public static BufferedReader getReader(String name) {
        final InputStream stream = getStream(name);
        if (stream == null)
            return null;
        return new BufferedReader(new InputStreamReader(stream));
    }

    public static String getString(String name) {
        final BufferedReader reader = getReader(name);
        if (reader == null)
            return null;
        try {
            try {
                final StringBuilder sb = new StringBuilder();
                while (true) {
                    final String line = reader.readLine();
                    if (line == null)
                        break;
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "could not get asset as string\n" + name, e);
        }
        return null;
    }
}
