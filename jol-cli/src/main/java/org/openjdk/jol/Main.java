/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jol;

import com.veontomo.InstrumentationAgent;
import org.openjdk.jol.operations.*;

import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.util.*;

public class Main {

    private static SortedMap<String, Operation> operations = new TreeMap<String, Operation>();

    static {
        registerOperation(new ObjectInternals());
        registerOperation(new ObjectExternals());
        registerOperation(new ObjectEstimates());
        registerOperation(new ObjectFootprint());
        registerOperation(new ObjectIdealPacking());
        registerOperation(new ObjectShapes());
        registerOperation(new StringCompress());
        registerOperation(new HeapDump());
        registerOperation(new HeapDumpStats());
    }

    private static void registerOperation(Operation op) {
        operations.put(op.label(), op);
    }

    public static void main(String... args) throws Exception {
        showObjectsSizes();
        System.out.println(InstrumentationAgent.getObjectSize(new int[]{}));
        System.out.println(InstrumentationAgent.getObjectSize(new Integer[]{}));
        String mode = (args.length >= 1) ? args[0] : "help";

        Operation op = operations.get(mode);
        if (op != null) {
            String[] modeArgs = Arrays.copyOfRange(args, 1, args.length);
            op.run(modeArgs);
        } else {
            if (!mode.equals("help")) {
                System.err.println("Unknown mode: " + mode);
                System.err.println();
                printHelp(System.err);
                System.exit(1);
            } else {
                printHelp(System.out);
                System.exit(0);
            }
        }
    }

    private static void showObjectsSizes() {

        class EmptyClass {
        }
        EmptyClass emptyClass = new EmptyClass();

        class StringClass {
            public String s;
        }
        StringClass stringClass = new StringClass();

        printObjectSize("empty string", "");
        printObjectSize("5-char string", "abcde");
        printObjectSize("long string", "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        printObjectSize("5-element string array", new String[]{"a", "b", "c", "d", "e"});
        printObjectSize("5-element empty string array", new String[5]);
        printObjectSize("5 element empty string array list", new ArrayList<String>(5));
        printObjectSize("5 element string array list", new ArrayList<String>(5) {{
            add("a");
            add("b");
            add("c");
            add("d");
            add("e");
        }});
        System.out.println("--- Integer");
        printObjectSize("max Integer", Integer.MAX_VALUE);
        printObjectSize("min Integer", Integer.MIN_VALUE);
        printObjectSize("0-capacity empty array of ints", new int[0]);
        printObjectSize("1-capacity empty array of ints", new int[1]);
        printObjectSize("2-capacity empty array of ints", new int[2]);
        printObjectSize("3-capacity empty array of ints", new int[3]);
        printObjectSize("4-capacity empty array of ints", new int[4]);
        printObjectSize("5-capacity empty array of ints", new int[5]);
        printObjectSize("1000-capacity empty array of ints", new int[1000]);
        printObjectSize("0-capacity empty array of Integers", new Integer[0]);
        printObjectSize("1-capacity empty array of Integers", new Integer[1]);
        printObjectSize("2-capacity empty array of Integers", new Integer[2]);
        printObjectSize("3-capacity empty array of Integers", new Integer[3]);
        printObjectSize("4-capacity empty array of Integers", new Integer[4]);
        printObjectSize("5-capacity empty array of Integers", new Integer[5]);
        printObjectSize("1000-capacity empty array of Integers", new Integer[1000]);
        printObjectSize("0-capacity empty ArrayList<Integer>", new ArrayList<Integer>(0));
        printObjectSize("1-capacity empty ArrayList<Integer>", new ArrayList<Integer>(1) {{
            add(1);
        }});
        printObjectSize("2-capacity empty ArrayList<Integer>", new ArrayList<Integer>(2) {{
            add(1);
            add(2);
        }});
        printObjectSize("3-capacity empty ArrayList<Integer>", new ArrayList<Integer>(3) {{
            add(1);
            add(1);
            add(1);
        }});
        printObjectSize("4-capacity empty ArrayList<Integer>", new ArrayList<Integer>(4) {{
            add(1);
            add(1);
            add(1);
            add(1);
        }});
        printObjectSize("5-capacity empty ArrayList<Integer>", new ArrayList<Integer>(5) {{
            add(1);
            add(1);
            add(1);
            add(1);
            add(1);
        }});
        printObjectSize("1000-capacity empty array of ArrayList<Integer>s", new ArrayList<Integer>(1000));
        System.out.println("---- Long");
        printObjectSize("max Long", Long.MAX_VALUE);
        printObjectSize("min Long", Long.MIN_VALUE);
        printObjectSize("0-capacity empty array of long", new long[0]);
        printObjectSize("1-capacity empty array of long", new long[1]);
        printObjectSize("2-capacity empty array of long", new long[2]);
        printObjectSize("3-capacity empty array of long", new long[3]);
        printObjectSize("4-capacity empty array of long", new long[4]);
        printObjectSize("5-capacity empty array of long", new long[5]);
        printObjectSize("1000-capacity empty array of long", new long[1000]);
        printObjectSize("0-capacity empty array of Long", new Long[0]);
        printObjectSize("1-capacity empty array of Long", new Long[1]);
        printObjectSize("2-capacity empty array of Long", new Long[2]);
        printObjectSize("3-capacity empty array of Long", new Long[3]);
        printObjectSize("4-capacity empty array of Long", new Long[4]);
        printObjectSize("5-capacity empty array of Long", new Long[5]);
        printObjectSize("1000-capacity empty array of Long", new Long[1000]);
        System.out.println("--- Float");
        printObjectSize("max Float", Float.MAX_VALUE);
        printObjectSize("max Float", Float.MAX_VALUE);
        printObjectSize("0-capacity empty array of float", new float[0]);
        printObjectSize("1-capacity empty array of float", new float[1]);
        printObjectSize("2-capacity empty array of float", new float[2]);
        printObjectSize("3-capacity empty array of float", new float[3]);
        printObjectSize("4-capacity empty array of float", new float[4]);
        printObjectSize("5-capacity empty array of float", new float[5]);
        printObjectSize("1000-capacity empty array of float", new float[1000]);
        printObjectSize("0-capacity empty array of Float", new Float[0]);
        printObjectSize("1-capacity empty array of Float", new Float[1]);
        printObjectSize("2-capacity empty array of Float", new Float[2]);
        printObjectSize("3-capacity empty array of Float", new Float[3]);
        printObjectSize("4-capacity empty array of Float", new Float[4]);
        printObjectSize("5-capacity empty array of Float", new Float[5]);
        printObjectSize("1000-capacity empty array of Float", new Float[1000]);
        System.out.println("--- Double");
        printObjectSize("max Double", Double.MAX_VALUE);
        printObjectSize("min Double", Double.MIN_VALUE);
        printObjectSize("0-capacity empty array of double", new double[0]);
        printObjectSize("1-capacity empty array of double", new double[1]);
        printObjectSize("2-capacity empty array of double", new double[2]);
        printObjectSize("3-capacity empty array of double", new double[3]);
        printObjectSize("4-capacity empty array of double", new double[4]);
        printObjectSize("5-capacity empty array of double", new double[5]);
        printObjectSize("1000-capacity empty array of double", new double[1000]);
        printObjectSize("0-capacity empty array of Double", new Double[0]);
        printObjectSize("1-capacity empty array of Double", new Double[1]);
        printObjectSize("2-capacity empty array of Double", new Double[2]);
        printObjectSize("3-capacity empty array of Double", new Double[3]);
        printObjectSize("4-capacity empty array of Double", new Double[4]);
        printObjectSize("5-capacity empty array of Double", new Double[5]);
        printObjectSize("1000-capacity empty array of Double", new Double[1000]);
        System.out.println("--- Short");
        printObjectSize("max Short", Short.MAX_VALUE);
        printObjectSize("min Short", Short.MIN_VALUE);
        printObjectSize("0-capacity empty array of short", new short[0]);
        printObjectSize("1-capacity empty array of short", new short[1]);
        printObjectSize("2-capacity empty array of short", new short[2]);
        printObjectSize("3-capacity empty array of short", new short[3]);
        printObjectSize("4-capacity empty array of short", new short[4]);
        printObjectSize("5-capacity empty array of short", new short[5]);
        printObjectSize("1000-capacity empty array of short", new short[1000]);
        printObjectSize("0-capacity empty array of Short", new Short[0]);
        printObjectSize("1-capacity empty array of Short", new Short[1]);
        printObjectSize("2-capacity empty array of Short", new Short[2]);
        printObjectSize("3-capacity empty array of Short", new Short[3]);
        printObjectSize("4-capacity empty array of Short", new Short[4]);
        printObjectSize("5-capacity empty array of Short", new Short[5]);
        printObjectSize("1000-capacity empty array of Short", new Short[1000]);
        System.out.println("--- Character");
        printObjectSize("max Character", Character.MAX_VALUE);
        printObjectSize("min Character", Character.MAX_VALUE);
        printObjectSize("0-capacity empty array of char", new char[0]);
        printObjectSize("1-capacity empty array of char", new char[1]);
        printObjectSize("2-capacity empty array of char", new char[2]);
        printObjectSize("3-capacity empty array of char", new char[3]);
        printObjectSize("4-capacity empty array of char", new char[4]);
        printObjectSize("5-capacity empty array of char", new char[5]);
        printObjectSize("1000-capacity empty array of char", new char[1000]);
        printObjectSize("0-capacity empty array of Character", new Character[0]);
        printObjectSize("1-capacity empty array of Character", new Character[1]);
        printObjectSize("2-capacity empty array of Character", new Character[2]);
        printObjectSize("3-capacity empty array of Character", new Character[3]);
        printObjectSize("4-capacity empty array of Character", new Character[4]);
        printObjectSize("5-capacity empty array of Character", new Character[5]);
        printObjectSize("1000-capacity empty array of Character", new Character[1000]);
        System.out.println("--- Byte");
        printObjectSize("max Byte", Byte.MAX_VALUE);
        printObjectSize("min Byte", Byte.MAX_VALUE);
        printObjectSize("0-capacity empty array of byte", new byte[0]);
        printObjectSize("1-capacity empty array of byte", new byte[1]);
        printObjectSize("2-capacity empty array of byte", new byte[2]);
        printObjectSize("3-capacity empty array of byte", new byte[3]);
        printObjectSize("4-capacity empty array of byte", new byte[4]);
        printObjectSize("5-capacity empty array of byte", new byte[5]);
        printObjectSize("1000-capacity empty array of byte", new byte[1000]);
        printObjectSize("0-capacity empty array of Byte", new Byte[0]);
        printObjectSize("1-capacity empty array of Byte", new Byte[1]);
        printObjectSize("2-capacity empty array of Byte", new Byte[2]);
        printObjectSize("3-capacity empty array of Byte", new Byte[3]);
        printObjectSize("4-capacity empty array of Byte", new Byte[4]);
        printObjectSize("5-capacity empty array of Byte", new Byte[5]);
        printObjectSize("1000-capacity empty array of Byte", new Byte[1000]);
        System.out.println("--- Boolean");
        printObjectSize("true Boolean", Boolean.TRUE);
        printObjectSize("false Boolean", Boolean.FALSE);
        printObjectSize("0-capacity empty array of boolean", new boolean[0]);
        printObjectSize("1-capacity empty array of boolean", new boolean[1]);
        printObjectSize("2-capacity empty array of boolean", new boolean[2]);
        printObjectSize("3-capacity empty array of boolean", new boolean[3]);
        printObjectSize("4-capacity empty array of boolean", new boolean[4]);
        printObjectSize("5-capacity empty array of boolean", new boolean[5]);
        printObjectSize("1000-capacity empty array of boolean", new boolean[1000]);
        printObjectSize("0-capacity empty array of Boolean", new Boolean[0]);
        printObjectSize("1-capacity empty array of Boolean", new Boolean[1]);
        printObjectSize("2-capacity empty array of Boolean", new Boolean[2]);
        printObjectSize("3-capacity empty array of Boolean", new Boolean[3]);
        printObjectSize("4-capacity empty array of Boolean", new Boolean[4]);
        printObjectSize("5-capacity empty array of Boolean", new Boolean[5]);
        printObjectSize("1000-capacity empty array of Boolean", new Boolean[1000]);
        System.out.println("--- Enum");
        printObjectSize("enum", Day.TUESDAY);
        printObjectSize("empty object", new Object());
        printObjectSize("empty class", emptyClass);
        printObjectSize("class with single string-valued field", stringClass);
    }

    private static void printObjectSize(String descr, Object object) {
        System.out.println(descr + ", object type: " + (object != null ? object.getClass() : "null object") +
                ", size: " + InstrumentationAgent.getObjectSize(object) + " bytes");
    }

    public enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    private static void printHelp(PrintStream pw) {
        pw.println("Usage: jol-cli.jar <mode> [optional arguments]*");
        pw.println();

        pw.println("Available modes: ");
        for (Operation lop : operations.values()) {
            pw.printf("  %20s: %s%n", lop.label(), lop.description());
        }
    }

}
