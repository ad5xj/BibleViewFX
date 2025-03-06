package org.crosswire.jsword.book.sword;

import org.crosswire.common.icu.DateFormatter;
import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawLDBackendState;

import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;

import java.text.DecimalFormat;
import java.text.MessageFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawLDBackend<T extends RawLDBackendState> extends AbstractKeyBackend<RawLDBackendState> {

    public RawLDBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd);
        this.datasize = datasize;
        this.entrysize = 4 + datasize;
    }

    public String readRawContent(RawLDBackendState state, Key key) throws IOException {
        return doReadRawContent(state, key.getName());
    }

    public RawLDBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getRawLDBackendState(getBookMetaData());
    }

    private String doReadRawContent(RawLDBackendState state, String key) throws IOException {
        if (key == null || key.length() == 0) {
            return "";
        }
        int pos = search(state, key);
        if (pos >= 0) {
            DataIndex index = getIndex(state, pos);
            DataEntry entry = getEntry(state, key, index);
            entry = getEntry(state, entry);
            if (entry.isLinkEntry()) {
                return doReadRawContent(state, entry.getLinkTarget());
            }
            return getRawText(entry);
        }
        throw new IOException(JSMsg.gettext("Key not found {0}", new Object[]{key}));
    }

    protected String getRawText(DataEntry entry) {
        String cipherKeyString = getBookMetaData().getProperty("CipherKey");
        byte[] cipherKeyBytes = null;
        if (cipherKeyString != null)
      try {
            cipherKeyBytes = cipherKeyString.getBytes(getBookMetaData().getBookCharset());
        } catch (UnsupportedEncodingException e) {
            cipherKeyBytes = cipherKeyString.getBytes();
        }
        return entry.getRawText(cipherKeyBytes);
    }

    public int getCardinality() {
        RawLDBackendState state = null;
        try {
            state = initState();
            if (state.getSize() == -1) {
                state.setSize((int) (state.getIdxRaf().length() / this.entrysize));
            }
            return state.getSize();
        } catch (BookException e) {
            return 0;
        } catch (IOException e) {
            return 0;
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    public Key get(int index) {
        RawLDBackendState state = null;
        try {
            state = initState();
            if (index < getCardinality()) {
                DataIndex dataIndex = getIndex(state, index);
                DataEntry entry = getEntry(state, getBookMetaData().getInitials(), dataIndex);
                String keytitle = internal2external(entry.getKey());
                return (Key) new DefaultLeafKeyList(keytitle);
            }
        } catch (BookException e) {

        } catch (IOException e) {

        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    public int indexOf(Key that) {
        RawLDBackendState state = null;
        try {
            state = initState();
            return search(state, that.getName());
        } catch (IOException e) {
            return -getCardinality() - 1;
        } catch (BookException e) {
            return -getCardinality() - 1;
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    public int getRawTextLength(Key key) {
        int i = 0;
        RawLDBackendState state = null;
        try {
            state = initState();
            int entrySize = 0;
            int entry = search(state, key.getName());
            byte[] buffer = SwordUtil.readRAF(state.getIdxRaf(), (entry * this.entrysize), this.entrysize);
            switch (this.datasize) {
            case 2:
                entrySize = SwordUtil.decodeLittleEndian16(buffer, 4);
                i = entrySize;
                return i;
            case 4:
                entrySize = SwordUtil.decodeLittleEndian32(buffer, 4);
                i = entrySize;
                return i;
            }
            assert false : this.datasize;
            i = entrySize;
            return i;
        } catch (IOException e) {
            return 0;
        } catch (BookException e) {
            return 0;
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    protected DataIndex getIndex(RawLDBackendState state, long entry) throws IOException {
        byte[] buffer = SwordUtil.readRAF(state.getIdxRaf(), entry * this.entrysize, this.entrysize);
        int entryOffset = SwordUtil.decodeLittleEndian32(buffer, 0);
        int entrySize = -1;
        switch (this.datasize) {
            case 2:
                entrySize = SwordUtil.decodeLittleEndian16(buffer, 4);
                return new DataIndex(entryOffset, entrySize);
            case 4:
                entrySize = SwordUtil.decodeLittleEndian32(buffer, 4);
                return new DataIndex(entryOffset, entrySize);
        }
        assert false : this.datasize;
        return new DataIndex(entryOffset, entrySize);
    }

    private DataEntry getEntry(RawLDBackendState state, String reply, DataIndex dataIndex) throws IOException {
        byte[] data = SwordUtil.readRAF(state.getDatRaf(), dataIndex.getOffset(), dataIndex.getSize());
        return new DataEntry(reply, data, getBookMetaData().getBookCharset());
    }

    protected DataEntry getEntry(RawLDBackendState state, DataEntry entry) {
        return entry;
    }

    private int search(RawLDBackendState state, String key) throws IOException {
        int total = getCardinality();
        int low = 0;
        int high = total;
        int match = -1;
        DataIndex dataIndex = null;
        String suppliedKey = null;
        while (high - low > 1) {
            int mid = low + high >>> 1;
            dataIndex = getIndex(state, mid);
            while (dataIndex.getSize() == 0) {
                mid += (high - mid > mid - low) ? 1 : -1;
                if (mid < low || mid > high) {
                    break;
                }
                dataIndex = getIndex(state, mid);
            }
            String str = normalizeForSearch(getEntry(state, key, dataIndex).getKey());
            if (suppliedKey == null) {
                suppliedKey = normalizeForSearch(external2internal(key, str));
            }
            int cmp = str.compareTo(suppliedKey);
            if (cmp < 0) {
                low = mid;
                continue;
            }
            if (cmp > 0) {
                high = mid;
                continue;
            }
            match = mid;
        }
        if (match >= 0) {
            return match;
        }
        dataIndex = getIndex(state, 0L);
        String entryKey = normalizeForSearch(getEntry(state, key, dataIndex).getKey());
        if (suppliedKey == null) {
            suppliedKey = normalizeForSearch(external2internal(key, entryKey));
        }
        if (entryKey.compareTo(suppliedKey) == 0) {
            return 0;
        }
        if ("true".equalsIgnoreCase(getBookMetaData().getProperty("CaseSensitiveKeys"))) {
            for (int i = 0; i < total; i++) {
                dataIndex = getIndex(state, i);
                if (getEntry(state, key, dataIndex).getKey().compareTo(key) == 0) {
                    return i;
                }
            }
        }
        return -(high + 1);
    }

    private String external2internal(String externalKey, String pattern) {
        if (externalKey.length() == 0) {
            return externalKey;
        }
        BookMetaData bmd = getBookMetaData();
        String keytitle = externalKey;
        if (BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory())) {
            Matcher m = DEVOTION_PATTERN.matcher(keytitle);
            if (m.matches()) {
                return keytitle;
            }
            Calendar greg = new GregorianCalendar();
            DateFormatter nameDF = DateFormatter.getDateInstance();
            nameDF.setLenient(true);
            Date date = nameDF.parse(keytitle);
            greg.setTime(date);
            Object[] objs = {Integer.valueOf(1 + greg.get(2)), Integer.valueOf(greg.get(5))};
            return DATE_KEY_FORMAT.format(objs);
        }
        if (bmd.hasFeature(FeatureType.GREEK_DEFINITIONS) || bmd.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
            Matcher m = STRONGS_PATTERN.matcher(keytitle);
            if (!m.matches()) {
                return keytitle;
            }
            if ("true".equalsIgnoreCase(bmd.getProperty("StrongsPadding"))) {
                int pos = keytitle.length() - 1;
                char lastLetter = keytitle.charAt(pos);
                boolean hasTrailingLetter = Character.isLetter(lastLetter);
                if (hasTrailingLetter) {
                    keytitle = keytitle.substring(0, pos);
                    pos--;
                    if (pos > 0 && keytitle.charAt(pos) == '!') {
                        keytitle = keytitle.substring(0, pos);
                    }
                }
                char type = keytitle.charAt(0);
                int strongsNumber = Integer.parseInt(keytitle.substring(1));
                StringBuilder buf = new StringBuilder();
                if (bmd.hasFeature(FeatureType.GREEK_DEFINITIONS) && bmd.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
                    buf.append(type);
                    buf.append(getZero4Pad().format(strongsNumber));
                    if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                        buf.append(lastLetter);
                    }
                    return buf.toString();
                }
                m = STRONGS_PATTERN.matcher(pattern);
                if (m.matches()) {
                    buf.append(type);
                    int numLength = m.group(2).length();
                    if (numLength == 4) {
                        buf.append(getZero4Pad().format(strongsNumber));
                    } else {
                        buf.append(getZero5Pad().format(strongsNumber));
                    }
                    if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                        buf.append(lastLetter);
                    }
                    return buf.toString();
                }
                return getZero5Pad().format(strongsNumber);
            }
            if (keytitle.charAt(1) == '0') {
                char type = keytitle.charAt(0);
                int pos = keytitle.length() - 1;
                char lastLetter = keytitle.charAt(pos);
                boolean hasTrailingLetter = Character.isLetter(lastLetter);
                if (hasTrailingLetter) {
                    keytitle = keytitle.substring(0, pos);
                    pos--;
                    if (pos > 0 && keytitle.charAt(pos) == '!') {
                        keytitle = keytitle.substring(0, pos);
                    }
                }
                int strongsNumber = Integer.parseInt(keytitle.substring(1));
                StringBuilder buf = new StringBuilder();
                buf.append(type);
                buf.append(strongsNumber);
                if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                    buf.append(lastLetter);
                }
            }
        }
        return keytitle;
    }

    private String internal2external(String internalKey) {
        BookMetaData bmd = getBookMetaData();
        String keytitle = internalKey;
        if (BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory()) && keytitle.length() >= 3) {
            Calendar greg = new GregorianCalendar();
            DateFormatter nameDF = DateFormatter.getDateInstance();
            String[] spec = StringUtil.splitAll(keytitle, '.');
            greg.set(2, Integer.parseInt(spec[0]) - 1);
            greg.set(5, Integer.parseInt(spec[1]));
            keytitle = nameDF.format(greg.getTime());
        }
        return keytitle;
    }

    private String normalizeForSearch(String internalKey) {
        BookMetaData bmd = getBookMetaData();
        String keytitle = internalKey;
        String caseSensitive = bmd.getProperty("CaseSensitiveKeys");
        if (!"true".equalsIgnoreCase(caseSensitive) && !BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory())) {
            return keytitle.toUpperCase(Locale.US);
        }
        return keytitle;
    }

    private DecimalFormat getZero5Pad() {
        return new DecimalFormat("00000");
    }

    private DecimalFormat getZero4Pad() {
        return new DecimalFormat("0000");
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
    }

    public void dumpIdxRaf() {
        RawLDBackendState state = null;
        long end = -1L;
        try {
            state = initState();
            end = getCardinality();
            StringBuilder buf = new StringBuilder();
            System.out.println("index\toffset\tsize\tkey\tvalue");
            long i;
            for (i = 0L; i < end; i++) {
                DataIndex index = getIndex(state, i);
                int offset = index.getOffset();
                int size = index.getSize();
                buf.setLength(0);
                buf.append(i);
                buf.append('\t');
                buf.append(offset);
                buf.append('\t');
                buf.append(size);
                if (size > 0) {
                    byte[] data = SwordUtil.readRAF(state.getDatRaf(), offset, size);
                    DataEntry entry = new DataEntry(Long.toString(i), data, getBookMetaData().getBookCharset());
                    String key = entry.getKey();
                    String raw = getRawText(entry);
                    buf.append('\t');
                    buf.append(key);
                    buf.append('\t');
                    if (raw.length() > 43) {
                        buf.append(raw.substring(0, 40).replace('\n', ' '));
                        buf.append("...");
                    } else {
                        buf.append(raw.replace('\n', ' '));
                    }
                } else {
                    buf.append("\t\t");
                }
                System.out.println(buf.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BookException e) {
            e.printStackTrace();
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    public void toIMP() {
        RawLDBackendState state = null;
        long end = -1L;
        try {
            state = initState();
            end = getCardinality();
            StringBuilder buf = new StringBuilder();
            long i;
            for (i = 0L; i < end; i++) {
                DataIndex index = getIndex(state, i);
                int offset = index.getOffset();
                int size = index.getSize();
                buf.setLength(0);
                buf.append("$$$");
                if (size > 0) {
                    byte[] data = SwordUtil.readRAF(state.getDatRaf(), offset, size);
                    DataEntry entry = new DataEntry(Long.toString(i), data, getBookMetaData().getBookCharset());
                    String key = entry.getKey();
                    String raw = getRawText(entry);
                    buf.append(key);
                    buf.append("\n");
                    buf.append(raw);
                }
                System.out.println(buf.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BookException e) {
            e.printStackTrace();
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    private static final MessageFormat DATE_KEY_FORMAT = new MessageFormat("{0,number,00}.{1,number,00}");

    private static final Pattern STRONGS_PATTERN = Pattern.compile("^([GH])(\\d+)((!)?([a-z])?)$");

    private static final Pattern DEVOTION_PATTERN = Pattern.compile("^\\d\\d\\.\\d\\d$");

    private final int datasize;

    private final int entrysize;

    private static final int OFFSETSIZE = 4;

    private static final long serialVersionUID = 818089833394450383L;
}
