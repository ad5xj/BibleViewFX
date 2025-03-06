package org.crosswire.jsword.book.install.sword;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.install.InstallerFactory;

import java.util.regex.Pattern;

public class HttpSwordInstallerFactory implements InstallerFactory {
    protected static final String PACKAGE_DIR = "packages/rawzip";

    private static final String LIST_DIR = "raw";

    private Pattern commaPattern = Pattern.compile(",");

    public Installer createInstaller() { return new HttpSwordInstaller(); }

    public Installer createInstaller(String installerDefinition) {
        String[] parts = this.commaPattern.split(installerDefinition, 6);

        switch (parts.length) {
        case 4:
            return createOldInstaller(parts);
        case 6:
            return createInstaller(parts);
        }
        throw new IllegalArgumentException(JSOtherMsg.lookupText("Not enough / symbols in url: {0}", new Object[]{installerDefinition}));
    }

    private Installer createInstaller(String[] parts) {
        AbstractSwordInstaller reply = new HttpSwordInstaller();
        reply.setHost(parts[0]);
        reply.setPackageDirectory(parts[1]);
        reply.setCatalogDirectory(parts[2]);
        if (parts[3].length() > 0) {
            reply.setProxyHost(parts[3]);
            if (parts[4].length() > 0) {
                reply.setProxyPort(Integer.valueOf(parts[4]));
            }
        }
        return reply;
    }

    private Installer createOldInstaller(String[] parts) {
        AbstractSwordInstaller reply = new HttpSwordInstaller();
        reply.setHost(parts[0]);
        reply.setPackageDirectory(parts[1] + '/' + "packages/rawzip");
        reply.setCatalogDirectory(parts[1] + '/' + "raw");
        if (parts[2].length() > 0) {
            reply.setProxyHost(parts[2]);
            if (parts[3].length() > 0) {
                reply.setProxyPort(Integer.valueOf(parts[3]));
            }
        }
        return reply;
    }
}