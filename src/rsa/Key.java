package rsa;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class Key {

    public final BigInteger n;

    public Key(BigInteger n) {
        this.n = n;
    }

    protected static BigInteger[] getKey(String file) {

        BigInteger[] values = new BigInteger[2];

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            for (int i = 0; (line = br.readLine()) != null; i++) {
                values[i] = new BigInteger(line);
            }

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return values;
    }
}

class PublicKey extends Key {

    public final BigInteger e;

    public PublicKey(BigInteger e, BigInteger n) {
        super(n);
        this.e = e;
    }

    public static PublicKey getPublicKey(String file) {

        if (!file.contains(".public.txt")) {
            file += ".public.txt";
        }
        
        BigInteger[] values = getKey(file);
        BigInteger e = values[0];
        BigInteger n = values[1];

        return new PublicKey(e, n);
    }

    public int maxMessageLength() {
        return (int) Math.floor((this.n).toString().length() / 3);
    }
}

class PrivateKey extends Key {

    public final BigInteger d;

    public PrivateKey(BigInteger d, BigInteger n) {
        super(n);
        this.d = d;
    }

    public static PrivateKey getPrivateKey(String file) throws FileNotFoundException, IOException {
        
        if (!file.contains(".private.txt")) {
            file += ".private.txt";
        }
        
        BigInteger[] values = getKey(file);
        BigInteger d = values[0];
        BigInteger n = values[1];

        return new PrivateKey(d, n);
    }
}

class KeyPair {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public KeyPair(PublicKey pub, PrivateKey priv) {
        this.publicKey = pub;
        this.privateKey = priv;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public static KeyPair generateNewPair(String keyName) {

        BigInteger p = Util.generatePrime(Util.newRandom(1000, 2000));
        BigInteger q = Util.generatePrime(Util.newRandom(1000, 2000));

        BigInteger n = p.multiply(q);

        BigInteger o = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        BigInteger e;

        int pow = Util.newRandom(1000, 2000);
        for (BigInteger i = BigInteger.valueOf(2).pow(pow); true; i = i.add(BigInteger.ONE)) {
            if (o.gcd(i).equals(BigInteger.ONE)) {
                e = i;
                break;
            }
        }

        BigInteger d = e.modInverse(o);

        PublicKey pub = new PublicKey(e, n);
        PrivateKey priv = new PrivateKey(d, n);

        Util.writeToFile(keyName + ".public.txt", pub.e + "\r\n" + pub.n);
        Util.writeToFile(keyName + ".private.txt", priv.d + "\r\n" + priv.n);

        return new KeyPair(pub, priv);

    }
}
