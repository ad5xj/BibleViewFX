package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.DefaultKeyList;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.net.URI;

public class TreeKeyIndex implements Activatable {

    private static final String EXTENSION_INDEX = ".idx";
    private static final String EXTENSION_DATA = ".dat";

    private boolean active;

    private File idxFile;
    private File datFile;

    private SwordBookMetaData bmd;

    private RandomAccessFile idxRaf;
    private RandomAccessFile datRaf;

    public TreeKeyIndex(SwordBookMetaData sbmd) {
        this.bmd = sbmd;
    }

    public TreeNode getRoot() throws IOException {
        return getTreeNode(getOffset(0));
    }

    public TreeNode getParent(TreeNode node) throws IOException {
        return getTreeNode(getOffset(node.getParent()));
    }

    public TreeNode getFirstChild(TreeNode node) throws IOException {
        return getTreeNode(getOffset(node.getFirstChild()));
    }

    public TreeNode getNextSibling(TreeNode node) throws IOException {
        return getTreeNode(getOffset(node.getNextSibling()));
    }

    private int getOffset(int index) throws IOException {
        if (index == -1) {
            return -1;
        }
        checkActive();
        byte[] buffer = SwordUtil.readRAF(this.idxRaf, index, 4);
        return SwordUtil.decodeLittleEndian32(buffer, 0);
    }

    private TreeNode getTreeNode(int offset) throws IOException {
        TreeNode node = new TreeNode(offset);
        if (offset == -1) {
            return node;
        }
        checkActive();
        byte[] buffer = SwordUtil.readRAF(this.datRaf, offset, 12);
        node.setParent(SwordUtil.decodeLittleEndian32(buffer, 0));
        node.setNextSibling(SwordUtil.decodeLittleEndian32(buffer, 4));
        node.setFirstChild(SwordUtil.decodeLittleEndian32(buffer, 8));
        buffer = SwordUtil.readUntilRAF(this.datRaf, (byte) 0);
        int size = buffer.length;
        if (buffer[size - 1] == 0) {
            size--;
        }
        DefaultKeyList defaultKeyList = new DefaultKeyList(null, this.bmd.getName());
        node.setName(SwordUtil.decode(defaultKeyList.getName(), buffer, size, this.bmd.getBookCharset()).trim());
        buffer = SwordUtil.readNextRAF(this.datRaf, 2);
        int userDataSize = SwordUtil.decodeLittleEndian16(buffer, 0);
        if (userDataSize > 0) {
            node.setUserData(SwordUtil.readNextRAF(this.datRaf, userDataSize));
        }
        return node;
    }

    public final void activate(Lock lock) {
        String path = null;
        try {
            path = getExpandedDataPath();
        } catch (BookException e) {
            Reporter.informUser(this, (LucidException) e);
            return;
        }
        this.idxFile = new File(path + ".idx");
        this.datFile = new File(path + ".dat");
        if (!this.idxFile.canRead()) {
            Reporter.informUser(this, (LucidException) new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.idxFile.getAbsolutePath()})));
            return;
        }
        if (!this.datFile.canRead()) {
            Reporter.informUser(this, (LucidException) new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.datFile.getAbsolutePath()})));
            return;
        }
        try {
            this.idxRaf = new RandomAccessFile(this.idxFile, "r");
            this.datRaf = new RandomAccessFile(this.datFile, "r");
        } catch (IOException ex) {
            log.error("failed to open files", ex);
            this.idxRaf = null;
            this.datRaf = null;
        }
        this.active = true;
    }

    public final void deactivate(Lock lock) {
        try {
            if (this.idxRaf != null) {
                this.idxRaf.close();
            }
            if (this.datRaf != null) {
                this.datRaf.close();
            }
        } catch (IOException ex) {
            log.error("failed to close nt files", ex);
        } finally {
            this.idxRaf = null;
            this.datRaf = null;
        }
        this.active = false;
    }

    protected final void checkActive() {
        if (!this.active) {
            Activator.activate(this);
        }
    }

    private String getExpandedDataPath() throws BookException {
        URI loc = NetUtil.lengthenURI(this.bmd.getLibrary(), this.bmd.getProperty("DataPath"));
        if (loc == null) {
            throw new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}.", new Object[0]));
        }
        return (new File(loc.getPath())).getAbsolutePath();
    }

    private static final Logger log = LoggerFactory.getLogger(TreeKeyIndex.class);
}
