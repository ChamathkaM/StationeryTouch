package com.example.stationerystore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    CartActivity cartActivity;

    @Before
    public void setUp(){
        cartActivity = new CartActivity();
    }

    @Test
    public void oneProductTotPrice_isCorrect(){
        int result = cartActivity.oneProductTotPrice(100,5);
        assertEquals(500,result,0.001);
    }

    @Test
    public void overallTotPrice_isCorrect(){
        int result = cartActivity.overallTotPrice(1500);
        assertEquals(1500,result,0.001);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}