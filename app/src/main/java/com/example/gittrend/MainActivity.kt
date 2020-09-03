package com.example.gittrend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gittrend.database.Repository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appViewModel by viewModels<AppViewModel>()
    private val repoList = mutableListOf<Repository>()

    private lateinit var repoAdapter: RepoAdapter
    private lateinit var repoRecyclerView: RecyclerView
    private lateinit var layoutNoInternet: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutNoInternet = findViewById(R.id.layout_no_internet)
        findViewById<Button>(R.id.button_retry).setOnClickListener { appViewModel.refreshFromApi() }

        repoRecyclerView = findViewById(R.id.repo_recycler_view)
        repoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        repoAdapter = RepoAdapter(repoList)
        repoRecyclerView.adapter = repoAdapter

        attachObservers()
    }

    private fun attachObservers() {
        appViewModel.repoListLiveData.observe(this) { newRepoList ->
            Log.d("repoCount", newRepoList.size.toString())
            if (newRepoList.isEmpty()) appViewModel.refreshFromApi()
            else {
                repoRecyclerView.visibility = View.VISIBLE
                layoutNoInternet.visibility = View.GONE
                repoList.clear()
                repoList.addAll(newRepoList)
                if (this::repoAdapter.isInitialized) repoAdapter.notifyDataSetChanged()
            }
        }

        appViewModel.noInternetLiveData.observe(this) { noInternet ->
            if (noInternet) {
                repoRecyclerView.visibility = View.GONE
                layoutNoInternet.visibility = View.VISIBLE
                appViewModel.noInternetLiveData.value = false
            }
        }
    }
}