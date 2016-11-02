/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.turn.ttorrent.common;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Utils {

    private final static char[] HEX_SYMBOLS = "0123456789ABCDEF".toCharArray();

    private Utils() {
    }

    /**
     * Convert a byte string to a string containing the hexadecimal
     * representation of the original data.
     *
     * @param bytes The byte array to convert.
     * @see <a href="http://stackoverflow.com/questions/332079">http://stackoverflow.com/questions/332079</a>
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_SYMBOLS[v >>> 4];
            hexChars[j * 2 + 1] = HEX_SYMBOLS[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String guessEncoding(byte[] bytes) {
        String DEFAULT_ENCODING = "UTF-8";
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            return simpleCode(bytes);
        }
        return encoding;
    }

    /**
     * 使用简单BOM方法判断编码
     * @param bytes
     * @return
     */
    private static String simpleCode(byte[] bytes) {
        String charset = "GB18030";
        byte[] first3Bytes = new byte[3];
        try {
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
}

