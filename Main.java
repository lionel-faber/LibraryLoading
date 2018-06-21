import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

  public static void main(String[] args) {

      String baseLibName = "safe_app";
      String libName = "safe_app_jni";
      String authBaseLibName = "safe_authenticator";
      String authLibName = "safe_authenticator_jni";

      String tempDir = System.getProperty("java.io.tmpdir");

      try {

      File generatedDir = new File(tempDir, "safe_app_java" + System.nanoTime());

      if (!generatedDir.mkdir()) {
        throw new IOException("Failed to create temp directory " + generatedDir.getName());
      }

      generatedDir.deleteOnExit();


      System.setProperty("java.library.path", generatedDir.getAbsolutePath());

      Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
      fieldSysPath.setAccessible(true);
      fieldSysPath.set(null, null);

      File file = new File(generatedDir, System.mapLibraryName(baseLibName));
      file.deleteOnExit();
      InputStream inputStream = Main.class.getResourceAsStream("libs/" + System.mapLibraryName(baseLibName));
      Files.copy(inputStream, file.toPath());

      file = new File(generatedDir, System.mapLibraryName(libName));
      file.deleteOnExit();
      inputStream = Main.class.getResourceAsStream("libs/"+ System.mapLibraryName(libName));
      Files.copy(inputStream, file.toPath());

      // System.loadLibrary("safe_app");
      System.loadLibrary("safe_app_jni");

      file = new File(generatedDir, System.mapLibraryName(authBaseLibName));
      file.deleteOnExit();
      inputStream = Main.class.getResourceAsStream("libs/" + System.mapLibraryName(authBaseLibName));
      Files.copy(inputStream, file.toPath());

      file = new File(generatedDir, System.mapLibraryName(authLibName));
      file.deleteOnExit();
      inputStream = Main.class.getResourceAsStream("libs/" + System.mapLibraryName(authLibName));
      Files.copy(inputStream, file.toPath());

      System.loadLibrary("safe_authenticator_jni");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
