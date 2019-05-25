/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ar.com.javacuriosities.labs.multiple_classloaders;

import java.util.HashMap;
import java.util.Map;

public class Leaker {

    public static void startLeak() {
        Map<DemoObject, String> h = new HashMap<>();
        Thread t = new Thread(new DemoThread(h, 1), "Leaking Allocator");
        t.start();
    }

    public static void startFastLeak() {
        Map<DemoObject, String> h = new HashMap<>();
        Thread t = new Thread(new DemoThread(h, 2), "Fast Leaking Allocator");
        t.start();
    }

    public static void startNonLeak() {
        Map<DemoObject, String> h = new HashMap<>();
        Thread t = new Thread(new DemoThread(h, 0), "Not Leaking Allocator");
        t.start();
    }

    private static class DemoObject {

        private long myField1 = 1;
        private long myField2 = 2;

        private char[] chunk = new char[255];

        private long position;

        public DemoObject(int pos) {
            position = pos;
        }

        @Override
        public int hashCode() {
            return (int) position;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof DemoObject) && (o.hashCode() == position);
        }
    }

    private static class DemoThread implements Runnable {
        private Map<DemoObject, String> table;
        private int leakspeed;

        DemoThread(Map<DemoObject, String> table, int leakspeed) {
            this.table = table;
            this.leakspeed = leakspeed;
        }

        @Override
        public void run() {
            int total = 0;
            while (true) {
                for (int i = 0; i <= 100; i++)
                    put(total + i);

                for (int i = 0; i <= 100 - leakspeed; i++)
                    remove(total + i);

                total += 101;

                for (int i = 0; i < 10; i++) {
                    // Keep busy yielding for a little while...
                    Thread.yield();
                }
                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                }
            }
        }

        private void put(int n) {
            table.put(new DemoObject(n), "foo");
        }

        private String remove(int n) {
            return table.remove(new DemoObject(n));
        }
    }
}