package org.crosswire.common.crypt;

public class Sapphire {

    private int[] cards;

    private int rotor;
    private int ratchet;
    private int avalanche;
    private int lastPlain;
    private int lastCipher;
    private int keypos;
    private int rsum;

    public Sapphire(byte[] aKey) {
        byte[] key = aKey;
        if (key == null) {
            key = new byte[0];
        }
        this.cards = new int[256];
        if (key.length > 0) {
            initialize(key);
        } else {
            hashInit();
        }
    }

    public byte cipher(byte b) {
        int bVal = b & 0xFF;
        this.ratchet += this.cards[this.rotor++];
        this.ratchet &= 0xFF;
        this.rotor &= 0xFF;
        int swaptemp = this.cards[this.lastCipher];
        this.cards[this.lastCipher] = this.cards[this.ratchet];
        this.cards[this.ratchet] = this.cards[this.lastPlain];
        this.cards[this.lastPlain] = this.cards[this.rotor];
        this.cards[this.rotor] = swaptemp;
        this.avalanche += this.cards[swaptemp];
        this.avalanche &= 0xFF;
        this.lastPlain = bVal ^ this.cards[this.cards[this.ratchet] + this.cards[this.rotor] & 0xFF] ^ this.cards[this.cards[this.cards[this.lastPlain] + this.cards[this.lastCipher] + this.cards[this.avalanche] & 0xFF]];
        this.lastCipher = bVal;
        return (byte) this.lastPlain;
    }

    public void burn() {
        for (int i = 0; i < 256; i++) {
            this.cards[i] = 0;
        }
        this.rotor = 0;
        this.ratchet = 0;
        this.avalanche = 0;
        this.lastPlain = 0;
        this.lastCipher = 0;
    }

    public void hashFinal(byte[] hash) {
        int i;
        for (i = 255; i >= 0; i--) {
            cipher((byte) i);
        }
        for (i = 0; i < hash.length; i++) {
            hash[i] = cipher((byte) 0);
        }
    }

    private void initialize(byte[] key) {
        for (int i = 0; i < 256; i++) {
            this.cards[i] = i;
        }
        int toswap = 0;
        this.keypos = 0;
        this.rsum = 0;
        for (int j = 255; j >= 0; j--) {
            toswap = keyrand(j, key);
            int k = this.cards[j];
            this.cards[j] = this.cards[toswap];
            this.cards[toswap] = k;
        }
        this.rotor = this.cards[1];
        this.ratchet = this.cards[3];
        this.avalanche = this.cards[5];
        this.lastPlain = this.cards[7];
        this.lastCipher = this.cards[this.rsum];
        toswap = 0;
        int swaptemp = toswap;
        this.rsum = swaptemp;
        this.keypos = this.rsum;
    }

    private void hashInit() {
        this.rotor = 1;
        this.ratchet = 3;
        this.avalanche = 5;
        this.lastPlain = 7;
        this.lastCipher = 11;
        int j = 255;
        for (int i = 0; i < 256; i++) {
            this.cards[i] = j--;
        }
    }

    private int keyrand(int limit, byte[] key) {
        if (limit == 0) {
            return 0;
        }
        int retryLimiter = 0;
        int mask = 1;
        while (mask < limit) {
            mask = (mask << 1) + 1;
        }
        while (true) {
            this.rsum = this.cards[this.rsum] + (key[this.keypos++] & 0xFF) & 0xFF;
            if (this.keypos >= key.length) {
                this.keypos = 0;
                this.rsum += key.length;
                this.rsum &= 0xFF;
            }
            int u = mask & this.rsum;
            if (++retryLimiter > 11) {
                u %= limit;
            }
            if (u <= limit) {
                return u;
            }
        }
    }
}
