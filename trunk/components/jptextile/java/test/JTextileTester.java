/*
 * Copyright (c) 2007, Florin T.PATRASCU.
 * All Rights Reserved.
 */

import ca.flop.jpublish.wiki.JTextile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class JTextileTester {
    public static void main(String[] args) throws Exception {
        FileInputStream in = new FileInputStream("test.txt");


        byte[] buf = new byte[in.available()];

        in.read(buf);

        String text = new String(buf);


        text = JTextile.textile(text);

        //System.out.println(text);

        FileOutputStream out = new FileOutputStream(new File("test-output.html"));
        out.write(text.getBytes());
        out.close();

    }
}