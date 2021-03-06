package org.tinymediamanager.thirdparty;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Platform;

public class MediaInfoUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaInfoUtils.class);

  /**
   * load media info from /native/*
   */
  public static void loadMediaInfo() {
    try {
      String miv = "";

      // dropped linux shipment
      if (!Platform.isLinux()) {
        String nativepath = "native/";

        // windows
        if (Platform.isWindows()) {
          nativepath += "windows";
        }
        // linux
        else if (Platform.isLinux()) {
          nativepath += "linux";
        }
        // osx
        else if (Platform.isMac()) {
          nativepath += "mac";
        }

        // mac uses the same lib for 32 and 64 bit
        if (!Platform.isMac()) {
          // https://en.wikipedia.org/wiki/X86-64
          if (Platform.is64Bit()) {
            nativepath += "-x64";
          }
          else {
            nativepath += "-x86";
          }
        }

        // need that, since we cannot try and reload/unload a Class
        // MI does not load over UNC, so copy to temp
        if (System.getProperty("user.dir", "").startsWith("\\\\") || System.getProperty("user.dir", "").startsWith("//")) {
          LOGGER.debug("We're on a network UNC path!");
          File tmpDir = new File(System.getProperty("java.io.tmpdir"), "tmm");
          File nativeDir = new File(tmpDir, nativepath);
          FileUtils.copyDirectory(new File(nativepath), nativeDir);

          System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
          LOGGER.debug("Loading native mediainfo lib from: {}", nativeDir.getAbsolutePath());
          miv = MediaInfo.version(); // load class
        }
        else {
          System.setProperty("jna.library.path", nativepath);
          LOGGER.debug("Loading native mediainfo lib from: {}", nativepath);
          miv = MediaInfo.version(); // load class
        }
      }
      else {
        miv = MediaInfo.version(); // load class
      }

      if (!StringUtils.isEmpty(miv)) {
        LOGGER.info("Using " + miv);
      }
      else {
        LOGGER.error("could not load MediaInfo!");
        if (Platform.isLinux()) {
          LOGGER.error("Please try do install the library from your distribution");
        }
      }

    }
    catch (

    IOException e)

    {
      LOGGER.error("Could not load mediainfo", e);
    }
  }
}
