package org.jchmlib.test;

import java.io.IOException;

import org.jchmlib.ChmFile;
import org.jchmlib.ChmUnitInfo;

public class ChmFind {
    public static void main(String[] argv) throws IOException {
        String obj = "behaviors";
        ChmFile chmFile = new ChmFile("F:/DHTML.chm");
        ChmUnitInfo ui = chmFile.resolveObject(obj);
        if (ui == null) {
            System.out.println("Object <" + obj + "> not found!");
        } else {
            System.out.println("Object <" + obj + "> found!");
        }
    }
}
