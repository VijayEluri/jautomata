/*
 * (C) Copyright 2005 Arnaud Bailly (arnaud.oqube@gmail.com),
 *     Yves Roos (yroos@lifl.fr) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rationals.ioautomata.testing;

import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import rationals.State;
import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOSynchronization;
import rationals.ioautomata.IOTransition;

import java.util.List;
import java.util.Set;


/**
 * @author  nono
 * @version $Id: AlphaBetaIOTesterTest.java 12 2007-06-01 07:03:41Z oqube $
 */
public class AlphaBetaIOTesterTest extends TestCase {

    private IOAutomaton impl;

    private IOAutomaton ndet;

    private IOSynchronization synch;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        impl = new IOAutomaton();
        State s1 = impl.addState(true, true);
        State s2 = impl.addState(false, false);
        impl.addTransition(new IOTransition(s1, CoreMatchers.equalTo("a"), s2, IOAlphabetType.INPUT));
        impl.addTransition(new IOTransition(s2, "b", s1, IOAlphabetType.OUTPUT));

        ndet = new IOAutomaton();
        s1 = ndet.addState(true, true);
        s2 = ndet.addState(false, false);
        ndet.addTransition(new IOTransition(s1, CoreMatchers.equalTo("a"), s2, IOAlphabetType.INPUT));
        ndet.addTransition(new IOTransition(s2, "b", s1, IOAlphabetType.OUTPUT));
        ndet.addTransition(new IOTransition(s2, "c", s1, IOAlphabetType.OUTPUT));
        synch = new IOSynchronization();
    }

    private void todo() throws IOTestFailure {
        SynchIOAutomatonSMAdapter ior = new SynchIOAutomatonSMAdapter(impl);
        Thread th = new Thread(ior);
        th.setDaemon(true);
        th.start();
        AlphaBetaIOTester tester = new AlphaBetaIOTester();
        /* a basic evaluator that stops when 50 tests have been done */
        Evaluator ev = new Evaluator() {
            int count = 0;

            /*
             * (non-Javadoc)
             *
             * @see rationals.ioautomata.testing.Evaluator#setTester(rationals.ioautomata.testing.AlphaBetaIOTester)
             */
            public void setTester(AlphaBetaIOTester tester) {
            }

            /*
             * (non-Javadoc)
             *
             * @see rationals.ioautomata.testing.Evaluator#start()
             */
            public void start() {
            }

            /*
             * (non-Javadoc)
             *
             * @see rationals.ioautomata.testing.Evaluator#eval(java.util.Set,
             *      java.util.List, java.util.Set)
             */
            public double eval(Set traces, List trace, Set st) {
                return 1;
            }

            /*
             * (non-Javadoc)
             *
             * @see rationals.ioautomata.testing.Evaluator#isEnough(java.util.Set)
             */
            public boolean isEnough(Set traces) {
                return count >= 50;
            }

            public void step(Set state, Set next) {
                count++;
            }

            public void reset() {
                // TODO Auto-generated method stub

            }

            public double eval(Set traces, IOTransition tr, Set st) {
                return 0;
            }
        };
        tester.setEvaluator(ev);
        tester.test(ior, ndet);
        ior.stop();
        assertTrue(!ior.isError());
    }
}
