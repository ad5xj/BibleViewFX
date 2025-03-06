package org.crosswire.jsword.book.sword;

import org.crosswire.common.compress.CompressorType;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawLDBackendState;
import org.crosswire.jsword.book.sword.state.ZLDBackendState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ZLDBackend extends RawLDBackend<ZLDBackendState> {

    private static final int ZDX_ENTRY_SIZE    = 8;
    private static final int BLOCK_ENTRY_SIZE  = 8;
    private static final int BLOCK_ENTRY_COUNT = 4;

    public ZLDBackend(SwordBookMetaData sbmd) {
        super(sbmd, 4);
    }

    @Override
    public ZLDBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getZLDBackendState(getBookMetaData());
    }

    @Override
    protected DataEntry getEntry(RawLDBackendState fileState, DataEntry entry) {
        ZLDBackendState state = null;
        if (fileState instanceof ZLDBackendState) {
            state = (ZLDBackendState) fileState;
        } else {
            log.error("Backend State was not of type ZLDBackendState. Ignoring this entry and exiting.");
            return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
        }
        DataIndex blockIndex = entry.getBlockIndex();
        long blockNum = blockIndex.getOffset();
        int blockEntry = blockIndex.getSize();
        byte[] uncompressed = null;
        if (blockNum == state.getLastBlockNum()) {
            uncompressed = state.getLastUncompressed();
        } else {
            try {
                byte[] temp = SwordUtil.readRAF(state.getZdxRaf(), blockNum * 8L, 8);
                if (temp == null || temp.length == 0) {
                    return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
                }
                int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
                temp = SwordUtil.readRAF(state.getZdtRaf(), blockStart, blockSize);
                decipher(temp);
                String compressType = getBookMetaData().getProperty("CompressType");
                uncompressed = CompressorType.fromString(compressType).getCompressor(temp).uncompress().toByteArray();
                state.setLastBlockNum(blockNum);
                state.setLastUncompressed(uncompressed);
            } catch (IOException e) {
                return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
            }
        }
        int entryCount = SwordUtil.decodeLittleEndian32(uncompressed, 0);
        if (blockEntry >= entryCount) {
            return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
        }
        int entryOffset = 4 + 8 * blockEntry;
        int entryStart = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset);
        int entrySize = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset + 4);
        int nullTerminator = SwordUtil.findByte(uncompressed, entryStart, (byte) 0);
        if (nullTerminator - entryStart + 1 == entrySize) {
            entrySize--;
        }
        byte[] entryBytes = new byte[entrySize];
        System.arraycopy(uncompressed, entryStart, entryBytes, 0, entrySize);
        DataEntry finalEntry = new DataEntry(entry.getName(), entryBytes, getBookMetaData().getBookCharset());
        return finalEntry;
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
    }

    @Override
    public void dumpIdxRaf() {
        ZLDBackendState zLDBackendState = null;
        RawLDBackendState state = null;
        long end = -1L;
        
        try {
            zLDBackendState = initState();
            end = getCardinality();
            StringBuilder buf = new StringBuilder();
            System.out.println("index\toffset\tsize\tkey\tvalue");
            long i;
            for (i = 0L; i < end; i++) {
                DataIndex index = getIndex((RawLDBackendState) zLDBackendState, i);
                int offset = index.getOffset();
                int size = index.getSize();
                buf.setLength(0);
                buf.append(i);
                buf.append('\t');
                buf.append(offset);
                buf.append('\t');
                buf.append(size);
                if (size > 0) {
                    byte[] data = SwordUtil.readRAF(zLDBackendState.getDatRaf(), offset, size);
                    DataEntry blockEntry = new DataEntry(Long.toString(i), data, getBookMetaData().getBookCharset());
                    DataIndex block = blockEntry.getBlockIndex();
                    DataEntry dataEntry = getEntry((RawLDBackendState) zLDBackendState, blockEntry);
                    String key = blockEntry.getKey();
                    buf.append('\t');
                    buf.append(key);
                    buf.append('\t');
                    buf.append(block.getOffset());
                    buf.append('\t');
                    buf.append(block.getSize());
                    buf.append('\t');
                    if (dataEntry.isLinkEntry()) {
                        String raw = dataEntry.getLinkTarget();
                        buf.append("Linked to: ").append(raw.replace('\n', ' '));
                    } else {
                        String raw = getRawText(dataEntry);
                        if (raw.length() > 43) {
                            buf.append(raw.substring(0, 40).replace('\n', ' '));
                            buf.append("...");
                        } else {
                            buf.append(raw);
                        }
                    }
                }
                System.out.println(buf.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BookException e) {
            e.printStackTrace();
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) zLDBackendState);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ZLDBackend.class);

    private static final long serialVersionUID = 3536098410391064446L;
}
