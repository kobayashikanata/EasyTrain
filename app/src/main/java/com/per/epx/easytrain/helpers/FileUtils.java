package com.per.epx.easytrain.helpers;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * File utils
 * For reading/writing file
 */
public class FileUtils {

    //For parsing line of string to object
    public interface LineParser<T> {
        T parse(String[] cells);

        //Return string value all the cells need to save
        String[] toCellList(T obj);
    }

    //For knowing if write/read process is successful.
    public interface IComplete<T> {
        void callback(boolean success, T extra);
    }

    //Default charset
    private static final Charset charset = Charset.forName("utf-8");

    //Read file with permission check
    public static <T> void readLinesSafety(Activity activity,
                                           final String path,
                                           final IComplete<List<String>> onComplete) {
        GrantHandler.requestStoragePermission(activity, new GrantHandler.Callback() {
            @Override
            public void onGrantResults(int[] grantResults) {
                if (onComplete != null) {
                    List<String> values = FileUtils.readLines(path);
                    onComplete.callback(values != null, values);
                }
            }
        });
    }

    //Read file with permission check
    public static <T> void readObjectsSafety(Activity activity,
                                             final String path,
                                             final LineParser<T> parser,
                                             final IComplete<List<T>> onComplete) {
        GrantHandler.requestStoragePermission(activity, new GrantHandler.Callback() {
            @Override
            public void onGrantResults(int[] grantResults) {
                if (onComplete != null) {
                    List<T> values = FileUtils.readLineByLine(path, parser);
                    onComplete.callback(values != null, values);
                }
            }
        });
    }

    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    //Read lines in file
    public static List<String> readLines(String path) {
        File file = new File(path);
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(openInputStream(file), charset);
            br = new BufferedReader(reader);
            String line;
            //Read line
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }

    //Read file line by line
    //Return objects parse by a LineParser
    public static <T> List<T> readLineByLine(String path, LineParser<T> parser) {
        File file = new File(path);
        BufferedReader br = null;
        List<T> objList = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(openInputStream(file), charset);
            br = new BufferedReader(reader);
            String line;
            //Read line
            while ((line = br.readLine()) != null) {
                String cellText = line.replace("\"", "").replace("\t", "");
                //Parse object
                T obj = parser.parse(cellText.split(","));
                if (obj != null)
                    objList.add(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return objList;
    }


    //Write file with permission check
    public static <T> void writeCsvListSafety(Activity activity,
                                              final String path,
                                              final boolean append,
                                              final List<T> objList,
                                              final LineParser<T> parser,
                                              final IComplete onComplete) {
        GrantHandler.requestStoragePermission(activity, new GrantHandler.Callback() {
            @Override
            public void onGrantResults(int[] grantResults) {
                boolean success = FileUtils.writeCsvList(path, append, objList, parser);
                if (onComplete != null) {
                    onComplete.callback(success, null);
                }
            }
        });
    }

    //Write a list type of long to file
    public static <T> boolean writeCsvList(String path, boolean append, List<T> objList, LineParser<T> parser) {
        //Check if empty
        if (objList.size() == 0) return false;
        //Build text
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objList.size(); i++) {
            //Get cell values by parser
            String[] cells = parser.toCellList(objList.get(i));
            if (cells == null || cells.length == 0) continue;
            //Append text
            for (String cell : cells) {
                builder.append(cell).append(",");
            }
            builder.append("\r\n");
        }
        return write(path, append, builder.toString());
    }

    //Write text to file
    public static boolean write(String path, boolean append, String text) {
        //Basic content check
        if (path == null || text == null)
            return false;
        //Get out stream and write file
        PrintWriter writer = null;
        try {
            OutputStream out = openOutputStream(new File(path), append);
            writer = new PrintWriter(new OutputStreamWriter(out, charset));
            writer.print(text);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return false;
    }

    //Open input stream safety with basic check
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file
                        + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent
                            + "' could not be created");
                } else {
                    if (!file.createNewFile()) {
                        throw new IOException("File '" + file
                                + "' could not be created");
                    }
                }
            }
        }
        return new FileInputStream(file);
    }

    //Open output stream safety with basic check
    public static FileOutputStream openOutputStream(File file, boolean append)
            throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file
                        + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file
                        + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent
                            + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    public static FileOutputStream openOutputStream(File file)
            throws IOException {
        return openOutputStream(file, false);
    }

    //Copy file
    public static boolean copyFile(File srcFile, File dtsFile, boolean rewrite) {
        if (!srcFile.exists()) {
            System.out.println("Copy file fail from not exist.");
            return false;
        }
        if (!srcFile.isFile()) {
            System.out.println("Copy file fail from wrong file.");
            return false;
        }
        if (!srcFile.canRead()) {
            System.out.println("Could not Copy file from unRead file.");
            return false;
        }
        if (!dtsFile.getParentFile().exists()) {
            dtsFile.getParentFile().mkdir();
        }
        if (dtsFile.exists() && rewrite)
            dtsFile.delete();
        try {
            FileInputStream input = new FileInputStream(srcFile);
            FileOutputStream output = new FileOutputStream(dtsFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, byteRead);
            }
            input.close();
            output.close();
            System.out.println("Copy file from " + srcFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyFile(String srcPath, String dtsPath, boolean rewrite) {
        File srcFile = new File(srcPath);
        File dtsFile = new File(dtsPath);
        return copyFile(srcFile, dtsFile, rewrite);
    }

    public static String pathRelateInternal(Context context, String fileName) {
        return new File(context.getApplicationContext().getFilesDir(), fileName).getAbsolutePath();
    }

    public static String pathRelateExternal(Context context, String folder, String fileName) {
        return new File(context.getExternalFilesDir(folder), fileName).getAbsolutePath();
    }

}
