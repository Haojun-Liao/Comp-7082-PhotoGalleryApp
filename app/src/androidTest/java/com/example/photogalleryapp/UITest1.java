package com.example.photogalleryapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

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

public class UITest1 {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    DateFormat df = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
    Date start, end;

    {
        try {
            start = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2021-09-21 00:00:00");
            end = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2021-09-22 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void captionTest() {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(clearText(),typeText("cap1"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cap1")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cap1")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cap1")));
    }

    @Test
    public void dateTest() {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(replaceText(new SimpleDateFormat(
                "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(start)), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(replaceText(new SimpleDateFormat(
                "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(end)), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("newer1")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("newer2")));

    }

    @Test
    public void dateAndCaptionTest() {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(replaceText(new SimpleDateFormat(
                "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(start)), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(replaceText(new SimpleDateFormat(
                "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(end)), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(replaceText("cap1"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cap1")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cap1")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cap1")));
    }
}
