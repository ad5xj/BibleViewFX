package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.state.GenBookBackendState;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.TreeKey;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class GenBookBackend extends AbstractBackend<GenBookBackendState> {

    private final TreeKeyIndex index;

    public GenBookBackend(SwordBookMetaData sbmd) {
        super(sbmd);
        this.index = new TreeKeyIndex(sbmd);
    }

    public GenBookBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getGenBookBackendState(getBookMetaData());
    }

    public boolean contains(Key key) {
        return (getRawTextLength(key) > 0);
    }

    public int getRawTextLength(Key key) {
        try {
            TreeNode node = find(key);
            if (node == null) {
                return 0;
            }
            byte[] userData = node.getUserData();
            if (userData.length == 8) {
                return SwordUtil.decodeLittleEndian32(userData, 4);
            }
            return 0;
        } catch (IOException e) {
            return 0;
        }
    }

    public String readRawContent(GenBookBackendState state, Key key) throws IOException, BookException {
        TreeNode node = find(key);
        if (node == null) {
            throw new BookException(JSMsg.gettext("No entry for '{0}' in {1}.", new Object[]{key.getName(), getBookMetaData().getInitials()}));
        }
        byte[] userData = node.getUserData();
        if (userData.length == 8) {
            int start = SwordUtil.decodeLittleEndian32(userData, 0);
            int size = SwordUtil.decodeLittleEndian32(userData, 4);
            byte[] data = SwordUtil.readRAF(state.getBdtRaf(), start, size);
            decipher(data);
            return SwordUtil.decode(key.getName(), data, getBookMetaData().getBookCharset());
        }
        return "";
    }

    private TreeNode find(Key key) throws IOException {
        List<String> path = new ArrayList<String>();
        for (Key parentKey = key; parentKey != null && parentKey.getName().length() > 0; parentKey = parentKey.getParent()) {
            path.add(parentKey.getName());
        }
        TreeNode node = this.index.getRoot();
        node = this.index.getFirstChild(node);
        for (int i = path.size() - 1; i >= 0; i--) {
            String name = path.get(i);
            while (node != null && !name.equals(node.getName())) {
                if (node.hasNextSibling()) {
                    node = this.index.getNextSibling(node);
                    continue;
                }
                log.error("Could not find {}", name);
                node = null;
            }
            if (node != null && name.equals(node.getName()) && i > 0) {
                node = this.index.getFirstChild(node);
            }
        }
        if (node != null && node.getName().equals(key.getName())) {
            return node;
        }
        return null;
    }

    public Key readIndex() {
        TreeKey treeKey = null;
        BookMetaData bmd = getBookMetaData();
        DefaultKeyList defaultKeyList = new DefaultKeyList(null, bmd.getName());
        try {
            TreeNode node = this.index.getRoot();
            treeKey = new TreeKey(node.getName(), null);
            doReadIndex(node, (Key) treeKey);
        } catch (IOException e) {
            log.error("Could not get read GenBook index", e);
        }
        return (Key) treeKey;
    }

    public void setAliasKey(GenBookBackendState state, Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void setRawText(GenBookBackendState rafBook, Key key, String text) throws BookException, IOException {
        throw new UnsupportedOperationException();
    }

    private void doReadIndex(TreeNode parentNode, Key parentKey) throws IOException {
        TreeNode currentNode = parentNode;
        if (currentNode.hasChildren()) {
            TreeNode childNode = this.index.getFirstChild(currentNode);
            while (true) {
                TreeKey childKey = new TreeKey(childNode.getName(), parentKey);
                parentKey.addAll((Key) childKey);
                doReadIndex(childNode, (Key) childKey);
                if (!childNode.hasNextSibling()) {
                    break;
                }
                childNode = this.index.getNextSibling(childNode);
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(GenBookBackend.class);
}
