package com.brunorodrigues.portfolio.github.ui.repository

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brunorodrigues.portfolio.github.R
import com.brunorodrigues.portfolio.github.factory.DefaultViewModelFactory
import com.brunorodrigues.portfolio.github.ui.BaseFragment

class RepositoryFragment : BaseFragment(RepositoryFragment::class.toString()) {

    companion object {
        fun newInstance() = RepositoryFragment()
    }

    private lateinit var viewModel: RepositoryViewModel
    private lateinit var adapter: RepositoryRecyclerViewAdapter
    private var page: Int = 1
    private var loading: Boolean = false
    private lateinit var progressBarCenter: ProgressBar
    private lateinit var progressBarBottom: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.repository_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(view != null) {
            progressBarCenter = view!!.findViewById(R.id.progressBarCenter)
            progressBarBottom = view!!.findViewById(R.id.progressBarBottom)
            progressBarBottom.visibility = View.GONE

            val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerViewRepository)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if(!loading) {
                        if(linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == (viewModel.repositories.value?.size ?: 0) - 1) {
                            progressBarBottom.visibility = View.VISIBLE
                            viewModel.loadRepositories(page)
                            loading = true
                            setIdle(false)
                        }
                    }
                }
            })

            adapter = RepositoryRecyclerViewAdapter(context)
            adapter.setItemClick {
                Log.d("setItemClick", it.toString());
            }
            recyclerView.adapter = adapter

            viewModel = ViewModelProvider(this, DefaultViewModelFactory()).get(RepositoryViewModel::class.java)
            viewModel.repositories.observe(viewLifecycleOwner, Observer {
               if(it.size > 0) {
                   adapter.items = it
                   adapter.notifyDataSetChanged()
                   page++
                   loading = false

                   if(progressBarBottom.visibility != View.GONE) progressBarBottom.visibility = View.GONE
                   if(progressBarCenter.visibility != View.GONE) progressBarCenter.visibility = View.GONE
               }
            })
            viewModel.error.observe(viewLifecycleOwner, Observer {
                Toast.makeText(context, R.string.error_message_toast, Toast.LENGTH_LONG).show()
            })
            viewModel.loadRepositories(page)
        }
    }

    override fun onDestroy() {
        viewModel.destroy()
        super.onDestroy()
    }
}
