package com.brunorodrigues.portfolio.github

import android.app.Instrumentation
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
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData

import org.hamcrest.Matchers.greaterThan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestFragment
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestRecyclerViewAdapter
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class PullRequestActivityTest {
    @get:Rule
    val rule = ActivityTestRule(PullRequestActivity::class.java, true, false)

    private lateinit var idlingResource: IdlingResource

    @Before
    fun setUp()  {
        Intents.init()
        val intent = Intent()
            .putExtra(PullRequestActivity.USER_NAME_KEY, "elastic")
            .putExtra(PullRequestActivity.REPOSITORY_NAME_KEY, "elasticsearch")
        rule.launchActivity(Intent(intent))
        idlingResource = rule.activity.supportFragmentManager.findFragmentByTag(PullRequestFragment::class.toString()) as IdlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun testHasData() {
        onView(withId(R.id.recyclerViewPullRequests)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.requireViewById<RecyclerView>(R.id.recyclerViewPullRequests)
            val adapter = viewById.adapter as PullRequestRecyclerViewAdapter
            assertThat(adapter.itemCount, greaterThan(0))
        })
    }

    @Test
    fun testInfinityScroll() {
        val adapter = AtomicReference<PullRequestRecyclerViewAdapter>()
        onView(withId(R.id.recyclerViewPullRequests)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.requireViewById<RecyclerView>(R.id.recyclerViewPullRequests)
            val temp = viewById.adapter as PullRequestRecyclerViewAdapter
            adapter.set(temp)
        })

        val firstPage = adapter.get().itemCount

        onView(withId(R.id.recyclerViewPullRequests)).perform(scrollToPosition<RecyclerView.ViewHolder>(adapter.get().itemCount - 1))

        assertThat(adapter.get().itemCount, greaterThan(firstPage))
    }

    @Test
    fun testNavigateToBrowser() {
        val adapter = AtomicReference<PullRequestRecyclerViewAdapter>()
        onView(withId(R.id.recyclerViewPullRequests)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.requireViewById<RecyclerView>(R.id.recyclerViewPullRequests)
            val temp = viewById.adapter as PullRequestRecyclerViewAdapter
            adapter.set(temp)
        })

        val url = adapter.get().items[0].htmlUrl

        val intentMatcher = allOf(hasAction(Intent.ACTION_VIEW), hasData(url))
        intending(intentMatcher).respondWith(Instrumentation.ActivityResult(0, null))

        onView(withId(R.id.recyclerViewPullRequests)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        intended(intentMatcher)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        Intents.release()
    }
}