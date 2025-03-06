package org.crosswire.jsword.book.install;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.EventObject;

public class InstallerEvent extends EventObject
{
    private static final long serialVersionUID = 3257290248836102194L;

    private boolean added;
    private transient Installer installer;

    public InstallerEvent(Object source, Installer installer, boolean added)
    {
        super(source);
        this.installer = installer;
        this.added = added;
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        this.installer = null;
        is.defaultReadObject();
    }

    public boolean isAddition()     { return this.added; }

    public Installer getInstaller() { return this.installer; }
}