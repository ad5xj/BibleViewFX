package org.crosswire.common.compress;

import java.io.ByteArrayInputStream;

public enum CompressorType {
    ZIP {
        public Compressor getCompressor(byte[] input) {
            return new Zip(new ByteArrayInputStream(input));
        }
    },
    LZSS {
        public Compressor getCompressor(byte[] input) {
            return new LZSS(new ByteArrayInputStream(input));
        }
    },
    BZIP2 {
        public Compressor getCompressor(byte[] input) {
            return new BZip2(new ByteArrayInputStream(input));
        }
    },
    GZIP {
        public Compressor getCompressor(byte[] input) {
            return new Gzip(new ByteArrayInputStream(input));
        }
    },
    XZ {
        public Compressor getCompressor(byte[] input) {
            return new XZ(new ByteArrayInputStream(input));
        }
    };

    public static CompressorType fromString(String name) {
        for (CompressorType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }
        assert false;
        return null;
    }

    public abstract Compressor getCompressor(byte[] paramArrayOfbyte);
}
