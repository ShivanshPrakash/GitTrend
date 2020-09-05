package com.example.gittrend

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.example.gittrend.adapters.RepoAdapter
import com.example.gittrend.database.Repository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private val appViewModel by viewModels<AppViewModel>()
    private val repoList = mutableListOf<Repository>()

    private lateinit var menuButton: ImageButton
    private lateinit var repoAdapter: RepoAdapter
    private lateinit var repoRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutNoInternet: ConstraintLayout
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupMenuButton()
        setSwipeRefreshLayout()
        setNoInternetLayout()
        setUpRecyclerView()

        skeletonScreen = Skeleton.bind(repoRecyclerView)
            .adapter(repoAdapter)
            .load(R.layout.layout_skeleton).show()

        attachObservers()
        if (appViewModel.isCacheExpired()) makeApiCall()
    }

    private fun setupMenuButton() {
        menuButton = findViewById(R.id.button_menu)
        menuButton.setOnClickListener {
            PopupMenu(this, it).apply {
                setOnMenuItemClickListener(this@MainActivity)
                inflate(R.menu.app_menu)
                show()
            }
        }
    }

    private fun setSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            makeApiCall()
        }
    }

    private fun setNoInternetLayout() {
        layoutNoInternet = findViewById(R.id.layout_no_internet)
        findViewById<Button>(R.id.button_retry).setOnClickListener { makeApiCall() }
    }

    private fun setUpRecyclerView() {
        repoRecyclerView = findViewById(R.id.repo_recycler_view)
        repoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        repoAdapter = RepoAdapter(repoList)
        repoRecyclerView.adapter = repoAdapter
    }

    private fun attachObservers() {
        appViewModel.repoListLiveData.observe(this) { newRepoList ->
            Log.d("repoCount", newRepoList.size.toString())
            if (newRepoList.isNotEmpty()) {
                repoList.clear()
                repoList.addAll(newRepoList)
                if (this::repoAdapter.isInitialized) {
                    appViewModel.expandedLayoutPosition?.let {
                        repoList[it].isExpanded = true
                        repoAdapter.expandedItemPosition = it
                    }
                    repoAdapter.notifyDataSetChanged()
                }
                displayContents()
            }
        }

        appViewModel.noInternetLiveData.observe(this) { noInternet ->
            if (noInternet) {
                displayNoInternet()
                appViewModel.noInternetLiveData.value = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appViewModel.expandedLayoutPosition = repoAdapter.expandedItemPosition
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_name -> {
                repoAdapter.expandedItemPosition?.let { repoList[it].isExpanded = false }
                repoAdapter.expandedItemPosition = null
                appViewModel.sortRepoListByName(repoList)
                repoAdapter.notifyDataSetChanged()
                true
            }
            R.id.sort_by_stars -> {
                repoAdapter.expandedItemPosition?.let { repoList[it].isExpanded = false }
                repoAdapter.expandedItemPosition = null
                appViewModel.sortRepoListByStars(repoList)
                repoAdapter.notifyDataSetChanged()
                true
            }
            else -> false
        }
    }

    private fun makeApiCall() {
        skeletonScreen.show()
        appViewModel.refreshFromApi()
    }


    private fun displayContents() {
        swipeRefreshLayout.visibility = View.VISIBLE
        layoutNoInternet.visibility = View.GONE
        menuButton.visibility = View.VISIBLE
        skeletonScreen.hide()
    }

    private fun displayNoInternet() {
        menuButton.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
        layoutNoInternet.visibility = View.VISIBLE
        skeletonScreen.hide()
    }
}