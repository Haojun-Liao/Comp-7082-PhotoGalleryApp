package com.example.photogalleryapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class UITest2 {
    @Rule
    public ActivityTestRule<MainView> activityRule =
            new ActivityTestRule<>(MainView.class);

    @Test
    public void LocationTest() {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.minLongitude)).perform(replaceText("30.0"), closeSoftKeyboard());
        onView(withId(R.id.maxLongitude)).perform(replaceText("40.0"), closeSoftKeyboard());
        onView(withId(R.id.minLatitude)).perform(replaceText("-130.0"), closeSoftKeyboard());
        onView(withId(R.id.maxLatitude)).perform(replaceText("-120.0"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        String lonText = getText(withId(R.id.tvLongitude));
        String latText = getText(withId(R.id.tvLatitude));
        double lonVal = Double.valueOf(lonText);
        double latVal = Double.valueOf(latText);
        assert(-130.0 < latVal && latVal < -120.0);
        assert(30.0< lonVal && lonVal < 40.0);
        onView(withId(R.id.btnNext)).perform(click());
        lonText = getText(withId(R.id.tvLongitude));
        latText = getText(withId(R.id.tvLatitude));
        lonVal = Double.valueOf(lonText);
        latVal = Double.valueOf(latText);
        assert(-130.0 < latVal && latVal < -120.0);
        assert(30.0 < lonVal && lonVal < 40.0);
    }

    String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};

        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view;
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }
}
