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

package ar.com.javacuriosities.labs.safepoints;

public class Deoptimization {

    public static void main(String[] args) throws InterruptedException {
        runManyTimes(new Burner1()); // JIT speculatively assumes a monomorphic callsite
        runManyTimes(new Burner2()); // Assumption fails
    }

    // Run with: -XX:+PrintSafepointStatistics  -XX:PrintSafepointStatisticsCount=1 -XX:+PrintCompilation
    // Notice that there was no apparent safepoint triggered by deoptimization.
    // This is because the JVM triggers safepoints every second by default with
    // "no vm operation" specified as the vmop.

    // If the same is run with -XX:+UnlockDiagnosticVMOptions -XX:GuaranteedSafepointInterval=0,
    // you will see the deoptimization-triggered safepoint

    private static void runManyTimes(Burner burner) {
        for (int i = 0; i < 100_000; i++) {
            burner.burn(10);
        }
    }

    private interface Burner {
        void burn(long nanos);
    }

    private static class Burner1 implements Burner {

        @Override
        public void burn(long nanos) {
            long endTime = System.nanoTime() + nanos;
            while (System.nanoTime() < endTime) ;
        }
    }

    private static class Burner2 implements Burner {
        @Override
        public void burn(long nanos) {

        }
    }
}