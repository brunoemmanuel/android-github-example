package com.brunorodrigues.portfolio.github

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.rule.ActivityTestRule

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent

import org.hamcrest.Matchers.greaterThan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryFragment
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryRecyclerViewAdapter
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class RepositoryActivityTest {
    @get:Rule
    val rule = ActivityTestRule(RepositoryActivity::class.java, true, false)

    private lateinit var idlingResource: IdlingResource

    @Before
    fun setUp()  {
        Intents.init()
        rule.launchActivity(Intent())
        idlingResource = rule.activity.supportFragmentManager.findFragmentByTag(RepositoryFragment::class.toString()) as IdlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun testHasData() {
        onView(withId(R.id.recyclerViewRepository)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.requireViewById<RecyclerView>(R.id.recyclerViewRepository)
            val adapter = viewById.adapter as RepositoryRecyclerViewAdapter
            assertThat(adapter.itemCount, greaterThan(0))
        })
    }

    @Test
    fun testNavigateToPullRequests() {
        onView(withId(R.id.recyclerViewRepository)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        intended(hasComponent(PullRequestActivity::class.java.name), times(1))
    }

    @Test
    fun testInfinityScroll() {
        val adapter = AtomicReference<RepositoryRecyclerViewAdapter>()
        onView(withId(R.id.recyclerViewRepository)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.requireViewById<RecyclerView>(R.id.recyclerViewRepository)
            val temp = viewById.adapter as RepositoryRecyclerViewAdapter
            adapter.set(temp)
        })

        val firstPage = adapter.get().itemCount

        onView(withId(R.id.recyclerViewRepository)).perform(scrollToPosition<RecyclerView.ViewHolder>(adapter.get().itemCount - 1))

        assertThat(adapter.get().itemCount, greaterThan(firstPage))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        Intents.release()
    }
}