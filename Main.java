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

    try {
      String baseLibName = "libsafe_app";
      String libName = "libsafe_app_jni";
      String authBaseLibName = "libsafe_authenticator";
      String authLibName = "libsafe_authenticator_jni";
      String extension = ".so";
      switch (OSInfo.getOs()) {
        case WINDOWS:
        libName = "safe_app_jni";
        baseLibName = "safe_app";
        authLibName = "safe_authenticator";
        extension = ".dll";
        break;
        case MAC:
        extension = ".dylib";
        break;
        default:
        break;
      }

      String tempDir = System.getProperty("java.io.tmpdir");

      File generatedDir = new File(tempDir, "safe_app_java" + System.nanoTime());

      if (!generatedDir.mkdir()) {
        throw new IOException("Failed to create temp directory " + generatedDir.getName());
      }

      generatedDir.deleteOnExit();


      System.setProperty("java.library.path", System.getProperty("java.library.path").concat(File.pathSeparator).concat(generatedDir.getAbsolutePath()));

      Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
      fieldSysPath.setAccessible(true);
      fieldSysPath.set(null, null);

      File file = new File(generatedDir, baseLibName.concat(extension));
      file.deleteOnExit();
      InputStream inputStream = Main.class.getResourceAsStream("libs/" + baseLibName.concat(extension));
      Files.copy(inputStream, file.toPath());

      file = new File(generatedDir, libName.concat(extension));
      file.deleteOnExit();
      inputStream = Main.class.getResourceAsStream("libs/" + libName.concat(extension));
      Files.copy(inputStream, file.toPath());

      System.loadLibrary("safe_app_jni");

      // file = new File(generatedDir, baseAuthLibName.concat(extension));
      // file.deleteOnExit();
      // inputStream = Main.class.getResourceAsStream("libs/" + baseAuthLibName.concat(extension));
      // Files.copy(inputStream, file.toPath());
      //
      // file = new File(generatedDir, authLibName.concat(extension));
      // file.deleteOnExit();
      // inputStream = Main.class.getResourceAsStream("libs/" + authLibName.concat(extension));
      // Files.copy(inputStream, file.toPath());
      //
      // System.loadLibrary("safe_authenticator");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
class OSInfo {

    public enum OS {
        WINDOWS,
        UNIX,
        POSIX_UNIX,
        MAC,
        OTHER;

        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    private static OS os = OS.OTHER;

    static {
        try {
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            osName = osName.toLowerCase();
            if (osName.contains("windows")) {
                os = OS.WINDOWS;
            } else if (osName.contains("linux")
                    || osName.contains("mpe/ix")
                    || osName.contains("freebsd")
                    || osName.contains("irix")
                    || osName.contains("digital unix")
                    || osName.contains("unix")) {
                os = OS.UNIX;
            } else if (osName.contains("mac os")) {
                os = OS.MAC;
            } else if (osName.contains("sun os")
                    || osName.contains("sunos")
                    || osName.contains("solaris")) {
                os = OS.POSIX_UNIX;
            } else if (osName.contains("hp-ux")
                    || osName.contains("aix")) {
                os = OS.POSIX_UNIX;
            } else {
                os = OS.OTHER;
            }

        } catch (Exception ex) {
            os = OS.OTHER;
        } finally {
            os.setVersion(System.getProperty("os.version"));
        }
    }

    public static OS getOs() {
        return os;
    }
}
