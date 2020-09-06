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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private val appViewModel by viewModels<AppViewModel>()

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
        repoAdapter = RepoAdapter(appViewModel.repoList)
        repoAdapter.expandedItemPosition = appViewModel.expandedLayoutPosition
        repoRecyclerView.adapter = repoAdapter
    }

    private fun attachObservers() {
        appViewModel.repoListLiveData.observe(this) { newRepoList ->
            Log.d("repoCount", newRepoList.size.toString())
            if (newRepoList.isNotEmpty()) {
                if (!appViewModel.onScreenRotation) {
                    appViewModel.onScreenRotation = true
                    appViewModel.repoList.clear()
                    appViewModel.repoList.addAll(newRepoList)
                    if (this::repoAdapter.isInitialized) {
                        appViewModel.expandedLayoutPosition = null
                        repoAdapter.expandedItemPosition = null
                        repoAdapter.notifyDataSetChanged()
                    }
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
                appViewModel.sortRepoListByName()
                repoAdapter.expandedItemPosition = appViewModel.expandedLayoutPosition
                repoAdapter.notifyDataSetChanged()
                true
            }
            R.id.sort_by_stars -> {
                appViewModel.sortRepoListByStars()
                repoAdapter.expandedItemPosition = appViewModel.expandedLayoutPosition
                repoAdapter.notifyDataSetChanged()
                true
            }
            else -> false
        }
    }

    private fun makeApiCall() {
        appViewModel.onScreenRotation = false
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