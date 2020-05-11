package com.brunorodrigues.portfolio.github.ui.pullrequest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brunorodrigues.portfolio.github.PullRequestActivity
import com.brunorodrigues.portfolio.github.R
import com.brunorodrigues.portfolio.github.factory.DefaultViewModelFactory
import com.brunorodrigues.portfolio.github.ui.BaseFragment
import org.jetbrains.annotations.TestOnly

class PullRequestFragment : BaseFragment(PullRequestFragment::class.toString()) {

    companion object {
        fun newInstance(bundle: Bundle) : PullRequestFragment {
            val fragment = PullRequestFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var viewModel: PullRequestViewModel
    private lateinit var adapter: PullRequestRecyclerViewAdapter
    private var page: Int = 1
    private var loading: Boolean = false
    private lateinit var userName: String
    private lateinit var repositoryName: String
    private lateinit var progressBarCenter: ProgressBar
    private lateinit var progressBarBottom: ProgressBar
    private lateinit var emptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.pull_request_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(view != null) {
            emptyState = view!!.findViewById(R.id.emptyState)
            emptyState.visibility = View.GONE
            progressBarCenter = view!!.findViewById(R.id.progressBarCenter)
            progressBarBottom = view!!.findViewById(R.id.progressBarBottom)
            progressBarBottom.visibility = View.GONE

            val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerViewPullRequests)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if(!loading) {
                        if(linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == (viewModel.pullRequests.value?.size ?: 0) - 1) {
                            progressBarBottom.visibility = View.VISIBLE
                            viewModel.loadPullRequests(userName, repositoryName, page)
                            loading = true
                            setIdle(false)
                        }
                    }
                }
            })

            adapter = PullRequestRecyclerViewAdapter(context)
            adapter.setItemClick {
                val pullRequest = viewModel.pullRequests.value?.get(it)
                if(pullRequest != null && pullRequest.htmlUrl != "") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pullRequest.htmlUrl)))
                }
            }
            recyclerView.adapter = adapter

            viewModel = ViewModelProvider(this, DefaultViewModelFactory()).get(PullRequestViewModel::class.java)

            val bundle = arguments
            if(bundle != null) {
                userName = bundle.getString(PullRequestActivity.USER_NAME_KEY, "")
                repositoryName = bundle.getString(PullRequestActivity.REPOSITORY_NAME_KEY, "")
                val isTest = bundle.getBoolean(PullRequestActivity.IS_TEST, false)

                (activity as AppCompatActivity).supportActionBar?.title = repositoryName
                if(!isTest) initializeViewModel()
            }
        }
    }

    private fun initializeViewModel() {
        activity?.runOnUiThread(Runnable {
            viewModel.pullRequests.observe(viewLifecycleOwner, Observer {
                if(it.size > 0) {
                    adapter.items = it
                    adapter.notifyDataSetChanged()
                    page++
                    loading = false

                    if(emptyState.visibility != View.GONE) emptyState.visibility = View.GONE
                } else {
                    if(emptyState.visibility != View.VISIBLE) emptyState.visibility = View.VISIBLE
                }
                if(progressBarBottom.visibility != View.GONE) progressBarBottom.visibility = View.GONE
                if(progressBarCenter.visibility != View.GONE) progressBarCenter.visibility = View.GONE
                setIdle(true)
            })
            viewModel.error.observe(viewLifecycleOwner, Observer {
                Toast.makeText(context, R.string.error_message_toast, Toast.LENGTH_LONG).show()
            })

            viewModel.loadPullRequests(userName, repositoryName, page)
        })
    }

    override fun onDestroy() {
        viewModel.destroy()
        super.onDestroy()
    }

    @TestOnly
    fun setViewModel(viewModel: PullRequestViewModel) {
        this.viewModel = viewModel
        initializeViewModel()
    }
}
