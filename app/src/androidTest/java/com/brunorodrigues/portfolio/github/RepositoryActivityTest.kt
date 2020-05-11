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

import org.mockito.Mockito.`when`

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.brunorodrigues.portfolio.github.api.Client
import com.brunorodrigues.portfolio.github.data.RepositoriesResponse
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryFragment
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryRecyclerViewAdapter
import com.brunorodrigues.portfolio.github.ui.repository.RepositoryViewModel
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class RepositoryActivityTest {
    @get:Rule
    val rule = ActivityTestRule(RepositoryActivity::class.java, true, false)

    @Mock
    lateinit var client: Client

    private var fragment: RepositoryFragment? = null
    private var viewModel: RepositoryViewModel? = null
    private var response: RepositoriesResponse

    init {
        val inputStream = javaClass.classLoader?.getResourceAsStream("mock_repository_response.json")
        val reader = JsonReader(InputStreamReader(inputStream))
        response = Gson().fromJson(reader, RepositoriesResponse::class.java)
    }

    @Before
    fun setUp()  {
        MockitoAnnotations.initMocks(this)
        Intents.init()
        val intent = Intent()
            .putExtra(RepositoryActivity.IS_TEST, true)
        rule.launchActivity(intent)
        fragment = rule.activity.supportFragmentManager.findFragmentByTag(RepositoryFragment::class.toString()) as RepositoryFragment
        IdlingRegistry.getInstance().register(fragment as IdlingResource)
    }

    @Test
    fun testHasData() = runBlockingTest {
        `when`(client.getRepositories(1)).thenReturn(response)
        viewModel = RepositoryViewModel(client)
        fragment?.setViewModel(viewModel!!)

        onView(withId(R.id.recyclerViewRepository)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.findViewById<RecyclerView>(R.id.recyclerViewRepository)
            val adapter = viewById.adapter as RepositoryRecyclerViewAdapter
            assertThat(adapter.itemCount, equalTo(response.items.size))
        })
    }

    @Test
    fun testNavigateToPullRequests() = runBlockingTest {
        `when`(client.getRepositories(1)).thenReturn(response)
        viewModel = RepositoryViewModel(client)
        fragment?.setViewModel(viewModel!!)

        onView(withId(R.id.recyclerViewRepository)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        intended(hasComponent(PullRequestActivity::class.java.name), times(1))
    }

    @Test
    fun testInfinityScroll() = runBlockingTest {
        `when`(client.getRepositories(1)).thenReturn(response)
        `when`(client.getRepositories(2)).thenReturn(response)
        viewModel = RepositoryViewModel(client)
        fragment?.setViewModel(viewModel!!)

        val adapter = AtomicReference<RepositoryRecyclerViewAdapter>()
        onView(withId(R.id.recyclerViewRepository)).check(ViewAssertion { view, noViewFoundException ->
            val viewById = rule.activity.findViewById<RecyclerView>(R.id.recyclerViewRepository)
            val temp = viewById.adapter as RepositoryRecyclerViewAdapter
            adapter.set(temp)
        })

        val firstPage = adapter.get().itemCount

        onView(withId(R.id.recyclerViewRepository)).perform(scrollToPosition<RecyclerView.ViewHolder>(adapter.get().itemCount - 1))

        assertThat(adapter.get().itemCount, greaterThan(firstPage))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(fragment as IdlingResource)
        Intents.release()
        fragment = null
        viewModel = null
    }
}