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

import org.mockito.Mockito.`when`

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.data.PullRequest
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestFragment
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestRecyclerViewAdapter
import com.brunorodrigues.portfolio.github.ui.pullrequest.PullRequestViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
class PullRequestActivityTest {
    @get:Rule
    val rule = ActivityTestRule(PullRequestActivity::class.java, true, false)

    @Mock
    lateinit var client: Client

    private var fragment: PullRequestFragment? = null
    private var viewModel: PullRequestViewModel? = null
    private var response: ArrayList<PullRequest>

    init {
        val inputStream = javaClass.classLoader?.getResourceAsStream("mock_pull_request_response.json")
        val reader = JsonReader(InputStreamReader(inputStream))
        val myType = object : TypeToken<ArrayList<PullRequest>>() {}.type
        response = Gson().fromJson(reader, myType)
    }

    @Before
    fun setUp()  {
        MockitoAnnotations.initMocks(this)
        Intents.init()
        val intent = Intent()
            .putExtra(PullRequestActivity.USER_NAME_KEY, "")
            .putExtra(PullRequestActivity.REPOSITORY_NAME_KEY, "")
            .putExtra(RepositoryActivity.IS_TEST, true)
        rule.launchActivity(Intent(intent))
        fragment = rule.activity.supportFragmentManager.findFragmentByTag(PullRequestFragment::class.toString()) as PullRequestFragment
        IdlingRegistry.getInstance().register(fragment as IdlingResource)
    }

    @Test
    fun testHasData() = runBlockingTest {
        `when`(client.getPullRequests("", "",1)).thenReturn(response)
        viewModel = PullRequestViewModel(client)
        fragment?.setViewModel(viewModel!!)

        onView(withId(R.id.recyclerViewPullRequests)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.findViewById<RecyclerView>(R.id.recyclerViewPullRequests)
            val adapter = viewById.adapter as PullRequestRecyclerViewAdapter
            assertThat(adapter.itemCount, greaterThan(0))
        })
    }

    @Test
    fun testInfinityScroll() = runBlockingTest {
        `when`(client.getPullRequests("", "",1)).thenReturn(response)
        `when`(client.getPullRequests("", "",2)).thenReturn(response)
        viewModel = PullRequestViewModel(client)
        fragment?.setViewModel(viewModel!!)

        val adapter = AtomicReference<PullRequestRecyclerViewAdapter>()
        onView(withId(R.id.recyclerViewPullRequests)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.findViewById<RecyclerView>(R.id.recyclerViewPullRequests)
            val temp = viewById.adapter as PullRequestRecyclerViewAdapter
            adapter.set(temp)
        })

        val firstPage = adapter.get().itemCount

        onView(withId(R.id.recyclerViewPullRequests)).perform(scrollToPosition<RecyclerView.ViewHolder>(adapter.get().itemCount - 1))

        assertThat(adapter.get().itemCount, greaterThan(firstPage))
    }

    @Test
    fun testNavigateToBrowser() = runBlockingTest {
        `when`(client.getPullRequests("", "",1)).thenReturn(response)
        viewModel = PullRequestViewModel(client)
        fragment?.setViewModel(viewModel!!)

        val adapter = AtomicReference<PullRequestRecyclerViewAdapter>()
        onView(withId(R.id.recyclerViewPullRequests)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.findViewById<RecyclerView>(R.id.recyclerViewPullRequests)
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
        IdlingRegistry.getInstance().unregister(fragment as IdlingResource)
        Intents.release()
        fragment = null
        viewModel = null
    }
}