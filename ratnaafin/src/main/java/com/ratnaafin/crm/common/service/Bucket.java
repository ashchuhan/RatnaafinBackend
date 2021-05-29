package com.ratnaafin.crm.common.service;

import java.io.File;

public class Bucket {
    private static final String ENCRYPTION_ALGO = "RSA/ECB/PKCS1Padding";
    public static final String DIGEST_ALGO = "SHA-1";
    private static final String LOCAL_PATH = new File(".").getAbsolutePath();
    private static final String SEPERATOR = File.separator;
    private static final String bankUploadPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIJKAIBAAKCAgEAogLt0ytyWdJKymULpfuXnlcozBeRKGm+W/0PXDojyDiPAcSH\n" +
            "dKYvgUVP4Wz1nEQ9Qlu1BEHBOv/lPBWLkX4zEXE2pW1RAJBjY/FlngxdmMTyLLrL\n" +
            "JwzxNnfZ43ZbWUWpoWk/w6DqR36CagiGRDBbn8E4svYKHZb5xFndNiYTVjI6RcOz\n" +
            "RLF4KHoPFBWaiNPU7b4jmQJQii58BeHml6WASUOZcSofa7k2o/Qy9S9gUj+famR7\n" +
            "i+fTPQPhJtCBw5C/y62LfDfKrNjgObZf7ptisIiKh74ichU4nHvx9fzqUAdy1omD\n" +
            "iWGE1sC+lq2ZD19cNuDUGHzTXdOoUGNas7G2U3NLRUjdxsxB+1JzaNwhR9NmlLIm\n" +
            "cIK786HyufnH5Ict1i3cxibRSgsG2Stw/M7/qhYLxpX6xQKgvfULE+ti9v5Zj40n\n" +
            "DcKFgJ7HXp+UzJ25XCsQHg8Id7um0Qn9t//tODqNAzha5EP0MBfGMo8SLfEAl8ML\n" +
            "LT/WG+OfW8HGT+elMgVv4P4Ct3UvW3vzrxhcZlluZTJ8m/VF+6zoGZ9CjBc6GHDX\n" +
            "NFe70KzCe0gvtTEne4ubyk1vopxZXQPk7TGgLOB2O1QMduH+0JJXZB/tdpkS4YI+\n" +
            "PK8c42UVVkIK7Wox7EzNXAMjJzMNisUxp/rKti07Lg2ySZHtROdU01DBWg0CAwEA\n" +
            "AQKCAgAzOTvapYT9M0kkXvhk3Xxduu8SQG1L2ssuKH/fyB4iIwfMJkgxsI2akMCl\n" +
            "5J5vbv/zaC1+kA+5SMlAywsa0EzzOdluSnbu97gFFIDtjLlXIQX08IZk7WmisBg7\n" +
            "G+PcAPOWLkHT55/LRQuU3Ocezi9Z/myf4LzoCCEmXMddZIJ+oVjdT/fxQy9blOQJ\n" +
            "lJ8MnfVXyeNVcG7okbPy5C7VSaWbBH2SUyAmHiRRiFdIQFdzCdysdeK+JGkchIp+\n" +
            "jDRLEgwiKNfOSZGSPuv+M1R3i7eTW0h5jLs1WNJSX7nr7bBj50b+3/Gv0R6AC+b2\n" +
            "myKmwp7UVxeYOndV1iMEb7Vw1UJw9VpAJ/wUL5s+eAtHUwiNo7TiSbVpuNfQjIVl\n" +
            "tC2t+w6pkOwkD1SbjvqHEO+kB2DrpjCYW5TH107OAReedXgcPANC1mh0YEN+KFV7\n" +
            "p1IGn9U3tthUWMiynH8XpZb+qguNvgVNjYfSpYnC3VK54WIa8j6H9buSr9O5R555\n" +
            "7AMBOKTfnsCwvvZxNkDmhzLj1aBkatH9XsD+yolrqP07j5+XtkTYehS7lGkBtRYP\n" +
            "ut4aSUpVxGSEWPYPNiOCeBi0epxUhisJ+Vc6BbqBw79uCF/mrBnwN3UGP/zBrs0M\n" +
            "YXlpfa9YxW/PDsmdnJ4WjkAIh6CcEvqSZrtl5iRb9eBX1Lc2fQKCAQEAzsK4f9xi\n" +
            "Yttoos+IzE3sXTlD9BTLAb6qjVmhQO7n2rlcpf5SUlfvK9Q+9nFiHV0q3q6lo/S3\n" +
            "j2tKBS2EmCei+j2X0mgJ3hsnvWOHupLu9YOXKzB0zxj4QCqHObxcqeCFsThGF0qK\n" +
            "/XkWx1v1hGWJivMHU+vMht9ho1JbNAvNyJPJCFMVpqKmQOzdODoJX6GKhb5YM4Za\n" +
            "/8fGBYs+0iAXrj4HGw84IDMc1l/Evbz2A2hiPN0bzQQxxbTXhoRBHIL5C/XqND4n\n" +
            "ayS7baAN73ReRMjjGw26Mp+0IzVlgWeNS+WiC0VTlKqOZqoZF7oW1pAWm5DM4dlJ\n" +
            "TcgDx87SADgu6wKCAQEAyJgMd5w+LZYvzvympWRhOl/L4s2K44xCtrQgLVjOT14L\n" +
            "HD05laFV/Ats4nLS6Dr9V9p4zAG8TcdYPTwXMBBAVWiqwH/ykxYrhDQdG7etU9rL\n" +
            "tRDhKlOm507SUtXuJUIu5BM6PxS4VvHMDByDVo5+K00G/4EfUxhLMPjC/zTj5m7r\n" +
            "P4CZ4LHKc/iaW5OoEAZbcoz5QaXLM1Zs37N6fIskwvgBlXd2R12ItNIqk4TCBxsd\n" +
            "hh1e4fEnFDWLdj8ySWncduVNQKeDLmXbS8RGUOrE74FAhSV+NiEfQE8eDOfh2lyq\n" +
            "qWbd9hqlvj8CFicdzZJuZxiNqBA+OvNT/4JHFJQM5wKCAQEArq6Sg+vk75JhR9RA\n" +
            "xiM7g9Ni1u9OSA4XN862zw5IsmESpmdEk68ZMRVjkIBgnWBeXL5+0RNqle/WHKPF\n" +
            "N2udZP/uqeQeGhklLPvO61FCNAjWCbYpqeJ1snh7wMqkGx3GteBOT1U6MkZAeXP2\n" +
            "7MGAP/VthnhcVsKZUgu9ZC5o0IITQUY4r40RGx9DE0LtMm9fgfk7uWRUs27LnVl0\n" +
            "XMCiYSKkjDcCeynTvkYxCQCBdIWXIcbIuyBFELzlrg9El1sMIA/GrBaq5PmmpEqq\n" +
            "G1lcX469WNjzK9wDrA9rz7V2Cd5bVlxe7eZe4tvo0UP2EZawkwP2JQWXyfAnSYMx\n" +
            "pt/QuwKCAQA1VLd71X5ygEuVrHZh4p429uEuG5ehh9j02k7zv3iagVZS27RDo/FI\n" +
            "YYFBc2W/eG+CvAMBujRmOWmsXh2MGcYA7yL2qw3DLVHEhFZ42AytDq66JRK4PiVR\n" +
            "uhMREu+N2Fq/KJ2Dx+56ry6gybPhf2DsAj6qD5pUbVEQZBLz5wvwZ+0DpR1EMACz\n" +
            "dJUVY9x65jXJ7tEWMw4jSAcKMp2OHzBW5bkGFAzOTH/cE0RelB1B5mOjFIv1ZjgN\n" +
            "l27MsWCdRBfpRG2VkvO9kjcLndNUEBbr1mFhBObZXVUBtAc5Ry008Q4um7rHg64t\n" +
            "NnNHkH7L+PKBVnXTCP6Cjc73T5u2TOZhAoIBABuDL4hGb2C+mFs6YddcoRmTSje5\n" +
            "4Uw5GY+rQJyCXWWb+U9m3LPU+Ss3hpwOBOzTRMKTiXuAbtEHTDtPByKdoFCNpwb8\n" +
            "c1RwkEN1orBFOyjEgHQoS0n2KiM4HQHy4MW1/feb+Ceued/eC4TBLRx85sw/aB1T\n" +
            "7SFSeywWo4rbW5aWEx5Zk5KCkXBYxT6QoDcQxA8USEifQ9tlYhncnQHWefucEMmP\n" +
            "pMurv+0qvzG+KldLKoyT12JDYWLfYNfYLXL0N/w4fOxbRob5ShVdE0F9PyKziYU4\n" +
            "6uOGej4SZKm0tvAq1BUnnwrErDOjz53jV3AxtLbW4JqwknN8umKYW2Yb8Nk=\n" +
            "-----END RSA PRIVATE KEY-----\n";
    private static final String gstItrUploadPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\r\n" +
            "MIIBOQIBAAJBALleSewgcV8ZWphDMMFCZNdzqI+A9Xu7POlrdX7L+fennroEzkS6\r\n" +
            "ciWjxsgPmmSPbCyI7cG/6udT5hWsh8Abb8cCAwEAAQJAHguU4sN9bMMgxoY5fFGl\r\n" +
            "I7DY9O2bnIMEQ/dJc5RfP08yvyK43/EL+4zxHjPRHahIwFGaam2TLBDIJaB2TObN\r\n" +
            "6QIhAOQejTaNNykpGOfndeUVrR7XG5nbvyAmVQgQMuD7ybS/AiEA0AYiMd4AiJVf\r\n" +
            "gtMhVYxaWGeyvdQMicoeUw1aa3wT3vkCIF+t8MzQrjI1apzaGDjdvp8Q2iLOaHuz\r\n" +
            "mDaplK2I7jFBAiB0tpxE4kvmVfiKjC6tstuVskjE3M5UGMS+0EUk/S6g8QIgS3OY\r\n" +
            "ntv4fA7CKWGM0TeiSVE+MeEy/Jm0PHBmfMwxu3c=\r\n" +
            "-----END RSA PRIVATE KEY-----\r\n";

    public static String getEncAlgorithm() {
        return ENCRYPTION_ALGO;
    }

    public static String getDigestAlgorithm() {
        return DIGEST_ALGO;
    }

    public static String getCurrentDir() {
        return LOCAL_PATH;
    }

    public static String getSeperator() {
        return SEPERATOR;
    }

    //    public static String getPrivateKey2() {
//        return privateKey2;
//    }
//
//    public static String getPrivateKey() {
//        return privateKey;
//    }
    public static String getBankUploadPrivatekey() {
        return bankUploadPrivateKey;
    }

    public static String getGstItrUploadPrivateKey() {
        return gstItrUploadPrivateKey;
    }
}
