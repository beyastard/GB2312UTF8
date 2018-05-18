/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BeySoft;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 *
 * @author Bey
 */
public class GB2312UTF8 {
    
    static OutputStream out = null;
    
    static int b_out[] = {
        201, 267, 279, 293, 484, 587, 625, 657,
        734, 782, 827, 874, 901, 980, 5590
    };
    
    static int e_out[] = {
        216, 268, 280, 294, 494, 594, 632, 694,
        748, 794, 836, 894, 903, 994, 5594
    };
    
    static char hexDigit[] = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    
    public static void main(String[] args) {
        try {
            out = new FileOutputStream("gb2312_utf8.html");
            generateTable();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private static void generateTable() throws IOException  {
        boolean reserved = false;
        String name = null;
        
        // GB2312 is not supported by JDK, therefore using GBK.
        CharsetDecoder gbDecoder = Charset.forName("GBK").newDecoder();
        CharsetEncoder u8Encoder = Charset.forName("UTF-8").newEncoder();
        
        // Our byte buffers
        ByteBuffer gbBuffer;
        ByteBuffer u8Buffer;
        
        CharBuffer cbuf;
        int count = 0;
        
        // Write out the beginning of the html file.
        writeString("<!DOCTYPE html>\n");
        writeString("<html>\n\n");
        writeString("<head>\n");
        writeString("\t<meta charset=\"gb2312\" />\n");
        writeString("</head>\n\n");
        writeString("<body>\n");
        writeString("\t<h1>GB2312/UTF-8 Table</h1>\n");
        
        // GB2312 has 94 rows and 94 cloumns
        for (int i = 1; i <= 94; i++) {
            // Row settings
            if (i >= 1 && i <= 9) {
                reserved = false;
                name = "Graphic symbols";
            }
            else if (i >= 10 && i <= 15) {
                reserved = true;
                name = "Reserved";
            }
            else if (i >= 16 && i <= 55) {
                reserved = false;
                name = "Level 1 characters";
            }
            else if (i >= 56 && i <= 87) {
                reserved = false;
                name = "Level 2 characters";
            }
            else if (i >= 88 && i <= 94) {
                reserved = true;
                name = "Reserved";
            }
            
            // Row title
            writeln();
            writeString("<p>\n<h3>");
            writeNumber(i);
            writeString(" Row: " + name + "</h3>");
            writeln();
            writeString("</p>");
            writeln();
            
            if (!reserved) {
                writeln();
                writeSectionHeader();
                
                // Loop through all characters in one row
                for (int j = 1; j <= 94; j++) {
                    byte hi = (byte)(0xA0 + i);
                    byte lo = (byte)(0xA0 + j);
                    
                    // Check if valid GB character
                    if (isValidGB(i, j)) {
                        // Get the GB and UTF-8 codes
                        gbBuffer = ByteBuffer.wrap(new byte[]{hi, lo});
                        
                        try {
                            cbuf = gbDecoder.decode(gbBuffer);
                            u8Buffer = u8Encoder.encode(cbuf);
                        }
                        catch (CharacterCodingException e) {
                            cbuf = null;
                            u8Buffer = null;
                        }
                    } else {
                        cbuf = null;
                        u8Buffer = null;
                    }
                    
                    writeNumber(i);
                    writeNumber(j);
                    writeString(" ");
                    
                    if (cbuf != null) {
                        writeByte(hi);
                        writeByte(lo);
                        writeString(" ");
                        writeHex(hi);
                        writeHex(lo);
                        count++;
                    } else {
                        writeGBSpace();
                        writeString(" null");
                    }
                    
                    writeString(" ");
                    writeByteBuffer(u8Buffer, 3);
                    
                    if (j%2 == 0) writeln();
                    else writeString("   ");
                }
                
                writeString("</pre>\n");
            }
        }
        
        // Write out the end of the HTML file
        writeString("</body>\n\n");
        writeString("</html>");
        
        // Log output
        System.out.println("Number of GB characters wrote: " + count);
    }

    private static void writeString(String s) throws IOException {
        if (s != null)
            for (int i = 0; i < s.length(); i++)
                out.write((int) (s.charAt(i) & 0xFF));
    }

    private static void writeln() throws IOException {
        out.write(0x0D);
        out.write(0x0A);
    }

    private static void writeNumber(int i) throws IOException {
        String s = "00" + String.valueOf(i);
        writeString(s.substring(s.length() - 2, s.length()));
    }

    private static void writeSectionHeader() throws IOException {
        writeString("<pre>\n");
        writeString("ÇøÎ» "); // 区位码 (ÇøÎ»Âë) location code
        writeGBSpace();
        writeString("  GB  UTF-8 ");
        writeString("   ");
        writeString("ÇøÎ» "); // 区位码 (ÇøÎ»Âë) location code
        writeGBSpace();
        writeString("  GB  UTF-8 \n\n");
    }

    private static void writeGBSpace() throws IOException {
        out.write(0xA1);
        out.write(0xA1);
    }

    private static boolean isValidGB(int i, int j) {
        for (int l = 0; l < b_out.length; l++)        
            if (i * 100 + j >= b_out[l] && i * 100 + j <= e_out[l])
                return false; 
        
        return true;
    }

    private static void writeByte(byte b) throws IOException {
        out.write(b & 0xFF);
    }

    private static void writeHex(byte b) throws IOException {
        out.write((int) hexDigit[(b >> 4) & 0x0F]);
        out.write((int) hexDigit[b & 0x0F]);
    }

    private static void writeByteBuffer(ByteBuffer b, int l) throws IOException {
        int i;
        
        if (b == null) {
            writeString("null");
            i = 2;
        } else {
            for (i = 0; i < b.limit(); i++)
                writeHex(b.get(i));
        }
        
        for (int j = i; j < l; j++)
            writeString("  ");
    }
}
