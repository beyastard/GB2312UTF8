/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BeySoft;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author Bey
 */
public class GB2312UTF8 {
    
    static OutputStream out = null;    
    
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
        // GB2312 is not supported by JDK, therefore using GBK.
        CharsetDecoder gbDecoder = Charset.forName("GBK").newDecoder();
        CharsetEncoder u8Encoder = Charset.forName("UTF-8").newEncoder();
        
        // Our byte buffers
        ByteBuffer gbBuffer;
        ByteBuffer u8Buffer;
        
        CharBuffer cbuf;
        int count = 0;
        
        boolean reserved = false;
        String name = null;
        
        // Write out the beginning of the html file.
        writeString("<!DOCTYPE html>\n");
        writeString("<html>\n\n");
        writeString("<head>\n");
        writeString("\t<meta charset=\"gb2312\" />\n");
        writeString("</head>\n\n");
        writeString("<body>\n");
        writeString("\t<h1>GB2312/Unicode/UTF-8 Table</h1>\n");
        
        // GB2312 has 94 rows and 94 cloumns
        for (int i = 1; i <= 94; i++) {
            // Define row settings
            if (i >= 1 && i <= 9) {
                reserved = false;
                name = "Graphic symbols";
            } else if (i >= 10 && i <= 15) {
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
            
            // writing row title
            writeln();
            writeString("<p>");
            writeNumber(i);
            writeString(" Row: " + name);
            writeln();
            writeString("</p>");
            writeln();
            
            if (!reserved) {
                writeln();
                writeTableHeader();
                
                
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

    private static void writeTableHeader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
